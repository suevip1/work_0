package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleProjectParamDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.JobBuildType;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.param.CatalogParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.AppTypeVO;
import com.dtstack.engine.api.vo.JobIdAndStatusVo;
import com.dtstack.engine.api.vo.JobLogVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.FillConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.ETaskGroupEnum;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleSqlTextTempDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dto.ApplicationInfo;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobLogDealer;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.jobdealer.JobSubmitDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.resource.JobComputeResourcePlain;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.model.cluster.system.Context;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.ConditionBranchJobStartTrigger;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.JobStartTriggerBase;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.MultiEngineFactory;
import com.dtstack.engine.master.pipeline.IPipeline;
import com.dtstack.engine.master.pipeline.PipelineBuilder;
import com.dtstack.engine.master.pipeline.params.UploadParamPipeline;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.utils.DtJobIdWorker;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.utils.JobExecuteOrderUtil;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.EngineJobRetry;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.engine.po.ScheduleSqlTextTemp;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 接收http请求
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 */
@Service
public class ActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private Context context;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private MultiEngineFactory multiEngineFactory;

    @Autowired
    private ScheduleSqlTextTempDao sqlTextTempDao;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobStruct scheduleJobStruct;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private ScheduleJobExpandDao  scheduleJobExpandDao;

    private final ObjectMapper objMapper = new ObjectMapper();

    @Autowired
    private JobComputeResourcePlain jobComputeResourcePlain;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ScheduleProjectParamService scheduleProjectParamService;

    @Autowired
    private ScheduleTaskWorkflowParamService scheduleTaskWorkflowParamService;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private JobLogDealer jobLogDealer;

    @Autowired
    private ScheduleJobService scheduleJobService;

    private DtJobIdWorker jobIdWorker;

    @Resource
    private ConditionBranchJobStartTrigger conditionBranchJobStartTrigger;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Resource
    private ThreadPoolTaskExecutor commonExecutor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 真实 ip:port (兼容客户配置域名的场景)
     */
    private volatile String localRealAddress;

    public static final String RUN_JOB_NAME = "runJob";
    private static final String RUN_DELIMITER = "_";

    private ThreadPoolExecutor logTimeOutPool =  new ThreadPoolExecutor(5, 5,
                                          60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
                new CustomThreadFactory("logTimeOutPool"),
                new CustomThreadRunsPolicy("logTimeOutPool", "log"));

    private static final PropertyFilter propertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("taskParams") || name.equalsIgnoreCase("sqlText"));

    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(ParamActionExt paramActionExt){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start  actionParam: {}", JSONObject.toJSONString(paramActionExt,propertyFilter));
        }
        try{
            checkParam(paramActionExt);
            ClientTypeEnum clientType = context.getClientType(paramActionExt.getEngineType(), paramActionExt.getTaskType());
            if (clientType == null) {
                LOGGER.error("jobId {} taskType {} not support", paramActionExt.getTaskId(), paramActionExt.getTaskType());
                return Boolean.FALSE;
            }
            //taskId唯一去重，并发请求时以数据库taskId主键去重返回false
            boolean canAccepted = receiveStartJob(paramActionExt);
            //会对重复数据做校验
            if(canAccepted){
                // 条件分支任务直接运行
                if (EScheduleJobType.CONDITION_BRANCH.getType().equals(paramActionExt.getTaskType())) {
                    conditionBranchJobStartTrigger.handleConditionTask(paramActionExt.getSqlText(), paramActionExt.getTaskId(), paramActionExt.getFlowJobId());
                    return true;
                }
                // 虚节点写入开始时间和结束时间，工作流临时运行会走到这
                // com.dtstack.engine.master.workflow.temporary.WorkflowTemporaryActuator.startJob
                if (EScheduleJobType.VIRTUAL.getType().equals(paramActionExt.getTaskType())) {
                    ScheduleJob update = new ScheduleJob();
                    update.setJobId(paramActionExt.getTaskId());
                    update.setExecTime(0L);
                    long currentTimeMillis = System.currentTimeMillis();
                    update.setExecStartTime(new Timestamp(currentTimeMillis));
                    update.setExecEndTime(new Timestamp(currentTimeMillis));
                    update.setStatus(RdosTaskStatus.FINISHED.getStatus());
                    update.setAppType(paramActionExt.getAppType());
                    scheduleJobDao.updateStatusWithExecTime(update);
                    scheduleJobExpandDao.updateJobExpandByJobId(update.getJobId(), FillConstant.VIRTUAL,RdosTaskStatus.FINISHED.getStatus(),new Date(),new Date());
                    return true;
                }
                if (EScheduleJobType.EVENT.getType().equals(paramActionExt.getTaskType())) {
                    //处理 事件任务
                    //事件任务周期调度和补数据 不会走addSubmitJob
                    //com.dtstack.engine.master.multiengine.UnnecessaryPreprocessJobService 即变更状态为running
                    scheduleJobService.updateJobToRunning(paramActionExt.getTaskId());
                    jobLogDealer.addExpireLogJob(paramActionExt.getSqlText(), paramActionExt.getTaskId(), paramActionExt.getComputeType(), paramActionExt.getEngineType(), clientType);
                    return true;
                }
                JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
                jobClient.setClientType(clientType);
                jobClient.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
                jobDealer.addSubmitJob(jobClient);
                return true;
            }
            LOGGER.warn("Job taskId：" + paramActionExt.getTaskId() + " duplicate submissions are not allowed");
        }catch (Exception e){
            LOGGER.error("", e);
            //任务提交出错 需要将状态从提交中 更新为失败 否则一直在提交中
            String taskId = paramActionExt.getTaskId();
            try {
                if (StringUtils.isNotBlank(taskId)) {
                    LOGGER.error("Job taskId：" + taskId + " submit error ", e);
                    ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
                    if (scheduleJob == null) {
                        //新job 任务
                        scheduleJob = buildScheduleJob(paramActionExt);
                        scheduleJob.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());
                        scheduleJobDao.insert(scheduleJob);
                        ScheduleJobExpand scheduleJobExpand = buildScheduleJobExpand(
                                paramActionExt.getTaskId(),
                                GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()),
                                "",
                                "",
                                ""
                        );
                        scheduleJobExpandDao.insert(scheduleJobExpand);
                    } else {
                        //直接失败
                        scheduleJobExpandDao.updateLogInfoByJobId(taskId,GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()),RdosTaskStatus.SUBMITFAILD.getStatus());
                        scheduleJobDao.jobFinish(taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("", ex);
            }
        }
        return false;
    }

    private ScheduleJobExpand buildScheduleJobExpand(String jobId,String logInfo,String engineLog,String jobExtraInfo,String retryTaskParams) {
        ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
        scheduleJobExpand.setLogInfo(logInfo);
        scheduleJobExpand.setJobId(jobId);
        scheduleJobExpand.setEngineLog(engineLog);
        scheduleJobExpand.setJobExtraInfo(jobExtraInfo);
        scheduleJobExpand.setRetryTaskParams(retryTaskParams);
        return scheduleJobExpand;
    }

    public Boolean startJob(ScheduleTaskShade batchTask,String jobId, String flowJobId) {
        LOGGER.info("startJob ScheduleTaskShade: {} jobId:{} flowJobId:{} ", JSONObject.toJSONString(batchTask), jobId, flowJobId);
        try {
            ParamActionExt paramActionExt = paramActionExt(batchTask, jobId, flowJobId);
            if (paramActionExt == null) {
                throw new RdosDefineException("extraInfo can't null or empty string");
            }
            return this.start(paramActionExt);
        } catch (Exception e) {
            LOGGER.error("startJob {} error",jobId, e);
            throw new RdosDefineException("task run fail: job init error");
        }
    }

    public Boolean startJobForWorkflowTemporary(ParamTaskAction paramTaskAction) {
        if (Objects.isNull(paramTaskAction)) {
            return false;
        }
        String jobId = paramTaskAction.getJobId();
        LOGGER.info("startJob ScheduleTaskShade: {} jobId:{}", JSONObject.toJSONString(paramTaskAction), paramTaskAction.getJobId());
        ParamActionExt paramActionExt = null;
        try {
            paramActionExt = paramActionExt(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
            if (paramActionExt == null) {
                throw new RdosDefineException("extraInfo can't null or empty string");
            }
            return this.start(paramActionExt);
        } catch (Exception e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("startJob {} error {}", jobId, errorMessage, e);
            ScheduleJob existJob = scheduleJobDao.getRdosJobByJobId(jobId);
            if (Objects.isNull(existJob)) {
                ScheduleJob scheduleJob = buildScheduleJob(paramTaskAction);
                if (Objects.nonNull(scheduleJob)) {
                    scheduleJob.setJobKey(String.format("%s%s%s", "workflowTempInitErrorJob", scheduleJob.getTaskId() + scheduleJob.getAppType(), new DateTime().toString("yyyyMMddmmss")));
                    scheduleJob.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());
                    scheduleJobDao.insert(scheduleJob);
                    ScheduleJobExpand scheduleJobExpand = buildScheduleJobExpand(
                            paramTaskAction.getJobId(),
                            GenerateErrorMsgUtil.generateErrorMsg(errorMessage), "", "", ""
                    );
                    scheduleJobExpandDao.insert(scheduleJobExpand);
                }
            }
            throw new RdosDefineException(String.format("task run fail: job init error: %s", errorMessage), e);
        }
    }


    public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws Exception {
        if (StringUtils.isBlank(jobId)) {
            jobId = this.generateUniqueSign();
        }
        LOGGER.info("startJob ScheduleTaskShade: {} jobId:{} flowJobId:{} ", JSONObject.toJSONString(batchTask), jobId, flowJobId);
        ScheduleJob scheduleJob = buildScheduleJob(batchTask, jobId, flowJobId);
        JSONObject info = JSONObject.parseObject(batchTask.getExtraInfo());
        //临时运行 需要将global 转换一层
        paramService.convertGlobalToParamType(batchTask.getExtraInfo(), (globalTaskParams, otherTaskParams) -> {
            List<ScheduleTaskParamShade> totalTaskParamShade = new ArrayList<>(otherTaskParams);
            List<ScheduleTaskParamShade> globalTaskShades = paramStruct.BOtoTaskParams(globalTaskParams);
            totalTaskParamShade.addAll(globalTaskShades);
            info.put(GlobalConst.taskParamToReplace, JSONObject.toJSONString(totalTaskParamShade));
        });
        ParamActionExt paramActionExt = paramActionExt(batchTask, scheduleJob, info);
        if (paramActionExt == null) {
            throw new RdosDefineException("extraInfo can't null or empty string");
        }

        paramActionExt.setCycTime(scheduleJob.getCycTime());
        paramActionExt.setTaskSourceId(batchTask.getTaskId());
        paramActionExt.setProjectId(batchTask.getProjectId());
        paramActionExt.setDtuicTenantId(batchTask.getDtuicTenantId());
        paramActionExt.setComponentVersion(batchTask.getComponentVersion());
        paramActionExt.setBusinessType(batchTask.getBusinessType());
        paramActionExt.setBusinessDate(scheduleJob.getBusinessDate());
        paramActionExt.setFlowJobId(flowJobId);
        return paramActionExt;
    }


    public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, ScheduleJob scheduleJob, JSONObject extraInfo) throws Exception {
        return this.parseParamActionExt(scheduleJob, batchTask, extraInfo);
    }

    public ScheduleJob buildScheduleJob(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws IOException, ParseException {
        String cycTime = jobRichOperator.getCycTime(0);
        String scheduleConf = batchTask.getScheduleConf();
        if (ETaskGroupEnum.MANUAL.getType().equals(batchTask.getTaskGroup())) {
            scheduleConf = ScheduleConfUtils.buildManualTaskScheduleConf(scheduleConf);
        }
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setJobName(RUN_JOB_NAME+RUN_DELIMITER+batchTask.getName()+RUN_DELIMITER+cycTime);
        scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
        scheduleJob.setComputeType(batchTask.getComputeType());

        scheduleJob.setTenantId(batchTask.getTenantId());
        scheduleJob.setProjectId(getOrDefault(batchTask.getProjectId(), -1L));
        //dtuicTenantId() 取 tenantId字段
        scheduleJob.setDtuicTenantId(getOrDefault(batchTask.getDtuicTenantId(), -1L));
        scheduleJob.setAppType(getOrDefault(batchTask.getAppType(), 0));
        scheduleJob.setJobKey(String.format("%s%s%s", "tempJob", batchTask.getTaskId() + batchTask.getAppType(), new DateTime().toString("yyyyMMdd")));
        scheduleJob.setTaskId(getOrDefault(batchTask.getTaskId(),-1L));
        scheduleJob.setCreateUserId(getOrDefault(batchTask.getOwnerUserId(), -1L));
        scheduleJob.setIsRestart(Restarted.NORMAL.getStatus());

        scheduleJob.setType(EScheduleType.TEMP_JOB.getType());
        scheduleJob.setBusinessDate(getOrDefault(jobRichOperator.getCycTime(-1), ""));
        scheduleJob.setResourceId(batchTask.getResourceId());
        scheduleJob.setCycTime(getOrDefault(cycTime, DateTime.now().toString("yyyyMMddHHmmss")));

        if (StringUtils.isNotBlank(scheduleConf)) {
            Map jsonMap = objMapper.readValue(scheduleConf, Map.class);
            jsonMap.put("isFailRetry", false);
            scheduleConf = JSON.toJSONString(jsonMap);
            batchTask.setScheduleConf(scheduleConf);
            JSONObject scheduleConfObj = JSONObject.parseObject(scheduleConf);
            //自定义调度日历和普通调度周期一样处理，临时运行不用管调度周期
            scheduleJob.setDependencyType(ScheduleFactory.parseSelfReliance(scheduleConfObj.toJavaObject(Map.class)));
            scheduleJob.setPeriodType(NumberUtils.toInt(String.valueOf(JSONPath.eval(scheduleConfObj, "$.periodType")), ESchedulePeriodType.DAY.getVal()));
            scheduleJob.setMaxRetryNum(NumberUtils.toInt(String.valueOf(JSONPath.eval(scheduleConfObj, "$.maxRetryNum")), 0));
        }

        scheduleJob.setFlowJobId(getOrDefault(flowJobId, "0"));
        scheduleJob.setTaskType(getOrDefault(batchTask.getTaskType(), -2));
        scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJob.setVersionId(getOrDefault(batchTask.getVersionId(), 0));
        scheduleJob.setComputeType(getOrDefault(batchTask.getComputeType(), 1));
        scheduleJob.setBusinessType(batchTask.getBusinessType());
        scheduleJob.setJobBuildType(JobBuildType.IMMEDIATELY.getType());
        scheduleJob.setJobExecuteOrder(JobExecuteOrderUtil.buildJobExecuteOrder(DateUtil.getUnStandardFormattedDate(System.currentTimeMillis()), new AtomicInteger()));

        return scheduleJob;
    }


    private ScheduleJob buildScheduleJob(ParamTaskAction paramTaskAction)  {
        ScheduleTaskShade batchTask = paramTaskAction.getBatchTask();
        String jobId = paramTaskAction.getJobId();
        String flowJobId = paramTaskAction.getFlowJobId();
        ScheduleJob scheduleJob = null;
        try {
            scheduleJob = buildScheduleJob(batchTask, jobId, flowJobId);
        } catch (Exception ignore) {}
        return scheduleJob;
    }


    public ParamActionExt parseParamActionExt(ScheduleJob scheduleJob, ScheduleTaskShade batchTask, JSONObject info) throws Exception {
        if (info == null) {
            throw new RdosDefineException("extraInfo can't null or empty string");
        }

        Integer multiEngineType = info.getInteger("multiEngineType");
        String ldapUserName = info.getString("ldapUserName");
        if (org.apache.commons.lang.StringUtils.isNotBlank(ldapUserName)) {
            info.remove("ldapUserName");
            info.remove("ldapPassword");
            info.remove("dbName");
        }
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        Object tenantId = actionParam.get("tenantId");
        if (Objects.isNull(tenantId)) {
            actionParam.put("tenantId", batchTask.getDtuicTenantId());
        }
        fillConsoleParam(actionParam, batchTask.getTaskId(), batchTask.getAppType(), scheduleJob.getJobId());
        dealActionParam(actionParam,multiEngineType,batchTask,scheduleJob);
        actionParam.put("resourceId", batchTask.getResourceId());
        actionParam.put("name", scheduleJob.getJobName());
        actionParam.put("taskId", scheduleJob.getJobId());
        actionParam.put("taskType", batchTask.getTaskType());
        actionParam.put("appType", batchTask.getAppType());
        actionParam.put("componentVersion",batchTask.getComponentVersion());
        actionParam.put("type",scheduleJob.getType());
        actionParam.put("fillId",scheduleJob.getFillId());
        actionParam.put("projectId",batchTask.getProjectId());

        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (Objects.isNull(userId)) {
            // 任务责任人，用于后续获取 ldap 账号
            userId = batchTask.getOwnerUserId();
            actionParam.put(ConfigConstant.USER_ID, userId);
        }

        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        JSONObject scheduleConf = JSONObject.parseObject(batchTask.getScheduleConf());
        if (scheduleConf != null && scheduleConf.containsKey(GlobalConst.IS_FAIL_RETRY)) {
            if (EScheduleType.FILL_DATA.getType().equals(scheduleJob.getType())) {
                actionParam.put(GlobalConst.MAX_RETRY_NUM, scheduleJob.getMaxRetryNum());
            } else {
                actionParam.put(GlobalConst.IS_FAIL_RETRY, scheduleConf.getBooleanValue(GlobalConst.IS_FAIL_RETRY));
                if (scheduleConf.getBooleanValue(GlobalConst.IS_FAIL_RETRY)) {
                    int maxRetryNum = scheduleConf.getIntValue(GlobalConst.MAX_RETRY_NUM) == 0 ? 3 : scheduleConf.getIntValue(GlobalConst.MAX_RETRY_NUM);
                    actionParam.put(GlobalConst.MAX_RETRY_NUM, maxRetryNum);
                    //离线 单位 分钟
                    Integer retryIntervalTime = scheduleConf.getInteger(GlobalConst.RETRY_INTERVAL_TIME);
                    if (null != retryIntervalTime) {
                        actionParam.put(GlobalConst.RETRY_INTERVAL_TIME, retryIntervalTime * 60 * 1000);
                    }
                } else {
                    actionParam.put(GlobalConst.MAX_RETRY_NUM, 0);
                }
            }
        }
        if (EScheduleJobType.SYNC.getType().equals(batchTask.getTaskType())) {
            //数据同步需要解析是perjob 还是session
            EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(batchTask.getTaskParams(), batchTask.getComputeType(), EngineType.Flink.name(), batchTask.getDtuicTenantId());
            actionParam.put("deployMode", eDeployMode.getType());
        }
        return PublicUtil.mapToObject(actionParam, ParamActionExt.class);
    }

    /**
     * 把控制台的全局参数里面的系统参数默认全部带上
     * 优先级关系：补数据时修改的自定义参数 > 提交任务时的任务参数 > 全局参数 > 系统参数
     *
     * @param actionParam 任务参数
     * @param taskId      taskId
     * @param appType     应用类型
     * @param jobId       实例任务 id
     */
    public void fillConsoleParam(Map<String, Object> actionParam, Long taskId, Integer appType, String jobId) {
        // 用户自定义参数
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            taskParamsToReplace = new ArrayList<>();
        }
        Map<String, ScheduleTaskParamShade> paramUniqueMap = taskParamsToReplace.stream()
                // 基于全局参数偏移量的场景下，可能存在使用同一个全局参数，但是替换目标不一样的情况
                .collect(Collectors.toMap(this::generateUniqueKeyForTaskParamShade, Function.identity()));

        Map<String, ScheduleTaskParamShade> replaceOffsetParaName = new HashMap<>();

        // 根据实际要替换的目标建立一个映射
        Map<String, ScheduleTaskParamShade> replaceTargetMap = taskParamsToReplace.stream()
                .collect(Collectors.toMap(e -> StringUtils.isNotBlank(e.getReplaceTarget()) ? e.getReplaceTarget() : e.getParamName(), Function.identity()));

        // 获取任务绑定的控制台参数，包括系统参数
        List<ScheduleTaskParamShade> taskBindParams = getALLTaskBindGlobalParam(taskId, appType);
        // 获取任务绑定的项目参数
        List<ScheduleTaskParamShade> taskBindProjectParams = getAllTaskBindProjectParams(taskId, appType);
        // 获取任务绑定的工作流参数
        List<ScheduleTaskParamShade> taskBindWorkflowParams = getAllTaskBindWorkflowParams(taskId, appType);
        // 同一任务全局参数、项目参数、工作流参数不会同名，故此处可以直接加入
        taskBindParams.addAll(taskBindProjectParams);
        taskBindParams.addAll(taskBindWorkflowParams);

        for (ScheduleTaskParamShade taskBindParam : taskBindParams) {
            // 如果实际要替换的目标已经存在对应的参数了，就跳过
            String replaceTarget = StringUtils.isNotBlank(taskBindParam.getReplaceTarget()) ? taskBindParam.getReplaceTarget() : taskBindParam.getParamName();
            if (Objects.nonNull(replaceTargetMap.get(replaceTarget))) {
                continue;
            }
            replaceOffsetParaName.put(replaceTarget,taskBindParam);
            // 提交过来的参数优先级高于 db 中存在的绑定的参数
            paramUniqueMap.putIfAbsent(generateUniqueKeyForTaskParamShade(taskBindParam), taskBindParam);
        }

        // 补数据时修改的自定义参数优先级最高, 覆盖任务级别的自定义参数
        // 全局参数偏移量 scheduleJobParams type为10 这里需要修正 以taskParam 为准
        // 任务设置 bdp.system.bizdate#-2 补数据设置bdp.system.bizdate#-3 补数据为准 (暂不考虑 补数据切换了全局参数名称 设置bdp.system.bizdate2#-1)
        List<ScheduleTaskParamShade> jobBindParams = getAllJobBindParams(jobId);
        for (ScheduleTaskParamShade jobBindParam : jobBindParams) {
            if (EParamType.GLOBAL_PARAM_BASE_CYCTIME.getType().equals(jobBindParam.getType())
                    && replaceOffsetParaName.containsKey(jobBindParam.getParamName())) {
                ScheduleTaskParamShade taskParam = replaceOffsetParaName.get(jobBindParam.getParamName());
                //覆盖offset
                String offset = paramService.getOffset(jobBindParam.getParamCommand());
                taskParam.setOffset(offset);
                paramUniqueMap.put(generateUniqueKeyForTaskParamShade(taskParam), taskParam);
            } else {
                paramUniqueMap.put(generateUniqueKeyForTaskParamShade(jobBindParam), jobBindParam);
            }
        }

        List<ScheduleTaskParamShade> values = new ArrayList<>(paramUniqueMap.values());
        actionParam.put(GlobalConst.taskParamToReplace, JSONObject.toJSONString(values));
    }

    private List<ScheduleTaskParamShade> getAllJobBindParams(String jobId) {
        List<ScheduleJobParam> jobParams = paramService.selectParamByJobId(jobId);
        return paramStruct.jobParamToTaskParams(jobParams);
    }

    private List<ScheduleTaskParamShade> getALLTaskBindGlobalParam(Long taskId, Integer appType) {
        // 系统参数
        List<ConsoleParam> consoleSysParams = paramService.selectSysParam();
        // 全局参数
        List<ConsoleParamBO> taskBindParams = paramService.selectByTaskId(taskId, appType);
        taskBindParams.addAll(paramStruct.toBOs(consoleSysParams));

        List<ScheduleTaskParamShade> allScheduleJobParamShades = Lists.newArrayList();
        allScheduleJobParamShades.addAll(paramStruct.BOtoTaskParams(taskBindParams));
        return allScheduleJobParamShades;
    }

    /**
     * 获取任务绑定的项目参数
     *
     * @param taskId
     * @param appType
     * @return
     */
    private List<ScheduleTaskParamShade> getAllTaskBindProjectParams(Long taskId, Integer appType) {
        List<ScheduleProjectParamDTO> taskBindProjectParams = scheduleProjectParamService.findTaskBindProjectParams(taskId, appType);
        return ScheduleProjectParamService.transProjectParams2TaskParamShades(taskBindProjectParams);
    }

    /**
     * 获取任务绑定的工作流参数
     *
     * @param taskId
     * @param appType
     * @return
     */
    private List<ScheduleTaskParamShade> getAllTaskBindWorkflowParams(Long taskId, Integer appType) {
        ScheduleTaskShade oneTask = scheduleTaskShadeDao.getOne(taskId, appType);
        if (oneTask != null && GlobalConst.ZERO_FLOW_ID.equals(oneTask.getFlowId())) {
            // 主节点不会使用工作流参数，所以无需获取工作流参数
            return Collections.emptyList();
        }
        return scheduleTaskWorkflowParamService.findTaskBindWorkflowParams(taskId, appType);
    }

    /**
     * @param scheduleTaskParamShade
     * @return paramName@type@replaceTarget
     */
    private String generateUniqueKeyForTaskParamShade(ScheduleTaskParamShade scheduleTaskParamShade) {
        if (Objects.isNull(scheduleTaskParamShade)) {
            return StringUtils.EMPTY;
        }
        return scheduleTaskParamShade.getParamName() + GlobalConst.STAR + scheduleTaskParamShade.getType() + GlobalConst.STAR + scheduleTaskParamShade.getReplaceTarget();
    }

    private void dealActionParam(Map<String, Object> actionParam, Integer multiEngineType, ScheduleTaskShade batchTask, ScheduleJob scheduleJob) throws Exception {
        IPipeline pipeline = null;
        String pipelineConfig = null;
        if (actionParam.containsKey(PipelineBuilder.pipelineKey)) {
            pipelineConfig = (String) actionParam.get(PipelineBuilder.pipelineKey);
            pipeline = PipelineBuilder.buildPipeline(pipelineConfig);
        }
        if (pipeline == null) {
            //走旧逻辑
            JobStartTriggerBase jobTriggerService = multiEngineFactory.getJobTriggerService(multiEngineType, batchTask.getTaskType(), batchTask.getEngineType());
            jobTriggerService.readyForTaskStartTrigger(actionParam, batchTask, scheduleJob);
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);

        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (Objects.isNull(userId)) {
            // 任务责任人，用于后续获取 ldap 账号
            userId = batchTask.getOwnerUserId();
        }
        final Long uicUserId = userId;

        Map<String, Object> pipelineInitMap = PipelineBuilder.getPipelineInitMap(pipelineConfig, scheduleJob, batchTask, taskParamsToReplace, (pipelineMap) -> {
            //fill 文件上传的信息
            JSONObject pluginInfo = pluginInfoManager.buildTaskPluginInfo(batchTask.getProjectId(),batchTask.getAppType(),batchTask.getTaskType(),batchTask.getDtuicTenantId(), ScheduleEngineType.Hadoop.getEngineName(), uicUserId, null,scheduleJob.getResourceId(), null);
            String hdfsTypeName = componentService.buildHdfsTypeName(batchTask.getDtuicTenantId(),null);
            pluginInfo.put(ConfigConstant.TYPE_NAME_KEY,hdfsTypeName);
            pipelineMap.put(UploadParamPipeline.tenantIdKey, batchTask.getDtuicTenantId());
            pipelineMap.put(UploadParamPipeline.pluginInfoKey, pluginInfo);
            pipelineMap.put(UploadParamPipeline.workOperatorKey, enginePluginsOperator);
            pipelineMap.put(UploadParamPipeline.rdosWrapperKey, rdosWrapper);
            pipelineMap.put(UploadParamPipeline.fileUploadPathKey, environmentContext.getHdfsTaskPath());
        });
        pipeline.execute(actionParam, pipelineInitMap);
    }



    /**
     * 停止的请求接口
     * @throws Exception
     */
    public Boolean stop(List<String> jobIds,Long operateId) {

        if(CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobIds不能为空");
        }
        return stop(jobIds, ForceCancelFlag.NO.getFlag(),operateId);
    }

    public Boolean stop(List<String> jobIds, Integer isForce,Long operateId) {
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.getRdosJobByJobIds(jobIds));
        jobStopDealer.addStopJobs(jobs, isForce,operateId);
        return true;
    }

    private void checkParam(ParamAction paramAction) throws Exception{

        if(StringUtils.isBlank(paramAction.getTaskId())){
           throw new RdosDefineException("param taskId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getComputeType() == null){
            throw new RdosDefineException("param computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getEngineType() == null){
            throw new RdosDefineException("param engineType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

//        if (paramAction.getAppType() == null) {
//            throw new RdosDefineException("param appType is not allow null", ErrorCode.INVALID_PARAMETERS);
//        }
    }

    /**
     * 处理从客户的发送过来的任务，会插入到engine_batch/stream_job 表
     * 修改任务状态为 ENGINEACCEPTED, 没有更新的逻辑
     *
     * @param paramActionExt
     * @return
     */
    private boolean receiveStartJob(ParamActionExt paramActionExt){
        String jobId = paramActionExt.getTaskId();
        Integer computerType = paramActionExt.getComputeType();
        //当前任务已经存在在engine里面了
        //不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if(engineJobCacheDao.getOne(jobId) != null){
            return false;
        }
        boolean result = false;
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
            if(scheduleJob == null){
                scheduleJob = buildScheduleJob(paramActionExt);
                scheduleJobDao.insert(scheduleJob);
                ScheduleJobExpand scheduleJobExpand = buildScheduleJobExpand(paramActionExt.getTaskId(), "", "", "", "");
                scheduleJobExpandDao.insert(scheduleJobExpand);
                if((EScheduleType.TEMP_JOB.getType().equals(scheduleJob.getType()))
                        && AppType.RDOS.getType().equals(scheduleJob.getAppType())){
                    //离线临时运行需要插入sql_text
                    ScheduleSqlTextTemp sqlTextTemp = new ScheduleSqlTextTemp();
                    sqlTextTemp.setEngineType(paramActionExt.getEngineType());
                    sqlTextTemp.setJobId(scheduleJob.getJobId());
                    sqlTextTemp.setSqlText(paramActionExt.getSqlText());
                    sqlTextTempDao.insert(sqlTextTemp);
                }
                result = true;
            }else{
                result = RdosTaskStatus.canStart(scheduleJob.getStatus());
                if(result && !RdosTaskStatus.ENGINEACCEPTED.getStatus().equals(scheduleJob.getStatus()) ){
                    scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
                    scheduleJob.setAppType(paramActionExt.getAppType());
                    scheduleJob.setDtuicTenantId(paramActionExt.getDtuicTenantId());
                    scheduleJob.setRetryType(paramActionExt.getRetryType());
                    scheduleJob.setMaxRetryNum(paramActionExt.getMaxRetryNum());
                    scheduleJob.setResourceId(paramActionExt.getResourceId());
                    if (AppType.STREAM.getType().equals(paramActionExt.getAppType())) {
                        scheduleJob.setRetryNum(0);
                    }
                    scheduleJobDao.update(scheduleJob);
                    LOGGER.info("jobId:{} update job status:{}.", scheduleJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
                }

                paramActionExt.setTaskSourceId(scheduleJob.getTaskId());
            }
            if (result && ComputeType.BATCH.getType().equals(computerType)){
                engineJobCheckpointDao.deleteByTaskId(jobId);
            } else {
                engineJobRetryDao.removeByJobId(jobId);
            }
        } catch (Exception e){
            LOGGER.error("", e);
        }
        return result;
    }

    private ScheduleJob buildScheduleJob(ParamActionExt paramActionExt) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(paramActionExt.getTaskId());
        scheduleJob.setJobName(getOrDefault(paramActionExt.getName(),""));
        scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(paramActionExt.getComputeType());

        scheduleJob.setTenantId(paramActionExt.getTenantId());
        scheduleJob.setProjectId(getOrDefault(paramActionExt.getProjectId(), -1L));
        //dtuicTenantId() 取 tenantId字段
        scheduleJob.setDtuicTenantId(getOrDefault(paramActionExt.getTenantId(), -1L));
        scheduleJob.setAppType(getOrDefault(paramActionExt.getAppType(), 0));
        scheduleJob.setJobKey(getOrDefault(paramActionExt.getJobKey(), String.format("%s%s%s", "tempJob", paramActionExt.getTaskId(), new DateTime().toString("yyyyMMdd") )));
        scheduleJob.setTaskId(getOrDefault(paramActionExt.getTaskSourceId(), -1L));
        scheduleJob.setCreateUserId(getOrDefault(paramActionExt.getUserId(), -1L));

        scheduleJob.setFillId(getOrDefault(paramActionExt.getFillId(),0L));
        scheduleJob.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
        scheduleJob.setIsRestart(getOrDefault(paramActionExt.getIsRestart(), 0));
        scheduleJob.setBusinessDate(getOrDefault(paramActionExt.getBusinessDate(), ""));
        scheduleJob.setCycTime(getOrDefault(paramActionExt.getCycTime(), ""));
        scheduleJob.setDependencyType(getOrDefault(paramActionExt.getDependencyType(), 0));
        scheduleJob.setFlowJobId(getOrDefault(paramActionExt.getFlowJobId(), "0"));
        scheduleJob.setTaskType(getOrDefault(paramActionExt.getTaskType(), -2));
        scheduleJob.setMaxRetryNum(getOrDefault(paramActionExt.getMaxRetryNum(), 0));
        scheduleJob.setRetryType(paramActionExt.getRetryType());
        scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJob.setVersionId(getOrDefault(paramActionExt.getVersionId(), 0));
        scheduleJob.setComputeType(getOrDefault(paramActionExt.getComputeType(), 1));
        scheduleJob.setPeriodType(paramActionExt.getPeriodType());
        scheduleJob.setBusinessType(paramActionExt.getBusinessType());
        scheduleJob.setResourceId(paramActionExt.getResourceId());
        scheduleJob.setJobBuildType(JobBuildType.IMMEDIATELY.getType());
        scheduleJob.setJobExecuteOrder(JobExecuteOrderUtil.buildJobExecuteOrder(DateUtil.getUnStandardFormattedDate(System.currentTimeMillis()), new AtomicInteger()));
        return scheduleJob;
    }

    private <T> T getOrDefault(T value, T defaultValue){
        return value != null? value : defaultValue;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Integer status( String jobId) throws Exception {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
        	return scheduleJob.getStatus();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds( List<String> jobIds) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobIds  is not allow empty", ErrorCode.INVALID_PARAMETERS);
        }
        Map<String,Integer> result = null;
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
        	result = new HashMap<>(scheduleJobs.size());
        	for (ScheduleJob scheduleJob : scheduleJobs){
        		result.put(scheduleJob.getJobId(), scheduleJob.getStatus());
        	}
        }
        return result;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态 封装成 JobIdAndStatusVo 对象返回
     */
    public List<JobIdAndStatusVo> statusByJobIdsToVo(List<String> jobIds) throws Exception {
        Map<String, Integer> map = this.statusByJobIds(jobIds);
        List<JobIdAndStatusVo> jobIdAndStatusVos = new LinkedList<>();
        if (map != null){
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JobIdAndStatusVo jobIdAndStatusVo = new JobIdAndStatusVo();
                jobIdAndStatusVo.setJobId(entry.getKey());
                jobIdAndStatusVo.setStatus(entry.getValue());
                jobIdAndStatusVos.add(jobIdAndStatusVo);
            }
        }

        return jobIdAndStatusVos;
    }


    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    public Long startTime( String jobId ) throws Exception {

        if (StringUtils.isBlank(jobId) ){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Date startTime = null;
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
        	startTime = scheduleJob.getExecStartTime();
        }
        if (startTime!=null){
            return startTime.getTime();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public ActionLogVO log( String jobId, Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId)||computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
            ScheduleJobExpand scheduleJobExpand = scheduleJobExpandDao.getLogByJobId(jobId);
            if (scheduleJobExpand != null) {
                vo.setLogInfo(scheduleJobExpand.getLogInfo());
                String engineLog = getEngineLog(jobId, scheduleJob, scheduleJobExpand);
                vo.setEngineLog(engineLog);
            }
        }
        return vo;
    }

    private String getEngineLog(String jobId, ScheduleJob scheduleJob,ScheduleJobExpand scheduleJobExpand) throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        String engineLog = scheduleJobExpand.getEngineLog();
        if (StringUtils.isBlank(engineLog)) {
            engineLog = CompletableFuture.supplyAsync(
                    () ->
                    jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getDtuicTenantId()),
                    logTimeOutPool
            ).get(environmentContext.getLogTimeout(), TimeUnit.SECONDS);
            if (engineLog == null) {
                engineLog = "";
            }
        }
        return engineLog;
    }

    public JobLogVO logUnite(String jobId,Integer pageInfo)  throws Exception {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);

        if (scheduleJob == null) {
            throw new RdosDefineException("job is not exist");
        }

        ScheduleTaskShade taskShadeDao = scheduleTaskShadeDao.getOne(scheduleJob.getTaskId(), scheduleJob.getAppType());

        if (taskShadeDao == null) {
            throw new RdosDefineException("task is not exist");
        }

        JobLogVO jobLogVO = new JobLogVO();
        jobLogVO.setName(taskShadeDao.getName());
        jobLogVO.setComputeType(taskShadeDao.getComputeType());
        jobLogVO.setTaskType(taskShadeDao.getTaskType());

        jobLogVO.setExecEndTime(scheduleJob.getExecEndTime());
        jobLogVO.setExecStartTime(scheduleJob.getExecStartTime());
        ScheduleJobExpand scheduleJobExpand = scheduleJobExpandDao.getLogByJobId(jobId);

        String engineLog = getEngineLog(jobId, scheduleJob,scheduleJobExpand);
        if (scheduleJobExpand != null) {
            jobLogVO.setEngineLog(engineLog);

            // 封装日志信息
            JSONObject info = new JSONObject();
            try {
                info = JSON.parseObject(scheduleJobExpand.getLogInfo());
            } catch (final Exception e) {
                LOGGER.error("parse jobId {} } logInfo error {}", jobId, scheduleJobExpand.getLogInfo());
                info.put("msg_info", scheduleJobExpand.getLogInfo());
            }

            if (info == null) {
                info = new JSONObject();
            }

            info.put("sql",taskShadeDao.getSqlText());
            info.put("engineLogErr",engineLog);
            jobLogVO.setLogInfo(info.toJSONString());
        }

        try {
            if (scheduleJob.getRetryNum() > 0) {
                String retryLog = buildRetryLog(scheduleJob.getJobId(), pageInfo, jobLogVO,engineLog);
                if (StringUtils.isNotBlank(retryLog)) {
                    jobLogVO.setLogInfo(retryLog);
                }
            }
        } catch (Exception e) {
            LOGGER.error("buildRetryLog error:{}", e.getMessage(), e);
        }

        if (pageInfo == null) {
            jobLogVO.setPageIndex(jobLogVO.getPageSize());
        } else {
            jobLogVO.setPageIndex(pageInfo);
        }

        return jobLogVO;
    }

    private String buildRetryLog(final String jobId, Integer pageInfo,JobLogVO batchServerLogVO,String engineLog) throws Exception {
        //先获取engine的日志总数信息
        List<ActionRetryLogVO> actionRetryLogVOs = retryLog(jobId,null);
        if (CollectionUtils.isEmpty(actionRetryLogVOs)) {
            return "";
        }
        batchServerLogVO.setPageSize(actionRetryLogVOs.size());
        if(Objects.isNull(pageInfo)){
            pageInfo = 0;
        }
        //engine 的 retryNum 从1 开始
        if (0 == pageInfo) {
            pageInfo = actionRetryLogVOs.size();
        }
        if (pageInfo > actionRetryLogVOs.size()) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        //获取对应的日志
        ActionRetryLogVO retryLogContent = retryLogDetail(jobId, pageInfo);
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(retryLogContent)) {
            return "";
        }
        Integer retryNumVal = retryLogContent.getRetryNum();
        int retryNum = 0;
        if(Objects.nonNull(retryNumVal)){
            retryNum = retryNumVal + 1;
        }
        String logInfo = retryLogContent.getLogInfo();
        String engineInfo = retryLogContent.getEngineLog();
        String retryTaskParams = retryLogContent.getRetryTaskParams();
        builder.append("====================第 ").append(retryNum).append("次重试====================").append("\n");

        if (!Strings.isNullOrEmpty(logInfo)) {
            builder.append("====================LogInfo start====================").append("\n");
            builder.append(logInfo).append("\n");
            builder.append("=====================LogInfo end=====================").append("\n");
        }
        if (!Strings.isNullOrEmpty(engineInfo)) {
            builder.append("==================EngineInfo  start==================").append("\n");
            builder.append(engineInfo).append("\n");
            builder.append("===================EngineInfo  end===================").append("\n");
        } else if (!Strings.isNullOrEmpty(engineLog)){
            builder.append("==================EngineInfo  start==================").append("\n");
            builder.append(engineLog).append("\n");
            builder.append("===================EngineInfo  end===================").append("\n");
        }
        if (!Strings.isNullOrEmpty(retryTaskParams)) {
            builder.append("==================RetryTaskParams  start==================").append("\n");
            builder.append(retryTaskParams).append("\n");
            builder.append("===================RetryTaskParams  end===================").append("\n");
        }

        builder.append("==================第").append(retryNum).append("次重试结束==================").append("\n");
        for (int j = 0; j < 10; j++) {
            builder.append("==" + "\n");
        }

        return builder.toString();
    }

    /**
     * 根据jobid 从es中获取日志
     */
    public String logFromEs(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob == null) {
            return StringUtils.EMPTY;
        }
        if (StringUtils.isNotEmpty(scheduleJob.getApplicationId())) {
            return elasticsearchService.searchWithJobId("taskId.keyword", scheduleJob.getApplicationId());
        }

        String logInfo = scheduleJobExpandDao.getLogInfoByJobId(jobId);
        JSONObject infoJsonObj;
        try {
            infoJsonObj = JSON.parseObject(logInfo);
        } catch (Exception e) {
            LOGGER.error("jobId:{}, parse logInfo error", jobId, e);
            return StringUtils.EMPTY;
        }
        if (infoJsonObj != null) {
            return infoJsonObj.getString("msg_info");
        } else {
            return StringUtils.EMPTY;
        }
    }



    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public List<ActionRetryLogVO> retryLog( String jobId,Long expandId) {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if (expandId == null) {
            ScheduleJobExpand expand = scheduleJobExpandDao.getExpandByJobId(jobId);
            expandId = expand.getId();
        }

        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());

        if (scheduleJob == null) {
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        int maxRetryLogSize = environmentContext.getMaxRetryLogSize();
        List<ActionRetryLogVO> logs = new ArrayList<>(5);
        List<EngineJobRetry> batchJobRetrys = engineJobRetryDao.listJobRetryByJobId(jobId,expandId,maxRetryLogSize);
        if (CollectionUtils.isNotEmpty(batchJobRetrys)) {
            batchJobRetrys.forEach(jobRetry->{
                ActionRetryLogVO vo = new ActionRetryLogVO();
                vo.setRetryNum(jobRetry.getRetryNum());
                vo.setLogInfo(jobRetry.getLogInfo());
                vo.setEngineLog(jobRetry.getEngineLog());
                vo.setRetryTaskParams(jobRetry.getRetryTaskParams());
                vo.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus()));
                vo.setExecEndTime(DateUtil.getDate(scheduleJob.getExecEndTime(),DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setExecStartTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
                logs.add(vo);
            });
        }
        return logs;
    }

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public ActionRetryLogVO retryLogDetail( String jobId, Integer retryNum) throws Exception {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        if (retryNum == null || retryNum <= 0) {
            retryNum = 1;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        //数组库中存储的retryNum为0开始的索引位置
        EngineJobRetry jobRetry = engineJobRetryDao.getJobRetryByJobId(jobId, retryNum - 1);
        ActionRetryLogVO vo = new ActionRetryLogVO();
        if (jobRetry != null) {
            vo.setRetryNum(jobRetry.getRetryNum());
            vo.setLogInfo(jobRetry.getLogInfo());
            String engineLog = jobRetry.getEngineLog();
            if (StringUtils.isBlank(jobRetry.getEngineLog())){
                engineLog = jobDealer.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), scheduleJob.getDtuicTenantId());
                if (engineLog != null){
                    LOGGER.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
                    engineJobRetryDao.updateEngineLog(jobRetry.getId(), engineLog);
                } else {
                    engineLog = "";
                }
            }
            vo.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus()));
            vo.setExecEndTime(DateUtil.getDate(scheduleJob.getExecEndTime(),DateUtil.STANDARD_DATETIME_FORMAT));
            vo.setExecStartTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            vo.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
            vo.setEngineLog(engineLog);
            vo.setRetryTaskParams(jobRetry.getRetryTaskParams());
            vo.setApplicationId(jobRetry.getApplicationId());
        }
        return vo;
    }

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<ActionJobEntityVO> entitys( List<String> jobIds) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<ActionJobEntityVO> result = null;
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
        	result = scheduleJobStruct.toActionJobEntityVO(scheduleJobs);
            List<ScheduleJobExpand> scheduleJobExpands = scheduleJobExpandDao.getLogByJobIds(jobIds);
            Map<String, List<ScheduleJobExpand>> jobExpandMap = scheduleJobExpands.stream().collect(Collectors.groupingBy(ScheduleJobExpand::getJobId));
            for (ActionJobEntityVO actionJobEntityVO : result) {
                List<ScheduleJobExpand> scheduleJobExpandList = jobExpandMap.get(actionJobEntityVO.getJobId());
                if (CollectionUtils.isNotEmpty(scheduleJobExpandList)) {
                    ScheduleJobExpand scheduleJobExpand = scheduleJobExpandList.stream().max(Comparator.comparing(ScheduleJobExpand::getRunNum)).orElse(null);
                    if (scheduleJobExpand != null) {
                        actionJobEntityVO.setLogInfo(scheduleJobExpand.getLogInfo());
                        actionJobEntityVO.setEngineLog(scheduleJobExpand.getEngineLog());
                    }
                }
            }
        }
        return result;
    }


    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    public List<String> containerInfos(ParamAction paramAction) throws Exception {
        checkParam(paramAction);
        //从数据库补齐数据
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(paramAction.getTaskId());
        if(scheduleJob != null){
            paramAction.setEngineTaskId(scheduleJob.getEngineJobId());
            paramAction.setApplicationId(scheduleJob.getApplicationId());
            JobClient jobClient =JobClientUtil.conversionJobClient(paramAction);
            return enginePluginsOperator.containerInfos(jobClient);
        }
        return null;
    }

    public String generateUniqueSign() {
        if (null == jobIdWorker) {
            String localAddress = getLocalRealAddress();
            if (StringUtils.isBlank(localAddress)) {
                throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
            }
            String[] ipPortSplit = localAddress.split("\\:");
            String[] ipSplit = ipPortSplit[0].split("\\.");
            jobIdWorker = DtJobIdWorker.getInstance(ipSplit.length >= 4 ? Integer.parseInt(ipSplit[3]) : 0, 0);
        }
        return jobIdWorker.nextJobId();
    }

    /**
     * 重置任务状态为未提交
     * @return
     */
    public String resetTaskStatus( String jobId){

        //check jobstatus can reset
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        Preconditions.checkNotNull(scheduleJob, "not exists job with id " + jobId);
        Integer currStatus = scheduleJob.getStatus();

        if(!RdosTaskStatus.canReset(currStatus)){
            LOGGER.error("jobId:{} can not update status current status is :{} ", jobId, currStatus);
            throw new RdosDefineException(String.format(" taskId(%s) can't reset status, current status(%d)", jobId, currStatus));
        }

        //do reset status
        scheduleJobExpandDao.updateLogByJobId(jobId,"","","",null,null);
        scheduleJobDao.updateJobStatusAndPhaseStatus(Collections.singletonList(jobId), RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode(), null,
                environmentContext.getLocalAddress(),scheduleJob.getStatus());
        LOGGER.info("jobId:{} update job status:{}.", jobId, RdosTaskStatus.UNSUBMIT.getStatus());
        return jobId;
    }

    /**
     * task 工程使用
     */
    public List<ActionJobStatusVO> listJobStatus( Long time,Integer appType) {
        if (time == null || time == 0L) {
            throw new RuntimeException("time is null");
        }

        List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobStatus(new Timestamp(time), ComputeType.BATCH.getType(),appType);
        return toVOS(scheduleJobs);
    }

    public List<ScheduleJob> listJobStatusScheduleJob(Long time, Integer appType) {
        if (time == null || time == 0L) {
            throw new RuntimeException("time is null");
        }

        return scheduleJobDao.listJobStatus(new Timestamp(time), null,appType);
    }

    private List<ActionJobStatusVO> toVOS(List<ScheduleJob> scheduleJobs){
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            List<ActionJobStatusVO> result = new ArrayList<>(scheduleJobs.size());
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ActionJobStatusVO data = batJobConvertMap(scheduleJob);
                result.add(data);
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }

    public List<ActionJobStatusVO> listJobStatusByJobIds( List<String> jobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
            return toVOS(scheduleJobs);
        }
        return Collections.EMPTY_LIST;
    }

    private ActionJobStatusVO batJobConvertMap(ScheduleJob scheduleJob){
        ActionJobStatusVO vo =new ActionJobStatusVO();
        vo.setJobId(scheduleJob.getJobId());
        vo.setStatus(scheduleJob.getStatus());
        vo.setExecStartTime(scheduleJob.getExecStartTime() == null ? new Timestamp(0) : scheduleJob.getExecStartTime());
        vo.setExecEndTime(scheduleJob.getExecEndTime() == null ? new Timestamp(0) : scheduleJob.getExecEndTime());
        vo.setExecTime(scheduleJob.getExecTime());
        vo.setRetryNum(scheduleJob.getRetryNum());
        vo.setBusinessType(scheduleJob.getBusinessType());
        return vo;
    }



    public List<AppTypeVO> getAllAppType() {
        AppType[] appTypes = AppType.values();
        List<AppTypeVO> appTypeVOS = Lists.newArrayList();

        for (AppType appType : appTypes) {
            AppTypeVO appTypeVO = new AppTypeVO();
            appTypeVO.setCode(appType.getType());
            appTypeVO.setMsg(appType.getName());

            appTypeVOS.add(appTypeVO);
        }

        return appTypeVOS;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean refreshStatus(ParamActionExt paramActionExt) {
        EngineJobCache jobCache = engineJobCacheDao.getOne(paramActionExt.getTaskId());
        if (null != jobCache) {
            throw new RdosDefineException(ErrorCode.JOB_STATUS_IS_SAME);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(paramActionExt.getTaskId());
        if (null == scheduleJob) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        if (!RdosTaskStatus.isStopped(scheduleJob.getStatus())) {
            throw new RdosDefineException(ErrorCode.INVALID_TASK_STATUS);
        }
        try {
            JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
            jobClient.setCallBack((jobStatus) -> scheduleJobDao.updateJobStatus(jobClient.getTaskId(), jobStatus));
            String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
            jobDealer.saveCache(jobClient, jobResource, EJobCacheStage.SUBMITTED.getStage(), true);
            ApplicationInfo applicationInfo = enginePluginsOperator.retrieveJob(jobClient);
            if (null == applicationInfo || StringUtils.isBlank(applicationInfo.getEngineJobId())) {
                throw new RdosDefineException(ErrorCode.GET_APPLICATION_INFO_ERROR);
            }
            scheduleJobDao.updateJobSubmitSuccess(jobClient.getTaskId(), applicationInfo.getEngineJobId(), paramActionExt.getApplicationId());
            scheduleJobExpandDao.updateLogByJobId(jobClient.getTaskId(),"","","",applicationInfo.getEngineJobId(),paramActionExt.getApplicationId());
            LOGGER.info("refresh jobId {} status by applicationId {} engineJobId {}", jobClient.getTaskId(), paramActionExt.getApplicationId(), applicationInfo.getEngineJobId());
            return shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.RUNNING.getStatus());
        } catch (Exception e) {
            throw new RdosDefineException(e);
        }
    }

    public String executeCatalog(CatalogParam catalogParam) throws Exception {
        ClientTypeEnum clientType = context.getClientType(catalogParam.getEngineType());
        JobClient jobClient = new JobClient();
        jobClient.setTaskId(catalogParam.getJobId());
        jobClient.setEngineType(catalogParam.getEngineType());
        jobClient.setComputeType(ComputeType.STREAM);
        jobClient.setTenantId(catalogParam.getTenantId());
        jobClient.setJobType(EJobType.CATALOG);
        jobClient.setClientType(clientType);
        jobClient.setSql(catalogParam.getDdl());

        JobClientUtil.convertCatalog(catalogParam.getCatalog(), jobClient);
        JobResult jobResult = enginePluginsOperator.submitJob(jobClient);
        if (null != jobResult) {
            return jobResult.getJsonStr();
        }
        return "";
    }


    public ActionLogVO expandLog(String jobId, Integer num) {
        if (StringUtils.isBlank(jobId)||num==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
            ScheduleJobExpand scheduleJobExpand = scheduleJobExpandDao.getLogByJobIdAndNum(jobId,num);
            if (scheduleJobExpand != null) {
                try {
                    vo.setLogInfo(scheduleJobExpand.getLogInfo());
                    String engineLog = getEngineLog(jobId, scheduleJob, scheduleJobExpand);
                    vo.setEngineLog(engineLog);
                } catch (Exception e) {
                   LOGGER.error("",e);
                }
            }
        }
        return vo;
    }

    /**
     * 获取 ip，兼容客户配置域名的场景(内部会将域名解析为 ip)
     */
    public String getLocalRealAddress() {
        if (StringUtils.isNotBlank(localRealAddress)) {
            return localRealAddress;
        }

        String originHttpAddress = environmentContext.getConfigHttpAddress();
        String realIp = null;
        // 1. 优先判断是否是 ip
        if (AddressUtil.isCorrectIp(originHttpAddress)) {
            realIp = originHttpAddress;
        } else if (AddressUtil.isDomain(originHttpAddress)) { // 2. 然后判断是否是域名
            try {
                // 3. 根据域名解析出 ip
                realIp = InetAddress.getByName(originHttpAddress).getHostAddress();
                LOGGER.info("orgin:{} check is domain, after parse:{}", originHttpAddress, realIp);
            } catch (UnknownHostException e) {
                LOGGER.error("orgin:{} check is domain, parse error", originHttpAddress, e);
            } catch (Exception e) {
                LOGGER.error("orgin:{} check is domain, error", originHttpAddress, e);
            }
        }
        // 4. 异常情况则获取缺省值
        if (StringUtils.isBlank(realIp)) {
            realIp = AddressUtil.getOneIp(true);
        }
        String port = environmentContext.getConfigHttpPort();
        localRealAddress = String.format("%s:%s", realIp, port);
        return localRealAddress;
    }

    public void hotReloading(ParamActionExt paramActionExt) {
        if (!AppType.STREAM.getType().equals(paramActionExt.getAppType())) {
            throw new RdosDefineException("only stream task support hot reloading");
        }
        if (StringUtils.isBlank(paramActionExt.getApplicationId())) {
            throw new RdosDefineException("hot reloading task appId not be null");
        }
        if (Boolean.TRUE.equals(redisTemplate.hasKey(GlobalConst.STATUS_BLACK_LIST + paramActionExt.getTaskId()))) {
            throw new RdosDefineException(ErrorCode.TASK_IS_RELOADING);
        }

        ClientTypeEnum clientType = context.getClientType(paramActionExt.getEngineType(), paramActionExt.getTaskType());
        if (clientType == null) {
            throw new RdosDefineException("task type not support");
        }

        EngineJobCache cache = engineJobCacheDao.getOne(paramActionExt.getTaskId());
        if (null == cache || (EJobCacheStage.SUBMITTED.getStage() != cache.getStage())) {
            throw new RdosDefineException("hot reloading task must be running");
        }

        try {
            Properties properties = PublicUtil.stringToProperties(paramActionExt.getTaskParams());
            if (!BooleanUtils.toBoolean(properties.getProperty(GlobalConst.HOT_RELOADING))) {
                throw new RdosDefineException("hot reloading param value must be true");
            }

        } catch (IOException e) {
            throw new RdosDefineException("hot reloading param error", e);
        }

        commonExecutor.execute(() -> {
            try {
                JobClient jobClient = JobClientUtil.conversionJobClient(paramActionExt);
                jobClient.setClientType(clientType);
                jobClient.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
                jobClient.setCallBack((jobStatus) -> LOGGER.info("hot reloading job {} status change to {},but only print", jobClient.getTaskId(), jobStatus));
                jobClient.setHotReloading(true);
                String jobResource = jobComputeResourcePlain.getJobResource(jobClient);
                GroupPriorityQueue groupPriorityQueue = jobDealer.getGroupPriorityQueue(jobResource);
                //热更新 过程 不在获取旧任务状态
                redisTemplate.opsForValue().set(GlobalConst.STATUS_BLACK_LIST + jobClient.getTaskId(), jobClient.getTaskId());
                redisTemplate.expire(GlobalConst.STATUS_BLACK_LIST + jobClient.getTaskId(), Duration.of(environmentContext.getStatusBlackTimeConf(), ChronoUnit.MINUTES));
                JobSubmitDealer jobSubmitDealer = groupPriorityQueue.getJobSubmitDealer();
                jobSubmitDealer.submit(jobClient);
            } catch (Exception e) {
                LOGGER.error(" {} hot reloading fail ", paramActionExt.getTaskId(), e);
                String msg = "hot reloading fail: " + ExceptionUtil.getErrorMessage(e);
                scheduleJobExpandDao.updateLogInfoByJobId(paramActionExt.getTaskId(), msg, null);
            }
        });
    }
}
