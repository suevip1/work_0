package com.dtstack.engine.master.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IJob;
import com.dtstack.dtcenter.loader.client.IYarn;
import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.dtcenter.loader.dto.JobParam;
import com.dtstack.dtcenter.loader.dto.SingleSqlInfo;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.RdbmsSourceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDescriptionDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.FsyncSqlParamInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.ComponentUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.dao.ScheduleJobChainOutputParamDao;
import com.dtstack.engine.master.dto.FsyncSql;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.mapstruct.OperatorStruct;
import com.dtstack.engine.master.mapstruct.PlugInStruct;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.utils.FsyncUtil;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : dazhi
 * date : 2022/3/3 1:36 PM
 * email : dazhi@dtstack.com
 * description : DatasourceX operator, 用于提交 rdb 任务
 */
@Component
public class DataSourceXOperator implements TaskOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceXOperator.class);

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private OperatorStruct operatorStruct;

    @Autowired
    private PlugInStruct plugInStruct;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleJobChainOutputParamDao scheduleJobChainOutputParamDao;

    @Value("${temp.job.max.preview.num:1000}")
    private Integer tempJobMaxPreviewNum;

    public JudgeResult judgeSlots(JobClient jobClient) {
        Integer dataSourceType = rdosWrapper.getDataSourceType(jobClient.getPluginInfo(), jobClient.getEngineType(), null, jobClient.getTenantId());
        String pluginInfo = getPluginInfo(jobClient, dataSourceType);
        IJob iJob = ClientCache.getJob(dataSourceType);
        JobParam jobParam = getJobParam(pluginInfo, jobClient);
        Boolean result = iJob.judgeSlots(getISourceDTO(pluginInfo, dataSourceType, jobClient.getTenantId()), jobParam);
        if (result) {
            return JudgeResult.ok();
        } else {
            return JudgeResult.notOk("DataSourceX judgeSlots result is false.");
        }
    }

    public JobResult submitJob(JobClient jobClient) {
        Integer dataSourceType = rdosWrapper.getDataSourceType(jobClient.getPluginInfo(), jobClient.getEngineType(), null, jobClient.getTenantId());
        String pluginInfo = getPluginInfo(jobClient, dataSourceType);
        IJob iJob = ClientCache.getJob(dataSourceType);
        // 补充 rdb 输出参数
        List<ScheduleJobChainOutputParam> outputParams = findRdbChainOutputProcessParams(jobClient.getTaskType(), jobClient.getTaskId());
        JobParam jobParam = getJobParam(pluginInfo, jobClient, outputParams);
        return operatorStruct.toJobResult(iJob.submitJob(getISourceDTO(pluginInfo, dataSourceType, jobClient.getTenantId()), jobParam));
    }

    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier, Consumer<String> originStatusConsumer) {
        try {
            Integer dataSourceType = rdosWrapper.getDataSourceType(jobIdentifier.getPluginInfo(), jobIdentifier.getEngineType(), null, jobIdentifier.getTenantId());
            cachePluginInfo(jobIdentifier, (t) -> getPluginInfo(t, dataSourceType));
            IJob iJob = ClientCache.getJob(dataSourceType);
            JobParam jobParam = getJobParam(jobIdentifier.getPluginInfo(), jobIdentifier);
            String jobStatus = iJob.getJobStatus(getISourceDTO(jobIdentifier.getPluginInfo(), dataSourceType, jobIdentifier.getTenantId()), jobParam);
            Optional.ofNullable(originStatusConsumer).ifPresent(cs -> {
                cs.accept(jobStatus);
            });
            return adaptStatus(jobIdentifier.getTaskId(), jobStatus);
        } catch (Exception e) {
            LOGGER.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    public String getEngineLog(JobIdentifier jobIdentifier) {
        Integer dataSourceType = rdosWrapper.getDataSourceType(jobIdentifier.getPluginInfo(), jobIdentifier.getEngineType(), null, jobIdentifier.getTenantId());
        cachePluginInfo(jobIdentifier, (t) -> getPluginInfo(t, dataSourceType));
        IJob iJob = ClientCache.getJob(dataSourceType);
        JobParam jobParam = getJobParam(jobIdentifier.getPluginInfo(), jobIdentifier);
        return iJob.getJobLog(getISourceDTO(jobIdentifier.getPluginInfo(), dataSourceType, jobIdentifier.getTenantId()), jobParam);
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        Integer dataSourceType = rdosWrapper.getDataSourceType(jobClient.getPluginInfo(), jobClient.getEngineType(), null, jobClient.getTenantId());
        String pluginInfo = getPluginInfo(jobClient, dataSourceType);
        IJob iJob = ClientCache.getJob(dataSourceType);
        JobParam jobParam = getJobParam(pluginInfo, jobClient);
        jobParam.setSql(jobClient.getSql());
        return operatorStruct.toJobResult(iJob.cancelJob(getISourceDTO(pluginInfo, dataSourceType, jobClient.getTenantId()), jobParam, true));
    }

    @Override
    public ComponentTestResult testConnect(String engineType, String pluginInfo, Long clusterId, Long tenantId, String versionName) {
        EngineTypeComponentType engineTypeComponentType = EngineTypeComponentType.getByEngineName(engineType);
        if (engineTypeComponentType == null) {
            throw new RdosDefineException("please checking console:engineType:" + engineType + " not find");
        }
        Integer dataSourceType = rdosWrapper.getDataSourceType(engineType, clusterId, engineTypeComponentType.getComponentType(), versionName);
        JSONObject pluginInfoJson = JSONObject.parseObject(pluginInfo);
        pluginInfoJson.put(ConfigConstant.DATASOURCE_TYPE, dataSourceType);

        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfoJson.toJSONString(), tenantId);
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            this.mockHiveSetSqlIfNeed(engineType, sourceDTO, pluginInfoJson);
            Boolean result = client.testCon(sourceDTO);
            componentTestResult.setResult(result);
            if (EngineTypeComponentType.YARN.equals(engineTypeComponentType)) {
                IYarn yarn = ClientCache.getYarn(sourceDTO.getSourceType());
                YarnResourceDescriptionDTO yarnResourceDescription = yarn.getYarnResourceDescription(sourceDTO);
                componentTestResult.setClusterResourceDescription(plugInStruct.yarnResourceDescriptionDTOtoComponentTestResult(yarnResourceDescription));
            }
        } catch (Exception e) {
            componentTestResult.setResult(Boolean.FALSE);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return componentTestResult;
    }

    /**
     * 测试是否支持 hive 的 set 语句
     *
     * @param engineType
     * @param sourceDTO
     * @throws Exception
     */
    private void mockHiveSetSqlIfNeed(String engineType, ISourceDTO sourceDTO, JSONObject pluginInfoJson) throws Exception {
        if (EngineTypeComponentType.HIVE != EngineTypeComponentType.getByEngineName(engineType)) {
            return;
        }
        // 默认校验
        boolean shouldMock = true;
        if (pluginInfoJson != null && pluginInfoJson.getJSONObject(PluginInfoConst.SELF_CONF) != null) {
            JSONObject selfConf = pluginInfoJson.getJSONObject(PluginInfoConst.SELF_CONF);
            boolean skipCheck = selfConf.containsKey(GlobalConst.HIVE_SET_CHECK_ENABLE) && BooleanUtils.isFalse(selfConf.getBoolean(GlobalConst.HIVE_SET_CHECK_ENABLE));
            if (skipCheck) {
                // 当配置了 「hive.set.check.enable=false」时，跳过校验
                shouldMock = false;
            }
        }
        if (!shouldMock) {
            return;
        }
        Method setQueueMethod = ReflectionUtils.findMethod(sourceDTO.getClass(), "setQueue", String.class);
        if (setQueueMethod != null) {
            setQueueMethod.invoke(sourceDTO, "mockQueue");
        }

        Method setJobNameMethod = ReflectionUtils.findMethod(RdbmsSourceDTO.class, "setJobName", String.class);
        if (setJobNameMethod != null) {
            setJobNameMethod.invoke(sourceDTO, "mockJobName");
        }
    }

    @Override
    public List<YarnApplicationInfoDTO> listApplicationByTag(JobClient jobClient, String tag) {
        return listApplicationByTag(jobClient.getTenantId(), tag);
    }

    @Override
    public JobExecInfo getJobExecInfo(JobIdentifier jobIdentifier, boolean removeResult, boolean removeOutput) {

        Integer dataSourceType = rdosWrapper.getDataSourceType(jobIdentifier.getPluginInfo(), jobIdentifier.getEngineType(), null, jobIdentifier.getTenantId());
        cachePluginInfo(jobIdentifier, (t) -> getPluginInfo(t, dataSourceType));
        IJob iJob = ClientCache.getJob(dataSourceType);
        JobParam jobParam = getJobParam(jobIdentifier.getPluginInfo(), jobIdentifier, removeResult, removeOutput);
        return iJob.getJobExecInfo(getISourceDTO(jobIdentifier.getPluginInfo(), dataSourceType, jobIdentifier.getTenantId()), jobParam);
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        List<YarnApplicationInfoDTO> yarnApplicationInfoDTOS = listApplicationByTag(jobIdentifier.getTenantId(), jobIdentifier.getTaskId())
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(app -> app.getStartTime().getTime()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(yarnApplicationInfoDTOS)) {
            return null;
        }
        // 返回开始时间最晚的 app log base info
        return yarnApplicationInfoDTOS.get(yarnApplicationInfoDTOS.size() - 1).getRollingLogBaseInfo();
    }

    public List<YarnApplicationInfoDTO> listApplicationByTag(Long tenantId, String tag) {
        Cluster cluster = clusterService.getCluster(tenantId);
        JSONObject yarnInfo = clusterService.getYarnInfo(cluster.getId(), tenantId, null);
        String typeName = yarnInfo.getString(ConfigConstant.TYPE_NAME_KEY);
        Integer dataSourceType = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.YARN);
        IYarn yarnClient = ClientCache.getYarn(dataSourceType);
        return yarnClient.listApplicationByTag(getISourceDTO(yarnInfo.toJSONString(), dataSourceType, tenantId), tag);
    }

    /**
     * 构建 DatasourceX 需要的 sourceDTO
     *
     * @param pluginInfo     pluginInfo
     * @param dataSourceType 对应 DatasourceX数据源类型
     * @param tenantId       租户 id
     * @return sourceDTO
     */
    private ISourceDTO getISourceDTO(String pluginInfo, Integer dataSourceType, Long tenantId) {
        if (StringUtils.isBlank(pluginInfo)) {
            throw new RdosDefineException("pluginInfo can't be empty");
        }
        JSONObject jsonObject = JSONObject.parseObject(pluginInfo);
        jsonObject.put(ConfigConstant.DATASOURCE_TYPE, dataSourceType);
        return PluginInfoToSourceDTO.getSourceDTO(jsonObject.toJSONString(), tenantId);
    }

    /**
     * DatasourceX 状态适配
     *
     * @param jobId     任务 id
     * @param statusStr dataSourceX 的任务状态
     * @return engine status
     */
    public static RdosTaskStatus adaptStatus(String jobId, String statusStr) {
        // 重复状态适配
        switch (statusStr) {
            case "NEW":
            case "PENDING":
            case "RUNNING":
            case "FINISHED_WAIT_FLUSH":
                return RdosTaskStatus.RUNNING;
            case "STOPPED":
            case "CANCELLED":
            case "CANCELED":
                return RdosTaskStatus.CANCELED;
            case "CANCELLING":
                return RdosTaskStatus.CANCELLING;
            case "FINISHED":
                return RdosTaskStatus.FINISHED;
            case "ERROR":
            case "FAILED":
                return RdosTaskStatus.FAILED;
            case "WAITCOMPUTE":
                return RdosTaskStatus.WAITCOMPUTE;
            case "SUSPENDED":
            case "DISCARDED":
            case "NOTFOUND":
                return RdosTaskStatus.NOTFOUND;
        }
        RdosTaskStatus reParseStatus = RdosTaskStatus.getTaskStatus(statusStr);
        if (null == reParseStatus) {
            LOGGER.warn("DatasourceX return status can't adapt, jobId: {}, status: {}", jobId, statusStr);
        }
        return null == reParseStatus ? RdosTaskStatus.NOTFOUND : reParseStatus;
    }

    /**
     * 获取 pluginInfo 信息
     *
     * @param jobIdentifier  任务配置信息
     * @param dataSourceType 数据源类型
     * @return pluginInfo 信息
     */
    private String getPluginInfo(JobIdentifier jobIdentifier, Integer dataSourceType) {
        return getPluginInfo(jobIdentifier.getTaskId(), jobIdentifier.getPluginInfo(), dataSourceType,
                jobIdentifier.getProjectId(), jobIdentifier.getAppType(), jobIdentifier.getTaskType(),
                jobIdentifier.getTenantId(), jobIdentifier.getEngineType(), jobIdentifier.getUserId(),
                jobIdentifier.getDeployMode(), jobIdentifier.getResourceId());
    }

    /**
     * 获取 pluginInfo 信息
     *
     * @param jobClient      任务配置信息
     * @param dataSourceType 数据源类型
     * @return pluginInfo 信息
     */
    private String getPluginInfo(JobClient jobClient, Integer dataSourceType) {
        return getPluginInfo(jobClient.getTaskId(), jobClient.getPluginInfo(), dataSourceType,
                jobClient.getProjectId(), jobClient.getAppType(), jobClient.getTaskType(),
                jobClient.getTenantId(), jobClient.getEngineType(), jobClient.getUserId(),
                jobClient.getDeployMode(), jobClient.getResourceId());
    }

    /**
     * 获取 pluginInfo 信息
     *
     * @param pluginInfo     任务本身的 pluginInfo
     * @param dataSourceType 数据源类型
     * @param projectId      项目 id
     * @param appType        应用类型
     * @param taskType       任务类型
     * @param dtUicTenantId  租户 id
     * @param engineType     引擎类型
     * @param dtUicUserId    用户类型
     * @param deployMode     任务提交模式
     * @param resourceId     资源队列 id
     * @return pluginInfo 信息
     */
    private String getPluginInfo(String jobId,
                                 String pluginInfo,
                                 Integer dataSourceType,
                                 Long projectId,
                                 Integer appType,
                                 Integer taskType,
                                 Long dtUicTenantId,
                                 String engineType,
                                 Long dtUicUserId,
                                 Integer deployMode,
                                 Long resourceId) {
        try {
            // jobClient中如果有pluginInfo(数据质量)以jobClient自带优先
            JSONObject info = JSONObject.parseObject(pluginInfo);
            if (MapUtils.isEmpty(info)) {
                // 重新构建, rdb sql 不支持多版本共存, componentVersionMap 置为 null
                info = pluginInfoManager.buildTaskPluginInfo(projectId, appType, taskType,
                        dtUicTenantId, engineType, dtUicUserId, deployMode, resourceId, null);
            }
            info.put(ConfigConstant.DATASOURCE_TYPE, dataSourceType);
            return info.toJSONString();

        } catch (Exception e) {
            LOGGER.error("jobId: {} buildPluginInfo failed!", jobId, e);
            throw new RdosDefineException("buildPluginInfo error", e);
        }
    }

    /**
     * 构造任务执行相关参数
     *
     * @param pluginInfo pluginInfo 信息
     * @param jobClient  任务配置信息
     * @return 构造好的 jobParam
     */
    private JobParam getJobParam(String pluginInfo, JobClient jobClient) {
        return getJobParam(pluginInfo, jobClient.getJobName(), jobClient.getTaskId(), jobClient.getSql(), jobClient.getTaskParams(), jobClient.getFsyncSqlParamInfo(), false,
                null, false);
    }

    private JobParam getJobParam(String pluginInfo, JobClient jobClient, List<ScheduleJobChainOutputParam> outputParams) {
        return getJobParam(pluginInfo, jobClient.getJobName(), jobClient.getTaskId(), jobClient.getSql(), jobClient.getTaskParams(), jobClient.getFsyncSqlParamInfo(), false,
                outputParams, false);
    }

    /**
     * 构造任务执行相关参数
     *
     * @param pluginInfo pluginInfo 信息
     * @param jobClient  任务配置信息
     * @return 构造好的 jobParam
     */
    private JobParam getJobParam(String pluginInfo, JobClient jobClient, boolean removeResult) {
        return getJobParam(pluginInfo, jobClient.getJobName(), jobClient.getTaskId(), jobClient.getSql(), jobClient.getTaskParams(), jobClient.getFsyncSqlParamInfo(), removeResult,
                null, false);
    }

    /**
     * 构造任务执行相关参数
     *
     * @param pluginInfo    pluginInfo 信息
     * @param jobIdentifier 任务信息
     * @return 构造好的 jobParam
     */
    private JobParam getJobParam(String pluginInfo, JobIdentifier jobIdentifier, boolean removeResult, boolean removeOutput) {
        return getJobParam(pluginInfo, null, jobIdentifier.getTaskId(), null, null, null, removeResult,
                null, removeOutput);
    }

    /**
     * 构造任务执行相关参数
     *
     * @param pluginInfo    pluginInfo 信息
     * @param jobIdentifier 任务信息
     * @return 构造好的 jobParam
     */
    private JobParam getJobParam(String pluginInfo, JobIdentifier jobIdentifier) {
        return getJobParam(pluginInfo, null, jobIdentifier.getTaskId(), null, null, null, false, null, false);
    }

    /**
     * 构造任务执行相关参数
     *
     * @param pluginInfo   pluginInfo 信息
     * @param jobName      任务名称
     * @param jobId        任务 id
     * @param sql          执行的 sql 信息
     * @param taskParams   任务环境参参数
     * @param outputParams 任务运行输出参数
     * @return 构造好的 jobParam
     */
    private JobParam getJobParam(String pluginInfo, String jobName, String jobId, String sql, String taskParams, FsyncSqlParamInfo fsyncSqlParamInfo, boolean removeResult,
                                 List<ScheduleJobChainOutputParam> outputParams, boolean removeOutput) {
        JSONObject pluginInfoObject = JSONObject.parseObject(pluginInfo, Feature.OrderedField);
        JSONObject selfConf = pluginInfoObject.getJSONObject(PluginInfoConst.SELF_CONF);
        if (MapUtils.isEmpty(selfConf)) {
            throw new RdosDefineException(String.format("get job param error, self conf is empty, jobId: %s", jobId));
        }
        Integer minJobPoolSize = selfConf.getInteger(GlobalConst.MIN_JOB_POOL_SIZE);
        Integer maxJobPoolSize = selfConf.getInteger(GlobalConst.MAX_JOB_POOL_SIZE);
        String componentSign = ComponentUtil.getComponentKeyNotNull(pluginInfo);

        List<SingleSqlInfo> sqlInfoList = Lists.newArrayList();
        // 临时查询最大行数
        Integer maxRow = null;
        if (fsyncSqlParamInfo != null && StringUtils.isNotBlank(fsyncSqlParamInfo.getFsyncSql())) {
            List<FsyncSql> fsyncSqlList = JSONArray.parseArray(fsyncSqlParamInfo.getFsyncSql(), FsyncSql.class);
            fsyncSqlList.forEach(sqlInfo -> {
                SingleSqlInfo singleSqlInfo = SingleSqlInfo.builder()
                        .sql(sqlInfo.getSql())
                        .sqlId(sqlInfo.getSqlId())
                        .sqlType(sqlInfo.getSqlType())
                        .needResult(FsyncUtil.containSelect(sqlInfo)).build();
                sqlInfoList.add(singleSqlInfo);
            });
            maxRow = Objects.isNull(fsyncSqlParamInfo.getMaxNum()) ? tempJobMaxPreviewNum : fsyncSqlParamInfo.getMaxNum();
        }
        List<SingleSqlInfo> outputSqlList = null;
        if (CollectionUtils.isNotEmpty(outputParams)) {
            outputSqlList = outputParams.stream().map(p -> {
                SingleSqlInfo sqlInfo = SingleSqlInfo.builder()
                        .sql(p.getParsedParamCommand())
                        .sqlId(p.getParamName())
                        .needResult(true)
                        .build();
                return sqlInfo;
            }).collect(Collectors.toList());
        }

        return JobParam.builder()
                .jobName(jobName)
                .jobId(jobId)
                .componentSign(componentSign)
                .sql(sql)
                .sqlInfoList(sqlInfoList)
                .minJobPoolSize(minJobPoolSize)
                .maxJobPoolSize(maxJobPoolSize)
                .taskParams(taskParams)
                .removeResult(removeResult)
                .outputSqlList(outputSqlList)
                .removeOutput(removeOutput)
                .maxRow(maxRow)
                .build();
    }

    /**
     * 获取 rdb 任务运行输出参数
     *
     * @param taskType 任务类型
     * @param jobId
     * @return
     */
    private List<ScheduleJobChainOutputParam> findRdbChainOutputProcessParams(Integer taskType, String jobId) {
        // 只处理 rdb 任务类型
        if (!JobChainParamHandler.supportRdbTaskTypes().contains(taskType)) {
            return Collections.emptyList();
        }
        // 获取需要运行 sql 的输出参数
        List<ScheduleJobChainOutputParam> outputParams = scheduleJobChainOutputParamDao.listByOutputParamType(jobId, EOutputParamType.PROCESSED.getType())
                .stream()
                .filter(p -> org.apache.commons.lang3.StringUtils.isNotEmpty(p.getParsedParamCommand()))
                .collect(Collectors.toList());
        return outputParams;
    }

    /**
     * rdb 任务状态，来自 com.dtstack.dtcenter.common.loader.rdbms.job.RdbSqJob.STATUS
     */
    public enum RDB_STATUS {
        RUNNING("RUNNING"),
        FINISHED("FINISHED"),
        // 任务执行完成，但 dagschedulex 未落盘完成
        FINISHED_WAIT_FLUSH("FINISHED_WAIT_FLUSH"),
        WAITCOMPUTE("WAITCOMPUTE"),
        CANCELED("CANCELED"), FAILED("FAILED"),
        NOTFOUND("NOTFOUND"),
        ;
        private String status;

        RDB_STATUS(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    private void cachePluginInfo(JobIdentifier jobIdentifier, Function<JobIdentifier, String> fillPluginInfo) {
        if (null == jobIdentifier.getPluginInfo() || StringUtils.isBlank(jobIdentifier.getPluginInfo())) {
            String pluginInfo = fillPluginInfo.apply(jobIdentifier);
            jobIdentifier.setPluginInfo(pluginInfo);
        }
    }
}