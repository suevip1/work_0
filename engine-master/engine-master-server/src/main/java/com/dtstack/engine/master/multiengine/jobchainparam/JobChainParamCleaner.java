package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.IdRangeDTO;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleJobChainOutputParamDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.cron.DataClearSchedule;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.cron.DataClearSchedule;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-01 00:13
 */
@Component
public class JobChainParamCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamCleaner.class);

    @Value("${temp.job.output.param.retainInDay:2}")
    private Integer tempJobOutputParamRetainInDay;

    @Value("${job.output.param.clean.size:100}")
    private Integer jobOutputParamCleanSize;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobChainOutputParamDao scheduleJobChainOutputParamDao;

    @Autowired
    private JobChainParamQuerier jobChainParamQuerier;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleDictDao scheduleDictDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    public static final String SCHEDULE_JOB_CHAIN_OUTPUT_PARAM = "schedule_job_chain_output_param";

    private static final String JOB_TYPE_CONDITION = " and job_type in (0, 1, 3) ";
    private static final String CLEAR_CONDITION = " and gmt_modified < %s %s ";
    private static final String OUTPUT_PARAM_TYPE_CONDITION = " and output_param_type in (%s) ";

    private static final ExecutorService asyncCleanPool = new ThreadPoolExecutor(3, 6,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(2000), new CustomThreadFactory("job-output-param-clean-executor"));

    private final ScheduledExecutorService asyncScheduledCleanPool = new ScheduledThreadPoolExecutor(1,
            new CustomThreadFactory("job-output-param-clean-scheduled-executor"));

    private static final ExecutorService cleanUpPool = new ThreadPoolExecutor(3, 6,
            2L, TimeUnit.HOURS,
            new ArrayBlockingQueue<>(2000), new CustomThreadFactory("job-output-param-cleanUp-executor"));

    public void asyncCleanJobOutputProcessedParamsIfNeed(List<ScheduleJobChainOutputParam> chainOutputParams) {
        try {
            asyncCleanPool.execute(() -> {
                try {
                    cleanJobOutputProcessedHdfsParamsIfNeed(chainOutputParams);
                } catch (Exception e) {
                    LOGGER.warn("cleanJobOutputProcessedParamsIfNeed error, chainOutputParams:{}", chainOutputParams, e);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("asyncCleanPool execute error, chainOutputParams:{}", chainOutputParams, e);
        }
    }

    /**
     * 清理 hdfs 文件
     *
     * @param chainOutputParams
     */
    public void cleanJobOutputProcessedHdfsParamsIfNeed(List<ScheduleJobChainOutputParam> chainOutputParams) {
        if (CollectionUtils.isEmpty(chainOutputParams)) {
            return;
        }
        List<ScheduleJobChainOutputParam> processedOutputParams = chainOutputParams.stream()
                .filter(s -> s.getOutputParamType().equals(EOutputParamType.PROCESSED.getType()))
                .filter(s -> StringUtils.isNotEmpty(s.getParamValue()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(processedOutputParams)) {
            return;
        }
        Set<String> cleanedPath = new HashSet<>();
        for (ScheduleJobChainOutputParam processedOutputParam : processedOutputParams) {
            String hdfsPath = processedOutputParam.getParamValue();
            if (cleanedPath.contains(hdfsPath)) {
                // 已经清理过的 hdfs 文件不再清理
                continue;
            }
            String jobId = processedOutputParam.getJobId();
            ScheduleJob job = scheduleJobDao.getRdosJobByJobId(jobId);
            if (job == null) {
                // 考虑到输出参数是预生成的，所以可能并没有调用到 start 接口生成该 Job，此时也不用清理
                LOGGER.info("jobId:{}, not find job, no need cleanJobOutputProcessedParams", jobId);
                continue;
            }
            // 不产生 hdfs 文件的任务无须处理
            if (!JobChainParamHandler.supportWorkerTaskTypes().contains(job.getTaskType())
                || EScheduleJobType.SYNC.getType().equals(job.getTaskType())) {
                continue;
            }

            Long dtuicTenantId = job.getDtuicTenantId();
            JSONObject pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(job.getProjectId(), job.getAppType(), job.getTaskType(), dtuicTenantId, ScheduleEngineType.Hadoop.getEngineName(), job.getCreateUserId(), null, null, null);
            String typeName = componentService.buildHdfsTypeName(dtuicTenantId, null);
            Integer dataSourceCode = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
            pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
            pluginInfoWithComponentType.put(ConfigConstant.DATASOURCE_TYPE, dataSourceCode);
            String pluginInfo = pluginInfoWithComponentType.toJSONString();

            jobChainParamQuerier.deleteHdfsFile(dtuicTenantId, dataSourceCode, pluginInfo, hdfsPath);
            cleanedPath.add(hdfsPath);
            LOGGER.info("cleanJobOutputProcessedParamsIfNeed, jobId:{}, clean:{}, over", jobId, hdfsPath);
        }
    }

    public void asyncCleanLastCycJobOutputParamsIfNeed(ScheduleJob scheduleJob) {
        try {
            asyncCleanPool.execute(() -> {
                try {
                    cleanLastCycJobOutputParamsIfNeed(scheduleJob);
                } catch (Exception e) {
                    LOGGER.warn("cleanLastCycJobOutputParamsIfNeed error, jobId:{}", scheduleJob.getJobId(), e);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("asyncCleanPool execute error, jobId:{}", scheduleJob.getJobId(), e);
        }
    }

    /**
     * 删除上一周期实例任务的临时文件
     *
     * @param scheduleJob
     */
    public void cleanLastCycJobOutputParamsIfNeed(ScheduleJob scheduleJob) {
        if (scheduleJob.getTaskId() == -1) {
            LOGGER.info("temp job, no need clean lastCycJobOutputParams, jobId:{}", scheduleJob.getJobId());
            return;
        }
        // 上个周期实例的任务
        List<ScheduleJob> jobs = scheduleJobDao.getJobRangeByCycTimeByLimit(scheduleJob.getTaskId(), false, scheduleJob.getCycTime(), scheduleJob.getAppType(), scheduleJob.getType(), 1, null);
        if (CollectionUtils.isEmpty(jobs)) {
            LOGGER.info("no lastCycJob, no need clean lastCycJobOutputParams, jobId:{}", scheduleJob.getJobId());
            return;
        }
        ScheduleJob lastCycJob = jobs.get(0);
        String lastCycJobId = lastCycJob.getJobId();
        // 是否存在需要清理的 hdfs 文件
        List<ScheduleJobChainOutputParam> lastCycJobOutputParams = scheduleJobChainOutputParamDao.listByOutputParamType(lastCycJobId, EOutputParamType.PROCESSED.getType());
        if (CollectionUtils.isEmpty(lastCycJobOutputParams)) {
            LOGGER.info("cleanLastCycJobOutputParamsIfNeed, no need clean hdfs, lastCycJobId:{}", lastCycJobId);
        } else {
            // 只针对将结果写到 hdfs 的任务类型
            if (JobChainParamHandler.supportWorkerTaskTypes().contains(lastCycJob.getTaskType())
                 && !EScheduleJobType.SYNC.getType().equals(lastCycJob.getTaskType())) {
                Long dtuicTenantId = lastCycJob.getDtuicTenantId();
                JSONObject pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(lastCycJob.getProjectId(), lastCycJob.getAppType(), lastCycJob.getTaskType(), dtuicTenantId, ScheduleEngineType.Hadoop.getEngineName(), lastCycJob.getCreateUserId(), null, null, null);
                String typeName = componentService.buildHdfsTypeName(dtuicTenantId, null);
                Integer dataSourceCode = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
                pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
                pluginInfoWithComponentType.put(ConfigConstant.DATASOURCE_TYPE,dataSourceCode);

                String pluginInfo = pluginInfoWithComponentType.toJSONString();

                Set<String> hdfsPaths = lastCycJobOutputParams.stream().map(ScheduleJobChainOutputParam::getParamValue).collect(Collectors.toSet());
                jobChainParamQuerier.deleteHdfsFiles(lastCycJob.getDtuicTenantId(), dataSourceCode, pluginInfo, hdfsPaths);
                LOGGER.info("cleanLastCycJobOutputParamsIfNeed, clean hdfs over, lastCycJobId:{}", lastCycJobId);
            }
        }

        // 最后清理 DB 中存储的上一周期实例的输出数据
        int affectRows = scheduleJobChainOutputParamDao.deleteByJobId(lastCycJobId);
        LOGGER.info("cleanLastCycJobOutputParamsIfNeed, db clean ok, affectRows:{}, lastCycJobId:{}", affectRows, lastCycJobId);
    }

    /**
     * 清理不再使用的临时、手动任务产生的输出参数
     */
    public void cleanNotUsedOutputParams() {
        // 阈值尽量小，防止查出的数据量过大
        List<Integer> ids = scheduleJobChainOutputParamDao.listTempJobOutputParamIdsByThreshold(tempJobOutputParamRetainInDay);
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        int batchIndex = 1;
        List<List<Integer>> idPartitions = Lists.partition(ids, jobOutputParamCleanSize);
        for (List<Integer> idPartition : idPartitions) {
            List<ScheduleJobChainOutputParam> tempJobOutputParams = scheduleJobChainOutputParamDao.listCleanJobOutputParamsById(idPartition, JobChainParamHandler.supportRdbTaskTypes());
            // 清理 hdfs 文件
            cleanJobOutputProcessedHdfsParamsIfNeed(
                    tempJobOutputParams.stream()
                            .filter(p -> JobChainParamHandler.supportWorkerTaskTypes().contains(p.getTaskType())
                                     && !EScheduleJobType.SYNC.getType().equals(p.getTaskType()))
                            .collect(Collectors.toList())
            );
            int affectRows = scheduleJobChainOutputParamDao.deleleBatchByIds(idPartition);
            LOGGER.info("cleanNotUsedOutputParams ok, batchIndex:{}, affectRows:{}", batchIndex++, affectRows);
        }
    }

    public void scheduledCleanNotUsedOutputParams() {
        asyncScheduledCleanPool.scheduleWithFixedDelay(
                () -> {
                    try {
                        cleanNotUsedOutputParams();
                    } catch (Exception e) {
                        LOGGER.error("scheduledCleanNotUsedOutputParams error", e);
                    }
                },
                0,
                2,
                TimeUnit.DAYS);
    }

    public void cleanUpCycAndPatchJobOutputParams(ScheduleDict jobOutputParamScheduleDict) {
        // 获取配置信息
        if (jobOutputParamScheduleDict == null) {
            return;
        }
        if (!SCHEDULE_JOB_CHAIN_OUTPUT_PARAM.equals(jobOutputParamScheduleDict.getDictName())) {
            LOGGER.info("dictName:{} not equals schedule_job_chain_output_param, skip", jobOutputParamScheduleDict.getDictName());
            return;
        }
        String scheduleDictValue = StringUtils.trim(jobOutputParamScheduleDict.getDictValue());
        if (StringUtils.isEmpty(scheduleDictValue)) {
            return;
        }
        JSONObject tableConfig = JSONObject.parseObject(scheduleDictValue);
        String sqlAppendWhere = (String) tableConfig.getOrDefault(DataClearSchedule.appendWhere, "");
        boolean existsMaliciousSql = DataClearSchedule.existsMaliciousSql(sqlAppendWhere);
        if (existsMaliciousSql) {
            LOGGER.info("appendWhere may contains malicious sql:{}, skip over", sqlAppendWhere);
            return;
        }
        sqlAppendWhere += JOB_TYPE_CONDITION;
        Integer deleteDate = (Integer) tableConfig.getOrDefault(DataClearSchedule.deleteDateConfig, 366);
        DateTime deleteTime = DateTime.now().plusDays(-deleteDate);
        sqlAppendWhere = String.format(CLEAR_CONDITION, deleteTime.toString(DateUtil.UN_STANDARD_DATETIME_FORMAT), sqlAppendWhere);

        IdRangeDTO idRange = scheduleJobChainOutputParamDao.queryIdRange(sqlAppendWhere);
        if (idRange == null) {
            LOGGER.info("cleanUpOutputParams, idRange null, skip over");
            return;
        }

        Integer idIncrement = (Integer) tableConfig.getOrDefault(DataClearSchedule.increment, 500);
        for (long i = idRange.getStartId(); i <= idRange.getEndId(); i = i + idIncrement) {
            String whereSql = String.format(" and id >= %s and id <= %s %s ", i, i + idIncrement, sqlAppendWhere);
            long count = scheduleJobChainOutputParamDao.queryCountByCondition(whereSql);
            if (count <= 0) {
                continue;
            }
            // 输出参数为 hdfs 路径的任务类型
            String processedCondition = whereSql + String.format(OUTPUT_PARAM_TYPE_CONDITION, EOutputParamType.PROCESSED.getType())
                    + String.format(" and task_type in (%s)", StringUtils.join(JobChainParamHandler.supportWorkerTaskTypes().stream()
                                    .filter(t -> !EScheduleJobType.SYNC.getType().equals(t)).collect(Collectors.toSet()), GlobalConst.COMMA))
                    + " order by task_id, app_type ";
            // 如果存在的话，删除远端 HDFS 的数据
            List<ScheduleJobChainOutputParam> processedChainParam = scheduleJobChainOutputParamDao.queryByCondition(processedCondition);
            if (CollectionUtils.isNotEmpty(processedChainParam)) {
                List<List<ScheduleJobChainOutputParam>> partition = Lists.partition(processedChainParam, 50);
                Map<String, HdfsPluginInfo> task2HdfsMap = new ConcurrentHashMap<>();
                for (List<ScheduleJobChainOutputParam> jobChainOutputParams : partition) {
                    cleanUpPool.submit(() -> cleanProcessedChainHdfsParam(jobChainOutputParams, task2HdfsMap));
                }
            }
            // 删除 DB 记录
            scheduleJobChainOutputParamDao.deleteByCondition(whereSql);
        }
    }

    /**
     * 清理计算结果
     *
     * @param processedChainParam
     * @param task2HdfsMap
     */
    private void cleanProcessedChainHdfsParam(List<ScheduleJobChainOutputParam> processedChainParam, Map<String, HdfsPluginInfo> task2HdfsMap) {
        if (CollectionUtils.isEmpty(processedChainParam)) {
            return;
        }
        Map<String, Optional<ScheduleTaskShade>> taskMap = new HashMap<>();
        for (ScheduleJobChainOutputParam jobChainOutputParam : processedChainParam) {
            if (!EOutputParamType.PROCESSED.getType().equals(jobChainOutputParam.getOutputParamType())) {
                continue;
            }
            if (!JobChainParamHandler.supportWorkerTaskTypes().contains(jobChainOutputParam.getTaskType())) {
                continue;
            }
            if (EScheduleJobType.SYNC.getType().equals(jobChainOutputParam.getTaskType())) {
                // 数据同步任务并未将结果写到 hdfs，不用清理
                continue;
            }
            Long taskId = jobChainOutputParam.getTaskId();
            Integer appType = jobChainOutputParam.getAppType();
            Optional<ScheduleTaskShade> taskOptional = taskMap.computeIfAbsent(taskId + GlobalConst.STAR + appType, k -> {
                ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOneIncludeDelete(taskId, appType);
                return Optional.ofNullable(taskShade);
            });
            ScheduleTaskShade taskShade = taskOptional.orElse(null);
            if (taskShade == null) {
                LOGGER.warn("cleanProcessedChainParam can't find taskShade, jobId:{}, task:{}, skip", jobChainOutputParam.getJobId(),
                        taskId + GlobalConst.STAR + appType);
                continue;
            }

            HdfsPluginInfo hdfsPluginInfo = task2HdfsMap.computeIfAbsent(taskId + GlobalConst.STAR + appType, k -> {
                try {
                    JSONObject pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(taskShade.getProjectId(), taskShade.getAppType(), taskShade.getTaskType(), taskShade.getDtuicTenantId(),
                            ScheduleEngineType.Hadoop.getEngineName(), taskShade.getOwnerUserId(), null, null, null);
                    String typeName = componentService.buildHdfsTypeName(taskShade.getDtuicTenantId(), null);
                    Integer dataSourceCode = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
                    pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
                    pluginInfoWithComponentType.put(ConfigConstant.DATASOURCE_TYPE,dataSourceCode);
                    String pluginInfo = pluginInfoWithComponentType.toJSONString();
                    return new HdfsPluginInfo(typeName, pluginInfo, dataSourceCode);
                } catch (Exception e) {
                    LOGGER.warn("cleanProcessedChainParam organize HdfsPluginInfo error, jobId:{}, task:{}, skip", jobChainOutputParam.getJobId(),
                            taskId + GlobalConst.STAR + appType,
                            e);
                    return new HdfsPluginInfo();
                }
            });
            if (StringUtils.isEmpty(hdfsPluginInfo.getPluginInfo()) || StringUtils.isEmpty(hdfsPluginInfo.getTypeName())) {
                continue;
            }
            jobChainParamQuerier.deleteHdfsFile(taskShade.getDtuicTenantId(), hdfsPluginInfo.getDataSourceCode(), hdfsPluginInfo.getPluginInfo(), jobChainOutputParam.getParamValue());
        }
    }

    // todo optimize: can move to handler class
    public static class HdfsPluginInfo {
        private String typeName;
        private String pluginInfo;
        private Integer dataSourceCode;

        public Integer getDataSourceCode() {
            return dataSourceCode;
        }

        public void setDataSourceCode(Integer dataSourceCode) {
            this.dataSourceCode = dataSourceCode;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getPluginInfo() {
            return pluginInfo;
        }

        public void setPluginInfo(String pluginInfo) {
            this.pluginInfo = pluginInfo;
        }

        public HdfsPluginInfo() {
        }

        public HdfsPluginInfo(String typeName, String pluginInfo) {
            this.typeName = typeName;
            this.pluginInfo = pluginInfo;
        }

        public HdfsPluginInfo(String typeName, String pluginInfo, Integer dataSourceCode) {
            this.typeName = typeName;
            this.pluginInfo = pluginInfo;
            this.dataSourceCode = dataSourceCode;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HdfsPluginInfo{");
            sb.append("typeName='").append(typeName).append('\'');
            sb.append(", pluginInfo='").append(pluginInfo).append('\'');
            sb.append(", dataSourceCode=").append(dataSourceCode);
            sb.append('}');
            return sb.toString();
        }
    }
}
