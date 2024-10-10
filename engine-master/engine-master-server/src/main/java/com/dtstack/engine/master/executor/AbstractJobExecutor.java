package com.dtstack.engine.master.executor;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.common.AlterDiscardPolicy;
import com.dtstack.engine.master.dto.ExecutorConfigDTO;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.scheduler.JobStatusSummitCheckOperator;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SignRunnable;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.ScheduleFillDataJobService;
import com.dtstack.engine.master.impl.ScheduleJobGanttTimeService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.listener.AlterEvent;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 生产者-消费者模式，考虑三种job的处理情况：
 * 1. 正常的调度任务和补数据任务的处理
 * 2. 其他节点宕机后故障恢复的数据处理
 * 3. JobGraphBuilder 执行时是否能够被正常处理（master 重启时会触发一次，如果能执行则会构建 jobgraph）
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public abstract class AbstractJobExecutor implements InitializingBean, Runnable, ApplicationListener<ApplicationStartedEvent> {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobExecutor.class);

    @Autowired
    protected ZkService zkService;

    @Autowired
    protected ScheduleJobDao scheduleJobDao;

    @Autowired
    protected ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected ScheduleJobService batchJobService;

    @Autowired
    protected JobRichOperator jobRichOperator;

    @Autowired
    protected ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    protected BatchFlowWorkJobService batchFlowWorkJobService;

    @Autowired
    protected ScheduleFillDataJobService scheduleFillDataJobService;

    @Autowired
    protected EngineJobCacheDao engineJobCacheDao;

    @Autowired
    protected ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private TaskAlterExecutor taskAlterExecutor;

    @Autowired
    protected AlterEvent alterEvent;

    @Autowired
    protected ScheduleDictService scheduleDictService;

    @Autowired
    protected JobStatusSummitCheckOperator jobStatusSummitCheckOperator;

    private ExecutorService executorService;

    private ExecutorService singleExecutor;

    private ExecutorService scanExecutor;

    private ScheduledExecutorService scheduledService;

    protected final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private LinkedBlockingQueue<ScheduleBatchJob> scheduleJobQueue = null;

    protected ExecutorConfigDTO executorConfigDTO;

    public abstract EScheduleType getScheduleType();

    public abstract void stop();

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), environmentContext.getAcquireQueueJobInterval(), environmentContext.getQueueSize());

        scheduleJobQueue = new LinkedBlockingQueue<>(environmentContext.getQueueSize());
        RUNNING.compareAndSet(false, true);

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getScheduleType() + "_AcquireJob"));

        List<ScheduleDict> scheduleDicts = scheduleDictService.listByDictType(DictType.EXECUTOR_CONFIG);

        if (CollectionUtils.isNotEmpty(scheduleDicts)) {
            for (ScheduleDict scheduleDict : scheduleDicts) {
                String dictName = scheduleDict.getDictName();

                if (getScheduleType().name().equals(dictName)) {
                    String dictValue = scheduleDict.getDictValue();
                    try {
                        executorConfigDTO = JSON.parseObject(dictValue,ExecutorConfigDTO.class);
                    } catch (Exception e) {
                        LOGGER.warn("config load error:", e);
                    }
                }
            }
        }

        String alarmThreadName = this.getClass().getSimpleName() + "_" + getScheduleType() + "_alarm";
        singleExecutor = new ThreadPoolExecutor(environmentContext.getSingleExecutorThreadNum(),environmentContext.getSingleExecutorThreadNum(),60,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),new CustomizableThreadFactory(alarmThreadName),new AlterDiscardPolicy());
        String threadName = this.getClass().getSimpleName() + "_" + getScheduleType() + "_startJobProcessor";
        executorService = new ThreadPoolExecutor(environmentContext.getJobExecutorPoolCorePoolSize(), environmentContext.getJobExecutorPoolMaximumPoolSize(), environmentContext.getJobExecutorPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(environmentContext.getJobExecutorPoolQueueSize()),
                new CustomThreadFactory(threadName),
                new CustomThreadRunsPolicy<ScheduleBatchJob>(threadName, getScheduleType().name(), (job -> {
                    batchJobService.updatePhaseStatusById(job.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
                    LOGGER.warn("reject task {},return task to db", job.getJobId());
                })));

        if (executorConfigDTO != null
                && executorConfigDTO.getThreadNum() != null
                && executorConfigDTO.getThreadNum() > 1) {
            String scanThreadName = this.getClass().getSimpleName() + "_" + getScheduleType() + "_scan";
            scanExecutor = new ThreadPoolExecutor(
                    executorConfigDTO.getThreadNum(),
                    executorConfigDTO.getThreadNum(),
                    environmentContext.getJobExecutorPoolKeepAliveTime(),
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(environmentContext.getJobExecutorPoolQueueSize()),
                    new CustomizableThreadFactory(scanThreadName));
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        scheduledService.scheduleWithFixedDelay(
                this::emitJob2Queue,
                0,
                environmentContext.getAcquireQueueJobInterval(),
                TimeUnit.MILLISECONDS);
    }

    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, Boolean isEq) {
        Pair<String, String> cycTime = getCycTime(false);
        Integer limit = 500;
        if (executorConfigDTO != null) {
            Integer scanNum = executorConfigDTO.getScanNum();
            if (scanNum != null) {
                limit = scanNum;
            }
        }
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), JobPhaseStatus.CREATE.getCode(), isEq
                , null, Restarted.NORMAL.getStatus(),limit);
        LOGGER.info("scheduleType:{} nodeAddress:{} leftTime:{} rightTime:{} start scanning since when startId:{}  isEq {}  queryJobSize {}.", getScheduleType(), nodeAddress, cycTime.getLeft(), cycTime.getRight(), startId, isEq,
                scheduleJobs.size());
        return getScheduleBatchJobList(scheduleJobs);
    }


    public void recoverOtherNode() {
        //处理其他节点故障恢复时转移而来的数据
    }

    @Override
    public void run() {
        while (RUNNING.get()) {

            ScheduleJob scheduleJob = null;
            try {
                ScheduleBatchJob scheduleBatchJob = scheduleJobQueue.take();


                scheduleJob = scheduleBatchJob.getScheduleJob();

                LOGGER.info("jobId:{} scheduleType:{} take job from queue.", scheduleJob.getJobId(), getScheduleType());
                this.start(scheduleBatchJob);
            } catch (Throwable e) {
                LOGGER.error("happens error:", e);
                try {
                    if (scheduleJob != null) {
                        batchJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                        LOGGER.error("jobId:{} scheduleType:{} submit failed.", scheduleJob.getJobId(), getScheduleType());
                    }
                } catch (Throwable ex) {
                    LOGGER.error("jobId:{} scheduleType:{} update status happens error:", scheduleJob.getJobId(), getScheduleType(), ex);
                }
            }
        }
    }

    /**
     * 增量从数据库获取id标示
     *
     * @param nodeAddress
     * @param isRestart
     * @return
     */
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        Pair<String, String> cycTime = getCycTime(true);
        Long listMinId = batchJobService.getListMinId(cycTime.getLeft(), cycTime.getRight());
        LOGGER.info("getListMinId scheduleType {} nodeAddress {} isRestart {} lastMinId is {} . cycStartTime {} cycEndTime {}", getScheduleType(), nodeAddress, isRestart, listMinId, cycTime.getLeft(), cycTime.getRight());
        return listMinId;
    }

    private void emitJob2Queue() {
        String nodeAddress = "";
        try {
            nodeAddress = zkService.getLocalAddress();
            if (StringUtils.isBlank(nodeAddress)) {
                return;
            }
            Long startId = getListMinId(nodeAddress, Restarted.NORMAL.getStatus());
            long startEnd = System.currentTimeMillis();
            LOGGER.info("startEnd emitJob2Queue  scheduleType {} nodeAddress {} startId is {} ", getScheduleType(), nodeAddress, startId);
            if (startId != null) {
                List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, Boolean.TRUE);
                LOGGER.info("emitJob2Queue startId is {} get job cost time {} ms", startId,System.currentTimeMillis()-startEnd);
                while (CollectionUtils.isNotEmpty(listExecJobs)) {
                    if (executorConfigDTO != null && executorConfigDTO.getThreadNum() != null && executorConfigDTO.getThreadNum() > 1) {
                        Integer threadNum = executorConfigDTO.getThreadNum();
                        int size = listExecJobs.size();
                        int totalBatch = size % threadNum != 0 ?
                                size / threadNum + 1 : size / threadNum;
                        List<List<ScheduleBatchJob>> partition = Lists.partition(listExecJobs, totalBatch);
                        if (partition.size() > 1) {
                            CountDownLatch ctl = new CountDownLatch(partition.size());
                            for (List<ScheduleBatchJob> scheduleBatchJobs : partition) {
                                try {
                                    String finalNodeAddress = nodeAddress;
                                    scanExecutor.execute(() -> {
                                        try {
                                            submit(finalNodeAddress, startEnd, scheduleBatchJobs);
                                        } finally {
                                            ctl.countDown();
                                        }
                                    });
                                } catch (Throwable e) {
                                    LOGGER.error("job error : ",e);
                                }
                            }
                            ctl.await();
                            LOGGER.info("emitJob2Queue startId is {} ctl time {} ms", startId,System.currentTimeMillis()-startEnd);
                        } else {
                            submit(nodeAddress, startEnd, listExecJobs);
                        }
                    } else {
                        submit(nodeAddress, startEnd, listExecJobs);
                    }
                    Optional<ScheduleBatchJob> max = listExecJobs.stream().max(Comparator.comparing(ScheduleBatchJob::getJobExecuteOrder));

                    if (max.isPresent()) {
                        startId = max.get().getJobExecuteOrder();
                    } else {
                        LOGGER.error("not fount max startId {}",getScheduleType());
                        break;
                    }
                    listExecJobs = this.listExecJob(startId, nodeAddress, Boolean.FALSE);
                }
                LOGGER.info("emitJob2Queue startId is {} initiate time {} ms", startId,System.currentTimeMillis()-startEnd);
            }
        } catch (Throwable e) {
            LOGGER.error("scheduleType:{} nodeAddress:{} emitJob2Queue error:", getScheduleType(), nodeAddress, e);
        }
    }

    private void submit(String nodeAddress, Long startEnd, List<ScheduleBatchJob> listExecJobs) {
        //定时未完成告警触发,重跑任务不触发告警
        List<ScheduleBatchJob> finalListExecJobs = listExecJobs.stream().filter(job -> !job.getIsRestart().equals(1)).collect(Collectors.toList());
        singleExecutor.submit(()->{
            taskAlterExecutor.scanningAlarm(finalListExecJobs);
        });
        // 按照appType分组
        Map<Integer, Set<Long>> groupByAppMap = listExecJobs.stream().collect(Collectors.groupingBy(shade -> shade.getScheduleJob().getAppType(),
                Collectors.mapping(ScheduleBatchJob::getTaskId, Collectors.toSet())));
        Table<Integer, Long, ScheduleTaskShade> cache = HashBasedTable.create();
        batchTaskShadeService.listTaskShadeByIdAndType(groupByAppMap).forEach((k, v) -> v.forEach(shade -> cache.put(k, shade.getTaskId(), shade)));
        for (ScheduleBatchJob scheduleBatchJob : listExecJobs) {
            // 节点检查是否能进入队列
            try {
                ScheduleTaskShade batchTask = cache.get(scheduleBatchJob.getScheduleJob().getAppType(), scheduleBatchJob.getTaskId());
                if (!scheduleJobGanttTimeService.checkExist(scheduleBatchJob.getJobId())) {
                    scheduleJobGanttTimeService.insert(scheduleBatchJob.getJobId());
                    scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.CYC_TIME);
                }
                if (batchTask == null) {
                    String errMsg = JobCheckStatus.NO_TASK.getMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), RdosTaskStatus.AUTOCANCELED.getStatus(), errMsg);
                    LOGGER.warn("jobId:{} scheduleType:{} submit failed for taskId:{} already deleted.", scheduleBatchJob.getJobId(), getScheduleType(), scheduleBatchJob.getTaskId());
                    continue;
                }

                Integer type = batchTask.getTaskType();
                Integer status = batchJobService.getJobStatus(scheduleBatchJob.getJobId());

                checkJobVersion(scheduleBatchJob.getScheduleJob(), batchTask);
                JobCheckRunInfo checkRunInfo;
                if (environmentContext.openNewSummitCheck()) {
                    checkRunInfo = jobStatusSummitCheckOperator.checkJobStatusMeetSubmissionConditions(status,batchTask, scheduleBatchJob,false);
                } else {
                    checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), batchTask);
                }
                if (EScheduleJobType.WORK_FLOW.getType().equals(type) || EScheduleJobType.ALGORITHM_LAB.getVal().equals(type)) {
                    LOGGER.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB so immediate put queue.", scheduleBatchJob.getJobId(), getScheduleType());
                    if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status) && isPutQueue(checkRunInfo, scheduleBatchJob)) {
                        putScheduleJob(scheduleBatchJob);
                    } else if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                        LOGGER.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB startEnd judgment son is execution complete.", scheduleBatchJob.getJobId(), getScheduleType());
                        batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob);
                    }
                } else if (EScheduleJobType.NOT_DO_TASK.getType().equals(type) || EScheduleJobType.EVENT.getType().equals(type)) {
                    checkJobStatusCanWaitCallBackOrTimeout(scheduleBatchJob, batchTask, status, checkRunInfo);
                } else {
                    if (isPutQueue(checkRunInfo, scheduleBatchJob)) {
                        // 更新job状态
                        boolean updateStatus = batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.CREATE, JobPhaseStatus.JOIN_THE_TEAM);
                        if (updateStatus) {
                            LOGGER.info("jobId:{} scheduleType:{} nodeAddress:{} JobPhaseStatus:{} update success", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, JobPhaseStatus.JOIN_THE_TEAM);
                            putScheduleJob(scheduleBatchJob);
                        }
                    }
                }
                LOGGER.info("startId is {} jobId is {} scheduleType {} isRestart {}", scheduleBatchJob.getJobExecuteOrder(), scheduleBatchJob.getJobId(), getScheduleType(), scheduleBatchJob.getIsRestart());
            } catch (Exception e) {
                LOGGER.error("jobId:{} scheduleType:{} nodeAddress:{} emitJob2Queue error:", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, e);
                Integer status = RdosTaskStatus.FAILED.getStatus();
                batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), status, ExceptionUtil.getErrorMessage(e));
            }
        }
        LOGGER.info("emitJob2Queue submit one select job cost time {} ms",System.currentTimeMillis()- startEnd);
    }

    private void checkJobStatusCanWaitCallBackOrTimeout(ScheduleBatchJob scheduleBatchJob, ScheduleTaskShade batchTask, Integer status, JobCheckRunInfo checkRunInfo) {
        EScheduleJobType taskType = EScheduleJobType.getEJobType(batchTask.getTaskType());
        //未提交状态 满足isPutQueue 则变更状态 等待回调否则不变
        if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
            if (isPutQueue(checkRunInfo, scheduleBatchJob)) {
                // 事件任务 补数据 或者外部事件提前触发了  直接成功 其他变更为运行中
                RdosTaskStatus updateStatus = RdosTaskStatus.RUNNING;
                if (EScheduleJobType.EVENT.equals(taskType)) {
                    if (EScheduleType.FILL_DATA.getType().equals(scheduleBatchJob.getScheduleType())) {
                        updateStatus = RdosTaskStatus.FINISHED;
                    }
                    ScheduleJobOperatorRecord externalEvent = scheduleJobOperatorRecordDao.getOperatorByTypeAndJobId(scheduleBatchJob.getJobId(), OperatorType.EVENT.getType());
                    if (externalEvent != null) {
                        updateStatus = RdosTaskStatus.FINISHED;
                        scheduleJobOperatorRecordDao.deleteByJobIdAndType(scheduleBatchJob.getJobId(), OperatorType.EVENT.getType());
                    }
                }
                LOGGER.info("jobId:{}  status:{} , update {}", scheduleBatchJob.getJobId(), status, updateStatus.getStatus());
                if (RdosTaskStatus.FINISHED.equals(updateStatus)) {
                    scheduleJobDao.jobFinish(scheduleBatchJob.getJobId(), RdosTaskStatus.FINISHED.getStatus());
                } else {
                    batchJobService.updateStatusByJobIdEqualsStatus(scheduleBatchJob.getJobId(), updateStatus.getStatus(), RdosTaskStatus.UNSUBMIT.getStatus());
                }
                scheduleJobGanttTimeService.ganttChartTimeRunImmediately(scheduleBatchJob.getJobId());
            }
            return;
        }

        // 已经提交状态 判断是否超时 更新状态
        if (batchJobService.isTimeOut(scheduleBatchJob.getScheduleJob(), batchTask, (timeout -> {
            String timeoutLog = "job get other system call back time out,change status to failed";
            if (EScheduleJobType.EVENT.equals(taskType)) {
                timeoutLog = String.format(GlobalConst.EVENT_FAIL, TimeUnit.MILLISECONDS.toMinutes(timeout));
            }
            batchJobService.updateLogAndFinish(scheduleBatchJob.getJobId(), timeoutLog, RdosTaskStatus.FAILED.getStatus());
        }))) {
            LOGGER.info("jobId:{} status:{} ,job timeout so update FAILED", scheduleBatchJob.getJobId(), status);
        }
    }


    private void checkJobVersion(ScheduleJob scheduleJob, ScheduleTaskShade batchTask) {
        if (null == scheduleJob || null == batchTask || null == scheduleJob.getVersionId() || null == batchTask.getVersionId()) {
            return;
        }
        //同步taskShade最新的versionId
        if (!batchTask.getVersionId().equals(scheduleJob.getVersionId())) {
            LOGGER.info("update scheduleJob jobId {} versionId from {} to {} taskId {}", scheduleJob.getJobId(), scheduleJob.getVersionId(), batchTask.getVersionId(), batchTask.getTaskId());
            scheduleJobDao.updateStatusByJobId(scheduleJob.getJobId(), null, batchTask.getVersionId(), null, null);
        }

        //同步taskShade最新的resourceId
        if (!Objects.equals(batchTask.getResourceId(), scheduleJob.getResourceId())) {
            LOGGER.info("update scheduleJob jobId {} resourceId from {} to {} taskId {}", scheduleJob.getJobId(), scheduleJob.getResourceId(), batchTask.getVersionId(), batchTask.getResourceId());
            scheduleJobDao.updateResourceIdByJobId(scheduleJob.getJobId(), batchTask.getResourceId());
        }
    }

    protected boolean isPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob) {
        Integer status;
        String errMsg = checkRunInfo.getErrMsg();
        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} put success queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.PARENT_DEPEND_TIME);
            return Boolean.TRUE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH
                || checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_SUCCESS
                || checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION
                || checkRunInfo.getStatus() == JobCheckStatus.CONSOLE_JOB_RESTRICT) {
            LOGGER.info("jobId:{}, checkRunInfo.status:{}, extInfo:{}, unable put to queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus(), checkRunInfo.getExtInfo());
            if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION
                    || checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION) {
                scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.PARENT_DEPEND_TIME);
            }
            return Boolean.FALSE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_DELETE
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_NO_CREATED
                || checkRunInfo.getStatus() == JobCheckStatus.RESOURCE_OVER_LIMIT
                || checkRunInfo.getStatus() == JobCheckStatus.REF_TASK_NOT_SUCCESS
        ) {
            status = RdosTaskStatus.FAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_CANCELED) {
            status = RdosTaskStatus.KILLED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_PAUSE
                || checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_FROZEN) {
            status = RdosTaskStatus.FROZEN.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_OVER_EXPIRE
                || JobCheckStatus.DEPENDENCY_JOB_EXPIRE.equals(checkRunInfo.getStatus())) {
            //更新为自动取消
            status = RdosTaskStatus.EXPIRE.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.NO_TASK) {
            status = RdosTaskStatus.AUTOCANCELED.getStatus();
        } else {
            LOGGER.error("appear unknown jobId:{} checkRunInfo.status:{} ", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        }
        LOGGER.info("jobId:{} checkRunInfo.status:{} errMsg:{} status:{} update status.", scheduleBatchJob.getJobId(), checkRunInfo.getStatus(), errMsg, status);
        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), status, errMsg);
        batchFlowWorkJobService.batchUpdateFlowSubJobStatus(scheduleBatchJob.getScheduleJob(), status);
        scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getJobId(), JobGanttChartEnum.PARENT_DEPEND_TIME);
        return Boolean.FALSE;
    }

    private void putScheduleJob(ScheduleBatchJob scheduleBatchJob) {
        try {
            if (scheduleJobQueue.contains(scheduleBatchJob)) {
                //元素已存在，返回true
                LOGGER.info("jobId:{} scheduleType:{} queue has contains ", scheduleBatchJob.getJobId(), getScheduleType());
                return;
            }
            scheduleJobQueue.put(scheduleBatchJob);
            LOGGER.info("jobId:{} scheduleType:{} enter queue", scheduleBatchJob.getJobId(), getScheduleType());
        } catch (InterruptedException e) {
            LOGGER.error("jobId:{} scheduleType:{} job phase rollback, error", scheduleBatchJob.getJobId(), getScheduleType(), e);
            batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
        }
    }

    protected List<ScheduleBatchJob> getScheduleBatchJobList(List<ScheduleJob> scheduleJobs) {
        List<ScheduleBatchJob> resultList = Lists.newArrayList();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey(), RelyTypeEnum.NORMAL.getType());
            scheduleBatchJob.setJobJobList(scheduleJobJobs);
            resultList.add(scheduleBatchJob);
        }

        return resultList;
    }

    /**
     * CycTimeDayGap 如果为0，取当前时间 往前退 24小时的时间范围
     */
    private Pair<String, String> getCycTime(boolean minJobId) {
        if (EScheduleType.NORMAL_SCHEDULE.getType().equals(getScheduleType().getType())) {
            return jobRichOperator.getCycTimeLimitEndNow(minJobId);
        }
        return new ImmutablePair<>(null, null);
    }

    private void start(ScheduleBatchJob scheduleBatchJob) {

        try {
            executorService.submit(new SignRunnable<ScheduleBatchJob>(scheduleBatchJob) {
                @Override
                public void run() {
                    try {
                        //提交代码里面会将jobStatus设置为submitting
                        batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                        LOGGER.info("--- jobId:{} scheduleType:{} send to engine.", scheduleBatchJob.getJobId(), getScheduleType());
                    } catch (Exception e) {
                        LOGGER.error("--- jobId:{} scheduleType:{} send to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
                        String errorMsg = null;
                        if (e instanceof RdosDefineException) {
                            errorMsg = ((RdosDefineException) e).getErrorMessage();
                        } else {
                            errorMsg = ExceptionUtil.getErrorMessage(e);
                        }
                        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errorMsg);
                        scheduleJobGanttTimeService.ganttChartTime(scheduleBatchJob.getScheduleJob().getJobId(), JobGanttChartEnum.JOB_SUBMIT_TIME);
                    } finally {
                        batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.EXECUTE_OVER);
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.info("--- jobId:{} scheduleType:{} executorService submit to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
            throw e;
        }
    }

    protected List<ScheduleJobOperatorRecord> removeOperatorRecord (List<ScheduleJobOperatorRecord> records,Integer operatorType) {
        if (CollectionUtils.isEmpty(records)) {
            return records;
        }

        List<ScheduleJobOperatorRecord>  scheduleJobOperatorRecords = Lists.newArrayList();
        List<String> needDeletedRecord = Lists.newArrayList();
        for (ScheduleJobOperatorRecord record : records) {
            try {
                Integer day = environmentContext.getKeepOperatorTimeByDay();
                if (record.getGmtCreate().toInstant()
                        .plus(environmentContext.getKeepOperatorTimeByDay(), ChronoUnit.DAYS)
                        .isAfter(Instant.now())) {
                    scheduleJobOperatorRecords.add(record);
                } else {
                    // 需要删除的操作
                    needDeletedRecord.add(record.getJobId());
                    LOGGER.info("remove schedule:[{}] operator record:[{}] type:[{}] timeout:[{}]", record.getJobId(), record.getId(), record.getOperatorType(),day);
                }
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }

        if (CollectionUtils.isNotEmpty(needDeletedRecord)) {
            scheduleJobOperatorRecordDao.deleteByJobIdsAndType(needDeletedRecord,operatorType);
            LOGGER.info("remove schedule:[{}] deleted success", needDeletedRecord);
        }
        return scheduleJobOperatorRecords;
    }

}
