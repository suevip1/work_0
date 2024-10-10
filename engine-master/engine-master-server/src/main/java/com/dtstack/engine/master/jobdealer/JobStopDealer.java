package com.dtstack.engine.master.jobdealer;


import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.StoppedJob;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.enums.OperateTypeEnum;
import com.dtstack.engine.master.impl.ScheduleJobExpandService;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.impl.ScheduleJobOperateService;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/5/26
 */
@Component
public class JobStopDealer implements InitializingBean, DisposableBean, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStopDealer.class);

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobOperatorRecordDao engineJobStopRecordDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private ScheduleJobOperateService scheduleJobOperateService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ScheduleJobHistoryService scheduleJobHistoryService;

    private static final int JOB_STOP_LIMIT = 1000;


    private static final int OPERATOR_EXPIRED_INTERVAL = 60000;

    private int jobStoppedRetry;
    private long jobStoppedDelay;
    private long jobAcquireStopCycle;


    private DelayBlockingQueue<StoppedJob<JobElement>> stopJobQueue = new DelayBlockingQueue<>(1000);

    private ExecutorService delayStopProcessorService;

    private ExecutorService asyncDealStopJobService;

    private ScheduledExecutorService scheduledService;

    private DelayStopProcessor delayStopProcessor;

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());

    private Predicate<EngineJobCache> isStreamAppJob= engineJobCache -> {
        if (ComputeType.STREAM.getType().equals(engineJobCache.getComputeType())) {
            return Boolean.TRUE;
        }
        if (!ComputeType.BATCH.getType().equals(engineJobCache.getComputeType())) {
            return Boolean.FALSE;
        }
        ParamAction paramAction;
        try {
            paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
        } catch (IOException e) {
            return Boolean.FALSE;
        }
        return AppType.STREAM.getType().equals(paramAction.getAppType());
    };

    public int addStopJobs(List<ScheduleJob> jobs, Integer isForce, Long operateId) {
        if (CollectionUtils.isEmpty(jobs)) {
            return 0;
        }
        if (jobs.size() > JOB_STOP_LIMIT) {
            throw new RdosDefineException("please don't stop too many tasks at once, limit:" + JOB_STOP_LIMIT);
        }
        List<ScheduleJob> needSendStopJobs = new ArrayList<>(jobs.size());
        // 去重
        Set<String> distinctJobIds = new HashSet<>();

        List<String> unSubmitJob = new ArrayList<>(jobs.size());
        List<String> unSubmitJobAndUpdateEndTime = new ArrayList<>(jobs.size());
        List<String> sendStopJob = new ArrayList<>(jobs.size());
        Date now = new Date();
        for (ScheduleJob job : jobs) {
            if (RdosTaskStatus.UNSUBMIT.getStatus().equals(job.getStatus())) {
                unSubmitJob.add(job.getJobId());
            } else if (SPECIAL_TASK_TYPES.contains(job.getTaskType()) || RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(job.getStatus())) {
                unSubmitJobAndUpdateEndTime.add(job.getJobId());
            } else {
                if (!distinctJobIds.add(job.getJobId())) {
                    continue;
                }
                needSendStopJobs.add(job);
            }
            if (operateId == null) {
                operateId = -1L;
            }
            scheduleJobOperateService.addScheduleJobOperate(job.getJobId(), OperateTypeEnum.STOP.getCode(),OperateTypeEnum.STOP.getMsg(), operateId);
        }
        List<String> alreadyExistJobIds = engineJobStopRecordDao.listStopRecordByJobIds(jobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toList()));
        // 停止已提交的
        if (CollectionUtils.isNotEmpty(needSendStopJobs)) {
            isForce = Optional.ofNullable(isForce).orElse(ForceCancelFlag.NO.getFlag());
            for (ScheduleJob job : needSendStopJobs) {
                ScheduleJobOperatorRecord jobStopRecord = new ScheduleJobOperatorRecord();
                jobStopRecord.setJobId(job.getJobId());
                if ((ComputeType.STREAM.getType().equals(job.getComputeType()) || (ComputeType.BATCH.getType().equals(job.getComputeType()) && AppType.STREAM.getType().equals(job.getAppType()))) &&
                        RdosTaskStatus.RUNNING.getStatus().equals(job.getStatus())) {
                    LOGGER.info("stream jobId:{} and status:{} is RUNNING, change status CANCELLING ", job.getJobId(), job.getStatus());
                    scheduleJobDao.updateJobStatus(job.getJobId(), RdosTaskStatus.CANCELLING.getStatus());
                }
                jobStopRecord.setOperatorType(OperatorType.STOP.getType());
                jobStopRecord.setForceCancelFlag(isForce);
                if (alreadyExistJobIds.contains(jobStopRecord.getJobId())) {
                    LOGGER.info("jobId:{}  is already exist in table. update record", jobStopRecord.getJobId());
                    engineJobStopRecordDao.update(jobStopRecord);
                } else {
                    engineJobStopRecordDao.insert(jobStopRecord);
                    alreadyExistJobIds.add(job.getJobId());
                    sendStopJob.add(jobStopRecord.getJobId());
                }
            }
        }
        //更新未提交任务状态
        if (CollectionUtils.isNotEmpty(unSubmitJob)) {
            scheduleJobDao.updateJobStatusByIds(RdosTaskStatus.CANCELED.getStatus(), unSubmitJob);
        }

        if (CollectionUtils.isNotEmpty(unSubmitJobAndUpdateEndTime)) {
            scheduleJobDao.updateJobStatusANdExceTimeByIds(RdosTaskStatus.CANCELED.getStatus(), unSubmitJobAndUpdateEndTime);
        }

        if (operateId != -1L) {
            List<String> stopJobIds = new ArrayList<>();
            stopJobIds.addAll(unSubmitJob);
            stopJobIds.addAll(unSubmitJobAndUpdateEndTime);
            scheduleJobExpandService.asyncAddKillLog(stopJobIds, operateId, now,RdosTaskStatus.CANCELED.getStatus());
            scheduleJobExpandService.asyncAddKillLog(sendStopJob, operateId, now,null);
        }

        return jobs.size();
    }

    public int addStopJobs(List<ScheduleJob> jobs, Long operateId) {
        return addStopJobs(jobs, ForceCancelFlag.NO.getFlag(), operateId);
    }


    @Override
    public void afterPropertiesSet() {
        jobStoppedRetry = environmentContext.getJobStoppedRetry();
        jobStoppedDelay = environmentContext.getJobStoppedDelay();
        jobAcquireStopCycle = environmentContext.getJobAcquireStopCycle();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        asyncDealStopJobService = new ThreadPoolExecutor(environmentContext.getAsyncDealStopJobPoolMinSize(),
                environmentContext.getAsyncDealStopJobPoolMaxSize(), 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(environmentContext.getAsyncDealStopJobQueueSize()),
                new CustomThreadFactory("asyncDealStopJob"),
                new CustomThreadRunsPolicy("asyncDealStopJob", "stop", 180));

        delayStopProcessor = new DelayStopProcessor();
        delayStopProcessorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("delayStopProcessor"));
        delayStopProcessorService.submit(delayStopProcessor);
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));

        AcquireStopJob acquireStopJob = new AcquireStopJob();
        scheduledService.scheduleWithFixedDelay(
                acquireStopJob,
                jobAcquireStopCycle,
                jobAcquireStopCycle,
                TimeUnit.MILLISECONDS);
        LOGGER.info("Initializing {} ok", this.getClass().getName());
    }

    @Override
    public void destroy() throws Exception {
        delayStopProcessor.close();
        delayStopProcessorService.shutdownNow();
        scheduledService.shutdownNow();
        asyncDealStopJobService.shutdownNow();
        LOGGER.info("job stop process thread is shutdown...");
    }

    private class AcquireStopJob implements Runnable {
        @Override
        public void run() {
            long tmpStartId = 0L;
            Timestamp operatorExpired = new Timestamp(System.currentTimeMillis() + OPERATOR_EXPIRED_INTERVAL);
            while (true) {
                try {
                    //根据条件判断是否有数据存在
                    List<ScheduleJobOperatorRecord> jobStopRecords = engineJobStopRecordDao.listStopJob(tmpStartId);
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }
                    //使用乐观锁防止多节点重复停止任务
                    Iterator<ScheduleJobOperatorRecord> it = jobStopRecords.iterator();
                    while (it.hasNext()) {
                        ScheduleJobOperatorRecord jobStopRecord = it.next();
                        tmpStartId = jobStopRecord.getId();
                        boolean localExpire = handleLocalExpireJob(jobStopRecord.getJobId());
                        if (localExpire) {
                            it.remove();
                        } else {
                            //已经被修改过version的任务代表其他节点正在处理，可以忽略
                            Integer update = engineJobStopRecordDao.updateOperatorExpiredVersion(jobStopRecord.getId(), operatorExpired, jobStopRecord.getVersion());
                            if (update != 1) {
                                it.remove();
                            }
                        }
                    }
                    LOGGER.info("optimistic lock filter job:{}",jobStopRecords);
                    //经乐观锁判断，经过remove后所剩下的数据
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }
                    Map<String, Integer> operatorTypeMap = jobStopRecords.stream().collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, ScheduleJobOperatorRecord::getOperatorType, (k, v) -> v));
                    List<String> jobIds = jobStopRecords.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toList());
                    List<EngineJobCache> jobCaches = engineJobCacheDao.getByJobIds(jobIds);

                    //为了下面兼容异常状态的任务停止
                    Map<String, EngineJobCache> jobCacheMap = new HashMap<>(jobCaches.size());
                    for (EngineJobCache jobCache : jobCaches) {
                        jobCacheMap.put(jobCache.getJobId(), jobCache);
                    }

                    for (ScheduleJobOperatorRecord jobStopRecord : jobStopRecords) {
                        EngineJobCache jobCache = jobCacheMap.get(jobStopRecord.getJobId());
                        Integer operatorType = operatorTypeMap.get(jobStopRecord.getJobId());
                        if (jobCache != null) {
                            //停止任务的时效性，发起停止操作要比任务存入jobCache表的时间要迟
                            if (jobCache.getGmtCreate().after(jobStopRecord.getGmtCreate())) {
                                engineJobStopRecordDao.delete(jobStopRecord.getId());
                                continue;
                            }

                            boolean forceCancelFlag = ForceCancelFlag.YES.getFlag().equals(jobStopRecord.getForceCancelFlag());
                            JobElement jobElement = new JobElement(jobCache.getJobId(), jobStopRecord.getId(),operatorType, forceCancelFlag );
                            long startTime = System.currentTimeMillis();
                            LOGGER.info("jobId {} start stop,send stop directives",jobStopRecord.getJobId());
                            asyncDealStopJobService.submit(() -> asyncDealStopJob(new StoppedJob<>(jobElement, jobStoppedRetry, jobStoppedDelay)));
                            LOGGER.info("jobId {} end stop, cost time:{} ms",jobStopRecord.getJobId(),System.currentTimeMillis()-startTime);
                        } else {
                            Integer cancelStatus = getCancelStatus(operatorType);
                            //jobcache表没有记录，可能任务已经停止。在update表时增加where条件不等于stopped
                            scheduleJobDao.updateTaskStatusNotStopped(jobStopRecord.getJobId(), cancelStatus, RdosTaskStatus.getStoppedStatus());
                            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobStopRecord.getJobId(), IsDeletedEnum.NOT_DELETE.getType());
                            scheduleJobHistoryService.updateScheduleJobHistoryTime(scheduleJob.getApplicationId(),scheduleJob.getAppType());
                            scheduleJobExpandService.updateTaskStatusNotStopped(jobStopRecord.getJobId(),cancelStatus,RdosTaskStatus.getStoppedStatus());
                            LOGGER.info("[Unnormal Job] jobId:{} update job status:{}, job is finished.", jobStopRecord.getJobId(), cancelStatus);
                            shardCache.updateLocalMemTaskStatus(jobStopRecord.getJobId(), cancelStatus);
                            engineJobStopRecordDao.delete(jobStopRecord.getId());
                        }
                    }

                    Thread.sleep(500);
                } catch (Throwable e) {
                    LOGGER.error("when acquire stop jobs happens error:", e);
                }
            }
        }

        /**
         * 需要在当前任务调度节点取消的任务
         *
         * @param jobId 实例 id
         * @return 是否是需要任务本身调度机器取消
         * @throws Exception 异常信息
         */
        private boolean handleLocalExpireJob(String jobId) throws Exception {
            EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
            if (Objects.nonNull(jobCache)) {
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                // 如果是 datasourceX 任务则不判断其他节点是否正在处理
                Integer clientType = paramAction.getClientType();
                // 判断是否是调度当前任务的节点
                if (ClientTypeEnum.DATASOURCEX_NO_STATUS.getCode().equals(clientType)) {
                    ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
                    return !environmentContext.getLocalAddress().equals(scheduleJob.getNodeAddress());
                }
            }
            return false;
        }
    }

    private class DelayStopProcessor implements Runnable {
        private volatile Boolean open = Boolean.TRUE;

        @Override
        public void run() {
            LOGGER.info("DelayStopProcessor thread is start...");
            while (open) {
                try {
                    StoppedJob<JobElement> stoppedJob = stopJobQueue.take();
                    asyncDealStopJobService.submit(() -> asyncDealStopJob(stoppedJob));
                } catch (InterruptedException ie) {
                    LOGGER.warn("interruption of stopJobQueue.take...");
                    break;
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }

        public void close() {
            open = Boolean.FALSE;
        }

    }

    private void asyncDealStopJob(StoppedJob<JobElement> stoppedJob) {
        try {
            Integer cancelStatus = getCancelStatus(stoppedJob.getJob().operatorTyp);
            EngineJobCache jobCache = engineJobCacheDao.getOne(stoppedJob.getJob().jobId);
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(stoppedJob.getJob().jobId);
            if (!checkExpired(stoppedJob.getJob(), jobCache)) {
                StoppedStatus stoppedStatus = this.stopJob(stoppedJob.getJob(), jobCache,scheduleJob);
                switch (stoppedStatus) {
                    case STOPPED:
                    case MISSED:
                        engineJobStopRecordDao.delete(stoppedJob.getJob().stopJobId);
                        redisTemplate.delete(GlobalConst.STOP + stoppedJob.getJob().jobId);
                        break;
                    case STOPPING:
                    case RETRY:
                        if (stoppedJob.isRetry()) {
                            if (StoppedStatus.STOPPING == stoppedStatus) {
                                stoppedJob.resetDelay(jobStoppedDelay * stoppedJob.getIncrCount() * 5);
                            } else {
                                stoppedJob.resetDelay(jobStoppedDelay);
                            }

                            redisTemplate.opsForValue().set(GlobalConst.STOP + stoppedJob.getJob().jobId, Integer.toString(stoppedJob.getIncrCount()),OPERATOR_EXPIRED_INTERVAL,TimeUnit.SECONDS);
                            stoppedJob.incrCount();
                            stopJobQueue.put(stoppedJob);
                        } else {
                            // 流任务或者实时计算提交的批任务
                            if (isStreamAppJob.test(jobCache)) {
                                // stream 任务 超过停止最大限制不更改状态 需要将停止中状态切换为运行中
                                engineJobStopRecordDao.delete(stoppedJob.getJob().stopJobId);
                                scheduleJobDao.updateStatusByJobIdEqualsStatus(stoppedJob.getJob().jobId, RdosTaskStatus.RUNNING.getStatus(),RdosTaskStatus.CANCELLING.getStatus());
                                redisTemplate.delete(GlobalConst.STOP + stoppedJob.getJob().jobId);
                                LOGGER.warn("stream jobId:{} retry limited ,job status can not change!", stoppedJob.getJob().jobId);
                            } else {
                                removeMemStatusAndJobCache(stoppedJob.getJob().jobId, cancelStatus,scheduleJob);
                                LOGGER.warn("jobId:{} retry limited!", stoppedJob.getJob().jobId);
                            }
                        }
                    default:
                }
            } else {
                Timestamp createTimeById = engineJobStopRecordDao.getJobCreateTimeById(stoppedJob.getJob().stopJobId);
                if(null == createTimeById){
                    return;
                }
                // fix 运行结束的后续处理游statusDealer处理，避免stopJobQueue队列捞出来时，将中间新提交的实时任务cache清理了
                removeMemStatusAndJobCache(stoppedJob.getJob().jobId, cancelStatus,scheduleJob);
                engineJobStopRecordDao.delete(stoppedJob.getJob().stopJobId);
                redisTemplate.delete(GlobalConst.STOP + stoppedJob.getJob().jobId);
                LOGGER.warn("delete stop record jobId {} stopJobId {} ", stoppedJob.getJob().jobId, stoppedJob.getJob().stopJobId);
            }

        } catch (Exception e) {
            LOGGER.error("asyncDealStopJob error: jobId {}",stoppedJob.getJob().stopJobId, e);
        }
    }

    private StoppedStatus stopJob(JobElement jobElement, EngineJobCache jobCache,ScheduleJob scheduleJob) throws Exception {
        if (jobCache == null) {
            if (scheduleJob != null && RdosTaskStatus.isStopped(scheduleJob.getStatus())) {
                LOGGER.info("jobId:{} stopped success, set job is STOPPED.", jobElement.jobId);
                return StoppedStatus.STOPPED;
            } else {
                this.removeMemStatusAndJobCache(jobElement.jobId,scheduleJob);
                LOGGER.info("jobId:{} jobCache is null, set job is MISSED.", jobElement.jobId);
                return StoppedStatus.MISSED;
            }
        } else if (null != scheduleJob && EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
            if (!RdosTaskStatus.getWaitStatus().contains(scheduleJob.getStatus()) || EJobCacheStage.PRIORITY.getStage() != jobCache.getStage()) {
                this.removeMemStatusAndJobCache(jobCache.getJobId(),scheduleJob);
                LOGGER.info("jobId:{} is unsubmitted, set job is STOPPED.", jobElement.jobId);
                return StoppedStatus.STOPPED;
            } else {
                //任务如果处于提交的状态过程中 但是stage由PRIORITY变更为SUBMITTED  直接删除会导致还是会提交到yarn上 占用资源
                LOGGER.info("jobId:{} is stopping.", jobCache.getJobId());
                return StoppedStatus.STOPPING;
            }
        } else {
            if (scheduleJob == null) {
                this.removeMemStatusAndJobCache(jobElement.jobId,null);
                LOGGER.info("jobId:{} scheduleJob is null, set job is MISSED.", jobElement.jobId);
                return StoppedStatus.MISSED;
            } else if (RdosTaskStatus.getStoppedAndNotFound().contains(scheduleJob.getStatus())) {
                this.removeMemStatusAndJobCache(jobElement.jobId,
                        OperatorType.TIMEOUT_STOP.getType().equals(jobElement.operatorTyp) ?
                                RdosTaskStatus.EXPIRE.getStatus() : RdosTaskStatus.CANCELED.getStatus(),scheduleJob);
                LOGGER.info("jobId:{} and status:{} is StoppedAndNotFound, set job is STOPPED.", jobElement.jobId, scheduleJob.getStatus());
                return StoppedStatus.STOPPED;
            }


            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
            Integer clientType = paramAction.getClientType();
            paramAction.setEngineTaskId(scheduleJob.getEngineJobId());
            paramAction.setApplicationId(scheduleJob.getApplicationId());
            JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
            jobClient.setForceCancel(jobElement.isForceCancel);

            if (StringUtils.isNotBlank(scheduleJob.getEngineJobId()) && !jobClient.getEngineTaskId().equals(scheduleJob.getEngineJobId())) {
                this.removeMemStatusAndJobCache(jobElement.jobId,scheduleJob);
                LOGGER.info("jobId:{} stopped success, because of [difference engineJobId].", paramAction.getTaskId());
                return StoppedStatus.STOPPED;
            }
            JobResult jobResult = operatorDistributor.getOperator(ClientTypeEnum.getClientTypeEnum(clientType), paramAction.getEngineType()).stopJob(jobClient);
            if (jobResult.getCheckRetry()) {
                LOGGER.info("jobId:{} is retry, msg: {}", paramAction.getTaskId(), jobResult.getMsgInfo());
                return StoppedStatus.RETRY;
            } else {
                LOGGER.info("jobId:{} is stopping.", paramAction.getTaskId());
                return StoppedStatus.STOPPING;
            }
        }

    }

    private void removeMemStatusAndJobCache(String jobId,ScheduleJob scheduleJob) {
        removeMemStatusAndJobCache(jobId, RdosTaskStatus.CANCELED.getStatus(),scheduleJob);
    }

    private void removeMemStatusAndJobCache(String jobId, Integer stopStatus,ScheduleJob scheduleJob) {
        shardCache.removeIfPresent(jobId);
        engineJobCacheDao.delete(jobId);
        //修改任务状态
        scheduleJobDao.updateJobStatusAndExecTime(jobId, stopStatus);

        if (scheduleJob != null) {
            scheduleJobHistoryService.updateScheduleJobHistoryTime(scheduleJob.getApplicationId(),scheduleJob.getAppType());
        }
        scheduleJobExpandService.updateJobStatusAndExecTime(jobId,stopStatus);
        LOGGER.info("jobId:{} delete jobCache and update job status:{}, job set finished.", jobId, stopStatus);
    }

    private Integer getCancelStatus (Integer operatorType) {
        return OperatorType.TIMEOUT_STOP.getType().equals(operatorType) ?
                RdosTaskStatus.EXPIRE.getStatus() : RdosTaskStatus.CANCELED.getStatus();
    }

    private boolean checkExpired(JobElement jobElement, EngineJobCache jobCache) {

        Timestamp getGmtCreate = engineJobStopRecordDao.getJobCreateTimeById(jobElement.stopJobId);
        if (jobCache != null && getGmtCreate != null) {
            return jobCache.getGmtCreate().after(getGmtCreate);
        } else {
            return true;
        }
    }

    private class JobElement {

        public String jobId;
        public long stopJobId;
        public boolean isForceCancel;
        public Integer operatorTyp;


        public JobElement(String jobId, long stopJobId, Integer operatorTyp, boolean isForceCancel) {
            this.jobId = jobId;
            this.stopJobId = stopJobId;
            this.operatorTyp = operatorTyp;
            this.isForceCancel = isForceCancel;
        }
    }
}
