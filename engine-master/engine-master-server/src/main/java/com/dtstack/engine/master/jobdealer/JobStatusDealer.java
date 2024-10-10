package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.common.AlterDiscardPolicy;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.executor.TaskAlterExecutor;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobLogType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.pojo.JobStatusFrequency;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.bo.JobFsyncInfo;
import com.dtstack.engine.master.bo.JobLogInfo;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.dto.JobChainParamStatusResult;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 *
 * @author toutian
 * create: 2020/01/17
 */
public class JobStatusDealer implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(JobStatusDealer.class);

    /**
     * 最大允许查询不到任务信息的次数--超过这个次数任务会被设置为CANCELED
     */
    private static int NOT_FOUND_LIMIT_TIMES;

    /**
     * 最大允许查询不到的任务信息最久时间
     */
    private static int NOT_FOUND_LIMIT_INTERVAL;

    public static final long INTERVAL = 3500;
    private final static int MULTIPLES = 5;
    private int logOutput = 0;

    private ApplicationContext applicationContext;
    private ShardManager shardManager;
    private ShardCache shardCache;
    private String jobResource;
    private ScheduleJobDao scheduleJobDao;
    private ScheduleJobService scheduleJobService;
    private EngineJobCacheDao engineJobCacheDao;
    private JobCheckpointDealer jobCheckpointDealer;
    private JobRestartDealer jobRestartDealer;
    private EnvironmentContext environmentContext;
    private long jobLogDelay;
    private JobLogDealer jobLogDealer;
    private ScheduleJobService batchJobService;
    private ScheduleJobHistoryService scheduleJobHistoryService;
    private OperatorDistributor operatorDistributor;
    private JobChainParamHandler jobChainParamHandler;
    private int taskStatusDealerPoolSize;
    private TaskParamsService taskParamsService;
    private ScheduleJobExpandDao scheduleJobExpandDao;
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;
    private ScheduleTaskShadeService ScheduleTaskShadeService;
    private ConsoleService consoleService;
    private ScheduleJobOperatorRecordDao engineJobStopRecordDao;
    private JobFsyncDealer jobFsyncDealer;
    private TaskAlterExecutor taskAlterExecutor;
    private StringRedisTemplate redisTemplate;


    //临时运行校验频率快
    private boolean isTempJob;

    /**
     * 记录job 连续某个状态的频次
     */
    private final Map<String, JobStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool;

    private ScheduleJobOperateService scheduleJobOperateService;

    private ExecutorService singleExecutor;

    public final String CANCELED_TEXT = "实例 notfound 导致任务失败：可能原因engine 重启 rdb 连接断开，flink session被杀死等等";

    @Override
    public void run() {
        try {
            if (LOGGER.isDebugEnabled() && LogCountUtil.count(logOutput++, MULTIPLES)) {
                LOGGER.debug("jobResource:{} start again gap:[{} ms]...", jobResource, INTERVAL * MULTIPLES);
            }

            List<Map.Entry<String, Integer>> jobs = new ArrayList<>(shardManager.getShard().entrySet());
            if (jobs.isEmpty()) {
                return;
            }

            jobs = jobs.stream().filter(job -> !RdosTaskStatus.needClean(job.getValue())).collect(Collectors.toList());

            Semaphore buildSemaphore = new Semaphore(taskStatusDealerPoolSize);
            CountDownLatch ctl = new CountDownLatch(jobs.size());
            for (Map.Entry<String, Integer> job : jobs) {
                try {
                    buildSemaphore.acquire();
                    taskStatusPool.submit(() -> {
                        try {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("jobId:{} before dealJob status:{}", job.getKey(), job.getValue());
                            }
                            dealJob(job.getKey());
                        } catch (Throwable e) {
                            LOGGER.error("jobId:{}", job.getKey(), e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    LOGGER.error("jobId:{} [acquire pool error]:", job.getKey(), e);
                    buildSemaphore.release();
                    ctl.countDown();
                }
            }
            ctl.await();

        } catch (Throwable e) {
            LOGGER.error("jobResource:{} run error:", jobResource, e);
        }
    }


    private void dealJob(String jobId) throws Exception {
        //热更新不获取旧任务状态
        if (Boolean.TRUE.equals(redisTemplate.hasKey(GlobalConst.STATUS_BLACK_LIST + jobId))) {
            return;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (scheduleJob == null || engineJobCache == null || StringUtils.isBlank(scheduleJob.getEngineJobId())) {
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.CANCELED.getStatus());

            Integer status = RdosTaskStatus.CANCELED.getStatus();
            String engineJobId = null;
            if (scheduleJob != null) {
                engineJobId = scheduleJob.getEngineJobId();

                if (RdosTaskStatus.getStoppedStatus().contains(scheduleJob.getStatus())) {
                    status = scheduleJob.getStatus();
                } else {
                    scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
                }
            } else {
                scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
            }

            engineJobCacheDao.delete(jobId);
            LOGGER.info("jobId:{} set job finished, status:{}, scheduleJob is {} null, engineJobCache is {} null, engineJobId is {} blank.",
                    jobId, status, scheduleJob == null ? "" : "not", engineJobCache == null ? "" : "not", engineJobId == null ? "" : "not");
        } else {
            String nodeAddress = engineJobCache.getNodeAddress();
            if (StringUtils.isNotBlank(nodeAddress) && !nodeAddress.equalsIgnoreCase(environmentContext.getLocalAddress())) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("------ jobId {} maybe has been failed over node address {} is not same to db {}", jobId, environmentContext.getLocalAddress(), nodeAddress);
                }
                shardCache.removeIfPresent(jobId);
                return;
            }
            String engineTaskId = scheduleJob.getEngineJobId();
            String appId = scheduleJob.getApplicationId();
            String engineType = engineJobCache.getEngineType();
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Map<String, Object> pluginInfo = paramAction.getPluginInfo();
            JobIdentifier jobIdentifier = new JobIdentifier(engineTaskId, appId, jobId, scheduleJob.getDtuicTenantId(), engineType,
                    taskParamsService.parseDeployTypeByTaskParams(paramAction.getTaskParams(), scheduleJob.getComputeType(), engineType, scheduleJob.getDtuicTenantId()).getType(),
                    paramAction.getUserId(), paramAction.getResourceId(), MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo), paramAction.getComponentVersion());
            jobIdentifier.setProjectId(scheduleJob.getProjectId());
            jobIdentifier.setAppType(scheduleJob.getAppType());
            jobIdentifier.setTaskType(scheduleJob.getTaskType());
            jobIdentifier.setClassArgs(paramAction.getExeArgs());
            jobIdentifier.setTaskParams(paramAction.getTaskParams());
            if (AppType.STREAM.getType().equals(scheduleJob.getAppType())) {
                String archiveFsDir = scheduleJobService.getJobExtraInfoOfValue(jobIdentifier.getTaskId(), JobResultConstant.ARCHIVE);
                jobIdentifier.setArchiveFsDir(archiveFsDir);
            }

            RdosTaskStatus rdosTaskStatus = operatorDistributor.getOperator(ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()), paramAction.getEngineType())
                    .getJobStatus(jobIdentifier, (iJobStatus) -> {
                        if (DataSourceXOperator.RDB_STATUS.FINISHED_WAIT_FLUSH.getStatus().equals(iJobStatus)) {
                            // 部分 rdb sql 需要将执行结果落盘
                            jobFsyncDealer(scheduleJob, jobIdentifier, scheduleJob.getType(), ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()), paramAction.getFsyncSql());
                        }
                    });

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("------ jobId:{} dealJob status:{}", jobId, rdosTaskStatus);
            }

            if (rdosTaskStatus != null) {

                rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, jobId);
                Integer status = rdosTaskStatus.getStatus();
                // 判断是否处于重试状态, 如果在重试状态则直接 return
                boolean isRestart = jobRestartDealer.checkAndRestart(
                        status,
                        scheduleJob,
                        engineJobCache,
                        (job, client) -> jobLogDealer.executeLogRunnable(
                                () -> {
                                    // 实时获取失败日志并保存
                                    String engineLog = operatorDistributor
                                            .getOperator(ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()), paramAction.getEngineType())
                                            .getEngineLog(jobIdentifier);
                                    jobRestartDealer.jobRetryRecord(job, client, engineLog);
                                }));
                if (isRestart) {
                    LOGGER.info("----- jobId:{} after dealJob status:{}", jobId, rdosTaskStatus);
                    return;
                }

                JobChainParamStatusResult chainParamStatusResult = jobChainParamHandler.checkOutputParamIfNeed(rdosTaskStatus, scheduleJob, jobIdentifier, paramAction.getClientType());
                rdosTaskStatus = chainParamStatusResult.getRdosTaskStatus();

                status = handlerTimeoutStatus(rdosTaskStatus.getStatus(), jobId);

                updateJobStatusWithPredicate(scheduleJob, jobId, status);

                //数据的更新顺序，先更新job_cache，再更新engine_batch_job
                if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                    if (EngineType.isFlink(engineType)) {
                        Integer finalStatus = status;
                        CompletableFuture.runAsync(() -> jobCheckpointDealer.updateCheckpointImmediately(new JobCheckpointInfo(jobIdentifier, engineType), engineTaskId, finalStatus));
                        scheduleJobHistoryService.updateScheduleJobHistoryTime(appId, scheduleJob.getAppType());
                    }
                    // 运行结束后的日志获取
                    jobLogDelayDealer(jobId, jobIdentifier, engineType, engineJobCache.getComputeType(), scheduleJob.getType(),
                            chainParamStatusResult.getLog(), ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()),status);
                    jobStatusFrequency.remove(jobId);
                    engineJobCacheDao.delete(jobId);
                    LOGGER.info("------ jobId:{} is stop status {} delete jobCache", jobId, status);
                }

                if (RdosTaskStatus.RUNNING.getStatus().equals(status)) {
                    if (EngineType.isFlink(engineType)) {
                        jobCheckpointDealer.addCheckpointTaskForQueue(scheduleJob.getComputeType(), jobId, jobIdentifier, engineType);
                    }
                    dealTimeoutJob(scheduleJob);
                }

                shardCache.updateLocalMemTaskStatus(jobId, status);
                if(!scheduleJob.getIsRestart().equals(1)){
                    //非重跑任务,超时未完成告警触发
                    ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
                    singleExecutor.submit(() -> taskAlterExecutor.scanningAlarm(Lists.newArrayList(scheduleBatchJob)));
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("------ jobId:{} after dealJob status:{}", jobId, rdosTaskStatus);
                }
            }
        }
    }

    private Integer handlerTimeoutStatus(Integer status, String jobId) {
        if (!RdosTaskStatus.getStoppedStatus().contains(status)) {
            return status;
        }
        List<ScheduleJobOperatorRecord> stopRecords = engineJobStopRecordDao.listTimeoutRecordByJobIds(Lists.newArrayList(jobId));
        if (CollectionUtils.isEmpty(stopRecords)) {
            return status;
        }
        return RdosTaskStatus.EXPIRE.getStatus();
    }

    private void jobFsyncDealer(ScheduleJob job, JobIdentifier jobIdentifier, Integer type, ClientTypeEnum clientTypeEnum, String fsyncSql) {
        JobFsyncInfo jobFsyncInfo = new JobFsyncInfo(job.getJobId(), type, clientTypeEnum, jobIdentifier, fsyncSql, job.getCycTime(), job.getGmtCreate());
        jobFsyncDealer.addJobInfo(jobFsyncInfo);
    }

    /**
     * 处理超时任务
     *
     * @param scheduleJob 任务实例
     */
    private void dealTimeoutJob(ScheduleJob scheduleJob) {
        ScheduleTaskShade scheduleTaskShade = ScheduleTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (Objects.isNull(scheduleTaskShade)) {
            return;
        }
        String scheduleConf = scheduleTaskShade.getScheduleConf();
        long timeoutSecond = ScheduleConfUtils.getRunTimeoutSecond(scheduleConf);
        if (timeoutSecond > 0) {
            Timestamp startTime = scheduleJob.getExecStartTime();
            if (startTime.getTime() + timeoutSecond * 1000 < System.currentTimeMillis()) {
                LOGGER.info("job timeout and stop, jobId: {}, scheduleConf: {}", scheduleJob.getJobId(), scheduleConf);
                consoleService.stopJob(scheduleJob.getJobId(), ForceCancelFlag.YES.getFlag(), true);
            }
        }
    }

    private void updateJobStatusWithPredicate(ScheduleJob scheduleJob, String jobId, Integer status) {

        // 流计算任务或者实时计算平台的批任务
        Predicate<ScheduleJob> isStreamTaskConditions = job ->
                ComputeType.STREAM.getType().equals(job.getComputeType())
                        || AppType.STREAM.getType().equals(job.getAppType());

        //流计算只有在状态变更(且任务没有被手动停止 进入CANCELLING)的时候才去更新schedule_job表
        Predicate<ScheduleJob> isStreamUpdateConditions = job ->
                isStreamTaskConditions.test(job)
                        && !job.getStatus().equals(status)
                        && !RdosTaskStatus.CANCELLING.getStatus().equals(job.getStatus());

        //流计算 任务被手动停止 进入CANCELLING 除非YARN上状态已结束 才回写, 引擎返回的最终状态需要回写到数据库
        Predicate<ScheduleJob> isStreamCancellingConditions = job ->
                isStreamTaskConditions.test(job)
                        && RdosTaskStatus.CANCELLING.getStatus().equals(job.getStatus())
                        && RdosTaskStatus.STOPPED_STATUS.contains(status);

        // 非实时计算平台的批任务
        Predicate<ScheduleJob> isNotStreamBatchConditions = job ->
                ComputeType.BATCH.getType().equals(job.getComputeType())
                        && !AppType.STREAM.getType().equals(job.getAppType());

        if (isNotStreamBatchConditions.test(scheduleJob) || isStreamUpdateConditions.test(scheduleJob) || isStreamCancellingConditions.test(scheduleJob)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("jobId:{} taskRule status {}", jobId, status);
            }
            if (RdosTaskStatus.FINISHED.getStatus().equals(status) && batchJobService.hasTaskRule(scheduleJob)) {
                // 判断子节点是否存在强弱任务
                LOGGER.info("jobId:{} taskRule update {}", jobId, RdosTaskStatus.RUNNING_TASK_RULE.getStatus());
                scheduleJobDao.updateJobStatusAndExecTime(jobId, RdosTaskStatus.RUNNING_TASK_RULE.getStatus());
                scheduleJobExpandDao.updateJobStatusAndExecTime(jobId,RdosTaskStatus.RUNNING_TASK_RULE.getStatus());
                scheduleJobGanttTimeService.ganttChartTime(scheduleJob.getJobId(), JobGanttChartEnum.VALID_JOB_TIME, scheduleJob.getType(), scheduleJob.getComputeType());
            } else {
                LOGGER.info("jobId:{} update {} schedule status {} ", jobId, status, scheduleJob.getStatus());
                String nodeAddress = scheduleJob.getNodeAddress();
                if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                    // 如果是停止状态 更新停止时间
                    scheduleJobDao.updateJobStatusAndExecTimeWithNodeAddress(jobId, status, nodeAddress);
                    scheduleJobExpandDao.updateJobStatusAndExecTime(jobId,status);
                } else if (!scheduleJob.getStatus().equals(status)) {
                    LOGGER.info("jobId:{} update {} schedule status {} success", jobId, status, scheduleJob.getStatus());
                    scheduleJobDao.updateJobStatusWithNodeAddress(jobId, status, nodeAddress);
                    scheduleJobExpandDao.updateJobStatus(jobId, status);
                }
            }

            if (TaskRuleEnum.STRONG_RULE.getCode().equals(scheduleJob.getTaskRule())) {
                // 强规则任务,查询父任务
                batchJobService.handleTaskRule(scheduleJob, status);
            }
        }
    }

    /**
     * 校验任务 not found 状态, 任务状态次数或者时间超过一定数值后则更新任务状态为 failed
     *
     * @param taskStatus 任务状态
     * @param jobId      任务 id
     * @return 更新后的状态
     */
    private RdosTaskStatus checkNotFoundStatus(RdosTaskStatus taskStatus, String jobId) {
        JobStatusFrequency statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        //如果状态为NotFound，则对频次进行判断
        if (statusPair.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue()) {
            if (statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES || System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL) {
                LOGGER.info(" job id {}  check not found status had try max , change status to {} ", jobId, RdosTaskStatus.FAILED.getStatus());
                scheduleJobOperateService.addScheduleJobOperate(jobId, OperateTypeEnum.STOP.getCode(), CANCELED_TEXT, null);
                return RdosTaskStatus.FAILED;
            }
        }
        return taskStatus;
    }

    private void jobLogDelayDealer(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, Integer type, String customLog, ClientTypeEnum clientType, Integer status) {
        //临时运行和运行失败的任务立马去获取日志
        long delayTime = EScheduleType.TEMP_JOB.getType().equals(type) || RdosTaskStatus.FAILED_STATUS.contains(status) ? 0L : jobLogDelay;
        JobLogInfo jobCompletedInfo = new JobLogInfo(jobId, jobIdentifier, engineType, computeType, delayTime, EJobLogType.FINISH_LOG, clientType);
        jobCompletedInfo.setCustomLog(customLog);
        jobLogDealer.addJobInfo(jobCompletedInfo);
    }

    /**
     * 更新任务状态频次
     *
     * @param jobId  任务 id
     * @param status 任务状态
     * @return 任务状态 dto
     */
    private JobStatusFrequency updateJobStatusFrequency(String jobId, Integer status) {
        JobStatusFrequency statusFrequency = jobStatusFrequency.computeIfAbsent(jobId, k -> new JobStatusFrequency(status));
        if (statusFrequency.getStatus().equals(status)) {
            statusFrequency.setNum(statusFrequency.getNum() + 1);
        } else {
            statusFrequency.resetJobStatus(status);
        }
        return statusFrequency;
    }

    public void setTempJob(boolean tempJob) {
        isTempJob = tempJob;
    }

    public void setShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    public void setShardCache(ShardCache shardCache) {
        this.shardCache = shardCache;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
        this.jobLogDelay = environmentContext.getJobLogDelay();


        this.taskStatusDealerPoolSize = environmentContext.getTaskStatusDealerPoolSize();
        NOT_FOUND_LIMIT_TIMES = environmentContext.getNotFoundLimit();
        NOT_FOUND_LIMIT_INTERVAL = environmentContext.getNotFoundTimeLimit();
        this.taskStatusPool = new ThreadPoolExecutor(taskStatusDealerPoolSize, taskStatusDealerPoolSize, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new CustomThreadFactory(jobResource + this.getClass().getSimpleName() + "DealJob"), new BlockCallerPolicy());
    }

    private void setBean() {
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.jobCheckpointDealer = applicationContext.getBean(JobCheckpointDealer.class);
        this.jobRestartDealer = applicationContext.getBean(JobRestartDealer.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.batchJobService = applicationContext.getBean(ScheduleJobService.class);
        this.taskParamsService = applicationContext.getBean(TaskParamsService.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobHistoryService = applicationContext.getBean(ScheduleJobHistoryService.class);
        this.operatorDistributor = applicationContext.getBean(OperatorDistributor.class);
        this.jobChainParamHandler = applicationContext.getBean(JobChainParamHandler.class);
        this.jobLogDealer = applicationContext.getBean(JobLogDealer.class);
        this.scheduleJobGanttTimeService = applicationContext.getBean(ScheduleJobGanttTimeService.class);
        this.ScheduleTaskShadeService = applicationContext.getBean(ScheduleTaskShadeService.class);
        this.consoleService = applicationContext.getBean(ConsoleService.class);
        this.engineJobStopRecordDao = applicationContext.getBean(ScheduleJobOperatorRecordDao.class);
        this.scheduleJobExpandDao = applicationContext.getBean(ScheduleJobExpandDao.class);
        this.jobFsyncDealer = applicationContext.getBean(JobFsyncDealer.class);
        this.scheduleJobOperateService = applicationContext.getBean(ScheduleJobOperateService.class);
        this.taskAlterExecutor = applicationContext.getBean(TaskAlterExecutor.class);
        this.redisTemplate = applicationContext.getBean(StringRedisTemplate.class);

        String alarmThreadName = this.getClass().getSimpleName()  + "_alarm";
        singleExecutor = new ThreadPoolExecutor(environmentContext.getSingleExecutorThreadNum(),environmentContext.getSingleExecutorThreadNum(),60,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),new CustomizableThreadFactory(alarmThreadName),new AlterDiscardPolicy());
    }

    public void start() {
        long jobStatusCheckInterVal = isTempJob ? environmentContext.getTempJobStatusCheckInterVal() : environmentContext.getJobStatusCheckInterVal();
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                jobStatusCheckInterVal,
                TimeUnit.MILLISECONDS);
        LOGGER.info("{} thread start interval {} ...", jobResource + this.getClass().getSimpleName(), jobStatusCheckInterVal);
    }
}