package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.impl.*;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.SimpleScheduleJobPO;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/29
 */
@Component
public class JobDealer implements InitializingBean, ApplicationContextAware, ApplicationListener<ApplicationReadyEvent>, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDealer.class);

    private ApplicationContext applicationContext;

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobSubmittedDealer jobSubmittedDealer;

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * key: jobResource, 计算引擎类型
     * value: queue
     */
    private Map<String, GroupPriorityQueue> priorityQueueMap = Maps.newConcurrentMap();

    private Map<String, JobPriorityDealer> priorityMap = Maps.newConcurrentMap();

    private ExecutorService executors = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("taskSubmittedDealer"));

    @Override
    public void afterPropertiesSet() throws Exception {
        SystemPropertyUtil.setHadoopUserName(environmentContext.getHadoopUserName());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        executors.execute(jobSubmittedDealer);

        if (environmentContext.openStrongPriority()) {
            int createPriorityTime = environmentContext.getCreatePriorityTime();
            ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()  + "_CreateJob"));
            scheduledService.scheduleWithFixedDelay(
                    this,
                    createPriorityTime * 10L,
                    createPriorityTime,
                    TimeUnit.MILLISECONDS);
        }

        ExecutorService recoverExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
        recoverExecutor.submit(new RecoverDealer());
        LOGGER.info("Initializing {} ok", this.getClass().getName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取所有节点的队列大小信息（job已经submitted的除外）
     * key1: nodeAddress,
     * key2: jobResource
     */
    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        List<String> allNodeAddress = engineJobCacheDao.getAllNodeAddress();
        Map<String, Map<String, GroupInfo>> allNodeGroupInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeGroupInfo.computeIfAbsent(nodeAddress, na -> {
                Map<String, GroupInfo> nodeGroupInfo = Maps.newHashMap();
                priorityQueueMap.forEach((jobResource, priorityQueue) -> {
                    int groupSize = engineJobCacheDao.countByStage(jobResource, EJobCacheStage.unSubmitted(), nodeAddress);
                    Long minPriority = engineJobCacheDao.minPriorityByStage(jobResource, Lists.newArrayList(EJobCacheStage.PRIORITY.getStage(), EJobCacheStage.LACKING.getStage()), nodeAddress);
                    minPriority = minPriority == null ? 0 : minPriority;
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setSize(groupSize);
                    groupInfo.setPriority(minPriority);
                    nodeGroupInfo.put(jobResource, groupInfo);
                });
                return nodeGroupInfo;
            });
        }
        return allNodeGroupInfo;
    }

    /**
     * 提交优先级队列->最终提交到具体执行组件
     */
    public void addSubmitJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);

        jobClient.setCallBack((jobStatus) -> {
            updateJobStatus(jobClient.getTaskId(), jobStatus);
        });

        LOGGER.info("jobId:{} openStrongPriority: {} time:{}",jobClient.getTaskId(),environmentContext.openStrongPriority(),
                new DateTime().toString(DateUtil.STANDARD_DATETIME_FORMAT));
        //加入节点的优先级队列
        if (environmentContext.openStrongPriority()) {
            this.addPriorityCache(jobResource,jobClient);
        } else {
            this.addGroupPriorityQueue(jobResource, jobClient, true, true);
        }

        scheduleJobGanttTimeService.ganttChartTime(jobClient.getTaskId(), JobGanttChartEnum.JOB_SUBMIT_TIME, jobClient.getType(), jobClient.getComputeType().getType());
    }

    private void addPriorityCache(String jobResource, JobClient jobClient) {
        JobPriorityDealer priorityDealer = getPriorityDealer(jobResource);
        Integer priority = scheduleTaskPriorityService.selectPriorityByTaskId(jobClient.getTaskSourceId(), jobClient.getAppType());
        long maxPriority = Long.parseLong(priority + String.valueOf(System.currentTimeMillis()));

        jobClient.setPriority(maxPriority);
        String taskName = scheduleJobService.convertTaskName(jobClient.getJobName(),
                jobClient.getAppType(), jobClient.getProjectId(),
                jobClient.getTaskId(), jobClient.getType());

        engineJobCacheDao.insert(jobClient.getTaskId(),
                jobClient.getEngineType(),
                jobClient.getComputeType().getType(),
                EJobCacheStage.DB.getStage(),
                JobClientUtil.getParamAction(jobClient).toString(),
                getNodeAddress(jobResource, priority,priorityDealer), jobClient.getJobName(),
                maxPriority, jobResource, jobClient.getTenantId(), taskName);

        jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());

    }

    private String getNodeAddress(String jobResource, Integer priority,JobPriorityDealer priorityDealer) {
        try {
            String key = jobResource + "_" + priority;
            Long increment = redisTemplate.opsForValue().increment(key);
            if (increment == null) {
                return environmentContext.getLocalAddress();
            }

            List<String> aliveNode = priorityDealer.getAliveNode();

            if (CollectionUtils.isEmpty(aliveNode)) {
                return environmentContext.getLocalAddress();
            }
            return aliveNode.get(increment.intValue() % aliveNode.size());
        } catch (Exception e) {
            LOGGER.error("",e);
            return environmentContext.getLocalAddress();
        }
    }

    private JobPriorityDealer getPriorityDealer(String jobResource) {
        return priorityMap.computeIfAbsent(jobResource, k -> JobPriorityDealer
                .builder()
                .setJobResource(jobResource)
                .setApplicationContext(applicationContext)
                .setJobDealer(this)
                .build());
    }

    /**
     * job cache 表已经存在
     * @param jobClients
     */
    private void addSubmitJobVast(List<JobClient> jobClients) {
        List<String> taskIds = jobClients.stream().map(JobClient::getTaskId).collect(Collectors.toList());
        int stage = EJobCacheStage.DB.getStage();
        updateCacheBatch(taskIds, stage);
        scheduleJobDao.updateJobStatusByJobIds(taskIds, RdosTaskStatus.WAITENGINE.getStatus());
        LOGGER.info(" addSubmitJobBatch jobId:{} update", JSONObject.toJSONString(taskIds));
        for (JobClient jobClient : jobClients) {
            jobClient.setCallBack((jobStatus) -> {
                updateJobStatus(jobClient.getTaskId(), jobStatus);
            });

            String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
            //加入节点的优先级队列
            try {
                if (environmentContext.openStrongPriority()) {
                    this.getPriorityDealer(jobResource);
                } else {
                    this.addGroupPriorityQueue(jobResource, jobClient, true, false);
                }
            } catch (Exception e) {
                LOGGER.error("addSubmitJobVast {} error ", jobClient.getTaskId(),e);
                dealSubmitFailJob(jobClient.getTaskId(), e.toString());
            }
        }
    }

    /**
     * 容灾时对已经提交到执行组件的任务，进行恢复
     */
    public void afterSubmitJobVast(List<JobClient> jobClients) {
        List<String> taskIds = jobClients.stream().map(JobClient::getTaskId).collect(Collectors.toList());
        updateCacheBatch(taskIds, EJobCacheStage.SUBMITTED.getStage());
        LOGGER.info(" afterSubmitJobBatch jobId:{} update", JSONObject.toJSONString(taskIds));
        for (String taskId : taskIds) {
            shardCache.updateLocalMemTaskStatus(taskId, RdosTaskStatus.SUBMITTED.getStatus());
        }
    }

    public boolean addGroupPriorityQueue(String jobResource, JobClient jobClient, boolean judgeBlock, boolean insert) {
        try {
            GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
            boolean rs = groupPriorityQueue.add(jobClient, judgeBlock, insert);
            if (!rs) {
                saveCache(jobClient, jobResource, EJobCacheStage.DB.getStage(), insert);
            }
            return rs;
        } catch (Exception e) {
            LOGGER.error("", e);
            dealSubmitFailJob(jobClient.getTaskId(), e.toString());
            return false;
        }
    }

    public boolean addRestartJob(JobClient jobClient) {
        String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
        if (environmentContext.openStrongPriority()) {
            JobPriorityDealer priorityDealer = getPriorityDealer(jobResource);
            return priorityDealer.addRestartJob(jobClient);
        } else {
            GroupPriorityQueue groupPriorityQueue = getGroupPriorityQueue(jobResource);
            return groupPriorityQueue.addRestartJob(jobClient);
        }
    }

    public GroupPriorityQueue getGroupPriorityQueue(String jobResource) {
        GroupPriorityQueue groupPriorityQueue = priorityQueueMap.computeIfAbsent(jobResource, k -> GroupPriorityQueue.builder()
                .setApplicationContext(applicationContext)
                .setJobResource(jobResource)
                .setJobDealer(this)
                .build());
        return groupPriorityQueue;
    }

    public void updateJobStatus(String jobId, Integer status) {
        scheduleJobDao.updateJobStatus(jobId, status);
        LOGGER.info("jobId:{} update job status:{}.", jobId, status);
    }

    public void saveCache(JobClient jobClient, String jobResource, int stage, boolean insert) {
        String nodeAddress = environmentContext.getLocalAddress();
        if (insert) {
            String taskName = scheduleJobService.convertTaskName(jobClient.getJobName(), jobClient.getAppType(), jobClient.getProjectId(), jobClient.getTaskId(), jobClient.getType());
            engineJobCacheDao.insert(jobClient.getTaskId(), jobClient.getEngineType(), jobClient.getComputeType().getType(), stage, JobClientUtil.getParamAction(jobClient).toString(), nodeAddress, jobClient.getJobName(), jobClient.getPriority(), jobResource, jobClient.getTenantId(), taskName);
            jobClient.doStatusCallBack(RdosTaskStatus.WAITENGINE.getStatus());
        } else {
            engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority(), null);
        }
    }

    private void updateCacheBatch(List<String> taskIds, int stage) {
        String nodeAddress = environmentContext.getLocalAddress();
        engineJobCacheDao.updateStageBatch(taskIds, stage, nodeAddress);
    }

    public void updateCache(JobClient jobClient, int stage) {
        String nodeAddress = environmentContext.getLocalAddress();
        engineJobCacheDao.updateStage(jobClient.getTaskId(), stage, nodeAddress, jobClient.getPriority(), null);
    }

    public String getAndUpdateEngineLog(String jobId, String engineJobId, String appId, Long dtuicTenantId) {

        if(StringUtils.isBlank(engineJobId)){
            return "";
        }
        String engineLog = null;
        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            if (null == engineJobCache) {
                return "";
            }
            String engineType = engineJobCache.getEngineType();
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Map<String, Object> pluginInfo = paramAction.getPluginInfo();
            JobIdentifier jobIdentifier = new JobIdentifier(engineJobId, appId, jobId, dtuicTenantId, engineType,
                    taskParamsService.parseDeployTypeByTaskParams(paramAction.getTaskParams(), engineJobCache.getComputeType(), engineJobCache.getEngineType(), dtuicTenantId).getType(),
                    paramAction.getUserId(), paramAction.getResourceId(), MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo), paramAction.getComponentVersion());
            jobIdentifier.setProjectId(paramAction.getProjectId());
            jobIdentifier.setAppType(paramAction.getAppType());
            jobIdentifier.setTaskType(paramAction.getTaskType());
            jobIdentifier.setClassArgs(paramAction.getExeArgs());
            //从engine获取log
            jobIdentifier.setArchiveFsDir(scheduleJobService.getJobExtraInfoOfValue(jobIdentifier.getTaskId(), JobResultConstant.ARCHIVE));
            engineLog = operatorDistributor.getOperator(ClientTypeEnum.getClientTypeEnum(paramAction.getClientType()),paramAction.getEngineType()).getEngineLog(jobIdentifier);
            if (engineLog != null) {
                scheduleJobExpandDao.updateEngineLog(jobId, engineLog);
            }
        } catch (Throwable e) {
            LOGGER.error("getAndUpdateEngineLog error jobId:{} error:.", jobId, e);
        }
        return engineLog;
    }

    /**
     * master 节点分发任务失败
     *
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, String errorMsg) {
        engineJobCacheDao.delete(taskId);
        scheduleJobExpandDao.updateLogInfoByJobId(taskId,errorMsg,RdosTaskStatus.SUBMITFAILD.getStatus());
        scheduleJobDao.jobFinish(taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
        LOGGER.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
    }

    @Override
    public void run() {
        String localAddress = environmentContext.getLocalAddress();

        try {
            if (!environmentContext.openStrongPriority()) {
                LOGGER.info("priority close nodeAddress:{}",localAddress);
                return;
            }

            List<String> jobResources = engineJobCacheDao.getAllJobResource(localAddress);

            for (String jobResource : jobResources) {
                getPriorityDealer(jobResource);
            }
        } catch (Exception e) {
            LOGGER.error("----broker:{} RecoverDealer error:", localAddress, e);
        }
    }

    class RecoverDealer implements Runnable {
        @Override
        public void run() {
            LOGGER.info("-----The task resumes after restart----");
            String localAddress = environmentContext.getLocalAddress();
            try {
                long startId = 0L;
                while (true) {
                    List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, localAddress, null, null,null);
                    if (CollectionUtils.isEmpty(jobCaches)) {
                        //两种情况：
                        //1. 可能本身没有jobcaches的数据
                        //2. master节点已经为此节点做了容灾
                        break;
                    }
                    List<JobClient> unSubmitClients = new ArrayList<>();
                    List<JobClient> submitClients = new ArrayList<>();
                    for (EngineJobCache jobCache : jobCaches) {
                        try {
                            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                            JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
                            if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                                unSubmitClients.add(jobClient);
                            } else {
                                submitClients.add(jobClient);
                            }
                            startId = jobCache.getId();
                        } catch (Exception e) {
                            LOGGER.error("RecoverDealer run jobId {} error", jobCache.getJobId(), e);
                            //数据转换异常--打日志
                            dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + ExceptionUtil.getErrorMessage(e));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(unSubmitClients)) {
                        addSubmitJobVast(unSubmitClients);
                    }
                    if (CollectionUtils.isNotEmpty(submitClients)) {
                        afterSubmitJobVast(submitClients);
                    }
                }
                LOGGER.info("cache deal end");

                // 恢复没有被容灾，但是状态丢失的任务
                long jobStartId = 0;
                // 扫描出 status = 0 和 19  phaseStatus = 1 
                List<SimpleScheduleJobPO> jobs = scheduleJobDao.listJobByStatusAddressAndPhaseStatus(jobStartId, RdosTaskStatus.getUnSubmitStatus(), localAddress,JobPhaseStatus.JOIN_THE_TEAM.getCode());
                while (CollectionUtils.isNotEmpty(jobs)) {
                    List<String> jobIds = jobs.stream().map(SimpleScheduleJobPO::getJobId).collect(Collectors.toList());
                    LOGGER.info("update job ids {}", jobIds);

                    scheduleJobDao.updateJobStatusAndPhaseStatusByIds(jobIds, RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode());
                    jobStartId = jobs.get(jobs.size()-1).getId();
                    jobs = scheduleJobDao.listJobByStatusAddressAndPhaseStatus(jobStartId, RdosTaskStatus.getUnSubmitStatus(), localAddress, JobPhaseStatus.JOIN_THE_TEAM.getCode());
                }
                LOGGER.info("job deal end");
            } catch (Exception e) {
                LOGGER.error("----broker:{} RecoverDealer error:", localAddress, e);
            }

            LOGGER.info("-----After the restart, the task ends and resumes-----");
        }
    }

}
