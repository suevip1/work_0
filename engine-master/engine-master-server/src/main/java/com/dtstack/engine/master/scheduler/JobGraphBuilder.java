package com.dtstack.engine.master.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EProjectScheduleStatus;
import com.dtstack.dtcenter.common.enums.ESubmitStatus;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.BaseEntity;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.JobBuildStatus;
import com.dtstack.engine.api.enums.JobBuildType;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoVO;
import com.dtstack.engine.api.vo.task.TaskCustomParamVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.FillLimitException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.dao.ScheduleJobParamDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.dto.CycTimeLimitDTO;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.enums.TaskRunOrderEnum;
import com.dtstack.engine.master.enums.UpDownRelyTypeEnum;
import com.dtstack.engine.master.faced.sdk.PublicServiceNotifyApiClientSdkFaced;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.JobGraphTriggerService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ScheduleJobBuildRecordService;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.impl.TimeService;
import com.dtstack.engine.master.listener.JobGraphEvent;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronCalenderParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronDayParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronHourParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronMinParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.CronUtils;
import com.dtstack.engine.master.utils.JobExecuteOrderUtil;
import com.dtstack.engine.master.utils.JobGraphUtils;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.NotifyMethodVO;
import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.po.ScheduleJobBuildRecord;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.AlarmSendParam;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.NotifyRecordParam;
import com.dtstack.pubsvc.sdk.alert.channel.dto.UserMessageDTO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NumericNode;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 1. 变为Master节点时会主动触发一次是否构建jobgraph的判断
 * 2. 定时任务调度时触发
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class JobGraphBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilder.class);

    /**
     * 系统调度的时候插入的默认batch_job名称
     */
    public static final String CRON_JOB_NAME = "cronJob";
    public static final String FILL_DATA_TYPE = "fillData";
    public static final String MANUAL_TASK = "manualJob";
    public static final String CRON_TRIGGER_TYPE = "cronTrigger";
    private static final String NORMAL_TASK_FLOW_ID = "0";

    public static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());

    private static final int TASK_BATCH_SIZE = 50;
    private static final int JOB_BATCH_SIZE = 50;
    private static final int MAX_TASK_BUILD_THREAD = 20;

    private static String dtfFormatString = "yyyyMMddHHmmss";

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private ScheduleJobService batchJobService;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private ScheduleTaskTaskShadeService taskTaskShadeService;

    @Autowired
    private JobGraphTriggerService jobGraphTriggerService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private ScheduleEngineProjectDao engineProjectDao;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private TimeService timeService;

    @Autowired
    private ScheduleDictDao dictDao;

    @Autowired
    private UicUserApiClient userApiClient;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private JobGraphEvent jobGraphEvent;

    @Autowired
    private ScheduleJobBuildRecordService scheduleJobBuildRecordService;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Resource
    private ScheduleFillDataJobDao scheduleFillDataJobDao;

    @Resource
    private ScheduleJobParamDao scheduleJobParamDao;

    @Autowired
    private PublicServiceNotifyApiClientSdkFaced publicServiceNotifyApiClientSdkFaced;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ParamService paramService;

    private final Lock lock = new ReentrantLock();

    private final Lock fillLock = new ReentrantLock();

    /**
     * 立即生成实例
     */
    public void buildTaskJobGraphImmediately() {
        // 获取当天所有新创建还未构建实例的记录
        List<ScheduleJobBuildRecord> buildRecordList = scheduleJobBuildRecordService.getRecordNotBuildForNormalImmediately();
        if (CollectionUtils.isEmpty(buildRecordList)) {
            return;
        }

        // 所有的 record id
        List<Long> recordIdList = buildRecordList.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

        // 按照 taskId + appType 分组
        Map<String, List<ScheduleJobBuildRecord>> recordGroup = buildRecordList.stream()
                .collect(Collectors.groupingBy(record -> record.getTaskId() + "_" + record.getAppType()));

        // 需要进行生成实例的记录集合, 分组后取最大值
        List<ScheduleJobBuildRecord> needBuildRecordList = recordGroup.values().stream()
                .map(recordList ->
                        recordList.stream()
                                .max(Comparator.comparingLong(record -> record.getGmtCreate().getTime()))
                                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 需要进行生成实例的记录 id 集合
        List<Long> needBuildRecordIdList = needBuildRecordList.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

        // 总 id remove 掉需要 build 的 id 得到需要自动取消的 id 集合
        recordIdList.removeAll(needBuildRecordIdList);

        if (CollectionUtils.isNotEmpty(recordIdList)) {
            // 更新状态为自动取消
            LOGGER.info("job build duplicate record ID, auto cancel : {}", recordIdList);
            scheduleJobBuildRecordService.updateJobBuildStatusAndLogByIdList(recordIdList, JobBuildStatus.AUTO_CANCELED.getStatus(), "任务重复, 当前变更为自动取消");
        }

        if (CollectionUtils.isEmpty(needBuildRecordIdList)) {
            LOGGER.info("record for need build is empty.");
            return;
        }

        LOGGER.info("record for need build ID : {}", needBuildRecordIdList);

        // 更新状态为构建中
        scheduleJobBuildRecordService.updateJobBuildStatusByIdList(needBuildRecordIdList, JobBuildStatus.RUNNING.getStatus());

        // 按照 app type 分组
        Map<Integer, List<ScheduleJobBuildRecord>> recordGroupByAppType = needBuildRecordList
                .stream()
                .collect(Collectors.groupingBy(ScheduleJobBuildRecord::getAppType));

        // 需要立即生成实例的所有任务
        List<ScheduleTaskShade> scheduleTaskShades = recordGroupByAppType.entrySet()
                .stream()
                .flatMap(entry -> {
                    // task id 集合
                    List<Long> taskIdList = entry.getValue()
                            .stream()
                            .map(ScheduleJobBuildRecord::getTaskId)
                            .collect(Collectors.toList());
                    return batchTaskShadeService.findTaskIds(taskIdList, Deleted.NORMAL.getStatus(), entry.getKey(), false).stream();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(scheduleTaskShades)) {
            scheduleJobBuildRecordService.updateJobBuildStatusAndLogByIdList(needBuildRecordIdList, JobBuildStatus.FAILED.getStatus(), "找不到对应的任务");
            return;
        }
        // 如果所有任务对应的租户已经解绑集群，则立即生成实例失败
        Map<Long, Long> tenantBindCluster = findTenantBindCluster(scheduleTaskShades);
        if (MapUtils.isEmpty(tenantBindCluster)) {
            scheduleJobBuildRecordService.updateJobBuildStatusAndLogByIdList(needBuildRecordIdList, JobBuildStatus.FAILED.getStatus(), "任务对应的租户已经解绑集群，立即生成实例失败");
            return;
        }
        // 过滤掉已经解绑集群的任务，防止后续将原有的实例关系删除
        scheduleTaskShades.removeIf(s -> {
            boolean notBindCluster = (tenantBindCluster.get(s.getDtuicTenantId()) == null);
            if (notBindCluster) {
                // only log
                LOGGER.info("buildTaskJobGraphImmediately, task [{}] appType [{}] projectId [{}] not bind cluster so skip", s.getTaskId(), s.getAppType(), s.getProjectId());
            }
            return notBindCluster;
        });

        // 需要立即生成实例的 task id 和对应实例的一级子 job(这一层 job 需要重新构建依赖关系)
        Map<Long, List<ScheduleJob>> idScheduleJobMap = Maps.newHashMap();

        // 只重新生成执行时间在 delayMinute 后的实例, 在 (now ~ now + delayMinute) 范围的实例不重新生成
        Integer delayMinute = environmentContext.getJobBuildImmediatelyDelayMinute();
        CycTimeLimitDTO cycTimeLimitDTO = jobRichOperator.getCycTimeNowToNextDay(delayMinute);

        // 当前时间后推 15 分钟后如果不在当天了，就不予生成实例
        if (BooleanUtils.isFalse(cycTimeLimitDTO.isToday())) {
            LOGGER.warn("delay {} minute is not today, not run.", delayMinute);
            scheduleJobBuildRecordService.updateJobBuildStatusAndLogByIdList(needBuildRecordIdList, JobBuildStatus.FAILED.getStatus(), "当天实例可生成");
            return;
        }

        List<ScheduleTaskShade> scheduleTaskShadeList = Lists.newArrayList();
        // 删除旧关系
        createJob(needBuildRecordIdList, scheduleTaskShades, idScheduleJobMap, cycTimeLimitDTO, scheduleTaskShadeList);
    }

    @Transactional(rollbackFor = Exception.class)
    private void createJob(List<Long> needBuildRecordIdList, List<ScheduleTaskShade> scheduleTaskShades, Map<Long, List<ScheduleJob>> idScheduleJobMap, CycTimeLimitDTO cycTimeLimitDTO, List<ScheduleTaskShade> scheduleTaskShadeList) {
        for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShades) {
            if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleTaskShade.getTaskType())) {
                List<ScheduleTaskShade> tasks = batchTaskShadeService.getFlowWorkSubTasks(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType(), null, null);
                for (ScheduleTaskShade task : tasks) {
                    deleteWaitJob(idScheduleJobMap, cycTimeLimitDTO, task);
                    scheduleTaskShadeList.add(task);
                }
            }

            deleteWaitJob(idScheduleJobMap, cycTimeLimitDTO, scheduleTaskShade);
            scheduleTaskShadeList.add(scheduleTaskShade);
        }

        try {
            // 构建实例
            buildJobGraph(cycTimeLimitDTO.getTriggerDay(), scheduleTaskShadeList, cycTimeLimitDTO.getStartTimeHourMinute(), cycTimeLimitDTO.getEndTimeHourMinute(), JobBuildType.IMMEDIATELY);
            // 重新映射 job job 关系
            for (Map.Entry<Long, List<ScheduleJob>> entry : idScheduleJobMap.entrySet()) {
                for (ScheduleJob scheduleJob : entry.getValue()) {
                    ScheduleTaskShade taskShade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
                    if (Objects.isNull(taskShade)) {
                        continue;
                    }
                    String scheduleStr = taskShade.getScheduleConf();
                    ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr, timeService, taskShade.getTaskId(), taskShade.getAppType());
                    ScheduleTaskTaskShade taskTaskShade = taskTaskShadeService.getOneByTaskId(scheduleJob.getTaskId(), entry.getKey(), taskShade.getAppType());
                    if (Objects.isNull(taskTaskShade)) {
                        continue;
                    }
                    // 下游任务新生成的依赖
                    List<FatherDependency> fatherDependencies = getExternalJobKeys(Lists.newArrayList(taskTaskShade), scheduleJob, scheduleCron, CRON_TRIGGER_TYPE);
                    if (CollectionUtils.isEmpty(fatherDependencies)) {
                        continue;
                    }
                    Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.now());
                    List<ScheduleJobJob> scheduleJobJobs = Lists.newArrayList();
                    for (FatherDependency dependencyJobKey : fatherDependencies) {
                        scheduleJobJobs.add(createNewJobJob(scheduleJob, scheduleJob.getJobKey(), dependencyJobKey.getJobKey(), timestampNow, dependencyJobKey.getAppType()));
                    }
                    // 批量插入新依赖关系
                    scheduleJobJobService.batchInsert(scheduleJobJobs);
                }
            }
            scheduleJobBuildRecordService.updateJobBuildStatusByIdList(needBuildRecordIdList, JobBuildStatus.FINISHED.getStatus());
        } catch (Throwable e) {
            LOGGER.error("build job graph error, record id : {}", needBuildRecordIdList, e);
            scheduleJobBuildRecordService.updateJobBuildStatusAndLogByIdList(needBuildRecordIdList, JobBuildStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
        }
    }

    private void deleteWaitJob(Map<Long, List<ScheduleJob>> idScheduleJobMap, CycTimeLimitDTO cycTimeLimitDTO, ScheduleTaskShade scheduleTaskShade) {
        // 等待删除的 job
        List<ScheduleJob> jobRangeByCycTime = batchJobService.getJobRangeByCycTime(scheduleTaskShade.getTaskId(), cycTimeLimitDTO.getStartTime(), cycTimeLimitDTO.getEndTime(), scheduleTaskShade.getAppType());
        if (CollectionUtils.isEmpty(jobRangeByCycTime)) {
            return;
        }
        List<String> jobKeyList = jobRangeByCycTime.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        batchJobService.deleteJobsByJobKey(jobKeyList);

        // 当前实例下的所有子 job, 过滤掉自依赖
        List<ScheduleJob> sonJob = batchJobService.getSonJob(jobKeyList).stream().filter(job -> !jobKeyList.contains(job.getJobKey())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonJob)) {
            return;
        }
        // 删除实例下游依赖
        scheduleJobJobService.deleteJobJobsByParentJobKey(jobKeyList);
        idScheduleJobMap.put(scheduleTaskShade.getTaskId(), sonJob);
    }

    /**
     * 1：如果当前节点是master-->每天晚上10点预先生成第二天的任务依赖;
     * 2：如果初始化master节点-->获取当天的jobgraph为null-->生成
     * 可能多线程调用
     *
     * @param triggerDay yyyy-MM-dd
     * @return
     */
    public void buildTaskJobGraph(String triggerDay) {

        if (environmentContext.getJobGraphBuilderSwitch()) {
            return;
        }

        lock.lock();

        //检查是否已经生成过
        String triggerTimeStr = triggerDay + " 00:00:00";
        Timestamp triggerTime = null;
        try {
            triggerTime = Timestamp.valueOf(triggerTimeStr);
            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);
            if (hasBuild) {
                LOGGER.info("trigger Day {} has build so break", triggerDay);
                return;
            }

            //清理周期实例脏数据
            cleanDirtyJobGraph(triggerDay);

            // 生成 job graph
            buildJobGraph(triggerDay, null, null, null, JobBuildType.THE_NEXT_DAY);

            //存储生成的jobRunBean
            jobGraphBuilder.saveJobGraph(triggerDay);
        } catch (Exception e) {
            LOGGER.error("buildTaskJobGraph ！！！", e);
            sendJobGraphExceptionMsg(triggerTime);
        } finally {
            LOGGER.info("buildTaskJobGraph exit & unlock ...");
            lock.unlock();
        }
    }

    private void sendJobGraphExceptionMsg(Timestamp triggerTime) {
        if (null == triggerTime) {
            return;
        }
        try {
            ZonedDateTime today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime current = ZonedDateTime.ofInstant(triggerTime.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
            if (today.equals(current)) {
                List<ScheduleDict> dicts = dictDao.listDictByType(DictType.ALERT_CONFIG.type);
                if (!CollectionUtils.isEmpty(dicts)) {
                    AlertConfigVO vo = JSONObject.parseObject(dicts.get(0).getDictValue(), AlertConfigVO.class);
                    if (vo.getEnabled() != null && vo.getEnabled()) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        exceptionNotify(vo, format.format(new Date()) + "调度系统周期实例生成异常，请及时处理！");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("sendJobGraphExceptionMsg Error", e);
        }
    }

    /**
     * 构建 job graph
     *
     * @param triggerDay         触发日期
     * @param scheduleTaskShades 实例集合, 为空则获取全部
     * @param beginTime          开始时间
     * @param endTime            结束时间
     * @param jobBuildType       实例构建方式
     * @throws InterruptedException 异常信息
     */
    public void buildJobGraph(String triggerDay, List<ScheduleTaskShade> scheduleTaskShades,
                              String beginTime, String endTime, JobBuildType jobBuildType) throws InterruptedException {

        Map<Integer, Set<Long>> projectAppMapping = getProjectWhiteListMapping();
        // TODO 立即生成实例暂时不考虑白名单
        if (environmentContext.getJobGraphWhiteList() && MapUtils.isEmpty(projectAppMapping) && JobBuildType.THE_NEXT_DAY.equals(jobBuildType)) {
            return;
        }

        int totalTask;
        if (JobBuildType.IMMEDIATELY.equals(jobBuildType)) {
            totalTask = CollectionUtils.isEmpty(scheduleTaskShades) ? 0 : scheduleTaskShades.size();
        } else {
            totalTask = batchTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), null, null, ETaskGroupEnum.NORMAL_SCHEDULE.getType());
        }

        LOGGER.info("Counting task which status=SUBMIT scheduleStatus=NORMAL totalTask:{}", totalTask);
        if (totalTask <= 0) {
            return;
        }

        // 异步执行线程池
        ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory("JobGraphBuilder"));

        // 工作流相关
        Map<String, List<String>> allFlowJobs = Maps.newHashMap();
        Map<String, String> flowJobId = new ConcurrentHashMap<>(totalTask);

        // count + 批次序号
        AtomicInteger count = new AtomicInteger();
        AtomicInteger batchIdx = new AtomicInteger();

        // 总批次
        int totalBatch = totalTask % TASK_BATCH_SIZE != 0 ?
                totalTask / TASK_BATCH_SIZE + 1 : totalTask / TASK_BATCH_SIZE;

        // 并发控制
        CountDownLatch ctl = new CountDownLatch(totalBatch);
        Semaphore buildSemaphore = new Semaphore(MAX_TASK_BUILD_THREAD);

        if (JobBuildType.IMMEDIATELY.equals(jobBuildType)) {
            // 立即生成实例
            for (List<ScheduleTaskShade> partitionTaskShade : Lists.partition(scheduleTaskShades, MAX_TASK_BUILD_THREAD)) {
                int batchIdxFinal = batchIdx.incrementAndGet();
                asyncBuildJobGraph(jobGraphBuildPool, allFlowJobs, partitionTaskShade,
                        projectAppMapping, triggerDay, beginTime, endTime, count, jobBuildType,
                        flowJobId, batchIdxFinal, buildSemaphore, ctl);
            }
        } else {
            // T+1 生成实例
            long startId = 0L;
            while (true) {
                final List<ScheduleTaskShade> batchTaskShades;
                int batchIdxFinal = batchIdx.incrementAndGet();
                if (batchIdxFinal > totalBatch) {
                    break;
                }
                batchTaskShades = batchTaskShadeService.listTaskByStatus(startId, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), TASK_BATCH_SIZE, null, null,ETaskGroupEnum.NORMAL_SCHEDULE.getType());
                if (batchTaskShades.isEmpty()) {
                    break;
                }
                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                LOGGER.info("batch-number:{} startId:{}", batchIdxFinal, startId);
                asyncBuildJobGraph(jobGraphBuildPool, allFlowJobs, batchTaskShades,
                        projectAppMapping, triggerDay, beginTime, endTime, count, jobBuildType,
                        flowJobId, batchIdxFinal, buildSemaphore, ctl);
            }
        }

        // 释放批次锁
        ctl.await();
        LOGGER.info("buildTaskJobGraph all done!!! allJobs size:{}", allFlowJobs.size());
        // 关闭实例生成线程池
        jobGraphBuildPool.shutdown();

        // 更新未填充的工作流
        for (Map.Entry<String, List<String>> listEntry : allFlowJobs.entrySet()) {
            String placeholder = listEntry.getKey();
            String flowJob = flowJobId.get(placeholder);
            if (StringUtils.isNotBlank(flowJob)) {
                batchJobService.updateFlowJob(placeholder,flowJob);
            } else {
                batchJobService.updateFlowJob(placeholder,NORMAL_TASK_FLOW_ID);
            }
        }
    }

    private void asyncBuildJobGraph(ExecutorService jobGraphBuildPool, Map<String, List<String>> allFlowJobs,
                                    List<ScheduleTaskShade> batchTaskShades, Map<Integer, Set<Long>> projectAppMapping,
                                    String triggerDay, String beginTime, String endTime, AtomicInteger count,
                                    JobBuildType jobBuildType, Map<String, String> flowJobId, int batchIdxFinal,
                                    Semaphore buildSemaphore, CountDownLatch ctl) {
        try {
            buildSemaphore.acquire();
            jobGraphBuildPool.execute(() ->
                    buildScheduleJob(
                            allFlowJobs, batchTaskShades, projectAppMapping, triggerDay,
                            beginTime, endTime, count, jobBuildType, flowJobId, batchIdxFinal,
                            buildSemaphore, ctl));
        } catch (Throwable e) {
            LOGGER.error("[acquire pool error]:", e);
            throw new RdosDefineException(e);
        }
    }

    public void buildScheduleJob(Map<String, List<String>> allFlowJobs, List<ScheduleTaskShade> batchTaskShades,
                      Map<Integer, Set<Long>> projectAppMapping, String triggerDay, String beginTime,
                      String endTime, AtomicInteger count, JobBuildType jobBuildType, Map<String, String> flowJobId,
                      Integer batchIdx, Semaphore buildSemaphore, CountDownLatch ctl) {
        try {
            if (CollectionUtils.isEmpty(batchTaskShades)) {
                return;
            }

            Map<Long, Long> tenantBindCluster = findTenantBindCluster(batchTaskShades);

            List<ScheduleBatchJob> allJobs = Lists.newArrayList();
            for (ScheduleTaskShade task : batchTaskShades) {
                try {
                    if (environmentContext.getJobGraphWhiteList() && !isInWhiteProject(task, projectAppMapping) && JobBuildType.THE_NEXT_DAY.equals(jobBuildType)) {
                        LOGGER.info("task [{}] appType [{}] not in white project so skip", task.getTaskId(), task.getAppType());
                        continue;
                    }
                    if (EScheduleJobType.GROUP.getType().equals(task.getTaskType())) {
                        LOGGER.info("task [{}] appType [{}] is group so skip", task.getTaskId(), task.getAppType());
                        continue;
                    }
                    // 集群被解绑之后无法生成实例
                    if (tenantBindCluster.get(task.getDtuicTenantId()) == null) {
                        LOGGER.info("task [{}] appType [{}] projectId [{}] not bind cluster so skip", task.getTaskId(), task.getAppType(), task.getProjectId());
                        continue;
                    }

                    List<ScheduleBatchJob> jobRunBeans = RetryUtil.executeWithRetry(() -> {
                        String cronJobName = CRON_JOB_NAME + "_" + task.getName();
                        return buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                                true, true, triggerDay, cronJobName,
                                null, beginTime, endTime,
                                task.getProjectId(), task.getTenantId(), count, false, jobBuildType);
                    }, environmentContext.getBuildJobErrorRetry(), 200, false);

                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                        for (ScheduleBatchJob jobRunBean : jobRunBeans) {
                            flowJobId.put(this.buildFlowReplaceId(task.getTaskId(), jobRunBean.getCycTime(), task.getAppType()), jobRunBean.getJobId());
                        }
                    }
                    allJobs.addAll(jobRunBeans);
                } catch (Throwable e) {
                    LOGGER.error("build task failure taskId:{} app type:{}", task.getTaskId(), task.getAppType(), e);
                }
            }
            LOGGER.info("batch-number:{} done!!! allJobs size:{}", batchIdx, allJobs.size());

                            // 填充工作流任务
                            for (ScheduleBatchJob job : allJobs) {
                                String flowIdKey = job.getScheduleJob().getFlowJobId();
                                if (!NORMAL_TASK_FLOW_ID.equals(flowIdKey)) {
                                    // 说明是工作流子任务
                                    String flowId = flowJobId.get(flowIdKey);
                                    if (StringUtils.isBlank(flowId)) {
                                        // 后面更新
                                        synchronized (allFlowJobs) {
                                            List<String> jodIds = allFlowJobs.get(flowIdKey);
                                            if (CollectionUtils.isEmpty(jodIds)) {
                                                jodIds = Lists.newArrayList();
                                            }
                                            jodIds.add(job.getJobId());
                                            allFlowJobs.put(flowIdKey, jodIds);
                                        }
                                    } else {
                                        job.getScheduleJob().setFlowJobId(flowId);
                                    }
                                }
                            }

                            // 插入周期实例
                            batchJobService.insertJobList(allJobs, EScheduleType.NORMAL_SCHEDULE.getType());
                            LOGGER.info("batch-number:{} done!!! allFlowJobs size:{}", batchIdx, allFlowJobs.size());
                        } catch (Throwable e) {
                            LOGGER.error("!!! buildTaskJobGraph  build job error !!!", e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();

        }
    }

    /**
     * 获取任务对应租户绑定的集群
     * @param batchTaskShades
     * @return
     */
    private Map<Long, Long> findTenantBindCluster(List<ScheduleTaskShade> batchTaskShades) {
        Set<Long> dtUicTenantIds = batchTaskShades.stream().map(ScheduleTaskShade::getDtuicTenantId).collect(Collectors.toSet());
        Map<Long, Long> uicTenantIdWithClusterId = clusterTenantDao.listByDtuicTenantId(Lists.newArrayList(dtUicTenantIds)).stream()
                .collect(Collectors.toMap(ClusterTenant::getDtUicTenantId, ClusterTenant::getClusterId, (v1, v2) -> v2));
        return uicTenantIdWithClusterId;
    }

    public void exceptionNotify(AlertConfigVO vo, String content) {
        if (CollectionUtils.isEmpty(vo.getMethods())) {
            LOGGER.warn("sendNotify failed because alterGateSources is empty");
            return;
        }
        List<UserMessageDTO> receivers = new ArrayList<>();

        ApiResponse<List<UICUserVO>> receiverUsersResponse = userApiClient.getByUserIds(vo.getReceivers());
        if (receiverUsersResponse.getSuccess()) {
            for (UICUserVO uicUser : receiverUsersResponse.getData()) {
                UserMessageDTO userDTO = new UserMessageDTO();
                userDTO.setEmail(uicUser.getEmail());
                userDTO.setTelephone(uicUser.getPhone());
                userDTO.setUserId(uicUser.getUserId());
                userDTO.setUsername(uicUser.getUserName());
                receivers.add(userDTO);
            }
        }

        // 调用业务中心接口保存告警内容
        Long contentId = saveAlarmContent(content);

        // 调用业务中心接口发送告警
        sendAlarm(vo, content, contentId, receivers);
    }

    private void sendAlarm(AlertConfigVO vo, String content, Long contentId, List<UserMessageDTO> receivers) {
        List<String> alertGateSources = vo.getMethods().stream()
                .map(NotifyMethodVO::getAlertGateSource)
                .collect(Collectors.toList());
        AlarmSendParam alarmSendParam = new AlarmSendParam();
        alarmSendParam.setProjectId(-1L);
        alarmSendParam.setTenantId(-1L);
        alarmSendParam.setStatus(0);
        alarmSendParam.setReadId(0L);
        alarmSendParam.setContent(content);
        alarmSendParam.setReceivers(receivers);
        alarmSendParam.setTitle("周期实例生成异常");
        alarmSendParam.setAppType(AppType.CONSOLE.getType());
        alarmSendParam.setAlertGateSources(alertGateSources);
        alarmSendParam.setWebhook(vo.getWebhook());

        Optional.ofNullable(contentId).ifPresent(alarmSendParam::setContentId);

        try {
            publicServiceNotifyApiClientSdkFaced.sendAlarmNew(alarmSendParam);
        } catch (Exception ignore) {}

    }

    private Long saveAlarmContent(String content) {
        NotifyRecordParam notifyRecordParam = new NotifyRecordParam();
        notifyRecordParam.setAppType(AppType.CONSOLE.getType());
        notifyRecordParam.setContent(content);
        notifyRecordParam.setTenantId(-1L);
        notifyRecordParam.setStatus(0);

        try {
            return publicServiceNotifyApiClientSdkFaced.generateContent(notifyRecordParam);
        } catch (Exception ignore) {
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void buildFillDataJobGraph(String fillName, Long fillId, Set<String> all, Set<String> run,
                                      List<String> blackTaskKey, String triggerDay, String beginTime,
                                      String endTime, Long projectId, Long tenantId, Long dtuicTenantId,
                                      Long userId, Boolean ignoreCycTime,EScheduleType scheduleType,
                                      Integer taskRunOrder,Integer closeRetry,AtomicInteger totalCount) throws Exception {

        ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory("manualOperation" + fillId));

        // 切割控制并发数
        List<String> allList = Lists.newArrayList(all);
        List<List<String>> partition = Lists.partition(allList, TASK_BATCH_SIZE);
        Semaphore buildSemaphore = new Semaphore(MAX_TASK_BUILD_THREAD);
        CountDownLatch ctl = new CountDownLatch(partition.size());
        AtomicInteger count = new AtomicInteger();
        FillFail fillFail = new FillFail(false);

        for (List<String> taskKey : partition) {
            buildSemaphore.acquire();
            jobGraphBuildPool.submit(() -> {
                try {
                    if (!fillFail.get()) {
                        // 判断补数据是否失败
                        Map<String, ScheduleBatchJob> saveMap = Maps.newHashMap();
                        for (String key : taskKey) {
                            try {
                                String[] split = key.split("-");
                                Long taskId = Long.parseLong(split[0]);
                                Integer appType = Integer.parseInt(split[1]);
                                String preStr = EScheduleType.MANUAL.equals(scheduleType) ? MANUAL_TASK : FILL_DATA_TYPE;
                                preStr = preStr + "_" + fillName;

                                ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(taskId, appType);

                                if (batchTask != null) {
                                    // 查询绑定任务
                                    List<ScheduleTaskShade> childTaskRuleByTask = batchTaskShadeService.findChildTaskRuleByTaskId(taskId, appType);

                                    List<ScheduleTaskShade> createList = Lists.newArrayList();
                                    for (ScheduleTaskShade scheduleTaskShade : childTaskRuleByTask) {
                                        if (!allList.contains(scheduleTaskShade.getTaskId() + "-" + scheduleTaskShade.getAppType())) {
                                            createList.addAll(childTaskRuleByTask);
                                        }
                                    }
                                    createList.add(batchTask);
                                    for (ScheduleTaskShade scheduleTaskShade : createList) {
                                        updateScheduleConf(scheduleTaskShade,taskRunOrder,closeRetry);
                                        List<ScheduleBatchJob> batchJobs = buildJobGraphByTaskShade(fillName, triggerDay, beginTime, endTime, projectId, tenantId, userId, allList, count,
                                                appType, preStr, batchTask, scheduleTaskShade,scheduleType,taskRunOrder,closeRetry);
                                        for (ScheduleBatchJob batchJob : batchJobs) {
                                            addMap(fillId, run, blackTaskKey, dtuicTenantId, ignoreCycTime, saveMap, key, appType, batchJob);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("fill id {} taskKey : {} error:", fillId, key, e);
                                throw new RdosDefineException(e);
                            }
                        }


                        try {
                            fillLock.lock();
                            if (totalCount.addAndGet(saveMap.values().size()) > environmentContext.getFillDataCreateLimitSize()) {
                                fillFail.set(true);
                                String format = String.format(FillGeneratStatusEnum.FILL_FAIL_LIMIT.getName()
                                        , environmentContext.getFillDataCreateLimitSize());
                                fillFail.setException(new FillLimitException(format));
                                fillFail.setReason(format);
                                return;
                            }
                        } finally {
                            fillLock.unlock();
                        }
                        savaFillJob(saveMap,scheduleType,fillId);
                    }
                } catch (Exception e) {
                    LOGGER.error("fill error:",e);
                    fillFail.set(true);
                    fillFail.setReason(e.getMessage());
                } finally {
                    buildSemaphore.release();
                    ctl.countDown();
                }
            });
        }
        ctl.await();
        jobGraphBuildPool.shutdown();

        if (fillFail.get()) {
            Exception exception = fillFail.getException();
            if (exception != null) {
                throw exception;
            } else {
                // 补数据实例有部分或者全部失败了，这次补数据标记失败
                throw new RdosDefineException(fillFail.reason);
            }

        }
    }

    private void updateScheduleConf(ScheduleTaskShade scheduleTaskShade, Integer taskRunOrder, Integer closeRetry) {
        String scheduleConf = scheduleTaskShade.getScheduleConf();
        JSONObject jsonObject = JSONObject.parseObject(scheduleConf);
        try {
            if (CloseRetryEnum.CLOSE.getType().equals(closeRetry)) {
                jsonObject.put("isFailRetry",false);
                jsonObject.put("maxRetryNum",0);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        try {
            Integer selfReliance = jsonObject.getInteger("selfReliance");
            if (TaskRunOrderEnum.AES.getType().equals(taskRunOrder)
                    && !DependencyType.SELF_DEPENDENCY_SUCCESS.getType().equals(selfReliance)
                    && !DependencyType.SELF_DEPENDENCY_END.getType().equals(selfReliance)
                    && !TaskRuleEnum.STRONG_RULE.getCode().equals(scheduleTaskShade.getTaskRule())) {
                jsonObject.put("selfReliance",DependencyType.SELF_DEPENDENCY_END.getType());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        scheduleTaskShade.setScheduleConf(jsonObject.toJSONString());
    }

    private List<ScheduleBatchJob> buildJobGraphByTaskShade(String fillName, String triggerDay, String beginTime, String endTime, Long projectId, Long tenantId,
                                                            Long userId, List<String> allList, AtomicInteger count, Integer appType,
                                                            String preStr, ScheduleTaskShade batchTask, ScheduleTaskShade scheduleTaskShade,EScheduleType scheduleType,
                                                            Integer taskRunOrder,Integer closeRetry) throws Exception {
        if (ETaskGroupEnum.MANUAL.getType().equals(scheduleTaskShade.getTaskGroup())) {
            scheduleTaskShade.setScheduleConf(ScheduleConfUtils.buildManualTaskScheduleConf(scheduleTaskShade.getScheduleConf()));
        }
        List<ScheduleBatchJob> batchJobs = new ArrayList<>();
        if (scheduleTaskShade.getFlowId() == 0) {
            // 查询出该任务的周期实例
            updateScheduleConf(scheduleTaskShade,taskRunOrder,closeRetry);
            batchJobs = buildJobRunBean(scheduleTaskShade, preStr, scheduleType, true,
                    true, triggerDay, fillName, userId, beginTime, endTime, projectId, tenantId, count);

            //工作流情况的处理
            if (scheduleTaskShade.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() ||
                    scheduleTaskShade.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
                Map<String, String> flowJobId = Maps.newHashMap();
                for (ScheduleBatchJob jobRunBean : batchJobs) {
                    flowJobId.put(scheduleTaskShade.getTaskId() + "_" + jobRunBean.getCycTime() + "_" + batchTask.getAppType(), jobRunBean.getJobId());
                }
                //将工作流下的子任务生成补数据任务实例
                List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(),
                        preStr, fillName, triggerDay, userId, beginTime, endTime, projectId, tenantId, appType,
                        taskRunOrder,closeRetry);
                LOGGER.error("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
                doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
                batchJobs.addAll(subTaskJobs);
            }
        } else {
            Long flowId = scheduleTaskShade.getFlowId();
            String flowKey = flowId + "-" + appType;
            if (!allList.contains(flowKey)) {
                // 生成周期实例
                updateScheduleConf(scheduleTaskShade,taskRunOrder,closeRetry);
                batchJobs = buildJobRunBean(scheduleTaskShade, preStr, scheduleType, true,
                        true, triggerDay, fillName, userId, beginTime, endTime, projectId, tenantId, count);

                                                if (CollectionUtils.isNotEmpty(batchJobs)) {
                                                    for (ScheduleBatchJob batchJob : batchJobs) {
                                                        if (batchJob.getScheduleJob() != null) {
                                                            batchJob.getScheduleJob().setFlowJobId(NORMAL_TASK_FLOW_ID);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        return batchJobs;
    }

    public static class FillFail {
        /**
         * 补数据状态
         */
        private final AtomicBoolean fillStatus;

        /**
         * 报错后异常信息，如果异常信息是 null 的，就抛出 RdosDefineException
         */
        private Exception exception;

        /**
         * 补数据原因
         */
        private String reason;

        public FillFail(Boolean fillStatus) {
            this.fillStatus = new AtomicBoolean(fillStatus);
            this.reason = "";
        }

        public Boolean get() {
            return fillStatus.get();
        }

        public void set(Boolean fillStatus) {
            this.fillStatus.set(fillStatus);
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }

    private void savaFillJob(Map<String, ScheduleBatchJob> allJob,EScheduleType scheduleType,Long fillId) {
        if (MapUtils.isEmpty(allJob)) {
            return;
        }
        // 处理自定义参数
        dealTaskCustomParam(fillId, allJob);
        batchJobService.insertJobList(allJob.values(), scheduleType.getType());
        List<ScheduleJobOperatorRecord> operatorJobIds = allJob.values()
                .stream()
                .map(scheduleBatchJob -> {
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(scheduleBatchJob.getJobId());
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    if (EScheduleType.MANUAL.equals(scheduleType)) {
                        record.setOperatorType(OperatorType.MANUAL.getType());
                    } else {
                        record.setOperatorType(OperatorType.FILL_DATA.getType());
                    }
                    record.setNodeAddress(scheduleBatchJob.getScheduleJob().getNodeAddress());
                    return record;
                })
                .collect(Collectors.toList());

        scheduleJobOperatorRecordDao.insertBatch(operatorJobIds);
    }

    /**
     * 插入补数据时的自定义运行参数
     *
     * @param fillId 补数据 id
     * @param allJob 生成的所有实例
     */
    private void dealTaskCustomParam(Long fillId, Map<String, ScheduleBatchJob> allJob) {
        // 先插入自定义配置, 目前只有按任务补数据有该参数设置, 且有自定义任务参数、工作流参数、全局参数、项目参数
        ScheduleFillDataJob fillDataJob = scheduleFillDataJobDao.getById(fillId);
        ScheduleFillDataInfoVO scheduleFillDataInfoVO = JSON.parseObject(fillDataJob.getFillDataInfo(), ScheduleFillDataInfoVO.class);
        if (FillDataTypeEnum.supportTaskCustomParam(scheduleFillDataInfoVO.getFillDataType())) {
            List<TaskCustomParamVO> taskCustomParamVOS = scheduleFillDataInfoVO.getTaskCustomParamVOList();
            if (CollectionUtils.isNotEmpty(taskCustomParamVOS)) {
                Map<Long, TaskCustomParamVO> taskIdParamMap = taskCustomParamVOS.stream()
                        .filter(task -> Objects.nonNull(task) && Objects.nonNull(task.getTaskId()))
                        .collect(
                                Collectors.toMap(
                                        TaskCustomParamVO::getTaskId,
                                        v -> v,
                                        (v1, v2) -> v2));
                if (MapUtils.isEmpty(taskIdParamMap)) {
                    return;
                }
                Map<String,Integer> globalParamCache = new HashMap<>();
                List<ScheduleJobParam> scheduleJobParamList = allJob.values().stream()
                        .map(scheduleBatchJob -> {
                            Long taskId = scheduleBatchJob.getTaskId();
                            ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
                            TaskCustomParamVO customParamVO = taskIdParamMap.get(taskId);
                            if (Objects.isNull(customParamVO)) {
                                return null;
                            }
                            // 替换自定义参数
                            String taskParamsToReplace = customParamVO.getTaskParamsToReplace();
                            if (StringUtils.isBlank(taskParamsToReplace)) {
                                return null;
                            }
                            // 先删除再插入
                            scheduleJobParamDao.deleteByJobId(scheduleJob.getJobId());
                            List<ScheduleTaskParamShade> ScheduleTaskParamShades = JSONObject.parseArray(taskParamsToReplace, ScheduleTaskParamShade.class);
                            return ScheduleTaskParamShades.stream().map(scheduleTaskParamShade -> {
                                ScheduleJobParam scheduleJobParam = new ScheduleJobParam();
                                scheduleJobParam.setJobId(scheduleJob.getJobId());
                                scheduleJobParam.setParamName(scheduleTaskParamShade.getParamName());
                                //补数据 全局参数  需要根据控制台参数细化type 否则会替换为空
                                if (EParamType.GLOBAL.getType().equals(scheduleTaskParamShade.getType())) {
                                    Integer convertParamType = globalParamCache.computeIfAbsent(scheduleTaskParamShade.getParamName(), (name) -> {
                                        ConsoleParam consoleParam = paramService.selectByName(name);
                                        if (null != consoleParam) {
                                            globalParamCache.put(scheduleTaskParamShade.getParamName(), consoleParam.getParamType());
                                            return consoleParam.getParamType();
                                        }
                                        return -1;
                                    });
                                    if (convertParamType >= 0) {
                                        scheduleJobParam.setParamType(convertParamType);
                                    } else {
                                        return null;
                                    }
                                } else {
                                    scheduleJobParam.setParamType(scheduleTaskParamShade.getType());
                                }
                                scheduleJobParam.setParamValue(scheduleTaskParamShade.getParamCommand());
                                return scheduleJobParam;
                            }).filter(Objects::nonNull).collect(Collectors.toList());
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(scheduleJobParamList)) {
                    scheduleJobParamDao.insertBatch(scheduleJobParamList);
                }
            }
        }
    }

    private void addMap(Long fillId, Set<String> runList, List<String> blackTaskKey, Long dtuicTenantId, Boolean ignoreCycTime, Map<String, ScheduleBatchJob> allJob, String key, Integer appType, ScheduleBatchJob batchJob) {
        ScheduleJob scheduleJob = batchJob.getScheduleJob();
        scheduleJob.setFillId(fillId);
        scheduleJob.setDtuicTenantId(dtuicTenantId);

        if (BooleanUtils.isTrue(ignoreCycTime)) {
            scheduleJob.setCycTime(DateTime.now().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
        }

        if (CollectionUtils.isNotEmpty(batchJob.getBatchJobJobList())) {
            for (ScheduleJobJob scheduleJobJob : batchJob.getBatchJobJobList()) {
                scheduleJobJob.setDtuicTenantId(dtuicTenantId);
                scheduleJobJob.setAppType(appType);
            }
        }

        if (runList.contains(key)) {
            scheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
        } else if (blackTaskKey.contains(key)) {
            scheduleJob.setFillType(FillJobTypeEnum.BLACK_JOB.getType());
        } else {
            scheduleJob.setFillType(FillJobTypeEnum.MIDDLE_JOB.getType());
        }

        allJob.put(batchJob.getJobKey(), batchJob);
    }

    /**
     * 当前任务所属项目是否在白名单项目中
     *
     * @param task
     * @param projectAppMapping
     * @return
     */
    private boolean isInWhiteProject(ScheduleTaskShade task, Map<Integer, Set<Long>> projectAppMapping) {
        Set<Long> projects = projectAppMapping.get(task.getAppType());
        return CollectionUtils.isNotEmpty(projects) && projects.contains(task.getProjectId());
    }

    /**
     * get white list project
     *
     * @return
     */
    private Map<Integer, Set<Long>> getProjectWhiteListMapping() {
        if (!environmentContext.getJobGraphWhiteList()) {
            return new HashMap<>(0);
        }
        List<ScheduleEngineProject> scheduleEngineProjects = engineProjectDao.listWhiteListProject();
        if (CollectionUtils.isEmpty(scheduleEngineProjects)) {
            LOGGER.info("Counting task which white list is empty");
            return new HashMap<>(0);
        }
        return scheduleEngineProjects.stream()
                .collect(Collectors.groupingBy(ScheduleEngineProject::getAppType,
                        Collectors.mapping(ScheduleEngineProject::getProjectId, Collectors.toSet())));
    }


    /**
     * 使用taskID cycTime appType 生成展位Id
     *
     * @param taskId
     * @param cycTime
     * @param appType
     * @return
     * @see JobGraphBuilder#doSetFlowJobIdForSubTasks(java.util.List, java.util.Map)
     */
    public String buildFlowReplaceId(Long taskId, String cycTime, Integer appType) {
        return taskId + "_" + cycTime + "_" + appType;
    }

    /**
     * 清理周期实例脏数据
     *
     * @param triggerDay
     */
    private void cleanDirtyJobGraph(String triggerDay) {
        String preCycTime = DateUtil.getTimeStrWithoutSymbol(triggerDay);
        int totalJob = batchJobService.countByCyctimeAndJobName(preCycTime, CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType());
        if (totalJob <= 0) {
            return;
        }
        LOGGER.info("Start cleaning dirty cron job graph,  totalJob:{}", totalJob);

        int totalBatch;
        if (totalJob % JOB_BATCH_SIZE != 0) {
            totalBatch = totalJob / JOB_BATCH_SIZE + 1;
        } else {
            totalBatch = totalJob / JOB_BATCH_SIZE;
        }
        long startId = 0L;
        int i = 0;

        while (true) {
            final int batchIdx = ++i;
            if (batchIdx > totalBatch) {
                break;
            }
            final List<ScheduleJob> scheduleJobList = batchJobService.listByCyctimeAndJobName(startId, preCycTime,
                    CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType(), JOB_BATCH_SIZE);
            if (scheduleJobList.isEmpty()) {
                break;
            }
            LOGGER.info("Start clean batchJobList, batch-number:{} startId:{}", batchIdx, startId);
            startId = scheduleJobList.get(scheduleJobList.size() - 1).getId();
            List<String> jobKeyList = new ArrayList<>();
            for (ScheduleJob scheduleJob : scheduleJobList) {
                jobKeyList.add(scheduleJob.getJobKey());
            }
            batchJobService.deleteJobsByJobKey(jobKeyList);
            LOGGER.info("batch-number:{} done! Cleaning dirty jobs size:{}", batchIdx, scheduleJobList.size());
        }
    }

    /**
     * <br>将工作流中的子任务flowJobId字段设置为所属工作流的实例id</br>
     * <br>用于BatchFlowWorkJobService中检查工作流子任务状态</br>
     *
     * @param jobList
     * @param flowJobId
     */
    public void doSetFlowJobIdForSubTasks(List<ScheduleBatchJob> jobList, Map<String, String> flowJobId) {
        for (ScheduleBatchJob job : jobList) {
            String flowIdKey = job.getScheduleJob().getFlowJobId();
            job.getScheduleJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, NORMAL_TASK_FLOW_ID));
        }
    }


    /**
     * 保存生成的jobGraph记录
     *
     * @param triggerDay
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public boolean saveJobGraph(String triggerDay) {
        LOGGER.info("start saveJobGraph to db {}", triggerDay);
        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Long minJobId = getDefaultMinJobId(triggerTimeStr);
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        try {
            RetryUtil.executeWithRetry(() -> {
                int addNum = jobGraphTriggerService.addJobTrigger(timestamp, minJobId);
                if (addNum > 0) {
                    jobGraphEvent.successEvent(triggerTimeStr);
                }
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("addJobTrigger triggerTimeStr {} error ", triggerTimeStr, e);
            throw new RdosDefineException(e);
        }

        return true;
    }

    private Long getDefaultMinJobId(String triggerTimeStr) {
        //minJobId 默认从0开始记录
        String triggerTimeStrWithoutSymbol = DateUtil.getTimeStrWithoutSymbol(triggerTimeStr);
        String timePrefix = triggerTimeStrWithoutSymbol.substring(2, triggerTimeStrWithoutSymbol.length() - 2);
        String zeroIncrement = String.format("%09d", 0);
        return Long.parseLong(timePrefix + zeroIncrement);
    }


    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType,
                                                  boolean needAddFather, boolean needSelfDependency, String triggerDay,
                                                  String jobName, Long createUserId, Long projectId, Long tenantId, AtomicInteger count) throws Exception {
        return buildJobRunBean(task, keyPreStr, scheduleType, needAddFather, needSelfDependency, triggerDay,
                jobName, createUserId, null, null, projectId, tenantId, count);
    }

    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType, boolean needAddFather,
                                                  boolean needSelfDependency, String triggerDay, String jobName, Long createUserId,
                                                  String beginTime, String endTime, Long projectId, Long tenantId, AtomicInteger count) throws Exception {

        return buildJobRunBean(task, keyPreStr, scheduleType, needAddFather, needSelfDependency, triggerDay, jobName, createUserId, beginTime, endTime, projectId, tenantId, count, true, JobBuildType.THE_NEXT_DAY);
    }

    /**
     * 生成某一个 task 的指定日期(以天为单位)的所有 job 实例
     *
     * @param task                 任务信息
     * @param keyPreStr            jobKey 前缀, 如 cronTrigger、fillData_fillName
     * @param scheduleType         调度类型 {@link EScheduleType}
     * @param needAddFather        是否需要处理父任务依赖，补数据第一个层级不需要
     * @param needSelfDependency   是否需要处理自依赖
     * @param triggerDay           生成哪一天的实例，如 2022-08-30
     * @param jobName              job 名称
     * @param createUserId         创建人用户 id
     * @param beginTime            开始时间，单位时分，如 10:00，目前仅补数据时可选择传递，当 beginTime 和 endTime
     *                             都为空时，当前任务配置的跨周期依赖无效
     * @param endTime              结束时间，单位时分，结束时间 如 18:00
     * @param projectId            项目 id
     * @param tenantId             租户 id
     * @param count                job 数量，递增
     * @param removeSelfDependency 是否清除跨周期自依赖
     * @param jobBuildType         实例构建类型
     * @return 生成的 job 实例
     * @throws Exception 生成实例过程中的异常信息
     */
    public List<ScheduleBatchJob> buildJobRunBean(ScheduleTaskShade task, String keyPreStr, EScheduleType scheduleType, boolean needAddFather,
                                                  boolean needSelfDependency, String triggerDay, String jobName, Long createUserId,
                                                  String beginTime, String endTime, Long projectId, Long tenantId,AtomicInteger count,
                                                  boolean removeSelfDependency, JobBuildType jobBuildType) throws Exception {

        String scheduleStr = task.getScheduleConf();
        if (ETaskGroupEnum.MANUAL.getType().equals(task.getTaskGroup())) {
            scheduleStr = ScheduleConfUtils.buildManualTaskScheduleConf(scheduleStr);
        }
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr, timeService, task.getTaskId(), task.getAppType());
        List<ScheduleBatchJob> jobList = Lists.newArrayList();
        Timestamp timestampNow = Timestamp.valueOf(LocalDateTime.now());
        Timestamp jobBuildTime = Timestamp.valueOf(triggerDay + " 00:00:00");
        boolean isFirst = true;
        boolean isGroupTask = false;

        // 正常调度生成job需要判断--任务有效时间范围
        if (scheduleType.equals(EScheduleType.NORMAL_SCHEDULE) &&
                (scheduleCron.getBeginDate().after(jobBuildTime) || scheduleCron.getEndDate().before(jobBuildTime))) {
            LOGGER.error("appType {} taskId {} out of normal schedule time ", task.getAppType(), task.getTaskId());
            return jobList;
        }

        List<String> triggerDayList = scheduleCron.getTriggerTime(triggerDay)
                .stream()
                .map(DateUtil::formatTimeToScheduleTrigger)
                .collect(Collectors.toList());

        // 处理分钟粒度任务
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            dealConcreteTime(triggerDayList, triggerDay, beginTime, endTime, removeSelfDependency);
            if (removeSelfDependency){
                scheduleCron.setSelfReliance(DependencyType.NO_SELF_DEPENDENCY.getType());
            }
        }

        for (int idx = 0; idx < triggerDayList.size(); idx++) {
            String triggerTime = triggerDayList.get(idx);
            String nextTriggerTime = null;
            if (triggerDayList.size() > 1) {
                if ((idx < triggerDayList.size() - 1)) {
                    //当前不是最后一个
                    nextTriggerTime = triggerDayList.get(idx + 1);
                } else {
                    DateTime nextDayExecute = new DateTime(jobBuildTime.getTime()).plusDays(1);
                    //当前最后一个，则获取下个周期的第一个
                    List<String> nextTriggerDays = scheduleCron.getTriggerTime(nextDayExecute.toString(DateUtil.DATE_FORMAT));
                    if (CollectionUtils.isNotEmpty(nextTriggerDays)) {
                        nextTriggerTime = nextTriggerDays.get(0);
                    }
                }
            }

            ScheduleJob scheduleJob = new ScheduleJob();
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            triggerTime = DateUtil.getTimeStrWithoutSymbol(triggerTime);
            String jobKey = generateJobKey(keyPreStr, task.getId(), triggerTime);
            String targetJobName = jobName;
            if (EScheduleType.NORMAL_SCHEDULE.equals(scheduleType)) {
                targetJobName = targetJobName + "_" + triggerTime;
            } else if (EScheduleType.manualOperatorType.contains(scheduleType.getType())) {
                //补数据的名称和后缀用‘-’分割开-->在查询的时候会用到
                targetJobName = targetJobName + "-" + task.getName() + "-" + triggerTime;
            }
            scheduleJob.setJobId(actionService.generateUniqueSign());
            scheduleJob.setJobKey(jobKey);
            scheduleJob.setJobName(targetJobName);
            scheduleJob.setPeriodType(scheduleCron.getPeriodType());
            scheduleJob.setTaskId(task.getTaskId());
            scheduleJob.setBusinessType(task.getBusinessType());
            scheduleJob.setComputeType(task.getComputeType());
            scheduleJob.setJobBuildType(jobBuildType.getType());
            //普通任务
            if (task.getFlowId() == 0) {
                scheduleJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
            } else {
                //工作流子节点
                ScheduleTaskShade flowTaskShade = batchTaskShadeService.getBatchTaskById(task.getFlowId(), task.getAppType());
                if (null == flowTaskShade || EScheduleJobType.GROUP.getType().equals(flowTaskShade.getTaskType())) {
                    scheduleJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
                    isGroupTask = null != flowTaskShade && EScheduleJobType.GROUP.getType().equals(flowTaskShade.getTaskType());
                } else {
                    //非 小时&分钟 任务
                    String flowJobTime = triggerTime;
                    if (triggerDayList.size() == 1) {
                        List<String> cycTime = getFlowWorkCycTime(flowTaskShade, triggerDay);
                        //其他类型的任务每天只会生成一个实例
                        if (CollectionUtils.isNotEmpty(cycTime)) {
                            flowJobTime = DateUtil.getTimeStrWithoutSymbol(cycTime.get(0));
                        }
                    }
                    scheduleJob.setFlowJobId(this.buildFlowReplaceId(flowTaskShade.getTaskId(), flowJobTime, flowTaskShade.getAppType()));
                }
            }

            scheduleJob.setGmtCreate(timestampNow);
            scheduleJob.setGmtModified(timestampNow);
            if (createUserId == null) {
                scheduleJob.setCreateUserId(task.getOwnerUserId());
            } else {
                scheduleJob.setCreateUserId(createUserId);
            }
            //针对跨项目补数据，项目id需要随机应变
            scheduleJob.setTenantId(tenantId);
            scheduleJob.setProjectId(projectId);
            scheduleJob.setDtuicTenantId(task.getDtuicTenantId());
            scheduleJob.setAppType(task.getAppType());
            scheduleJob.setResourceId(task.getResourceId());

            scheduleJob.setType(scheduleType.getType());
            scheduleJob.setCycTime(triggerTime);
            scheduleJob.setJobExecuteOrder(JobExecuteOrderUtil.buildJobExecuteOrder(triggerTime, count));

            scheduleJob.setIsRestart(Restarted.NORMAL.getStatus());
            scheduleJob.setFillType(FillJobTypeEnum.DEFAULT.getType());
            scheduleJob.setDependencyType(scheduleCron.getSelfReliance());

            scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
            scheduleJob.setTaskType(task.getTaskType());
            scheduleJob.setMaxRetryNum(scheduleCron.getMaxRetryNum());
            scheduleJob.setVersionId(task.getVersionId());
            scheduleJob.setNextCycTime(nextTriggerTime);


            //业务时间等于执行时间 -1 天
            String businessDate = generateBizDateFromCycTime(triggerTime);
            scheduleJob.setBusinessDate(businessDate);
            scheduleJob.setTaskRule(task.getTaskRule());
            //保存自定义调度日历Id
            if (ESchedulePeriodType.CALENDER.getVal() == scheduleCron.getPeriodType()) {
                Long calenderId = calenderService.getCalenderIdByTaskId(task.getTaskId(), task.getAppType());
                scheduleJob.setCalenderId(calenderId);
            }

            //任务流中的子任务且没有父任务依赖，起始节点将任务流节点作为父任务加入
            if (task.getFlowId() > 0 && !whetherHasParentTask(task.getTaskId(), task.getAppType()) && !isGroupTask) {
                List<String> keys = getJobKeys(Lists.newArrayList(task.getFlowId()), scheduleJob, scheduleCron, keyPreStr);
                if(CollectionUtils.isNotEmpty(keys)){
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, keys.get(0), timestampNow, task.getAppType()));
                }
            }

            //获取依赖的父task 的 jobKey
            if (needAddFather) {
                List<FatherDependency> fatherDependency = getDependencyJobKeys(scheduleType, scheduleJob, scheduleCron, keyPreStr);
                for (FatherDependency dependencyJobKey : fatherDependency) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("get Job {} Job key  {} cron {} cycTime {}", jobKey, dependencyJobKey, JSONObject.toJSONString(scheduleCron), scheduleJob.getCycTime());
                    }
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, dependencyJobKey.getJobKey(), timestampNow, dependencyJobKey.getAppType()));
                }
            }

            if (needSelfDependency) {
                dealSelfDependency(scheduleCron.getSelfReliance(), scheduleJob, scheduleCron, isFirst, scheduleBatchJob, keyPreStr, scheduleType, jobKey, timestampNow);
            }

            jobList.add(scheduleBatchJob);
            isFirst = false;
        }

        return jobList;
    }

    private void dealConcreteTime(List<String> triggerDayList, String triggerDay, String beginTime, String endTime, boolean reverse) {
        DateTimeFormatter ddd = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        beginTime = triggerDay + " " + beginTime + ":00";
        endTime = triggerDay + " " + endTime + ":00";

        DateTime begin = DateTime.parse(beginTime, ddd);
        DateTime end = DateTime.parse(endTime, ddd);

        List<String> remove = Lists.newArrayList();
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isBefore(begin)) {
                remove.add(cur);
            } else {
                break;
            }
        }
        if (reverse) {
            Collections.reverse(triggerDayList);
        }
        for (String cur : triggerDayList) {
            if (DateTime.parse(cur, ddd).isAfter(end)) {
                remove.add(cur);
            } else {
                break;
            }
        }
        triggerDayList.removeAll(remove);
    }

    private List<String> getFlowWorkCycTime(ScheduleTaskShade flowTaskShade, String triggerDay) {
        List<String> triggerTime = Lists.newArrayList();
        if (flowTaskShade != null) {
            try {
                if (ETaskGroupEnum.MANUAL.getType().equals(flowTaskShade.getTaskGroup())) {
                    flowTaskShade.setScheduleConf(ScheduleConfUtils.buildManualTaskScheduleConf(flowTaskShade.getScheduleConf()));
                }
                String scheduleStr = flowTaskShade.getScheduleConf();
                ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleStr, timeService, flowTaskShade.getTaskId(), flowTaskShade.getAppType());
                triggerTime = scheduleCron.getTriggerTime(triggerDay);
            } catch (Exception e) {
                LOGGER.error("getFlowWorkCycTime error with flowId: " + flowTaskShade.getTaskId(), e);
            }
        }
        return triggerTime;
    }

    /**
     * 第一次执行的时候是没有上一个周期的----所以如果查询不到上一个周期的话则不设置依赖
     * 如果是自依赖的话获取上一个周期的jobKey
     *
     * @param selfReliance
     * @param scheduleJob
     * @param scheduleCron
     * @param isFirst
     * @param scheduleBatchJob
     * @param keyPreStr        生成jobKey的前缀
     * @param scheduleType
     * @param jobKey
     * @param timestampNow
     * @throws ParseException
     */

    /**
     * 处理任务跨周期中的自依赖关系
     *
     * @param selfReliance     任务依赖类型 {@link DependencyType}
     * @param scheduleJob      当前任务实例
     * @param scheduleCron     调度属性解析器
     * @param isFirst          是否是第一个节点
     * @param scheduleBatchJob 实例和实例间依赖关系
     * @param keyPreStr        jobKey 前缀
     * @param scheduleType     调度类型
     * @param jobKey           当前实例的 jobKey
     * @param timestampNow     当前时间
     */
    private void dealSelfDependency(Integer selfReliance, ScheduleJob scheduleJob, ScheduleCron scheduleCron, boolean isFirst,
                                    ScheduleBatchJob scheduleBatchJob, String keyPreStr, EScheduleType scheduleType, String jobKey,
                                    Timestamp timestampNow) {

        // 修改 直接改成如果等于NO_SELF_DEPENDENCY就返回
        // 注意这里依赖下游任务的上一个周期完成状态和依赖下游任务的上一个周期的结束:只要运行结束就可以这两个条件也会添加自依赖，原因是如果不加上自依赖的话，
        // 如果对上游任务下一个周期杀死操作，就会有可能导致两个任务同时运行，导致锁表。
        if (DependencyType.NO_SELF_DEPENDENCY.getType().equals(selfReliance)) {
            return;
        }

        //获取上一个周期的jobKey
        String preSelfJobKey = getSelfDependencyJobKeys(scheduleJob, scheduleCron, keyPreStr);
        if (preSelfJobKey != null) {
            //需要查库判断是否存在
            if (isFirst) {
                ScheduleJob dbScheduleJob = batchJobService.getJobByJobKeyAndType(preSelfJobKey, scheduleType.getType());
                if (dbScheduleJob != null) {
                    scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow, scheduleJob.getAppType()));
                }
            } else {
                scheduleBatchJob.addBatchJobJob(createNewJobJob(scheduleJob, jobKey, preSelfJobKey, timestampNow, scheduleJob.getAppType()));
            }
        }
    }


    public ScheduleJobJob createNewJobJob(ScheduleJob scheduleJob, String jobKey, String parentKey, Timestamp timestamp, Integer parentAppType) {
        ScheduleJobJob jobJobJob = new ScheduleJobJob();
        jobJobJob.setTenantId(scheduleJob.getTenantId());
        jobJobJob.setProjectId(scheduleJob.getProjectId());
        jobJobJob.setDtuicTenantId(scheduleJob.getDtuicTenantId());
        jobJobJob.setAppType(scheduleJob.getAppType());
        jobJobJob.setJobKey(jobKey);
        jobJobJob.setParentJobKey(parentKey);
        jobJobJob.setParentAppType(parentAppType);
        jobJobJob.setGmtModified(timestamp);
        jobJobJob.setGmtCreate(timestamp);
        return jobJobJob;
    }


    /**
     * 如果当前任务的任务周期是天---依赖的任务周期也是天---则获取当天的父任务key
     * 其他情况:获取依赖的父任务中小于等于子任务的触发时间点
     *
     * @param scheduleType 调度类型
     * @param scheduleJob  job 实例
     * @param scheduleCron 当前任务的调度属性解析器
     * @param keyPreStr    jobKey 前缀
     * @return 当前实例的所有父 job 实例集合
     */
    public List<FatherDependency> getDependencyJobKeys(EScheduleType scheduleType, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {

        List<ScheduleTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(scheduleJob.getTaskId(), scheduleJob.getAppType());

        // 所有父任务的jobKey
        // return getJobKeys(pIdList, batchJob, scheduleCron, keyPreStr);
        // 补数据运行时，需要所有周期实例立即运行
        if (EScheduleType.manualOperatorType.contains(scheduleType.getType())) {
            return getJobKeysByTaskTasks(taskTasks, scheduleJob, scheduleCron, keyPreStr);
        }
        // 假设2019年7月10号创建一个月调度周期实例，每月20日执行，子任务是天任务。这时，7月20日之前，父任务从未生成过实例，子任务都不能调度执行，
        // 该方法排除了这种情况，如果存在调度日期不一致且父实例找不到的情况下则当前依赖不生效
        return getExternalJobKeys(taskTasks, scheduleJob, scheduleCron, keyPreStr);

    }

    /**
     * 获取当前 job 实例的父 job 实例对应 jobKey 集合，排除由于人为原因(创建时间、系统问题)导致的被依赖任务没生成的情况，比如
     * 日任务依赖周任务，日任务依赖月任务，周任务依赖月任务，但所被依赖的父任务没有生成 graph，导致当前任务不能执行或者执行失败。
     *
     * @param taskTasks    当前任务的父任务依赖关系
     * @param scheduleJob  当前调度实例
     * @param scheduleCron 调度属性
     * @param keyPreStr    jobKey 前缀
     * @return 父 job 实例对应 jobKey 集合
     */
    private List<FatherDependency> getExternalJobKeys(List<ScheduleTaskTaskShade> taskTasks, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<FatherDependency> jobKeyList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(taskTasks)) {
            Map<Integer, List<ScheduleTaskTaskShade>> taskMaps = taskTasks.stream()
                    .filter(scheduleTaskTaskShade -> scheduleTaskTaskShade.getParentAppType() != null
                            && scheduleTaskTaskShade.getParentTaskId() != null)
                    .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));

            for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : taskMaps.entrySet()) {
                List<ScheduleTaskTaskShade> taskTaskShades = entry.getValue();
                Integer appType = entry.getKey();
                Map<Long, List<ScheduleTaskTaskShade>> taskShadeMap = taskTaskShades.stream()
                        .filter(scheduleTaskTaskShade -> scheduleTaskTaskShade.getParentTaskId() != null)
                        .collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentTaskId));
                List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(Lists.newArrayList(taskShadeMap.keySet()), appType);
                for (ScheduleTaskShade pTask : pTaskList) {
                    try {
                        // 这里肯定只有一条边，如果出现多条说的程序已经计算错误
                        List<ScheduleTaskTaskShade> scheduleTaskTaskShades = taskShadeMap.get(pTask.getTaskId());

                        if (scheduleTaskTaskShades.size() != 1) {
                            LOGGER.warn("task {} warn has more site ",pTask.getTaskId());
                        }

                        ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf(), timeService, pTask.getTaskId(), pTask.getAppType());
                        ScheduleTaskTaskShade scheduleTaskTaskShade = scheduleTaskTaskShades.get(0);
                        Integer upDownRelyType = scheduleTaskTaskShade.getUpDownRelyType();

                        String fatherLastJobCycTime;
                        if (UpDownRelyTypeEnum.CUSTOM.getType().equals(upDownRelyType)) {
                            fatherLastJobCycTime = getCustomLastJobBusinessDate(scheduleJob,pScheduleCron,scheduleTaskTaskShade.getCustomOffset());
                        } else {
                            // 执行时间
                            fatherLastJobCycTime = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                        }

                        if (StringUtils.isBlank(fatherLastJobCycTime)) {
                            LOGGER.info("getExternalJobKeys , the parent job of " + pTask.getTaskId() + " cycTime is null ,current job is " + scheduleJob.getJobId());
                            continue;
                        }
                        String pjobKey = generateJobKey(keyPreStr, pTask.getId(), fatherLastJobCycTime);
                        // BatchJob (cycTime 20191211000000 businessDate 20191210000000)  fatherLastJobCycTime 20191211000000
                        // 判断的时候需要拿执行时间判断
                        DateTime jobCycTime = new DateTime(DateUtil.getTimestamp(scheduleJob.getCycTime(), dtfFormatString));
                        DateTime fatherCycTime = new DateTime(DateUtil.getTimestamp(fatherLastJobCycTime, dtfFormatString));

                        // 如果父任务在当前任务业务日期不同，则查询父任务是有已生成，若没生成则不处理当前父依赖关系，
                        // 这样可以避免父任务上一周期没生成时，子任务实例运行失败
                        if (fatherCycTime.getDayOfYear() != jobCycTime.getDayOfYear()) {
                            // 判断父任务是否生成
                            ScheduleJob pScheduleJob = batchJobService.getJobByJobKeyAndType(pjobKey, EScheduleType.NORMAL_SCHEDULE.getType());
                            if (pScheduleJob == null) {
                                LOGGER.error("getExternalJobKeys ,but not found the parent job of " + pTask.getTaskId()
                                        + " ,current job is " + scheduleJob.getJobId() + ", the pjobKey = " + pjobKey);
                                continue;
                            }
                        }

                        FatherDependency fatherDependency = new FatherDependency();
                        fatherDependency.setAppType(appType);
                        fatherDependency.setJobKey(pjobKey);
                        jobKeyList.add(fatherDependency);
                    } catch (Exception e) {
                        LOGGER.error("getExternalJobKeys parse task" + pTask.getId() + " error", e);
                    }
                }
            }
        }
        return jobKeyList;
    }




    /**
     * 是否有父任务依赖
     *
     * @param taskId  应用 taskId 对应 task_shade 表中 task_id 字段
     * @param appType 应用类型
     * @return true-有父任务，false-无
     */
    private boolean whetherHasParentTask(Long taskId, Integer appType) {
        List<ScheduleTaskTaskShade> taskTasks = taskTaskShadeService.getAllParentTask(taskId, appType);
        return CollectionUtils.isNotEmpty(taskTasks);
    }

    private List<String> getJobKeys(List<Long> taskShadeIds, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<String> jobKeyList = Lists.newArrayList();
        List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(taskShadeIds, scheduleJob.getAppType());
        for (ScheduleTaskShade pTask : pTaskList) {
            try {
                ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(pTask.getScheduleConf(), timeService, pTask.getTaskId(), pTask.getAppType());
                String pBusinessDate = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                String pjobKey = generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                jobKeyList.add(pjobKey);
            } catch (Exception e) {
                // 解析失败则当前依赖关系不生效
                LOGGER.error("parse task" + pTask.getId() + " error", e);
            }
        }
        return jobKeyList;
    }

    /**
     * 获取当前 job 实例的父 job 实例对应 jobKey 集合
     *
     * @param taskTasks    当前任务的父任务依赖关系
     * @param scheduleJob  当前调度实例
     * @param scheduleCron 调度属性
     * @param keyPreStr    jobKey 前缀
     * @return 父 job 实例对应 jobKey 集合
     */
    private List<FatherDependency> getJobKeysByTaskTasks(List<ScheduleTaskTaskShade> taskTasks, ScheduleJob scheduleJob, ScheduleCron scheduleCron, String keyPreStr) {
        List<FatherDependency> jobKeyList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskTasks)) {

            // 按照父任务类型分组
            Map<Integer, List<ScheduleTaskTaskShade>> taskMaps = taskTasks.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentAppType));

            for (Map.Entry<Integer, List<ScheduleTaskTaskShade>> entry : taskMaps.entrySet()) {
                List<ScheduleTaskTaskShade> taskTaskShades = entry.getValue();
                Integer appType = entry.getKey();
                // 父任务 id 集合
                Map<Long, List<ScheduleTaskTaskShade>> taskShadeMap = taskTaskShades.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentTaskId));
                // 所有的父任务集合
                List<ScheduleTaskShade> pTaskList = batchTaskShadeService.getTaskByIds(Lists.newArrayList(taskShadeMap.keySet()), appType);
                for (ScheduleTaskShade pTask : pTaskList) {
                    try {
                        // 这里肯定只有一条边，如果出现多条说的程序已经计算错误
                        List<ScheduleTaskTaskShade> scheduleTaskTaskShades = taskShadeMap.get(pTask.getTaskId());

                        if (scheduleTaskTaskShades.size() != 1) {
                            LOGGER.warn("task {} warn has more site ",pTask.getTaskId());
                        }

                        String scheduleConf = pTask.getScheduleConf();
                        if (ETaskGroupEnum.MANUAL.getType().equals(pTask.getTaskGroup())) {
                            scheduleConf = ScheduleConfUtils.buildManualTaskScheduleConf(pTask.getScheduleConf());
                        }
                        // 解析调度属性
                        ScheduleTaskTaskShade scheduleTaskTaskShade = scheduleTaskTaskShades.get(0);
                        Integer upDownRelyType = scheduleTaskTaskShade.getUpDownRelyType();
                        ScheduleCron pScheduleCron = ScheduleFactory.parseFromJson(scheduleConf, timeService, pTask.getTaskId(), pTask.getAppType());

                        String pBusinessDate;
                        // 执行时间
                        if (UpDownRelyTypeEnum.CUSTOM.getType().equals(upDownRelyType)) {
                            pBusinessDate = getCustomLastJobBusinessDate(scheduleJob,pScheduleCron,scheduleTaskTaskShade.getCustomOffset());
                        } else {
                            pBusinessDate = getFatherLastJobBusinessDate(scheduleJob, pScheduleCron, scheduleCron);
                        }

                        FatherDependency fatherDependency = new FatherDependency();
                        String pjobKey = generateJobKey(keyPreStr, pTask.getId(), pBusinessDate);
                        fatherDependency.setJobKey(pjobKey);
                        fatherDependency.setAppType(appType);
                        jobKeyList.add(fatherDependency);
                    } catch (Exception e) {
                        // 解析失败该 job 实例当前的父任务依赖关系失效
                        LOGGER.error("parse task" + pTask.getId() + " error", e);
                    }
                }
            }
        }

        return jobKeyList;
    }


    public String getSelfDependencyJobKeys(ScheduleJob scheduleJob, ScheduleCron cron, String keyPreStr) {
        String preTriggerDateStr;
        if (ESchedulePeriodType.CALENDER.getVal() == cron.getPeriodType()) {
            ScheduleCronCalenderParser calenderParser = (ScheduleCronCalenderParser) cron;
            DateTime triggerDate = new DateTime(DateUtil.getTimestamp(scheduleJob.getCycTime(), dtfFormatString));
            preTriggerDateStr = calenderParser.getNearestTime(triggerDate, false);
        } else {
            //获取上一个执行周期的触发时间
            preTriggerDateStr = JobGraphUtils.getPrePeriodJobTriggerDateStr(scheduleJob.getCycTime(), cron);
        }
        //原逻辑是拿batchJob的taskId 作为key
        //现在task中 taskId + appType 才是唯一
        //现在采用taskShade表的id
        ScheduleTaskShade shade = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (null != shade && StringUtils.isNotBlank(preTriggerDateStr)) {
            return generateJobKey(keyPreStr, shade.getId(), preTriggerDateStr);
        }
        return null;
    }

    /**
     * 构建 jobKey
     *
     * @param triggerType jobKey 前缀
     * @param taskId      任务 id(task_shade 表中 id，而不是 task_id)
     * @param triggerTime 格式要求:yyyyMMddHHmmss
     * @return 生成的 jobKey
     */
    public static String generateJobKey(String triggerType, long taskId, String triggerTime) {
        triggerTime = triggerTime.replace("-", "").replace(":", "").replace(" ", "");
        return triggerType + "_" + taskId + "_" + triggerTime;
    }

    /**
     * 返回有序的队列
     *
     * @param dayListStr
     * @return
     */
    public static List<Integer> getSortTimeList(String dayListStr, String hourStr, String minuteStr, String secondStr) {

        int hour = Integer.valueOf(hourStr.trim());
        int minute = Integer.valueOf(minuteStr.trim());
        int second = Integer.valueOf(secondStr);

        int suffix = hour * 10000 + minute * 100 + second;
        String[] dayArr = dayListStr.split(",");
        List<Integer> sortList = Lists.newArrayList();
        for (String dayStr : dayArr) {
            int dayInteger = MathUtil.getIntegerVal(dayStr.trim()) * 1000000 + suffix;
            sortList.add(dayInteger);
        }

        Collections.sort(sortList);
        return sortList;
    }

    /**
     * 获取父任务当前时间实例中执行时间小于 dateTime 且最接近的执行时间
     * <p>
     * 如果父任务为天任务：
     * 1. 若父任务执行时间大于当前子任务 job 执行时间且子任务不是天任务返回父任务昨天执行时间的执行时间
     * 2. 否则返回今天执行时间对应的执行时间
     * <p>这样就限制了，如果父子任务都是天任务，即使子任务执行时间比父任务执行时间靠后，当天生成的
     * 子任务 job 也会依赖当天的父任务生成的 job
     * <p>该方法是不考虑父 job 是否存在的情况的
     *
     * @param childScheduleJob 子任务 job 实例
     * @param fatherCron       父任务调度属性
     * @param childCron        子任务调度属性
     * @return 父任务实例的 jobKey
     */
    public String getFatherLastJobBusinessDate(ScheduleJob childScheduleJob, ScheduleCron fatherCron, ScheduleCron childCron) {
        // job 执行时间
        DateTime dateTime = new DateTime(DateUtil.getTimestamp(childScheduleJob.getCycTime(), dtfFormatString));
        if (ESchedulePeriodType.CALENDER.getVal() == fatherCron.getPeriodType()) {
            ScheduleCronCalenderParser calenderParser = (ScheduleCronCalenderParser) fatherCron;
            return getCalenderNextCycle(calenderParser,childScheduleJob,dateTime);
        } else {
            String pCronstr = fatherCron.getCronStr();
            // 切分 cron 表达式
            String[] timeFields = pCronstr.split("\\s+");
            if (timeFields.length != 6) {
                throw new RdosDefineException("illegal param of cron str:" + pCronstr);
            }

            if (fatherCron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
                dateTime = getCloseInDateTimeOfMonth(timeFields, dateTime);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
                dateTime = getCloseInDateTimeOfWeek(timeFields, dateTime);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() != ESchedulePeriodType.DAY.getVal()) {
                dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, false);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal() && childCron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
                dateTime = getCloseInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, true);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
                dateTime = getCloseInDateTimeOfHour(dateTime, (ScheduleCronHourParser) fatherCron);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
                dateTime = getCloseInDateTimeOfMin(dateTime, (ScheduleCronMinParser) fatherCron);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.CUSTOM.getVal()) {
                CronExpression expression = null;
                try {
                    expression = new CronExpression(fatherCron.getCronStr());
                } catch (ParseException e) {
                    throw new RdosDefineException("cron express is invalid:" + fatherCron.getCronStr(), e);
                }
                // 判断在childScheduleJob.getCycTime()中是否有实例，且实例仅有一个
                Date todayCron = getTodayCron(childScheduleJob, expression);

                if (todayCron == null) {
                    // 父任务为自定义调度,则从当前子任务执行前寻找
                    dateTime = new DateTime(findLastDateBeforeCurrent(expression, dateTime.toDate(),
                            dateTime.toDate().toInstant().atZone(DateUtil.DEFAULT_ZONE).toLocalDateTime(), 0, false));
                } else {
                    dateTime = new DateTime(todayCron);
                }
            } else {
                throw new RuntimeException("not support period type of " + fatherCron.getPeriodType());
            }

            return DateUtil.getFormattedDate(dateTime.getMillis(), dtfFormatString);
        }
    }

    private String getCustomLastJobBusinessDate(ScheduleJob childScheduleJob, ScheduleCron fatherCron, Integer customOffset) {
        // job 执行时间
        DateTime dateTime = new DateTime(DateUtil.getTimestamp(childScheduleJob.getCycTime(), dtfFormatString));

        if (ESchedulePeriodType.CALENDER.getVal() == fatherCron.getPeriodType()) {
            ScheduleCronCalenderParser calenderParser = (ScheduleCronCalenderParser) fatherCron;
            return getCalenderOffsetCycle(calenderParser,dateTime,customOffset);
        } else {
            String pCronstr = fatherCron.getCronStr();
            // 切分 cron 表达式
            String[] timeFields = pCronstr.split("\\s+");
            if (timeFields.length != 6) {
                throw new RdosDefineException("illegal param of cron str:" + pCronstr);
            }

            if (fatherCron.getPeriodType() == ESchedulePeriodType.MONTH.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfMonth(timeFields, dateTime,customOffset);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.WEEK.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfWeek(timeFields, dateTime,customOffset);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.DAY.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfDay(dateTime, (ScheduleCronDayParser) fatherCron, customOffset);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.HOUR.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfHour(dateTime, (ScheduleCronHourParser) fatherCron,customOffset);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.MIN.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfMin(dateTime, (ScheduleCronMinParser) fatherCron,customOffset);
            } else if (fatherCron.getPeriodType() == ESchedulePeriodType.CUSTOM.getVal()) {
                dateTime = getCustomOffsetInDateTimeOfCustom(dateTime,fatherCron,customOffset);
            } else {
                throw new RuntimeException("not support period type of " + fatherCron.getPeriodType());
            }

            return DateUtil.getFormattedDate(dateTime.getMillis(), dtfFormatString);
        }
    }

    private DateTime getCustomOffsetInDateTimeOfCustom(DateTime dateTime, ScheduleCron fatherCron, Integer customOffset) {
        boolean symbol = true;
        String cronStr = fatherCron.getCronStr();
        Date date = dateTime.toDate();

        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        if (customOffset == 1) {
            if (CronUtils.isMatch(cronStr, date)) {
                return new DateTime(date);
            }
        }

        int count = 0;
        if (symbol) {
            while (count < customOffset) {
                // 下一个周期
                date = CronUtils.next(cronStr, date);
                count++;
            }
        } else {
            while (count < customOffset) {
                date = CronUtils.last(cronStr, date);
                count++;
            }
        }
        return new DateTime(date);
    }

    private DateTime getCustomOffsetInDateTimeOfMin(DateTime dateTime, ScheduleCronMinParser fatherCron, Integer customOffset) {
        int childTime = dateTime.getHourOfDay() * 60 + dateTime.getMinuteOfHour();
        int triggerTime = -1;
        int begin = fatherCron.getBeginHour() * 60 + fatherCron.getBeginMin();
        int end = fatherCron.getEndHour() * 60 + fatherCron.getEndMin();

        List<Integer> minTimes = getSortMinTimeList(begin, end, fatherCron.getGapNum());
        int count = 0;
        boolean symbol = true;

        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }


        if (end - begin < 0) {
            throw new RdosDefineException("illegal cron str :" + fatherCron.getCronStr());
        }

        if (symbol) {
            for (Integer minTime : minTimes) {
                if (minTime >= childTime) {
                    triggerTime = minTime;
                    count++;
                }

                if (count >= customOffset) {
                    break;
                }
            }

            while (count < customOffset) {
                dateTime = dateTime.plusDays(1);

                for (Integer minTime : minTimes) {
                    count++;
                    triggerTime = minTime;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        } else {
            Collections.reverse(minTimes);
            for (Integer minTime : minTimes) {
                if (minTime <= childTime) {
                    triggerTime = minTime;
                    count++;
                }

                if (count >= customOffset) {
                    break;
                }
            }

            while (count < customOffset) {
                dateTime = dateTime.minusDays(1);

                for (Integer minTime : minTimes) {
                    count++;
                    triggerTime = minTime;
                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        }

        int hour = triggerTime / 60;
        int minute = triggerTime % 60;

        dateTime = dateTime.withTime(hour, minute, 0, 0);
        return dateTime;
    }

    private List<Integer> getSortMinTimeList(int begin, int end, int gapNum) {
        List<Integer> minTimes = Lists.newArrayList();
        for (int i = begin; i <= end; ) {
            minTimes.add(i);
            i = i + gapNum;
        }

        return minTimes;
    }

    private DateTime getCustomOffsetInDateTimeOfHour(DateTime dateTime, ScheduleCronHourParser fatherCron, Integer customOffset) {
        boolean symbol = true;
        int childTime = dateTime.getHourOfDay() * 100 + dateTime.getMinuteOfHour();
        List<Integer> sortHourList = getSortHourTimeList(fatherCron);

        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        int triggerTime = -1;

        int count = 0;
        if (symbol) {
            for (Integer fatherTime : sortHourList) {
                if (fatherTime >= childTime) {
                    triggerTime = fatherTime;
                    count++;
                }

                if (count >= customOffset) {
                    break;
                }

            }

            while (count < customOffset) {
                // 找前一天
                dateTime = dateTime.plusDays(1);

                for (Integer fatherTime : sortHourList) {
                    count++;
                    triggerTime = fatherTime;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        } else {
            Collections.reverse(sortHourList);
            for (Integer fatherTime : sortHourList) {
                if (fatherTime <= childTime) {
                    triggerTime = fatherTime;
                    count++;
                }

                if (count >= customOffset) {
                    break;
                }

            }

            while (count < customOffset) {
                // 找前一天
                dateTime = dateTime.minusDays(1);

                for (Integer fatherTime : sortHourList) {
                    count++;
                    triggerTime = fatherTime;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        }

        int hour = triggerTime / 100;
        int min = triggerTime % 100;
        dateTime = dateTime.withTime(hour, min, 0, 0);

        return dateTime;
    }

    private List<Integer> getSortHourTimeList(ScheduleCronHourParser fatherCron) {
        List<Integer> sortHourList = Lists.newArrayList();
        for (int i = fatherCron.getBeginHour(); i <= fatherCron.getEndHour(); ) {
            sortHourList.add((i * 100 + fatherCron.getBeginMinute()));
            i += fatherCron.getGapNum();
        }
        return sortHourList;
    }

    private DateTime getCustomOffsetInDateTimeOfDay(DateTime dateTime, ScheduleCronDayParser fatherCron, Integer customOffset) {
        DateTime fatherCurrDayTime = dateTime.withTime(fatherCron.getHour(), fatherCron.getMinute(), 0, 0);
        boolean symbol = true;
        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        int needOffset = customOffset;
        if (fatherCurrDayTime.getMillis() != dateTime.getMillis())  {
            // 不存在T0
            needOffset--;
        }

        if (customOffset == 1 & fatherCurrDayTime.getMillis() == dateTime.getMillis()) {
            return fatherCurrDayTime;
        }

        if (symbol) {
            if (fatherCurrDayTime.getMillis() < dateTime.getMillis()) {
                fatherCurrDayTime = fatherCurrDayTime.plusDays(1);
            }

            if (needOffset > 0) {
                // 设置偏移量
                fatherCurrDayTime = fatherCurrDayTime.plusDays(needOffset);
            }
        } else {
            if (fatherCurrDayTime.getMillis() > dateTime.getMillis()) {
                fatherCurrDayTime = fatherCurrDayTime.minusDays(1);
            }

            if (needOffset > 0) {
                // 设置偏移量
                fatherCurrDayTime = fatherCurrDayTime.minusDays(needOffset);
            }
        }

        return fatherCurrDayTime;
    }

    private DateTime getCustomOffsetInDateTimeOfWeek(String[] timeFields, DateTime dateTime, Integer customOffset) {
        boolean symbol = true;
        String dayStr = timeFields[5];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.dayOfWeek().get() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();
        int dependencyTime = -1;

        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        int count = 0;
        if (symbol) {
            // 向前找
            // 1. 当月的时候
            for (int time : timeList) {
                if (targetTime <= time) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

            while (count < customOffset) {
                // 2. 当月没有找到，到下一个周找
                dateTime = dateTime.plusWeeks(1);
                for (int time : timeList) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

        } else {
            // 向后找
            Collections.reverse(timeList);

            // 1. 当月的时候
            for (int time : timeList) {
                if (targetTime >= time) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

            while (count < customOffset) {
                // 2. 当月没有找到，到上一个周找
                dateTime = dateTime.minusWeeks(1);
                for (int time : timeList) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        }

        dateTime = getDateTime(dateTime, dependencyTime);
        return dateTime;
    }

    @NotNull
    private static DateTime getDateTime(DateTime dateTime, int dependencyTime) {
        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfWeek(day).withTime(hour, min, sec, 0);
        return dateTime;
    }

    private DateTime getCustomOffsetInDateTimeOfMonth(String[] timeFields, DateTime dateTime, Integer customOffset) {
        String dayStr = timeFields[3];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];


        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.getDayOfMonth() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        int dependencyTime = -1;
        boolean symbol = true;
        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        int count = 0;
        if (symbol) {
            // 向前找
            // 1. 当月的时候
            for (int time : timeList) {
                if (targetTime <= time) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

            while (count < customOffset) {
                // 2. 当月没有找到，到下一个月找
                dateTime = dateTime.plusMonths(1);
                for (int time : timeList) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

        } else {
            // 向后找
            Collections.reverse(timeList);

            // 1. 当月的时候
            for (int time : timeList) {
                if (targetTime >= time) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }

            while (count < customOffset) {
                // 2. 当月没有找到，到下一个月找
                dateTime = dateTime.minusMonths(1);
                for (int time : timeList) {
                    count++;
                    dependencyTime = time;

                    if (count >= customOffset) {
                        break;
                    }
                }
            }
        }

        dateTime = getMonthTime(dateTime, dependencyTime);
        return dateTime;
    }

    @NotNull
    private static DateTime getMonthTime(DateTime dateTime, int dependencyTime) {
        int day = dependencyTime / 1000000;
        dependencyTime = dependencyTime - day * 1000000;
        int hour = dependencyTime / 10000;
        dependencyTime = dependencyTime - hour * 10000;
        int min = dependencyTime / 100;
        int sec = dependencyTime % 100;

        dateTime = dateTime.withDayOfMonth(day).withTime(hour, min, sec, 0);
        return dateTime;
    }

    private String getCalenderOffsetCycle(ScheduleCronCalenderParser calenderParser, DateTime dateTime, Integer customOffset) {
        return calenderParser.getNearestOffset(dateTime,customOffset, true);
    }

    private String getCalenderNextCycle(ScheduleCronCalenderParser calenderParser, ScheduleJob childScheduleJob, DateTime dateTime) {
        String cycTime = childScheduleJob.getCycTime();
        String startTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString)).withMillisOfDay(0).toString(dtfFormatString);
        String endTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString)).withMillisOfDay(0).plusDays(1).minusSeconds(1).toString(dtfFormatString);

        List<String> fatherTimeList = calenderParser.ListBetweenTime(startTime, endTime);

        if (CollectionUtils.isEmpty(fatherTimeList) || fatherTimeList.size() > 1) {
            return calenderParser.getNearestTime(dateTime, true);
        }

        return fatherTimeList.get(0);
    }

    public static DateTime getCloseInDateTimeOfMonth(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[3];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.getDayOfMonth() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        int startTime = dateTime.getDayOfMonth() * 1000000;
        int endTime = dateTime.getDayOfMonth() * 1000000 + 235959;

        Integer dependencyTime = -1;
        for (int time : timeList) {
            // 判断上游月任务是否有同一天任务
            if (startTime < time && endTime > time) {
                // 说明在同一天内
                dependencyTime = time;
                break;
            }

            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }
        //说明应该是上一个月的最后一个执行时间
        if (dependencyTime == -1) {
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusMonths(1);
        }

        dateTime = getMonthTime(dateTime, dependencyTime);
        return dateTime;
    }

    public DateTime getCloseInDateTimeOfWeek(String[] timeFields, DateTime dateTime) {
        String dayStr = timeFields[5];
        String hourStr = timeFields[2];
        String minStr = timeFields[1];
        String secStr = timeFields[0];
        List<Integer> timeList = getSortTimeList(dayStr, hourStr, minStr, secStr);
        int targetTime = dateTime.dayOfWeek().get() * 1000000 + dateTime.getHourOfDay() * 10000 +
                dateTime.getMinuteOfHour() * 100 + dateTime.getSecondOfMinute();

        int startTime = dateTime.dayOfWeek().get() * 1000000;
        int endTime = dateTime.dayOfWeek().get() * 1000000 + 235959;

        Integer dependencyTime = -1;
        for (int time : timeList) {
            // 判断上游月任务是否有同一天任务
            if (startTime < time && endTime > time) {
                // 说明在同一天内
                dependencyTime = time;
                break;
            }

            if (targetTime < time) {
                break;
            }

            dependencyTime = time;
        }

        if (dependencyTime == -1) {//说明应该是上周的最后一个执行时间
            dependencyTime = timeList.get(timeList.size() - 1);
            dateTime = dateTime.minusWeeks(1);
        }

        dateTime = getDateTime(dateTime, dependencyTime);
        return dateTime;
    }


    /**
     * 仅对父任务是天任务生效，获取父任务(天调度)执行时间
     * 如果父任务执行时间大于当前子任务 job 执行时间且子任务不是天任务返回父任务昨天对执行时间，
     * 否则返回今天的执行时间
     *
     * @param dateTime     子任务 job 执行时间
     * @param fatherCron   父任务 cron 解析器
     * @param isSamePeriod 是否是同一调度周期类型
     * @return 父任务天实例执行时间
     */
    public DateTime getCloseInDateTimeOfDay(DateTime dateTime, ScheduleCronDayParser fatherCron, boolean isSamePeriod) {
        DateTime fatherCurrDayTime = dateTime.withTime(fatherCron.getHour(), fatherCron.getMinute(), 0, 0);
        if (fatherCurrDayTime.isAfter(dateTime) && !isSamePeriod) {//依赖昨天的
            fatherCurrDayTime = fatherCurrDayTime.minusDays(1);
        }

        return fatherCurrDayTime;
    }

    public DateTime getCloseInDateTimeOfHour(DateTime dateTime, ScheduleCronHourParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 100 + dateTime.getMinuteOfHour();
        int triggerTime = -1;

        for (int i = fatherCron.getBeginHour(); i <= fatherCron.getEndHour(); ) {
            int fatherTime = i * 100 + fatherCron.getBeginMinute();

            // 找最靠近的 fatherTime
            if (fatherTime > childTime) {
                break;
            }

            triggerTime = fatherTime;
            i += fatherCron.getGapNum();
        }

        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            // 命中的时间
            int hitHour = -1;
            for (int i = fatherCron.getBeginHour(); i <= fatherCron.getEndHour(); i += fatherCron.getGapNum()) {
                hitHour = i;
            }
            triggerTime = hitHour * 100 + fatherCron.getBeginMinute();
        }

        int hour = triggerTime / 100;
        int min = triggerTime % 100;
        dateTime = dateTime.withTime(hour, min, 0, 0);

        return dateTime;
    }

    public DateTime getCloseInDateTimeOfMin(DateTime dateTime, ScheduleCronMinParser fatherCron) {
        int childTime = dateTime.getHourOfDay() * 60 + dateTime.getMinuteOfHour();
        int triggerTime = -1;
        int begin = fatherCron.getBeginHour() * 60 + fatherCron.getBeginMin();
        int end = fatherCron.getEndHour() * 60 + fatherCron.getEndMin();

        if (end - begin < 0) {
            throw new RdosDefineException("illegal cron str :" + fatherCron.getCronStr());
        }

        for (int i = begin; i <= end; ) {
            if (i > childTime) {
                break;
            }
            triggerTime = i;
            i += fatherCron.getGapNum();
        }

        int hour = 0;
        int minute = 0;
        if (triggerTime == -1) {//获取昨天最后一个执行时间
            dateTime = dateTime.minusDays(1);
            int remainder = (end - begin) % fatherCron.getGapNum();
            //余数肯定不会超过59,所以直接减
            minute = fatherCron.getEndMin() - remainder;
            hour = fatherCron.getEndHour();

        } else {
            hour = triggerTime / 60;
            minute = triggerTime % 60;
        }
        dateTime = dateTime.withTime(hour, minute, 0, 0);

        return dateTime;
    }


    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Boolean isRoot, Integer appType, Long fillId, Long dtuicTenantId, AtomicInteger count) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>(16);
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                if (jsonNode.has("appType")) {
                    JsonNode node = jsonNode.get("appType");
                    appType = node.asInt();
                }

                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, isRoot, appType, fillId, dtuicTenantId, count);
                result.putAll(stringScheduleBatchJobMap);
            }
        }
        return result;
    }


    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(ArrayNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId, Long projectId, Long tenantId, Boolean isRoot, Integer appType, Long fillId, Long dtuicTenantId, AtomicInteger count) throws Exception {
        Map<String, ScheduleBatchJob> result = new HashMap<>();
        if (jsonObject != null && jsonObject.size() > 0) {
            for (JsonNode jsonNode : jsonObject) {
                if (jsonNode.has("appType")) {
                    JsonNode node = jsonNode.get("appType");
                    appType = node.asInt();
                }

                Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = buildFillDataJobGraph(jsonNode, fillJobName, needFather, triggerDay, createUserId, null, null, projectId, tenantId, isRoot, appType, fillId, dtuicTenantId, count);
                result.putAll(stringScheduleBatchJobMap);
            }
        }
        return result;
    }

    /**
     * 构建补数据的jobGraph
     *
     * @param jsonObject
     * @param triggerDay yyyy-MM-dd
     * @param projectId
     * @param tenantId
     * @param isRoot
     * @param fillId     补数据id
     * @return
     */
    public Map<String, ScheduleBatchJob> buildFillDataJobGraph(JsonNode jsonObject, String fillJobName, boolean needFather,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long projectId, Long tenantId, Boolean isRoot,
                                                               @Param("appType") Integer appType, Long fillId, Long dtuicTenantId, AtomicInteger count) throws Exception {

        if (!jsonObject.has("task")) {
            throw new RdosDefineException("can't get task field from jsonObject:" + jsonObject.toString(), ErrorCode.SERVER_EXCEPTION);
        }

        NumericNode fatherNode = (NumericNode) jsonObject.get("task");
        //生成jobList
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(fatherNode.asLong(), appType);
        String preStr = FILL_DATA_TYPE + "_" + fillJobName;
        Map<String, ScheduleBatchJob> result = Maps.newLinkedHashMap();
        Map<String, String> flowJobId = Maps.newHashMap();
        List<ScheduleBatchJob> batchJobs;
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, beginTime, endTime, projectId, tenantId, count);
        } else {
            batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, needFather,
                    true, triggerDay, fillJobName, createUserId, projectId, tenantId, count);
        }
        //针对专门补工作流子节点
        doSetFlowJobIdForSubTasks(batchJobs, flowJobId);
        //工作流情况的处理
        if (batchTask.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal() ||
                batchTask.getTaskType().intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
            for (ScheduleBatchJob jobRunBean : batchJobs) {
                flowJobId.put(batchTask.getTaskId() + "_" + jobRunBean.getCycTime() + "_" + batchTask.getAppType(), jobRunBean.getJobId());
            }
            //将工作流下的子任务生成补数据任务实例
            List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(), preStr, fillJobName, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, appType,null,null);
            LOGGER.error("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
            doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
            batchJobs.addAll(subTaskJobs);
        }
        for (ScheduleBatchJob batchJob : batchJobs) {
            if (batchJob.getScheduleJob() != null) {
                batchJob.getScheduleJob().setFillId(fillId);
                batchJob.getScheduleJob().setDtuicTenantId(dtuicTenantId);
            }
            if (CollectionUtils.isNotEmpty(batchJob.getBatchJobJobList())) {
                for (ScheduleJobJob scheduleJobJob : batchJob.getBatchJobJobList()) {
                    scheduleJobJob.setDtuicTenantId(dtuicTenantId);
                    scheduleJobJob.setAppType(appType);
                }
            }

            result.put(batchJob.getJobKey(), batchJob);
        }

        Integer sonAppType = appType;
        if (jsonObject.has("children")) {
            ArrayNode arrayNode = (ArrayNode) jsonObject.get("children");
            for (JsonNode node : arrayNode) {
                if (node.has("appType")) {
                    JsonNode jsonNode = node.get("appType");
                    sonAppType = jsonNode.asInt();
                }
                Map<String, ScheduleBatchJob> childNodeMap = buildFillDataJobGraph(node, fillJobName, true, triggerDay, createUserId, beginTime, endTime, projectId, tenantId, true, sonAppType, fillId, dtuicTenantId, count);
                if (childNodeMap != null) {
                    result.putAll(childNodeMap);
                }
            }
        }

        return result;
    }

    // 虽然没用到，预留具体时间缺省时补工作流数据
    private List<ScheduleBatchJob> buildSubTasksJobForFlowWork(Long taskId, String preStr, String fillJobName, String triggerDay, Long createUserId, Long projectId, Long tenantId, Integer appType) throws Exception {
        return buildSubTasksJobForFlowWork(taskId, preStr, fillJobName, triggerDay, createUserId, null, null, projectId, tenantId, appType,null,null);
    }

    private List<ScheduleBatchJob> buildSubTasksJobForFlowWork(Long taskId, String preStr, String fillJobName,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime,
                                                               Long projectId,
                                                               Long tenantId,
                                                               Integer appType,
                                                               Integer taskRunOrder,
                                                               Integer closeRetry) throws Exception {
        List<ScheduleBatchJob> result = Lists.newArrayList();
        //获取全部子任务
        List<ScheduleTaskShade> subTasks = batchTaskShadeService.getFlowWorkSubTasks(taskId, appType, null, null);
        AtomicInteger atomicInteger = new AtomicInteger();
        for (ScheduleTaskShade taskShade : subTasks) {
            String subKeyPreStr = preStr;
            String subFillJobName = fillJobName;
            //子任务需添加依赖关系
            updateScheduleConf(taskShade,taskRunOrder,closeRetry);
            List<ScheduleBatchJob> batchJobs = buildJobRunBean(taskShade, subKeyPreStr, EScheduleType.FILL_DATA, true, true,
                    triggerDay, subFillJobName, createUserId, beginTime, endTime, projectId, tenantId, atomicInteger);
            result.addAll(batchJobs);
        }

        return result;
    }

    /**
     * 根据cycTime计算bizTime
     *
     * @param cycTime cycTime格式必须是yyyyMMddHHmmss
     * @return
     */
    public String generateBizDateFromCycTime(String cycTime) {
        DateTime cycDateTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString));
        DateTime bizDate = cycDateTime.minusDays(1);
        return bizDate.toString(dtfFormatString);
    }

    private Date getTodayCron(ScheduleJob childScheduleJob, CronExpression expression) {
        String cycTime = childScheduleJob.getCycTime();
        Date startTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString)).withMillisOfDay(0).toDate();
        Date endTime = new DateTime(DateUtil.getTimestamp(cycTime, dtfFormatString)).withMillisOfDay(0).plusDays(1).minusSeconds(1).toDate();


        List<Date> todayList = Lists.newArrayList();

        Date isLastDate = expression.getNextValidTimeAfter(startTime);
        while (endTime.after(isLastDate)) {
            todayList.add(isLastDate);

            isLastDate = expression.getNextInvalidTimeAfter(isLastDate);
        }

        if (CollectionUtils.isEmpty(todayList) || todayList.size() > 1) {
            return null;
        }

        return todayList.get(0);
    }

    public static Date findLastDateBeforeCurrent(CronExpression expression, Date currTriggerDate,
                                                 LocalDateTime findDate, int change, boolean sameTask) {
        // 不同任务可能存在触发时间一样, 同个任务触发时间不可能相同, -1s防止下次执行时间刚好是本次
        findDate = sameTask ? findDate.plusSeconds(-1) : findDate;
        if (change == 20) {
            change = 0;
            findDate = findDate.plusDays(-1);
        } else if (change > 15) {
            findDate = findDate.plusYears(-1L);
        } else if (change > 10) {
            findDate = findDate.plusMonths(-1L);
        } else if (change > 5) {
            findDate = findDate.plusWeeks(-1L);
        } else {
            findDate = findDate.plusDays(-1L);
        }
        // 计算下次执行时间
        Date isLastDate = expression.getNextValidTimeAfter(new Date(findDate.toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli()));
        // 前一次执行时间在本次之前，需要确定是不是最后一次
        if (isLastDate.before(currTriggerDate)) {
            Date lastDate = isLastDate;
            while ((isLastDate = expression.getNextValidTimeAfter(isLastDate)).before(currTriggerDate)) {
                lastDate = isLastDate;
            }
            // 如果不是同一个任务，并且触发时间相同则返回这个触发时间否则返回上一个触发时间
            return !sameTask && !isLastDate.after(currTriggerDate) ? isLastDate : lastDate;
        } else if (!sameTask && !isLastDate.after(currTriggerDate)) {
            return currTriggerDate;
        }

        return findLastDateBeforeCurrent(expression, currTriggerDate, sameTask ? findDate.plusSeconds(1) : findDate, change + 1, sameTask);

    }

}
