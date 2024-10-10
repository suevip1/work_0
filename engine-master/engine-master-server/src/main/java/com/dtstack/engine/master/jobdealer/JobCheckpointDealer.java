package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.CheckpointDetail;
import com.dtstack.engine.api.enums.CheckpointType;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.CheckpointQueryParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.StreamTaskService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.utils.TimeUtils;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  checkpoint管理
 * @author maqi
 */
@Component
public class JobCheckpointDealer implements ApplicationListener<ApplicationReadyEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(JobCheckpointDealer.class);

    private final static String CHECKPOINT_RETAINED_KEY = "state.checkpoints.num-retained";

    private final static String CHECKPOINT_ID_KEY = "id";

    private final static String CHECKPOINT_SAVEPATH_KEY = "external_path";

    private static final String TASK_PARAMS_KEY = "taskParams";

    private static final char SEPARATOR = '_';

    private static final String CHECKPOINT_STATUS_KEY = "status";

    private static final String CHECKPOINT_FAILED_STATUS = "FAILED";

    private final static String FLINK_CP_HISTORY_KEY = "history";

    public static final String SQL_CHECKPOINT_INTERVAL_KEY = "sql.checkpoint.interval";
    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "flink.checkpoint.interval";
    public static final String EXECUTION_CHECKPOINT_INTERVAL_KEY = "execution.checkpointing.interval";

    public static final String SQL_CHECKPOINT_CLEANUP_MODE_KEY = "sql.checkpoint.cleanup.mode";
    public static final String FLINK_CHECKPOINT_CLEANUP_MODE_KEY = "flink.checkpoint.cleanup.mode";

    public static final int JOB_CHECKPOINT_CONFIG = 50;

    /** 已经插入到db的checkpoint，其id缓存数量*/
    public static final long CHECKPOINT_INSERTED_RECORD = 200;

    /**
     * 存在checkpoint 外部存储路径的taskid
     */
    private Map<String, Integer> taskEngineIdAndRetainedNum = Maps.newConcurrentMap();

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private StreamTaskService streamTaskService;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Value("${checkpoint.retry.day:3}")
    private Integer checkRetryDay;

    private Map<String,String> queuePutRecord = new ConcurrentHashMap<>();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    private Cache<String, Map<String, Object>> checkpointConfigCache = CacheBuilder.newBuilder().maximumSize(JOB_CHECKPOINT_CONFIG).build();

    private DelayBlockingQueue<JobCheckpointInfo> delayBlockingQueue = new DelayBlockingQueue<>(1000);

    private ExecutorService checkpointPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()+"checkpointPool"));

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        checkpointPool.submit(() -> {
            while (true) {
                String taskId = "";
                String engineJobId = "";
                try {
                    JobCheckpointInfo taskInfo = delayBlockingQueue.take();
                    if (null != taskInfo.getJobIdentifier()) {
                        engineJobId = taskInfo.getJobIdentifier().getEngineJobId();
                    }
                    taskId = taskInfo.getTaskId();
                    String recordEngineJobId = queuePutRecord.get(taskId);
                    if(StringUtils.isBlank(recordEngineJobId) || !recordEngineJobId.equalsIgnoreCase(engineJobId)){
                        LOGGER.warn("delay queue jobId:{} engineJobId :{} is not same to record:{} so skip", taskId, engineJobId, recordEngineJobId);
                        continue;
                    }
                    Integer status = scheduleJobDao.getStatusByJobId(taskId);
                    updateCheckpointImmediately(taskInfo, engineJobId, status);

                } catch (Exception e) {
                    LOGGER.error("update delay checkpoint jobId {}  engineJobId {} error ", taskId, engineJobId, e);
                    if (StringUtils.isNotBlank(taskId)) {
                        try {
                            queuePutRecord.remove(taskId);
                            addFailedCheckpoint(taskId, engineJobId);
                        } catch (Exception exception) {
                            LOGGER.error("update addFailedCheckpoint jobId {}  engineJobId {} error ", taskId, engineJobId, exception);
                        }
                    }
                }
            }
        });
    }

    private void addFailedCheckpoint(String taskId, String engineJobId){
        engineJobCheckpointDao.insert(taskId, engineJobId, null, null, null, null,0L,0L);
    }

    /**
     *  获取最新checkpoint
     * @param taskInfo
     * @param engineJobId
     */
    public void updateCheckpointImmediately(JobCheckpointInfo taskInfo, String engineJobId, Integer status) {
        String taskId = "";
        try {
            taskId = taskInfo.getJobIdentifier().getTaskId();
            if (getCheckpointInterval(taskId) > 0 || RdosTaskStatus.getStoppedStatus().contains(status)) {
                updateJobCheckpoints(taskInfo.getJobIdentifier());

                if (RdosTaskStatus.RUNNING.getStatus().equals(status) ||
                    RdosTaskStatus.CANCELLING.getStatus().equals(status) ||
                    RdosTaskStatus.NOTFOUND.getStatus().equals(status) ||
                    RdosTaskStatus.FAILING.getStatus().equals(status)) {
                    taskInfo.refreshExpired();
                    delayBlockingQueue.put(taskInfo);
                }

                if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                    boolean checkpointStopClean = isCheckpointStopClean(taskId);
                    LOGGER.info(" taskId {}  status is stop {}  cleanCheckpoint {}", taskId, status, checkpointStopClean);
                    if (checkpointStopClean) {
                        engineJobCheckpointDao.cleanAllCheckpointByTaskEngineId(engineJobId);
                    }
                    taskEngineIdAndRetainedNum.remove(engineJobId);
                    checkpointConfigCache.invalidate(taskId);
                    queuePutRecord.remove(taskId);
                }
            }
        } catch (Exception e) {
            LOGGER.error(" taskId {}  engineJobId {}  updateCheckpointImmediately error", taskId, engineJobId, e);
        }
    }

    public void updateJobCheckpoints(JobIdentifier jobIdentifier) {
        String checkpointJsonStr = enginePluginsOperator.getCheckpoints(jobIdentifier);
        String engineTaskId = jobIdentifier.getEngineJobId();
        String taskId = jobIdentifier.getTaskId();

        if (Strings.isNullOrEmpty(checkpointJsonStr)) {
            addFailedCheckpoint(taskId, engineTaskId);
            LOGGER.info("taskId {} engineTaskId {} can't get checkpoint info.", taskId, engineTaskId);
            return;
        }
        try {
            Map<String, Object> checkpointInfo = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if (!checkpointInfo.containsKey(FLINK_CP_HISTORY_KEY) || null == checkpointInfo.get(FLINK_CP_HISTORY_KEY)) {
                LOGGER.info("taskId {} engineTaskId {} can't get checkpoint history key....", taskId, engineTaskId);
                return;
            }

            List<Map<String, Object>> checkpointHistoryInfo = (List<Map<String, Object>>) checkpointInfo.get(FLINK_CP_HISTORY_KEY);

            for (Map<String, Object> entity : checkpointHistoryInfo) {
                String checkpointId = String.valueOf(entity.get(CHECKPOINT_ID_KEY));
                String status = String.valueOf(entity.get(CHECKPOINT_STATUS_KEY));
                String checkpointCacheKey = engineTaskId + SEPARATOR + checkpointId;
                boolean containsInCache = StringUtils.isEmpty(checkpointInsertedCache.getIfPresent(checkpointCacheKey));

                if (StringUtils.equalsIgnoreCase(CHECKPOINT_FAILED_STATUS, status) && containsInCache) {
                    addFailedCheckpoint(taskId, engineTaskId);
                    checkpointInsertedCache.put(checkpointCacheKey, "1");
                }

            }
        } catch (Exception e) {
            addFailedCheckpoint(taskId,engineTaskId);
            LOGGER.error("taskId:{} ,engineTaskId:{}, error:", taskId, engineTaskId, e);
        }
    }


    public void addCheckpointTaskForQueue(Integer computeType, String taskId, JobIdentifier jobIdentifier, String engineTypeName) throws ExecutionException {
        long checkpointInterval = getCheckpointInterval(taskId);
        boolean canPutQueue = !queuePutRecord.containsKey(taskId);
        if (queuePutRecord.containsKey(taskId) && !queuePutRecord.get(taskId).equalsIgnoreCase(jobIdentifier.getEngineJobId())) {
            //jobId flink are not same
            canPutQueue = true;
        }
        if (checkpointInterval > 0 && canPutQueue) {
            //queuePutRecord去重 保证队列中taskId唯一 后续通过refreshExpired来间隔获取
            int retainedNum = 11;
            try {
                String componentVersionValue = scheduleDictService.convertVersionNameToValue(jobIdentifier.getComponentVersion(), jobIdentifier.getEngineType());
                String pluginInfo = pluginInfoManager.buildTaskPluginInfo(jobIdentifier.getProjectId(),jobIdentifier.getAppType(),jobIdentifier.getTaskType(),jobIdentifier.getTenantId(),
                        jobIdentifier.getEngineType(), jobIdentifier.getUserId(), jobIdentifier.getDeployMode(), jobIdentifier.getResourceId(),
                        Collections.singletonMap(EngineTypeComponentType.getByEngineName(jobIdentifier.getEngineType())
                                .getComponentType().getTypeCode(), componentVersionValue)).toJSONString();
                retainedNum = getRetainedNumFromPluginInfo(pluginInfo);
            } catch (Exception e) {
                LOGGER.info("get checkpoint plugin info {} error", taskId, e);
            }
            try {
                taskEngineIdAndRetainedNum.put(jobIdentifier.getEngineJobId(), retainedNum);
                JobCheckpointInfo taskInfo = new JobCheckpointInfo(computeType, taskId, jobIdentifier, engineTypeName, checkpointInterval);
                delayBlockingQueue.put(taskInfo);
                queuePutRecord.put(taskId, jobIdentifier.getEngineJobId());
                LOGGER.info("add taskId {} to checkpoint delay queue,{}", taskId, taskInfo);
            } catch (Exception e) {
                LOGGER.error("taskId {} addCheckpointTaskForQueue error ", taskId, e);
            }
        }

    }

    /**
     *   获取任务对应的环境配置信息
     * @param jobId
     * @return
     * @throws ExecutionException
     */
    private Map<String, Object> getJobParamsByJobId(String jobId) throws ExecutionException {
        return checkpointConfigCache.get(jobId, () -> {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            if(null == engineJobCache){
                return new HashMap<>(0);
            }
            String jobInfo = engineJobCache.getJobInfo();
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
            String taskParamsStr = String.valueOf(pluginInfoMap.get(TASK_PARAMS_KEY));
            Map<String, Object> paramInfo = taskParamsService.convertPropertiesToMap(taskParamsStr);
            String jobExtraFlinkCheckpointInterval = getFlinkJobExtraInfo(jobId, engineJobCache.getEngineType(), JobResultConstant.FLINK_CHECKPOINT);
            if (StringUtils.isNotBlank(jobExtraFlinkCheckpointInterval) && Integer.parseInt(jobExtraFlinkCheckpointInterval) > 0) {
                LOGGER.info("flink {} job extra info checkpoint interval {}", jobId, jobExtraFlinkCheckpointInterval);
                paramInfo.putIfAbsent(FLINK_CHECKPOINT_INTERVAL_KEY, jobExtraFlinkCheckpointInterval);
            }
            return paramInfo;
        });
    }

    /**
     * 获取flink任务job_graph的flink.checkpoint.interval参数信息
     * 优先级低于task_param
     * @param jobId
     * @param engineType
     * @return
     */
    private String getFlinkJobExtraInfo(String jobId, String engineType,String key) {
        if (EngineType.isFlink(engineType)) {
            String jobExtraInfo = scheduleJobExpandDao.getJobExtraInfo(jobId);
            if(StringUtils.isBlank(jobExtraInfo)){
                return null;
            }
            return JSONObject.parseObject(jobExtraInfo).getString(key);

        }
        return null;
    }


    private int getRetainedNumFromPluginInfo(String pluginInfo) {
        JSONObject pluginInfoJson = JSONObject.parseObject(pluginInfo);
        return MapUtils.getInteger(pluginInfoJson.getJSONObject(EComponentType.FLINK.getConfName()), CHECKPOINT_RETAINED_KEY, 1);
    }


    private boolean isCheckpointStopClean(String jobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(jobId);
        if (null == params) {
            return false;
        }
        Boolean sqlCleanMode = MathUtil.getBoolean(params.get(SQL_CHECKPOINT_CLEANUP_MODE_KEY), false);
        Boolean flinkCleanMode = MathUtil.getBoolean(params.get(FLINK_CHECKPOINT_CLEANUP_MODE_KEY), false);
        return sqlCleanMode || flinkCleanMode;
    }

    public long getCheckpointInterval(String jobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(jobId);
        if (null == params) {
            return 0L;
        }
        long sqlCheckpointInterval = MathUtil.getLongVal(params.get(SQL_CHECKPOINT_INTERVAL_KEY), 0L);
        long flinkCheckpointInterval = 0L;
        try {
            flinkCheckpointInterval = MathUtil.getLongVal(params.get(FLINK_CHECKPOINT_INTERVAL_KEY), 0L);
        } catch (Exception e) {
            String flinkCheckpointIntervalStr = params.getOrDefault(FLINK_CHECKPOINT_INTERVAL_KEY, "").toString().trim();
            flinkCheckpointInterval = toMillis(flinkCheckpointIntervalStr, 0L);
        }
        String executionStr = getString(params, EXECUTION_CHECKPOINT_INTERVAL_KEY, "");
        long executionCheckpointInterval = toMillis(executionStr, 0L);
        return Math.max(Math.max(sqlCheckpointInterval, flinkCheckpointInterval), executionCheckpointInterval);
    }

    private String getString(Map<String, Object> params, String key, String def) {
        return params.getOrDefault(EXECUTION_CHECKPOINT_INTERVAL_KEY, "").toString().trim();
    }

    private long toMillis(String text, long def) {
        if (StringUtils.isBlank(text)) {
            return def;
        }
        Duration duration = TimeUtils.parseDuration(text);
        return duration.toMillis();
    }


    public String getRetryCheckPointPath(JobClient jobClient) {
        Integer deployMode = jobClient.getDeployMode();

        if (null == deployMode && ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(jobClient.getEngineType())) {
            //解析参数
            ParamAction action = JobClientUtil.getParamAction(jobClient);
            deployMode = taskParamsService.parseDeployTypeByTaskParams(action.getTaskParams(),action.getComputeType(), EngineType.Flink.name(),jobClient.getTenantId()).getType();
        }

        JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId()
                , jobClient.getTenantId(), jobClient.getEngineType(), deployMode, jobClient.getUserId(), jobClient.getResourceId(), null, jobClient.getComponentVersion());
        jobIdentifier.setProjectId(jobClient.getProjectId());
        jobIdentifier.setAppType(jobClient.getAppType());
        jobIdentifier.setTaskType(jobClient.getTaskType());
        jobIdentifier.setClassArgs(jobClient.getClassArgs());
        jobIdentifier.setTaskParams(jobClient.getTaskParams());
        updateJobCheckpoints(jobIdentifier);
        //5.3checkPoint重构后，需要从hdfs上实时拿
        CheckpointQueryParam checkpointQueryParam = new CheckpointQueryParam();
        checkpointQueryParam.setJobId(jobClient.getTaskId());
        if (StringUtils.isBlank(jobClient.getComponentVersion())) {
            //离线数据同步 开启ck componentVersion为空 取默认
            Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(jobClient.getTenantId());
            String componentVersion = componentService.getComponentVersion(clusterId, EComponentType.FLINK.getTypeCode(), null);
            jobClient.setComponentVersion(componentVersion);
        }
        checkpointQueryParam.setComponentVersion(jobClient.getComponentVersion());
        checkpointQueryParam.setCurrentPage(1);
        checkpointQueryParam.setPageSize(1);
        checkpointQueryParam.setCheckpointTypeList(Lists.newArrayList(CheckpointType.CHECKPOINT.getType()));
        DateTime dateTime = DateTime.now();
        //查询最近3天的ck
        checkpointQueryParam.setStartTime(dateTime.minusDays(checkRetryDay).toDate().getTime());
        checkpointQueryParam.setEndTime(new Date().getTime());
        PageResult<List<CheckpointDetail>> checkPointPageResult = streamTaskService.queryCheckPoint(checkpointQueryParam);
        List<CheckpointDetail> checkpointDetails = checkPointPageResult.getData();

        return CollectionUtils.isEmpty(checkpointDetails) ? "" : checkpointDetails.get(0).getSavePath();
    }

}
