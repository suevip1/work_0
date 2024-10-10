package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.FileFindDTO;
import com.dtstack.dtcenter.loader.dto.FileFindResultDTO;
import com.dtstack.dtcenter.loader.dto.FileStatus;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.CheckpointDetail;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.enums.CheckpointType;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CheckpointQueryParam;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.FlinkTableResult;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.stream.EngineStreamJobVO;
import com.dtstack.engine.api.vo.stream.FlinkQueryResultVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.bean.FlinkWebUrlResultDTO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleJobHistoryDao;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.po.ScheduleJobHistory;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reason: 查询实时任务数据
 * Date: 2018/10/11
 * Company: www.dtstack.com
 * @author jiangbo
 */
@Service
public class StreamTaskService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTaskService.class);

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobHistoryDao jobHistoryDao;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleJobHistoryService jobHistoryService;

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * 查询 jobId 对应的 appId 最大数量限制
     */
    private final static Integer APP_ID_LIMIT = 100;

    /**
     * 运行结束的 checkpoint 缓存, key 为 job_id_applicationId
     */
    public Cache<String, Optional<List<CheckpointDetail>>> stoppedCheckpointCache;

    /**
     * 运行结束的 archive history 缓存, key 为 job_id_applicationId
     */
    private Cache<String, Optional<JSONArray>> stoppedArchiveHistoryCache;



    @Override
    public void afterPropertiesSet() throws Exception {
        stoppedCheckpointCache = CacheBuilder.newBuilder()
                .maximumSize(environmentContext.stoppedCheckpointCacheSize())
                .expireAfterWrite(environmentContext.stoppedCheckpointCacheExpireTime(), TimeUnit.SECONDS).build();

        stoppedArchiveHistoryCache = CacheBuilder.newBuilder()
                        .maximumSize(environmentContext.stoppedCheckpointCacheSize()).build();
    }

    /**
     * 查询 生成失败的 checkPoint
     */
    public List<EngineJobCheckpoint> getFailedCheckPoint(String taskId, Long triggerStart, Long triggerEnd, Integer size){
        List<EngineJobCheckpoint> failedCheckPointList = engineJobCheckpointDao.listFailedByTaskIdAndRangeTime(taskId, triggerStart, triggerEnd, size);
        if(CollectionUtils.isNotEmpty(failedCheckPointList)) {
            engineJobCheckpointDao.updateFailedCheckpoint(failedCheckPointList);
        }
        return failedCheckPointList;
    }

    /**
     * 查询checkPoint
     */
    public List<EngineJobCheckpoint> getCheckPoint( String taskId, Long triggerStart, Long triggerEnd){
        return engineJobCheckpointDao.listByTaskIdAndRangeTime(taskId, triggerStart, triggerEnd);
    }

    /**
     * 查询checkPoint
     */
    public EngineJobCheckpoint getSavePoint( String taskId){
        return engineJobCheckpointDao.findLatestSavepointByTaskId(taskId);
    }

    public EngineJobCheckpoint getByTaskIdAndEngineTaskId( String taskId,  String engineTaskId){
        return engineJobCheckpointDao.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    /**
     * 查询stream job
     */
    public List<ScheduleJob> getEngineStreamJob( List<String> taskIds){

        if(CollectionUtils.isEmpty(taskIds)){
            return Collections.EMPTY_LIST;
        }
        List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(taskIds);

        if (CollectionUtils.isNotEmpty(jobs)){
            List<ScheduleJobExpand> logByJobIds = scheduleJobExpandDao.getLogByJobIds(taskIds);
            Map<String, List<ScheduleJobExpand>> jobExpandMap = logByJobIds.stream().collect(Collectors.groupingBy(ScheduleJobExpand::getJobId));
            for (ScheduleJob scheduleJob : jobs) {
                scheduleJob.setStatus(RdosTaskStatus.getShowStatus(scheduleJob.getStatus()));
                List<ScheduleJobExpand> scheduleJobExpandList = jobExpandMap.get(scheduleJob.getJobId());
                if (CollectionUtils.isNotEmpty(scheduleJobExpandList)) {
                    ScheduleJobExpand scheduleJobExpand = scheduleJobExpandList.stream().max(Comparator.comparing(ScheduleJobExpand::getRunNum)).orElse(null);
                    if (scheduleJobExpand != null) {
                        scheduleJob.setLogInfo(scheduleJobExpand.getLogInfo());
                        scheduleJob.setEngineLog(scheduleJobExpand.getEngineLog());
                    }
                }
            }
        }
        return jobs;
    }

    /**
     * 查询stream job
     */
    public List<EngineStreamJobVO> getEngineStreamJobNew(List<String> taskIds) {
        if(CollectionUtils.isEmpty(taskIds)){
            return Lists.newArrayList();
        }

        List<ScheduleJob> jobs = scheduleJobDao.getRdosJobByJobIds(taskIds);
        List<EngineStreamJobVO> vos = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(jobs)){
            List<ScheduleJobExpand> logByJobIds = scheduleJobExpandDao.getLogByJobIds(taskIds);
            Map<String, List<ScheduleJobExpand>> jobExpandMap = logByJobIds.stream().collect(Collectors.groupingBy(ScheduleJobExpand::getJobId));
            for (ScheduleJob scheduleJob : jobs) {
                List<ScheduleJobExpand> scheduleJobExpandList = jobExpandMap.get(scheduleJob.getJobId());
                EngineStreamJobVO vo = new EngineStreamJobVO();
                vo.setApplicationId(scheduleJob.getApplicationId());
                vo.setEngineJobId(scheduleJob.getEngineJobId());
                vo.setExecEndTime(scheduleJob.getExecEndTime());
                vo.setExecStartTime(scheduleJob.getExecStartTime());
                vo.setJobId(scheduleJob.getJobId());
                vo.setStatus(RdosTaskStatus.getShowStatus(scheduleJob.getStatus()));
                vo.setTaskId(scheduleJob.getTaskId());
                if (CollectionUtils.isNotEmpty(scheduleJobExpandList)) {
                    ScheduleJobExpand scheduleJobExpand = scheduleJobExpandList.stream().max(Comparator.comparing(ScheduleJobExpand::getRunNum)).orElse(null);
                    if (scheduleJobExpand != null) {
                        vo.setLogInfo(scheduleJobExpand.getLogInfo());
                        vo.setEngineLog(scheduleJobExpand.getEngineLog());
                    }
                }
                vos.add(vo);
            }
        }
        return vos;
    }

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus( Integer status){
        return scheduleJobDao.getJobIdsByStatus(status, ComputeType.STREAM.getType());
    }

    /**
     * 获取任务的状态
     */
    public Integer getTaskStatus(String taskId) {
        Integer status = null;
        if (StringUtils.isNotEmpty(taskId)) {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
            if (scheduleJob != null) {
                status = scheduleJob.getStatus();
                return RdosTaskStatus.getShowStatus(status);
            }
        }

        return null;
    }

    /**
     * 获取实时计算运行中任务的日志URL
     * @param taskId
     * @return
     */
    public List<String> getRunningTaskLogUrl( String taskId) {

        Preconditions.checkState(StringUtils.isNotEmpty(taskId), "taskId can't be empty");

        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
        Preconditions.checkNotNull(scheduleJob, "can't find record by taskId" + taskId);

        //只获取运行中的任务的log—url
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            throw new RdosDefineException(String.format("job:%s not running status now status is %s", taskId,status), ErrorCode.INVALID_TASK_STATUS);
        }

        String applicationId = scheduleJob.getApplicationId();

        if (StringUtils.isEmpty(applicationId)) {
            throw new RdosDefineException(String.format("job %s not running applicationId is empty", taskId), ErrorCode.INVALID_TASK_RUN_MODE);
        }

        JobClient jobClient = null;
        JobIdentifier jobIdentifier = null;

        //如何获取url前缀
        try{
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(taskId);
            if (engineJobCache == null) {
                throw new RdosDefineException(String.format("job:%s not exist in job cache table ", taskId),ErrorCode.JOB_CACHE_NOT_EXIST);
            }
            String jobInfo = engineJobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), applicationId, taskId, scheduleJob.getDtuicTenantId(), engineJobCache.getEngineType(),
                    EDeployMode.PERJOB.getType(), paramAction.getUserId(), paramAction.getResourceId(), null, paramAction.getComponentVersion());
            jobIdentifier.setProjectId(paramAction.getProjectId());
            jobIdentifier.setAppType(paramAction.getAppType());
            jobIdentifier.setTaskType(paramAction.getTaskType());
            jobIdentifier.setClassArgs(paramAction.getExeArgs());
            jobClient = JobClientUtil.conversionJobClient(paramAction);
            return enginePluginsOperator.getRollingLogBaseInfo(jobIdentifier);

        }catch (Exception e){
            if (e instanceof RdosDefineException) {
                throw (RdosDefineException) e;
            } else {
                if (jobClient != null) {
                    jobIdentifier.setArchiveFsDir(scheduleJobService.getJobExtraInfoOfValue(jobIdentifier.getTaskId(), JobResultConstant.ARCHIVE));
                    RdosTaskStatus jobStatus = enginePluginsOperator.getJobStatus(jobIdentifier, null);
                    Integer statusCode = jobStatus.getStatus();
                    if (RdosTaskStatus.getStoppedStatus().contains(statusCode)) {
                        throw new RdosDefineException(String.format("job:%s had stop ", taskId), ErrorCode.INVALID_TASK_STATUS, e);
                    }
                }
                throw new RdosDefineException(String.format("get job:%s ref application url error..", taskId), ErrorCode.UNKNOWN_ERROR, e);
            }

        }

    }

    public CheckResult grammarCheck(ParamActionExt paramActionExt) {
        LOGGER.info("grammarCheck actionParam: {}", JSONObject.toJSONString(paramActionExt));
        CheckResult checkResult = null;
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            checkResult = enginePluginsOperator.grammarCheck(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return checkResult;
    }

    public Long getTotalSize(String jobId) {
        return engineJobCheckpointDao.getTotalSize(jobId);
    }


    public CheckResult executeSql(ParamActionExt paramActionExt) throws Exception {
        LOGGER.info("executeSql actionParam: {}", JSONObject.toJSONString(paramActionExt));
        CheckResult checkResult = null;
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            checkResult = enginePluginsOperator.executeSql(jobClient);
        } catch (Exception e) {
            checkResult = CheckResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return checkResult;
    }

    public List<FlinkTableResult> executeFlinkSQL(ParamActionExt paramActionExt) throws Exception {
        LOGGER.info("executeSql actionParam: {}", JSONObject.toJSONString(paramActionExt));
        List<FlinkTableResult> flinkTableResultList = null;
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            flinkTableResultList = enginePluginsOperator.executeFlinkSQL(jobClient);
        } catch (Exception e) {
            flinkTableResultList = FlinkTableResult.exception(ExceptionUtil.getErrorMessage(e));
        }
        return flinkTableResultList;
    }

    /**
     * 根据条件查询 checkpoint, 限制最大查询近 100 次
     *
     * @param checkpointQueryParam checkpoint 查询条件
     * @return checkpoint 查询结果
     */
    public PageResult<List<CheckpointDetail>> queryCheckPoint(CheckpointQueryParam checkpointQueryParam) {
        if (StringUtils.isBlank(checkpointQueryParam.getJobId())) {
            throw new DtCenterDefException("jobId can't be empty.");
        }
        if (StringUtils.isBlank(checkpointQueryParam.getComponentVersion())) {
            throw new DtCenterDefException("componentVersion can't be empty.");
        }
        // key -> engineJobId (热更新appId不变 appId不唯一)
        Map<String, List<CheckpointDetail>> resultMap = Maps.newHashMap();

        // 获取所有的运行记录
        String jobId = checkpointQueryParam.getJobId();
        Integer appIdLimit = Objects.isNull(checkpointQueryParam.getAppIdLimit()) ? APP_ID_LIMIT : checkpointQueryParam.getAppIdLimit();
        // 倒序排列 job 执行历史
        List<ScheduleJobHistory> scheduleJobHistories = jobHistoryDao.listByJobId(jobId, appIdLimit);
        // 过滤查询条件
        if (CollectionUtils.isNotEmpty(checkpointQueryParam.getApplicationIdList())) {
            scheduleJobHistories = scheduleJobHistories
                    .stream()
                    .filter(scheduleJobHistory -> checkpointQueryParam.getApplicationIdList().contains(scheduleJobHistory.getApplicationId()))
                    .collect(Collectors.toList());
        }
        // 判断任务状态
        ScheduleJob scheduleJob = scheduleJobService.getSimpleByJobId(checkpointQueryParam.getJobId(), IsDeletedEnum.NOT_DELETE.getType());
        if (Objects.isNull(scheduleJob)) {
            // 实例不存在直接返回
            return PageResult.EMPTY_PAGE_RESULT;
        }

        // 构建组件版本
        String componentVersion = scheduleDictService.convertVersionNameToValue(checkpointQueryParam.getComponentVersion(), ScheduleEngineType.Flink.getEngineName());
        Map<Integer, String> componentVersionMap = StringUtils.isBlank(componentVersion) ? null : Collections.singletonMap(EComponentType.FLINK.getTypeCode(), componentVersion);

        // flink 组件配置
        String flinkConf = clusterService.getConfigByKey(scheduleJob.getDtuicTenantId(), EComponentType.FLINK.getConfName(), false, componentVersionMap, false);
        if (StringUtils.isBlank(flinkConf)) {
            throw new DtCenterDefException("当前集群缺少 flink 配置");
        }
        JSONObject perJobJson = JSONObject.parseObject(flinkConf).getJSONObject(EDeployMode.PERJOB.getMode());
        if (MapUtils.isEmpty(perJobJson)) {
            throw new DtCenterDefException("flink 缺少 perjob 模式配置");
        }

        String archiveDir = scheduleJobService.getJobExtraInfoOfValue(scheduleJob.getJobId(), JobResultConstant.ARCHIVE);
        // 为空则从 flink 配置中获取
        if (StringUtils.isBlank(archiveDir)) {
            archiveDir = perJobJson.getString(ConfigConstant.ARCHIVE_FS_DIR);
        }

        String checkpointsDir = perJobJson.getString(ConfigConstant.CHECK_POINTS_DIR);
        String savePointsDir = perJobJson.getString(ConfigConstant.SAVE_POINTS_DIR);

        if (CollectionUtils.isEmpty(scheduleJobHistories)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        // 构建 hdfs pluginInfo
        JSONObject hdfsPluginInfo = pluginInfoManager.buildTaskPluginInfo(
                scheduleJob.getProjectId(), scheduleJob.getAppType(),
                scheduleJob.getTaskType(), scheduleJob.getDtuicTenantId(),
                ScheduleEngineType.Hadoop.getEngineName(), scheduleJob.getCreateUserId(),
                null, null, null);

        // running 的 appId
        String runningEngineJobId = RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus()) ?
                scheduleJobHistories.get(0).getEngineJobId() : null;

        List<Integer> checkpointTypeList = checkpointQueryParam.getCheckpointTypeList();
        String finalArchiveDir = archiveDir;
        Map<String, JSONArray> archiveJsonMap = scheduleJobHistories.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getEngineJobId(),
                        getArchiveJson(checkpointQueryParam, jobId, scheduleJob, finalArchiveDir, hdfsPluginInfo, runningEngineJobId, v)), HashMap::putAll);

        // 处理 cp
        if (CollectionUtils.isEmpty(checkpointTypeList) || checkpointTypeList.contains(CheckpointType.CHECKPOINT.getType())) {
            handleFsCheckpoint(hdfsPluginInfo, scheduleJob.getDtuicTenantId(), resultMap, CheckpointType.CHECKPOINT, scheduleJobHistories, scheduleJob, checkpointsDir, runningEngineJobId, archiveJsonMap);
        }
        // 处理 sp
        if (CollectionUtils.isEmpty(checkpointTypeList) || checkpointTypeList.contains(CheckpointType.SAVEPOINT.getType())) {
            handleFsCheckpoint(hdfsPluginInfo, scheduleJob.getDtuicTenantId(), resultMap, CheckpointType.SAVEPOINT, scheduleJobHistories, scheduleJob, savePointsDir, runningEngineJobId, archiveJsonMap);
        }

        // 读取 archive 路径文件 存储和耗时 archive log 优先 hdfs
        for (ScheduleJobHistory scheduleJobHistory : scheduleJobHistories) {
            List<CheckpointDetail> detailList = resultMap.get(scheduleJobHistory.getEngineJobId());
            if (CollectionUtils.isEmpty(detailList)) {
                continue;
            }
            Map<String, CheckpointDetail> detailMap = detailList
                    .stream()
                    .filter(h -> StringUtils.isNotBlank(h.getSavePath()))
                    .collect(Collectors.toMap(h -> getCheckPointName(h.getSavePath()), h -> h, (h1, h2) -> h2));

            if (MapUtils.isEmpty(detailMap)) {
                continue;
            }
            // 先读取缓存
            JSONArray history = archiveJsonMap.get(scheduleJobHistory.getEngineJobId());
            if (Objects.isNull(history) || history.isEmpty()) {
                continue;
            }

            for (int i = 0; i < history.size(); i++) {
                JSONObject detailJson = history.getJSONObject(i);
                String checkpointSavePath = detailJson.getString(ConfigConstant.CHECKPOINT_SAVEPATH_KEY);
                if (StringUtils.isBlank(checkpointSavePath)) {
                    continue;
                }
                String checkPointName = getCheckPointName(checkpointSavePath);
                CheckpointDetail detail = detailMap.get(checkPointName);
                if (Objects.isNull(detail)) {
                    continue;
                }
                String status = detailJson.getString(ConfigConstant.CHECKPOINT_STATUS_KEY);
                if (!StringUtils.equals(status, ConfigConstant.CHECKPOINT_COMPLETED_STATUS)) {
                    // checkpoint 失败不统计
                    continue;
                }
                detail.setCheckpointId(MapUtils.getString(detailJson, ConfigConstant.CHECKPOINT_ID_KEY));
                detail.setTriggerTime(MapUtils.getLong(detailJson, ConfigConstant.TRIGGER_TIMESTAMP_KEY));
                detail.setDuration(MapUtils.getLong(detailJson, ConfigConstant.END_TO_END_DURATION));
                detail.setStorageSize(MapUtils.getLong(detailJson, ConfigConstant.CHECKPOINT_STATE_SIZE));
            }
        }
        // 排序处理
        Stream<CheckpointDetail> checkpointDetailStream = resultMap.values().stream()
                .flatMap(Collection::stream)
                .filter(detail -> Objects.nonNull(detail.getTriggerTime()) && Objects.nonNull(detail.getApplicationId()) && Objects.nonNull(detail.getEngineJobId()));
        if (Objects.nonNull(checkpointQueryParam.getStartTime())) {
            checkpointDetailStream = checkpointDetailStream.filter(checkpointDetail -> checkpointDetail.getTriggerTime() >= checkpointQueryParam.getStartTime());
        }
        if (Objects.nonNull(checkpointQueryParam.getEndTime())) {
            checkpointDetailStream = checkpointDetailStream.filter(checkpointDetail -> checkpointDetail.getTriggerTime() <= checkpointQueryParam.getEndTime());
        }

        List<CheckpointDetail> allCheckpoint = checkpointDetailStream.sorted((o1, o2) -> {
            if (!StringUtils.equals(o1.getEngineJobId(), o2.getEngineJobId())) {
                // appId 倒序排列
                return o2.getEngineJobId().compareTo(o1.getEngineJobId());
            }
            if (!o1.getCheckpointType().equals(o2.getCheckpointType())) {
                // savepoint 在前
                return o2.getCheckpointType() - o1.getCheckpointType();
            }
            // 按照触发时间倒序排列
            return (int) (o2.getTriggerTime() - o1.getTriggerTime());
        }).collect(Collectors.toList());


        // 分页后的结果
        List<CheckpointDetail> result = allCheckpoint.stream()
                .skip((long) (checkpointQueryParam.getCurrentPage() - 1) * checkpointQueryParam.getPageSize())
                .limit(checkpointQueryParam.getPageSize()).collect(Collectors.toList());
        return new PageResult<>(checkpointQueryParam.getCurrentPage(), checkpointQueryParam.getPageSize(), allCheckpoint.size(), result);
    }

    private JSONArray getArchiveJson(CheckpointQueryParam checkpointQueryParam, String jobId, ScheduleJob scheduleJob, String archiveDir, JSONObject hdfsPluginInfo, String runningEngineJobId, ScheduleJobHistory scheduleJobHistory) {
        // 先读取缓存
        Optional<JSONArray> cacheHistoryOptional = stoppedArchiveHistoryCache.getIfPresent(getCheckpointCacheKey(scheduleJob.getJobId(), scheduleJobHistory.getEngineJobId()));

        JSONArray history;
        if (Objects.nonNull(cacheHistoryOptional)) {
            history = cacheHistoryOptional.orElse(null);
        } else {
            if (StringUtils.equals(runningEngineJobId, scheduleJobHistory.getEngineJobId())) {
                // 处理 running 状态
                JobIdentifier jobIdentifier = new JobIdentifier(scheduleJobHistory.getEngineJobId(), scheduleJobHistory.getApplicationId(), jobId, scheduleJob.getDtuicTenantId(), ScheduleEngineType.Flink.getEngineName(),
                        EDeployMode.PERJOB.getType(), scheduleJob.getCreateUserId(), scheduleJob.getResourceId(), null, checkpointQueryParam.getComponentVersion());
                String checkpointJsonStr = enginePluginsOperator.getCheckpoints(jobIdentifier);
                if (StringUtils.isNotBlank(checkpointJsonStr)) {
                    history = JSONObject.parseObject(checkpointJsonStr).getJSONArray("history");
                } else {
                    history = null;
                }
            } else {
                String jobArchivePath = archiveDir + ConfigConstant.SP + scheduleJobHistory.getEngineJobId();
                history = getCpHistoryFromFs(hdfsPluginInfo, scheduleJob.getDtuicTenantId(), jobArchivePath, scheduleJobHistory.getEngineJobId());
                if (null != history && !history.isEmpty()) {
                    // 添加缓存
                    stoppedArchiveHistoryCache.put(getCheckpointCacheKey(scheduleJob.getJobId(), scheduleJobHistory.getEngineJobId()), Optional.of(history));
                }
            }
        }
        return history;
    }

    /**
     * 根据 jobId 查询 checkpoint 总大小
     *
     * @param jobId            任务 id
     * @param componentVersion 组件版本
     * @return checkpoint 总大小
     * 请使用 com.dtstack.engine.master.impl.StreamTaskService#queryCheckpointTotalSize(com.dtstack.engine.api.param.CheckpointQueryParam)
     */
    @Deprecated
    public Long getCheckpointTotalSize(String jobId, String componentVersion) {
        CheckpointQueryParam checkpointQueryParam = new CheckpointQueryParam();
        checkpointQueryParam.setCurrentPage(1);
        checkpointQueryParam.setPageSize(Integer.MAX_VALUE);
        checkpointQueryParam.setJobId(jobId);
        checkpointQueryParam.setComponentVersion(componentVersion);
        List<CheckpointDetail> checkpointDetails = queryCheckPoint(checkpointQueryParam).getData();
        if (CollectionUtils.isEmpty(checkpointDetails)) {
            return 0L;
        }
        return checkpointDetails.stream()
                .filter(detail -> Objects.nonNull(detail.getStorageSize()))
                .mapToLong(CheckpointDetail::getStorageSize)
                .sum();
    }

    /**
     * 根据 jobId 查询 checkpoint 总大小
     *
     * @param checkpointQueryParam
     * @return
     */
    public Long queryCheckpointTotalSize(CheckpointQueryParam checkpointQueryParam) {
        CheckpointQueryParam checkpointQueryParamCopy = new CheckpointQueryParam();
        checkpointQueryParamCopy.setCurrentPage(1);
        checkpointQueryParamCopy.setPageSize(Integer.MAX_VALUE);
        checkpointQueryParamCopy.setJobId(checkpointQueryParam.getJobId());
        checkpointQueryParamCopy.setComponentVersion(checkpointQueryParam.getComponentVersion());
        checkpointQueryParamCopy.setTaskParams(checkpointQueryParam.getTaskParams());
        List<CheckpointDetail> checkpointDetails = queryCheckPoint(checkpointQueryParamCopy).getData();
        if (CollectionUtils.isEmpty(checkpointDetails)) {
            return 0L;
        }
        return checkpointDetails.stream()
                .filter(detail -> Objects.nonNull(detail.getStorageSize()))
                .mapToLong(CheckpointDetail::getStorageSize)
                .sum();
    }

    /**
     * 处理存储组件上的检查点
     *
     * @param pluginInfo           pluginInfo 信息
     * @param dtUicTenantId        租户 id
     * @param resultMap            结果集
     * @param checkpointType       检查点类型
     * @param scheduleJobHistories 任务历史
     * @param scheduleJob          任务实例
     * @param dirPath              检查点在存储组件上的目录地址
     * @param runningEngineJobId         正在运行的任务的 appId
     */
    private void handleFsCheckpoint(JSONObject pluginInfo, Long dtUicTenantId, Map<String, List<CheckpointDetail>> resultMap, CheckpointType checkpointType,
                                    List<ScheduleJobHistory> scheduleJobHistories, ScheduleJob scheduleJob, String dirPath, String runningEngineJobId,Map<String, JSONArray> archiveJsonMap) {

        // 需要查询 hdfs 的 ScheduleJobHistory
        Map<String, ScheduleJobHistory> notCacheJobHistoryMap = Maps.newHashMap();
        // 处理缓存
        for (ScheduleJobHistory scheduleJobHistory : scheduleJobHistories) {
            if (StringUtils.isBlank(scheduleJobHistory.getEngineJobId())) {
                continue;
            }
            String cacheKey = getCheckpointCacheKey(scheduleJob.getJobId(), scheduleJobHistory.getEngineJobId(),checkpointType);
            Optional<List<CheckpointDetail>> cacheDetails = stoppedCheckpointCache.getIfPresent(cacheKey);
            if (Objects.isNull(cacheDetails)) {
                notCacheJobHistoryMap.put(scheduleJobHistory.getEngineJobId(), scheduleJobHistory);
            } else {
                List<CheckpointDetail> detailList = cacheDetails.orElse(null);
                if (CollectionUtils.isNotEmpty(detailList)) {
                    List<CheckpointDetail> checkpointDetails = resultMap.get(scheduleJobHistory.getEngineJobId());

                    if (CollectionUtils.isEmpty(checkpointDetails)) {
                        checkpointDetails = Lists.newArrayList();
                    }

                    checkpointDetails.addAll(detailList);
                    resultMap.put(scheduleJobHistory.getEngineJobId(), checkpointDetails);
                }
            }
        }

        // 构建需要查询 fs 的基本条件信息
        List<FileFindDTO> cpFileFindDTOS = notCacheJobHistoryMap.values().stream().map(h -> FileFindDTO.builder()
                .isPathPattern(checkpointType.equals(CheckpointType.SAVEPOINT))
                .uniqueKey(h.getEngineJobId())
                .path(getCheckpointDir(dirPath, h.getEngineJobId(), checkpointType))
                .build()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cpFileFindDTOS)) {
            return;
        }
        IHdfsFile hdfsClient = fillPluginInfoAndGetHdfsClient(pluginInfo, dtUicTenantId);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtUicTenantId);
        List<FileFindResultDTO> fileFindResultDTOS = hdfsClient.listFiles(sourceDTO, cpFileFindDTOS);
        for (FileFindResultDTO fileFindResultDTO : fileFindResultDTOS) {
            String engineJobId = fileFindResultDTO.getUniqueKey();
            List<CheckpointDetail> detailListByAppId = Lists.newArrayList();
            ScheduleJobHistory jobHistory = notCacheJobHistoryMap.get(engineJobId);

            List<FileStatus> fileStatusList = fileFindResultDTO.getFileStatusList().stream()
                    .filter(file -> !file.getPath().endsWith("shared") && !file.getPath().endsWith("taskowned")).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(fileStatusList)) {
                continue;
            }
            for (int i = 0; i < fileStatusList.size(); i++) {
                FileStatus fileStatus = fileStatusList.get(i);
                if (i == fileStatusList.size() - 1) {
                    //最新的ck 得确认是否生成完成
                    List<HDFSContentSummary> contentSummary = hdfsClient.getContentSummary(sourceDTO, Lists.newArrayList(fileStatus.getPath()));
                    if (CollectionUtils.isEmpty(contentSummary) || contentSummary.get(0).getSpaceConsumed() <= 0) {
                        continue;
                    }
                }
                CheckpointDetail checkpointDetail = new CheckpointDetail();
                checkpointDetail.setApplicationId(jobHistory.getApplicationId());
                checkpointDetail.setEngineJobId(jobHistory.getEngineJobId());
                checkpointDetail.setJobId(scheduleJob.getJobId());
                checkpointDetail.setSavePath(fileStatus.getPath());
                checkpointDetail.setCheckpointType(checkpointType.getType());
                checkpointDetail.setTriggerTime(fileStatus.getModification_time());
                if (null == archiveJsonMap.get(engineJobId)) {
                    //kill 导致 archive 日志不存在 存储大小需要取hdfs
                    List<HDFSContentSummary> contentSummary = hdfsClient.getContentSummary(sourceDTO, Lists.newArrayList(fileStatus.getPath()));
                    checkpointDetail.setStorageSize(contentSummary.stream().map(HDFSContentSummary::getSpaceConsumed)
                            .reduce(Long::sum).orElse(0L));
                }
                detailListByAppId.add(checkpointDetail);
            }

            // 正在运行的任务不放入缓存
            if (!StringUtils.equals(runningEngineJobId, jobHistory.getEngineJobId())) {
                stoppedCheckpointCache.put(getCheckpointCacheKey(scheduleJob.getJobId(), jobHistory.getEngineJobId(),checkpointType), Optional.of(detailListByAppId));
            }
            List<CheckpointDetail> checkpointDetails = resultMap.get(jobHistory.getEngineJobId());

            if (CollectionUtils.isEmpty(checkpointDetails)) {
                checkpointDetails = Lists.newArrayList();
            }
            checkpointDetails.addAll(detailListByAppId);
            resultMap.put(jobHistory.getEngineJobId(), checkpointDetails);
        }
    }

    private String getCheckpointCacheKey(String jobId, String engineJobId,CheckpointType checkpointType) {
        return String.format("%s_%s_%s", jobId, engineJobId,checkpointType.name());
    }

    private String getCheckpointCacheKey(String jobId, String engineJobId) {
        return String.format("%s_%s", jobId, engineJobId);
    }

    /**
     * 获取 checkpoint 路径
     *
     * @param dirPath        文件夹路径
     * @param engineJobId    engineJobId
     * @param checkpointType checkpoint 类型
     * @return checkpoint 完整路径
     */
    public String getCheckpointDir(String dirPath, String engineJobId, CheckpointType checkpointType) {
        switch (checkpointType) {
            case CHECKPOINT:
                return dirPath + ConfigConstant.SP + engineJobId;
            case SAVEPOINT:
                return dirPath + ConfigConstant.SP + "savepoint-" + engineJobId.substring(0, 6) + "-*";
            default:
                throw new DtCenterDefException(String.format("not support checkpoint type: %s", checkpointType));
        }
    }

    /**
     * 从存储组件获取 checkpoint history
     *
     * @param pluginInfo     pluginInfo 信息
     * @param dtUicTenantId  租户 id
     * @param jobArchivePath archive 路径
     * @param engineJobId    引擎 job id
     * @return checkpoint history
     */
    public JSONArray getCpHistoryFromFs(JSONObject pluginInfo, Long dtUicTenantId, String jobArchivePath, String engineJobId) {
        if (MapUtils.isEmpty(pluginInfo)) {
            LOGGER.warn("pluginInfo is null, tenantId: {}, jobArchivePath: {}, engineJobId: {}", dtUicTenantId, jobArchivePath, engineJobId);
            return null;
        }
        String jobUrlPath =
                String.format(ConfigConstant.JOB_CHECKPOINTS_URL_FORMAT, engineJobId);
        IHdfsFile client = fillPluginInfoAndGetHdfsClient(pluginInfo, dtUicTenantId);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtUicTenantId);
        String archiveContent;
        try {
            //这里未检验hdfs文件是否存在 不打印日志
            archiveContent = client.getHdfsWithScript(sourceDTO, jobArchivePath);
        } catch (Exception ignoreException) {
            return null;
        }
        if (StringUtils.isBlank(archiveContent)) {
            return null;
        }
        JSONObject archiveJson = JSONObject.parseObject(archiveContent);
        JSONArray archiveArray = archiveJson.getJSONArray("archive");
        if (Objects.isNull(archiveArray) || archiveArray.isEmpty()) {
            return null;
        }
        for (int i = 0; i < archiveArray.size(); i++) {
            JSONObject archiveRow = archiveArray.getJSONObject(i);
            if (StringUtils.equals(archiveRow.getString("path"), jobUrlPath)) {
                String cpJson = archiveRow.getString("json");
                if (StringUtils.isNotBlank(cpJson)) {
                    return JSONObject.parseObject(cpJson).getJSONArray("history");
                }
            }
        }
        return null;
    }

    /**
     * 获取 checkpoint 名称
     *
     * @param path checkpoint 路径
     * @return checkpoint 名称
     */
    public String getCheckPointName(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String[] split = path.split(ConfigConstant.SP);
        return split[split.length - 1];
    }

    /**
     * 填充 pluginInfo 信息并获取 hdfs client
     *
     * @param pluginInfo    pluginInfo 信息
     * @param dtUicTenantId 租户 id
     * @return hdfs 客户端
     */
    public IHdfsFile fillPluginInfoAndGetHdfsClient(JSONObject pluginInfo, Long dtUicTenantId) {
        String typeName = componentService.buildHdfsTypeName(dtUicTenantId, null);
        Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, typeName);
        pluginInfo.put("dataSourceType", dataSourceCodeByDiceName);
        return ClientCache.getHdfs(dataSourceCodeByDiceName);
    }

    /**
     * 获取 flink select 查询结果
     * @param jobId
     * @return
     */
    public FlinkQueryResultVO queryJobData(String jobId) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            return FlinkQueryResultVO.error(String.format("job:%s is not running status", jobId));
        }
        String engineJobId = scheduleJob.getEngineJobId();
        if (StringUtils.isEmpty(engineJobId)) {
            throw new RdosDefineException(String.format("job:%s engineJobId is empty", jobId));
        }
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            throw new RdosDefineException(String.format("job:%s not exist in job cache table", jobId), ErrorCode.JOB_CACHE_NOT_EXIST);
        }

        String engineType = engineJobCache.getEngineType();

        ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
        Map<String, Object> pluginInfo = paramAction.getPluginInfo();
        JobIdentifier jobIdentifier = new JobIdentifier(engineJobId, scheduleJob.getApplicationId(), jobId, scheduleJob.getDtuicTenantId(), engineType,
                taskParamsService.parseDeployTypeByTaskParams(paramAction.getTaskParams(), engineJobCache.getComputeType(), engineJobCache.getEngineType(), scheduleJob.getDtuicTenantId()).getType(),
                paramAction.getUserId(), paramAction.getResourceId(), MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo), paramAction.getComponentVersion());
        jobIdentifier.setProjectId(paramAction.getProjectId());
        jobIdentifier.setAppType(paramAction.getAppType());
        jobIdentifier.setTaskType(paramAction.getTaskType());
        jobIdentifier.setClassArgs(paramAction.getExeArgs());
        return enginePluginsOperator.queryJobData(jobIdentifier);
    }

    public OfflineReturnVO offline(List<String> taskIds) {

        return new OfflineReturnVO();
        //实时任务下线，不清除sp、cp的关联关系
    }

    public FlinkWebUrlResultDTO getWebMonitorUrl(String taskId) {
        FlinkWebUrlResultDTO checkResult = null;
        try {

            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
            Preconditions.checkNotNull(scheduleJob, "can't find record by taskId" + taskId);

            //只获取运行中的任务的log—url
            Integer status = scheduleJob.getStatus();
            if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
                throw new RdosDefineException(String.format("job:%s not running status now status is %s", taskId,status), ErrorCode.INVALID_TASK_STATUS);
            }

            String applicationId = scheduleJob.getApplicationId();

            if (StringUtils.isEmpty(applicationId)) {
                throw new RdosDefineException(String.format("job %s not running applicationId is empty", taskId), ErrorCode.INVALID_TASK_RUN_MODE);
            }
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(taskId);
            if (engineJobCache == null) {
                throw new RdosDefineException(String.format("job:%s not exist in job cache table ", taskId),ErrorCode.JOB_CACHE_NOT_EXIST);
            }
            JobIdentifier jobIdentifier = null;
            String jobInfo = engineJobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), applicationId, taskId, scheduleJob.getDtuicTenantId(), engineJobCache.getEngineType(),
                    EDeployMode.PERJOB.getType(), paramAction.getUserId(), paramAction.getResourceId(), null, paramAction.getComponentVersion());
            jobIdentifier.setProjectId(paramAction.getProjectId());
            jobIdentifier.setAppType(paramAction.getAppType());
            jobIdentifier.setTaskType(paramAction.getTaskType());
            jobIdentifier.setClassArgs(paramAction.getExeArgs());
            checkResult = enginePluginsOperator.getWebMonitorUrl(jobIdentifier);
        } catch (Exception e) {
            LOGGER.warn(" failed to get flink web monitor url ",e);
        }
        return checkResult;
    }
}
