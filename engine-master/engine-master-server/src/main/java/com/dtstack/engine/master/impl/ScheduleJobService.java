package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.common.enums.EProjectScheduleStatus;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.dtcenter.common.enums.Sort;
import com.dtstack.engine.api.domain.AppTenantEntity;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.api.domain.ScheduleEngineJob;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTask;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.dto.ScheduleSqlTextDTO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.enums.CandlerBatchTypeEnum;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.RollingJobLogParam;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobRuleTimeVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobScienceJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusCountVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobStatusVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobTopRunTimeVO;
import com.dtstack.engine.dto.ScheduleJobCount;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.dto.SimpleJob;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.ChartDataVO;
import com.dtstack.engine.api.vo.JobStatusVo;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.OperatorVO;
import com.dtstack.engine.api.vo.RestartJobInfoVO;
import com.dtstack.engine.api.vo.RestartJobVO;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobBeanVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import com.dtstack.engine.api.vo.ScheduleServerLogVO;
import com.dtstack.engine.api.vo.TaskExeInfoVo;
import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.api.vo.job.BlockJobNumVO;
import com.dtstack.engine.api.vo.job.BlockJobVO;
import com.dtstack.engine.api.vo.job.JobExecInfoVO;
import com.dtstack.engine.api.vo.job.JobGraphBuildJudgeVO;
import com.dtstack.engine.api.vo.job.JobRunCountVO;
import com.dtstack.engine.api.vo.job.JobStopProcessVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleAlterPageVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleAlterReturnVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoEnhanceVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobImmediatelyVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateEnhanceVO;
import com.dtstack.engine.api.vo.task.FillDataTaskVO;
import com.dtstack.engine.api.vo.task.TaskCustomParamVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.QueryWorkFlowModel;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.enums.RelyTypeEnum;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.*;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.dto.*;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.common.util.ScheduleConfUtils;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.JobGraphTriggerDao;
import com.dtstack.engine.dao.ScheduleFillDataJobDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.dao.ScheduleJobFailedDao;
import com.dtstack.engine.dao.ScheduleJobGraphDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.dao.ScheduleJobParamDao;
import com.dtstack.engine.dao.ScheduleSqlTextTempDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dao.ScheduleTaskTaskShadeDao;
import com.dtstack.engine.dto.FillDataQueryDTO;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.diagnosis.JobDiagnosisChain;
import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.dto.CycTimeLimitDTO;
import com.dtstack.engine.master.enums.*;
import com.dtstack.engine.master.enums.CloseRetryEnum;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.enums.SearchTypeEnum;
import com.dtstack.engine.master.enums.TaskRunOrderEnum;
import com.dtstack.engine.master.dto.FillLimitationDTO;
import com.dtstack.engine.master.enums.*;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.mapstruct.ScheduleJobStruct;
import com.dtstack.engine.master.multiengine.UnnecessaryPreprocessJobService;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.scheduler.JobStatusSummitCheckOperator;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.sync.FillDataEnhanceRunnable;
import com.dtstack.engine.master.sync.FillDataRunnable;
import com.dtstack.engine.master.sync.FillDataThreadPoolExecutor;
import com.dtstack.engine.master.sync.ForkJoinJobTask;
import com.dtstack.engine.master.sync.RestartJobRunnable;
import com.dtstack.engine.master.sync.RestartRunnable;
import com.dtstack.engine.master.utils.*;
import com.dtstack.engine.master.sync.fill.AbstractFillDataTask;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.utils.JobGraphUtils;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.master.utils.TaskUtils;
import com.dtstack.engine.master.utils.TimeUtils;
import com.dtstack.engine.master.vo.BatchSecienceJobChartVO;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.master.worker.DataSourceXOperator;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ScheduleFillDataJob;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.po.ScheduleJobGraph;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.po.ScheduleJobParam;
import com.dtstack.engine.po.ScheduleJobRelyCheck;
import com.dtstack.engine.po.ScheduleSqlTextTemp;
import com.dtstack.engine.po.ScheduleTaskTag;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class ScheduleJobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final AtomicLong atomicLong = new AtomicLong(0);

    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BUSINESS_DATE = "business_date";

    private static final int TOTAL_HOUR_DAY = 24;

    public static final String HADOOP_USER_NAME = "hadoopUserName";

    private final String LOG_TEM = "%s: %s(所属租户：%s,所属项目：%s)";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s";

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal(), EScheduleJobType.ALGORITHM_LAB.getVal());



    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeDao scheduleTaskTaskShadeDao;

    @Autowired
    private ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private ScheduleFillDataJobDao scheduleFillDataJobDao;

    @Autowired
    private ScheduleFillDataJobService scheduleFillDataJobService;

    @Autowired
    private ScheduleJobJobService batchJobJobService;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ActionService actionService;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JobGraphTriggerDao jobGraphTriggerDao;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private UserService userService;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobFailedDao scheduleJobFailedDao;

    @Autowired
    private FillDataThreadPoolExecutor fillDataThreadPoolExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScheduleJobStruct scheduleJobStruct;

    @Autowired
    private ResourceGroupService resourceGroupService;


    @Autowired
    private ScheduleJobExpandDao  scheduleJobExpandDao;

    @Autowired
    private ScheduleJobOperateService scheduleJobOperateService;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private TenantService tenantService;

    private final static List<Integer> FINISH_STATUS = Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus(), RdosTaskStatus.MANUALSUCCESS.getStatus(), RdosTaskStatus.CANCELLING.getStatus(), RdosTaskStatus.CANCELED.getStatus());
    private final static List<Integer> FAILED_STATUS = Lists.newArrayList(RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus(), RdosTaskStatus.KILLED.getStatus());

    @Autowired
    private ScheduleSqlTextTempDao sqlTextTempDao;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Autowired
    private TimeService timeService;

    @Resource
    private ScheduleJobParamDao scheduleJobParamDao;

    @Autowired
    private ScheduleJobGraphDao scheduleJobGraphDao;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Autowired
    private DataSourceXOperator dataSourceXOperator;

    @Autowired
    private ParamService paramService;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    @Autowired
    private JobDiagnosisChain jobDiagnosisChain;

    @Autowired
    protected JobStatusSummitCheckOperator jobStatusSummitCheckOperator;

    @Autowired
    private ScheduleJobRelyCheckService scheduleJobRelyCheckService;

    @Autowired
    protected UicUserApiClient uicUserApiClient;


    @Autowired
    private ScheduleTaskTagService scheduleTaskTagService;

    @Autowired
    private ScheduleJobGanttTimeService scheduleJobGanttTimeService;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    /**
     * 根据任务id展示任务详情
     *
     * @author toutian
     */
    public ScheduleJob getJobById( long jobId) {
        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        if (null!= scheduleJob) {
            // 如果拿不到用户时，使用默认的用户
            if (!AppType.STREAM.getType().equals(scheduleJob.getAppType())) {
                scheduleJob.setSubmitUserName(getHadoopUserName(scheduleJob));
            }
            encapsulationLog(scheduleJob.getJobId(),scheduleJob);
        }

        return scheduleJob;
    }

    private String getHadoopUserName(ScheduleJob scheduleJob) {
        try {
            Integer taskType = scheduleJob.getTaskType();
            EComponentType eComponentType = ComponentVersionUtil.TASK_COMPONENT.get(taskType);
            if (eComponentType == null) {
                return environmentContext.getHadoopUserName();
            }

            Long dtuicTenantId = scheduleJob.getDtuicTenantId();
            Long clusterId = clusterTenantDao.getClusterIdByDtUicTenantId(dtuicTenantId);
            Component component = componentService.getComponentByClusterId(clusterId, eComponentType.getTypeCode(), null);

            ComponentConfig config = componentConfigService.getComponentConfigByKey(component.getId(),
                    HADOOP_USER_NAME);

            if (config != null) {
                return config.getValue();
            }

            if (StringUtils.isNotBlank(scheduleJob.getSubmitUserName())) {
                return scheduleJob.getSubmitUserName();
            }
            return environmentContext.getHadoopUserName();
        } catch (Exception e) {
            LOGGER.error("",e);
        }
        return environmentContext.getHadoopUserName();
    }

    public ScheduleJob getJobByJobKeyAndType(String jobKey, int type) {
        return scheduleJobDao.getByJobKeyAndType(jobKey, type);
    }


    /**
     * 获取指定状态的工作任务列表
     *
     * @param projectId
     * @param tenantId
     * @param appType
     * @param dtuicTenantId
     * @return
     */
    public PageResult getStatusJobList( Long projectId,  Long tenantId,  Integer appType,
                                        Long dtuicTenantId,  Integer status,  int pageSize,  int pageIndex) {
        if (null == status || null == dtuicTenantId) {
            return null;
        }
        List<Integer> statusCode = RdosTaskStatus.getCollectionStatus(status);
        if (CollectionUtils.isEmpty(statusCode)) {
            return null;
        }
        List<Map<String, Object>> data = scheduleJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, statusCode);
        if(CollectionUtils.isEmpty(data)){
            return null;
        }
        int count = 0;
        for (Map<String, Object> info : data) {
            if(null != info.get("count")) {
                count += MathUtil.getIntegerVal(info.get("count"));
            }
        }
        PageQuery<Object> pageQuery = new PageQuery<>(pageIndex, pageSize);
        List<Map<String, Object>> dataMaps = scheduleJobDao.selectStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, statusCode, pageQuery.getStart(), pageQuery.getPageSize());
        return new PageResult<>(dataMaps, count, pageQuery);
    }

    /**
     * 获取各个状态任务的数量
     */
    public ScheduleJobStatusVO getStatusCount( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId) {
        ScheduleJobStatusVO scheduleJobStatusVO =new ScheduleJobStatusVO();
        List<Map<String, Object>> data = scheduleJobDao.countByStatusAndType(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectId, appType, dtuicTenantId, null);
        buildCount( scheduleJobStatusVO, data);
        return scheduleJobStatusVO;
    }

    private void buildCount(ScheduleJobStatusVO scheduleJobStatusVO, List<Map<String, Object>> data) {
        int all = 0;
        List<ScheduleJobStatusCountVO> scheduleJobStatusCountVOS = Lists.newArrayList();
        for (Integer code : RdosTaskStatus.getCollectionStatus().keySet()) {
            List<Integer> status = RdosTaskStatus.getCollectionStatus(code);
            ScheduleJobStatusCountVO scheduleJobStatusCountVO = new ScheduleJobStatusCountVO();
            int count = 0;
            for (Map<String, Object> info : data) {
                if (status.contains(MathUtil.getIntegerVal(info.get("status")))) {
                    count += MathUtil.getIntegerVal(info.get("count"));
                }
            }
            all += count;
            RdosTaskStatus taskStatus = RdosTaskStatus.getTaskStatus(code);
            if (taskStatus != null) {
                scheduleJobStatusCountVO.setTaskName(taskStatus.name());
                scheduleJobStatusCountVO.setTaskStatusName(taskStatus.name());
            }
            scheduleJobStatusCountVO.setCount(count);
            scheduleJobStatusCountVOS.add(scheduleJobStatusCountVO);
        }
        scheduleJobStatusVO.setAll(all);
        scheduleJobStatusVO.setScheduleJobStatusCountVO(scheduleJobStatusCountVOS);
    }

    public List<ScheduleJobStatusVO> getStatusCountByProjectIds(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId) {
        List<ScheduleJobStatusVO> scheduleJobStatusVOS = Lists.newArrayList();

        if (CollectionUtils.isEmpty(projectIds)) {
            return scheduleJobStatusVOS;
        }

        if (projectIds.size() > environmentContext.getMaxBatchTask()) {
            List<List<Long>> partition = Lists.partition(projectIds, environmentContext.getMaxBatchTask());
            for (List<Long> ids : partition) {
                JobStatusCount(ids, tenantId, appType, dtuicTenantId, scheduleJobStatusVOS);
            }
        } else {
            JobStatusCount(projectIds, tenantId, appType, dtuicTenantId, scheduleJobStatusVOS);
        }

        return scheduleJobStatusVOS;
    }

    private void JobStatusCount(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId, List<ScheduleJobStatusVO> scheduleJobStatusVOS) {
        List<ScheduleJobCount> scheduleJobCounts = getScheduleJobCounts(projectIds, tenantId, appType, dtuicTenantId,null);

        if (scheduleJobCounts != null) {
            Map<Long, List<ScheduleJobCount>> listMap = scheduleJobCounts.stream().collect(Collectors.groupingBy(ScheduleJobCount::getProjectId));

            for (Long projectId : projectIds) {
                ScheduleJobStatusVO scheduleJobStatusVO = new ScheduleJobStatusVO();
                scheduleJobStatusVO.setProjectId(projectId);
                List<ScheduleJobCount> dataCount = listMap.get(projectId);
                List<Map<String, Object>> data = toListMap(dataCount);
                buildCount(scheduleJobStatusVO, data);
                scheduleJobStatusVOS.add(scheduleJobStatusVO);
            }
        }
    }

    public List<ScheduleJobCount> getScheduleJobCounts(List<Long> projectIds, Long tenantId, Integer appType, Long dtuicTenantId,List<Integer> status) {
        return scheduleJobDao.countByStatusAndTypeProjectIds(EScheduleType.NORMAL_SCHEDULE.getType(), DateUtil.getUnStandardFormattedDate(DateUtil.calTodayMills()),
                DateUtil.getUnStandardFormattedDate(DateUtil.TOMORROW_ZERO()), tenantId, projectIds, appType, dtuicTenantId, status);
    }

    private List<Map<String, Object>> toListMap(List<ScheduleJobCount> dataCount) {
        List<Map<String, Object>> data = Lists.newArrayList();
        if (CollectionUtils.isEmpty(dataCount)) {
            return data;
        }
        for (ScheduleJobCount scheduleJobCount : dataCount) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("count", scheduleJobCount.getCount());
            map.put("status", scheduleJobCount.getStatus());
            data.add(map);
        }
        return data;
    }

    /**
     * 运行时长top排序
     */
    public List<JobTopOrderVO> runTimeTopOrder( Long projectId,
                                                Long startTime,
                                                Long endTime,  Integer appType,  Long dtuicTenantId) {

        if (null != startTime && null != endTime) {
            startTime = startTime * 1000;
            endTime = endTime * 1000;
        } else {
            startTime = DateUtil.calTodayMills();
            endTime = DateUtil.TOMORROW_ZERO();
        }

        PageQuery<Object> pageQuery = new PageQuery<>(1, 10);
        List<Map<String, Object>> list = scheduleJobDao.listTopRunTime(projectId, new Timestamp(startTime), new Timestamp(endTime), pageQuery, appType,dtuicTenantId);

        return this.transResult2JobTopOrderVOS(list);
    }

    /**
     * 运行时长top10
     */
    public List<JobTopOrderVO> queryTopSizeRunTimeJob(ScheduleJobTopRunTimeVO topRunTimeVO) {
        Long startTime = topRunTimeVO.getStartTime();
        Long endTime = topRunTimeVO.getEndTime();
        if (startTime == null || endTime == null) {
            throw new RdosDefineException("startTime or endTime is required", ErrorCode.INVALID_PARAMETERS);
        }
        // 秒 --> 毫秒
        startTime = startTime * 1000;
        endTime = endTime * 1000;

        if (topRunTimeVO.getTopSize() == null) {
            topRunTimeVO.setTopSize(ScheduleJobTopRunTimeVO.TOP_DEFAULT_MAX_SIZE);
        }
        if (topRunTimeVO.getTopSize() > ScheduleJobTopRunTimeVO.TOP_MAX_SIZE) {
            throw new RdosDefineException("topSize is too large", ErrorCode.INVALID_PARAMETERS);
        }

        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        scheduleJobDTO.setExecStartDay(DateUtil.getDateByLong(startTime));
        scheduleJobDTO.setExecEndDay(DateUtil.getDateByLong(endTime));
        scheduleJobDTO.setPageSize(topRunTimeVO.getTopSize());
        scheduleJobDTO.setAppType(topRunTimeVO.getAppType());
        scheduleJobDTO.setDtuicTenantId(topRunTimeVO.getDtuicTenantId());
        scheduleJobDTO.setProjectId(topRunTimeVO.getProjectId());
        scheduleJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());

        List<Map<String, Object>> list = scheduleJobDao.listTopSizeRunTimeJob(scheduleJobDTO);
        return this.transResult2JobTopOrderVOS(list);
    }

    private List<JobTopOrderVO> transResult2JobTopOrderVOS(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<JobTopOrderVO> jobTopOrderVOS = new ArrayList<>(list.size());
        for (Map<String, Object> info : list) {
            //b.id, b.taskId, b.cycTime, b.type, eb.execTime
            Long jobId = MathUtil.getLongVal(info.get("id"));
            Long taskId = MathUtil.getLongVal(info.get("taskId"));
            String cycTime = MathUtil.getString(info.get("cycTime"));
            Integer type = MathUtil.getIntegerVal(info.get("type"));
            Long execTime = MathUtil.getLongVal(info.get("execTime"));
            execTime = execTime == null ? 0 : execTime;

            JobTopOrderVO jobTopOrderVO = new JobTopOrderVO();
            jobTopOrderVO.setRunTime(DateUtil.getTimeDifference(execTime * 1000));
            jobTopOrderVO.setJobId(null != jobId ? jobId : 0);
            jobTopOrderVO.setTaskId(null != taskId ? taskId : 0);
            if (null != cycTime) {
                jobTopOrderVO.setCycTime(DateUtil.addTimeSplit(cycTime));
            }
            jobTopOrderVO.setType(null != type ? type : 0);
            Integer taskType = MathUtil.getIntegerVal(info.get("taskType"));
            jobTopOrderVO.setTaskType(null != taskType ? taskType : 0);
            jobTopOrderVO.setIsDeleted(MathUtil.getIntegerVal(info.get("isDeleted")));
            jobTopOrderVO.setCreateUserId(MathUtil.getLongVal(info.get("createUserId")));
            jobTopOrderVO.setTaskName(MathUtil.getString(info.get("taskName")));
            jobTopOrderVO.setOwnerUserId(MathUtil.getLongVal(info.get("ownerUserId")));

            jobTopOrderVOS.add(jobTopOrderVO);
        }
        userService.fillTopOrderJobUserName(jobTopOrderVOS);
        return jobTopOrderVOS;
    }

    /**
     * 近30天任务出错排行
     */
    public List<JobTopErrorVO> errorTopOrder(Long projectId, Long tenantId, Integer appType, Long dtuicTenantId) {
        if (environmentContext.getOpenErrorTop()) {
            // 查询当天任务排名
            Timestamp time = new Timestamp(DateUtil.getLastDay(0));
            PageQuery<Object> pageQuery = new PageQuery<>(1, 10);
            String startCycTime = dayFormatterAll.print(getTime(time.getTime(), 0).getTime());
            List<JobTopErrorVO> jobTopErrorVOS = scheduleJobDao.listTopErrorByType(dtuicTenantId, tenantId, projectId, EScheduleType.NORMAL_SCHEDULE.getType(), startCycTime, FAILED_STATUS, pageQuery, appType);

            // 查询前29天任务排名
            Timestamp timeTo = new Timestamp(DateUtil.getLastDay(30));
            List<JobTopErrorVO> jobTopErrorVOSTo = scheduleJobFailedDao.listTopError(appType, dtuicTenantId, projectId, timeTo);
            List<JobTopErrorVO> merge = merge(jobTopErrorVOS, jobTopErrorVOSTo);
            List<JobTopErrorVO> result = merge.stream().sorted(Comparator.comparing(JobTopErrorVO::getErrorCount).reversed()).collect(Collectors.toList());

            if (result.size()>10) {
                return result.subList(0,9);
            }
            return result;
        } else {
            // 查询当天任务排名
            Timestamp time = new Timestamp(DateUtil.getLastDay(30));
            PageQuery<Object> pageQuery = new PageQuery<>(1, 10);
            String startCycTime = dayFormatterAll.print(getTime(time.getTime(), 0).getTime());
            List<JobTopErrorVO> jobTopErrorVOS = scheduleJobDao.listTopErrorByType(dtuicTenantId, tenantId, projectId, EScheduleType.NORMAL_SCHEDULE.getType(), startCycTime, FAILED_STATUS, pageQuery, appType);
            return jobTopErrorVOS;
        }
    }

    private List<JobTopErrorVO> merge(List<JobTopErrorVO> jobTopErrorVOS, List<JobTopErrorVO> jobTopErrorVOSTo) {
        Map<Long,JobTopErrorVO> totalMaps = Maps.newHashMap();
        for (JobTopErrorVO jobTopErrorVO : jobTopErrorVOS) {
            sumErrorCount(totalMaps, jobTopErrorVO);
        }

        for (JobTopErrorVO errorVO : jobTopErrorVOSTo) {
            sumErrorCount(totalMaps, errorVO);
        }

        return Lists.newArrayList(totalMaps.values());
    }

    private void sumErrorCount(Map<Long, JobTopErrorVO> totalMaps, JobTopErrorVO jobTopErrorVO) {
        long taskId = jobTopErrorVO.getTaskId();
        JobTopErrorVO jobTopVO = totalMaps.get(taskId);
        if (jobTopVO == null) {
             jobTopVO = new JobTopErrorVO();
        }
        int errorCount = jobTopVO.getErrorCount();
        jobTopVO.setTaskId(taskId);
        jobTopVO.setErrorCount(errorCount+jobTopErrorVO.getErrorCount());
        totalMaps.put(taskId,jobTopVO);
    }


    /**
     * 曲线图数据
     */
    public ScheduleJobChartVO getJobGraph( Long projectId,  Long tenantId,  Integer appType,  Long dtuicTenantId) {

        List<Integer> statusList = new ArrayList<>(4);
        List<Integer> finishedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FINISHED.getStatus());
        List<Integer> failedList = RdosTaskStatus.getCollectionStatus(RdosTaskStatus.FAILED.getStatus());
        statusList.addAll(finishedList);
        statusList.addAll(failedList);
        String today = DateTime.now().plusDays(0).withTime(0,0,0,0).toString(timeFormatter);
        String yesterday = DateTime.now().plusDays(-1).withTime(0,0,0,0).toString(timeFormatter);
        String lastMonth = DateTime.now().plusDays(-30).withTime(0,0,0,0).toString(timeFormatter);
        List<Object> todayJobList = finishData(scheduleJobDao.listTodayJobs(today,statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        List<Object> yesterdayJobList = finishData(scheduleJobDao.listYesterdayJobs(yesterday, today, statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType, dtuicTenantId));
        List<Object> monthJobList;
        if (environmentContext.getOpenRealJobGraph()) {
            monthJobList = getNoRealJobGraph(projectId,dtuicTenantId,appType);
        } else {
            monthJobList = finishData(scheduleJobDao.listMonthJobs(lastMonth,statusList, EScheduleType.NORMAL_SCHEDULE.getType(), projectId, tenantId, appType,dtuicTenantId));
        }

        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            monthJobList.set(i, (Long) monthJobList.get(i) / 30);
        }
        return new ScheduleJobChartVO(todayJobList, yesterdayJobList, monthJobList);
    }

    private List<Object> getNoRealJobGraph(Long projectId, Long dtuicTenantId, Integer appType) {
        String date = DateTime.now().plusDays(-30).withTime(0, 0, 0, 0).toString("yyyyMMdd");

        List<ScheduleJobGraph> scheduleJobGraphs = scheduleJobGraphDao.selectByDate(dtuicTenantId, appType, projectId, date);
        Map<Integer, Integer> collect = scheduleJobGraphs.stream().collect(Collectors.groupingBy(ScheduleJobGraph::getHour, Collectors.mapping(ScheduleJobGraph::getCount, Collectors.summingInt(g -> g))));
        List<Object> dataList = Lists.newArrayList();
        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            dataList.add(new Long(collect.getOrDefault(i, 0)));
        }

        return dataList;
    }

    /**
     * 获取数据科学的曲线图
     *
     * @return
     */
    public ChartDataVO getScienceJobGraph( long projectId,  Long tenantId,
                                           String taskType) {

        List<Integer> finishedList = Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus());
        List<Integer> failedList = Lists.newArrayList(RdosTaskStatus.FAILED.getStatus(), RdosTaskStatus.SUBMITFAILD.getStatus());
        List<Integer> deployList = Lists.newArrayList(RdosTaskStatus.UNSUBMIT.getStatus(), RdosTaskStatus.SUBMITTING.getStatus(), RdosTaskStatus.WAITENGINE.getStatus());
        List<Integer> taskTypes =  convertStringToList(taskType);
        List<Map<String, Object>> successCnt = scheduleJobDao.listThirtyDayJobs(finishedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> failCnt = scheduleJobDao.listThirtyDayJobs(failedList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> deployCnt = scheduleJobDao.listThirtyDayJobs(deployList, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        List<Map<String, Object>> totalCnt = scheduleJobDao.listThirtyDayJobs(null, EScheduleType.NORMAL_SCHEDULE.getType(), taskTypes, projectId, tenantId);
        BatchSecienceJobChartVO result = new BatchSecienceJobChartVO();
        return result.format(totalCnt, successCnt, failCnt, deployCnt);
    }

    public ScheduleJobScienceJobStatusVO countScienceJobStatus( List<Long> projectIds,  Long tenantId,  Integer runStatus,  Integer type,  String taskType,
                                                      String cycStartTime,  String cycEndTime) {
        if(StringUtils.isBlank(taskType)){
            throw new RdosDefineException("任务类型不能为空");
        }
        Map<String, Object> stringObjectMap = scheduleJobDao.countScienceJobStatus(runStatus, projectIds, type, convertStringToList(taskType), tenantId, cycStartTime, cycEndTime);
        ScheduleJobScienceJobStatusVO scienceJobStatusVO = new ScheduleJobScienceJobStatusVO();
        if(null == stringObjectMap){
            return scienceJobStatusVO;
        }
        scienceJobStatusVO.setTotal(stringObjectMap.get("total")==null?0:Integer.parseInt(stringObjectMap.get("total").toString()));
        scienceJobStatusVO.setDeployCount(stringObjectMap.get("deployCount")==null?0:Integer.parseInt(stringObjectMap.get("deployCount").toString()));
        scienceJobStatusVO.setFailCount(stringObjectMap.get("failCount")==null?0:Integer.parseInt(stringObjectMap.get("failCount").toString()));
        scienceJobStatusVO.setSuccessCount(stringObjectMap.get("successCount")==null?0:Integer.parseInt(stringObjectMap.get("successCount").toString()));
        return scienceJobStatusVO;
    }

    private List<Object> finishData(List<Map<String, Object>> metadata) {
        Map<String, Long> dataMap = new HashMap<>();
        List<Object> dataList = new ArrayList<>();

        for (Map<String, Object> data : metadata) {
            //只要看到每个时间点完成的任务数
            dataMap.put(MathUtil.getString(data.get("hour")), MathUtil.getLongVal(data.get("data")));
        }

        String hour;
        for (int i = 0; i < TOTAL_HOUR_DAY; i++) {
            if (i < 10) {
                hour = "0" + i;
            } else {
                hour = "" + i;
            }
            dataList.add(dataMap.getOrDefault(hour, 0L));
        }
        return dataList;
    }


    /**
     * 任务运维 - 搜索
     *
     * @return
     * @author toutian
     */
    public PageResult<List<com.dtstack.engine.api.vo.ScheduleJobVO>> queryJobs(QueryJobDTO vo) throws Exception {

        if (vo.getType() == null && CollectionUtils.isEmpty(vo.getTypes())) {
            throw new RdosDefineException("Type parameter is required", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        boolean queryAll = false;
        if (StringUtils.isNotBlank(vo.getTaskName()) ||
                vo.getCycEndDay() != null ||
                vo.getCycStartDay() != null ||
                CollectionUtils.isNotEmpty(vo.getResourceIds()) ||
                StringUtils.isNotBlank(vo.getJobStatuses()) ||
                StringUtils.isNotBlank(vo.getTaskType())) {
            //条件查询：针对工作流任务，查询全部父子节点
            queryAll = true;
        } else {
            //无条件：只查询工作流父节点
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());

        // 设置是模糊匹配类型还是精确匹配
        String searchType = vo.getSearchType();
        changeSearchType(batchJobDTO, searchType);
        batchJobDTO.setPageQuery(true);
        pageQuery.setModel(batchJobDTO);

        int count = 0;
        List<com.dtstack.engine.api.vo.ScheduleJobVO> result = new ArrayList<>();
        PageResult<List<com.dtstack.engine.api.vo.ScheduleJobVO>> conditionResult = getTaskIdsByCondition(vo, batchJobDTO, pageQuery, count, result);
        if (conditionResult != null) {
            return conditionResult;
        }
        if (AppType.DATASCIENCE.getType().equals(vo.getAppType())) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
            count = queryScienceJob(batchJobDTO, queryAll, pageQuery, result);
        } else {
            count = queryNormalJob(batchJobDTO, queryAll, pageQuery, result);
        }
        userService.fillScheduleJobVO(result);

        return new PageResult<>(result, count, pageQuery);
    }

    private PageResult<List<com.dtstack.engine.api.vo.ScheduleJobVO>> getTaskIdsByCondition(QueryJobDTO vo, ScheduleJobDTO batchJobDTO,
                                                                                            PageQuery<ScheduleJobDTO> pageQuery,
                                                                                            int count, List<com.dtstack.engine.api.vo.ScheduleJobVO> result) {
        List<Long> taskIds = null;
        if (CollectionUtils.isNotEmpty(batchJobDTO.getCalenderIds())) {
            taskIds = calenderService.getAllTaskByCalenderId(batchJobDTO.getCalenderIds(), batchJobDTO.getAppType());
            if (CollectionUtils.isEmpty(taskIds)) {
                return new PageResult<>(result, count, pageQuery);
            }
        }
        //先将满足条件的taskId查出来，缩小过滤范围
        if (StringUtils.isNotBlank(vo.getTaskName()) || null != vo.getOwnerId()) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(), vo.getTaskName(),
                    vo.getAppType(), vo.getOwnerId(), vo.getProjectIds(), batchJobDTO.getSearchType(), null, null);
            if (CollectionUtils.isEmpty(batchTaskShades)) {
                return new PageResult<>(result, count, pageQuery);
            }
            List<Long> nameTaskIds = batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskIds)) {
                taskIds.retainAll(nameTaskIds);
            } else {
                taskIds = nameTaskIds;
            }
        }
        if(!CollectionUtils.isEmpty(taskIds)){
            if (CollectionUtils.isEmpty(batchJobDTO.getTaskIds())) {
                batchJobDTO.setTaskIds(taskIds);
            } else {
                List<Long> queryTaskIds = batchJobDTO.getTaskIds();
                queryTaskIds.retainAll(taskIds);
                batchJobDTO.setTaskIds(queryTaskIds);
            }
        }
        return null;
    }


    private void changeSearchType(ScheduleJobDTO batchJobDTO, String searchType) {
        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            //全模糊匹配
            batchJobDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            //精确匹配
            batchJobDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            //右模糊匹配
            batchJobDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchJobDTO.setSearchType(4);
        } else {
            batchJobDTO.setSearchType(1);
        }
    }

    /**
     * 正常查询 分钟小时不归类
     * @param batchJobDTO
     * @param queryAll
     * @param pageQuery
     * @param result
     * @return
     * @throws Exception
     */
    private int queryNormalJob(ScheduleJobDTO batchJobDTO, boolean queryAll, PageQuery<ScheduleJobDTO> pageQuery, List<com.dtstack.engine.api.vo.ScheduleJobVO> result) throws Exception {
        int count = scheduleJobDao.generalCount(batchJobDTO);
        if (count > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                List<Long> resourceIds = scheduleJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
                Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(resourceIds);
                Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(scheduleJobs);
                List<ScheduleJobVO> batchJobVOS = this.transfer(scheduleJobs, shadeMap,groupInfo);

                if (queryAll) {
                    //处理工作流下级
                    dealFlowWorkSubJobs(batchJobVOS);
                } else {
                    //前端异步获取relatedJobs
                    //dealFlowWorkJobs(vos, shadeMap);
                }
                if (CollectionUtils.isNotEmpty(batchJobVOS)) {
                    for (ScheduleJobVO batchJobVO : batchJobVOS) {
                        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(batchJobVO.getStatus())) {
                            batchJobVO.setStatus(RdosTaskStatus.RUNNING.getStatus());
                        }
                        result.add(batchJobVO);
                    }
                }
            }
        }
        return count;
    }



    /**
     * 算法查询 分钟小时归类
     * @param batchJobDTO
     * @param queryAll
     * @param pageQuery
     * @param result
     * @return
     * @throws Exception
     */
    private int queryScienceJob(ScheduleJobDTO batchJobDTO, boolean queryAll, PageQuery<ScheduleJobDTO> pageQuery, List<com.dtstack.engine.api.vo.ScheduleJobVO> result) throws Exception {

        int count = scheduleJobDao.generalScienceCount(batchJobDTO);
        if (count > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalScienceQuery(pageQuery);
            if (CollectionUtils.isNotEmpty(scheduleJobs)) {

                List<Long> resourceIds = scheduleJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
                Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(resourceIds);
                //获取任务id->任务的map
                Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(scheduleJobs);

                List<ScheduleJobVO> batchJobVOS = this.transfer(scheduleJobs, shadeMap,groupInfo);

                if (queryAll) {
                    //处理工作流下级
                    dealFlowWorkSubJobs(batchJobVOS);
                } else {
                    //前端异步获取relatedJobs
                    //dealFlowWorkJobs(vos, shadeMap);
                }
                if (CollectionUtils.isNotEmpty(batchJobVOS)) {
                    for (ScheduleJobVO batchJobVO : batchJobVOS) {
                        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(batchJobVO.getStatus())) {
                            batchJobVO.setStatus(RdosTaskStatus.RUNNING.getStatus());
                        }
                        result.add(batchJobVO);
                    }
                }
            }
        }
        return count;
    }

    public List<SchedulePeriodInfoVO> displayPeriods( boolean isAfter,  Long jobId,  Long projectId,  int limit) throws Exception {
        ScheduleJob job = scheduleJobDao.getOne(jobId);
        if (job == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        //需要根据查询的job的类型来
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getJobRangeByCycTimeByLimit(job.getTaskId(), isAfter, job.getCycTime(),job.getAppType(),job.getType(),limit,null);
        Collections.sort(scheduleJobs, new Comparator<ScheduleJob>() {

            @Override
            public int compare(ScheduleJob o1, ScheduleJob o2) {
                if (!NumberUtils.isNumber(o1.getCycTime())) {
                    return 1;
                }

                if (!NumberUtils.isNumber(o2.getCycTime())) {
                    return -1;
                }

                if (Long.parseLong(o1.getCycTime()) < Long.parseLong(o2.getCycTime())) {
                    return 1;
                }
                if (Long.parseLong(o1.getCycTime()) > Long.parseLong(o2.getCycTime())) {
                    return -1;
                }
                return 0;
            }
        });
        List<SchedulePeriodInfoVO> vos = new ArrayList<>(scheduleJobs.size());
        scheduleJobs.forEach(e -> {
            SchedulePeriodInfoVO vo = new SchedulePeriodInfoVO();
            vo.setJobId(e.getId());
            vo.setCycTime(DateUtil.addTimeSplit(e.getCycTime()));
            vo.setStatus(e.getStatus());
            vo.setTaskId(e.getTaskId());
            vo.setVersion(e.getVersionId());
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleJobVO getRelatedJobs( String jobId,  String query) throws Exception {
        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        if(null == vo){
            return null;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        if(null == scheduleJob){
            throw new RdosDefineException("该实例对象不存在");
        }
        Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(scheduleJob));
        Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(Lists.newArrayList(scheduleJob.getResourceId()));
        List<ScheduleJobVO> transfer = this.transfer(Lists.newArrayList(scheduleJob), shadeMap,groupInfo);
        if (CollectionUtils.isEmpty(transfer)) {
            return null;
        }
        ScheduleJobVO batchJobVO = transfer.get(0);

        if (EScheduleJobType.WORK_FLOW.getVal().equals(batchJobVO.getBatchTask().getTaskType())) {
            vo.setSplitFiledFlag(true);
            //除去任务类型中的工作流类型的条件，用于展示下游节点
            if (StringUtils.isNotBlank(vo.getTaskType())) {
                vo.setTaskType(vo.getTaskType().replace(String.valueOf(EScheduleJobType.WORK_FLOW.getVal()), ""));
            }
            ScheduleJobDTO batchJobDTO = createQuery(vo);
            batchJobDTO.setPageQuery(false);
            batchJobDTO.setFlowJobId(jobId);
            PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
            pageQuery.setModel(batchJobDTO);
            batchJobDTO.setNeedQuerySonNode(true);
            List<ScheduleJob> subJobs = scheduleJobDao.generalQuery(pageQuery);
            Map<Long, ScheduleTaskForFillDataDTO> subShadeMap = this.prepare(subJobs);
            List<Long> subResourceIds = subJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
            subResourceIds.removeAll(groupInfo.keySet());
            if(CollectionUtils.isNotEmpty(subResourceIds)){
                Map<Long, ResourceGroupDetail> subGropInfo = resourceGroupService.getGroupInfo(subResourceIds);
                groupInfo.putAll(subGropInfo);
            }
            List<ScheduleJobVO> subJobVOs = this.transfer(subJobs, subShadeMap,groupInfo);
            List<com.dtstack.engine.api.vo.ScheduleJobVO> relatedJobVOs= new ArrayList<>(subJobVOs.size());
            subJobVOs.forEach(subJobVO -> relatedJobVOs.add(subJobVO));
            batchJobVO.setRelatedJobs(relatedJobVOs);
            userService.fillScheduleJobVO(Lists.newArrayList(batchJobVO));
            return batchJobVO;
        } else {
            throw new RdosDefineException("Only workflow tasks have subordinate nodes");
        }

    }


    //处理工作流子节点
    private void dealFlowWorkSubJobs(List<ScheduleJobVO> vos) throws Exception {

        Map<String, ScheduleJobVO> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<ScheduleJobVO> copy = Lists.newArrayList(vos);
        Iterator<ScheduleJobVO> iterator = vos.iterator();
        while (iterator.hasNext()) {
            ScheduleJobVO jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                //是工作流子节点
                if (record.containsKey(flowJobId)) {
                    ScheduleJobVO flowVo = record.get(flowJobId);
                    flowVo.getRelatedJobs().add(jobVO);
                    iterator.remove();
                } else {
                    ScheduleJobVO flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        //查出来的任务列表中有该子结点的工作流
                        flowVO = copy.get(voIndex.get(flowJobId));
                        //将工作流子节点设置为工作流关联jobs
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        ScheduleJob flow = scheduleJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(Lists.newArrayList(flow.getResourceId()));
                        Map<Long, ScheduleTaskForFillDataDTO> batchTaskShadeMap = this.prepare(Lists.newArrayList(flow));
                        List<ScheduleJobVO> flowVOs = this.transfer(Lists.newArrayList(flow), batchTaskShadeMap,groupInfo);
                        flowVO = flowVOs.get(0);
                        flowVO.setRelatedJobs(Lists.newArrayList(jobVO));
                        //将工作流子结点替换成工作流
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }


    private Map<Long, ScheduleTaskForFillDataDTO> prepare(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return new HashMap<>(0);
        }
        Integer appType = scheduleJobs.get(0).getAppType();

        Set<Long> taskIdList = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());

        return scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIdList, null,appType).stream()
                .collect(Collectors.toMap(ScheduleTaskForFillDataDTO::getTaskId, scheduleTaskForFillDataDTO -> scheduleTaskForFillDataDTO));
    }

    private List<ScheduleJobVO> transfer(List<ScheduleJob> scheduleJobs, Map<Long, ScheduleTaskForFillDataDTO> batchTaskShadeMap,Map<Long, ResourceGroupDetail> groupInfo) {

        if(CollectionUtils.isEmpty(scheduleJobs)){
            return Collections.EMPTY_LIST;
        }
        List<ScheduleJobVO> vos = new ArrayList<>(scheduleJobs.size());
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleTaskForFillDataDTO taskShade = batchTaskShadeMap.get(scheduleJob.getTaskId());
            if (taskShade == null) {
                continue;
            }
            //维持旧接口 数据结构
            ScheduleEngineJob engineJob = new ScheduleEngineJob();
            engineJob.setStatus(scheduleJob.getStatus());
            engineJob.setRetryNum(scheduleJob.getRetryNum());
            String voTaskName = taskShade.getName();
            ScheduleJobVO batchJobVO = new ScheduleJobVO(scheduleJob);
            if (scheduleJob.getExecStartTime() != null) {
                batchJobVO.setExecStartDate(timeFormatter.print(scheduleJob.getExecStartTime().getTime()));
                engineJob.setExecStartTime(new Timestamp(scheduleJob.getExecStartTime().getTime()));
            }
            if (scheduleJob.getExecEndTime() != null) {
                batchJobVO.setExecEndDate(timeFormatter.print(scheduleJob.getExecEndTime().getTime()));
                engineJob.setExecEndTime(new Timestamp(scheduleJob.getExecEndTime().getTime()));
            }
            engineJob.setExecTime(scheduleJob.getExecTime());
            batchJobVO.setScheduleEngineJob(engineJob);

            ScheduleTaskVO taskVO = new ScheduleTaskVO(taskShade);
            taskVO.setName(voTaskName);
            if (scheduleJob.getPeriodType() != null) {
                batchJobVO.setTaskPeriodId(scheduleJob.getPeriodType());
            }
            batchJobVO.setVersion(scheduleJob.getVersionId());
            batchJobVO.setBatchTask(taskVO);
            batchJobVO.setOwnerUserId(taskShade.getOwnerUserId());
            batchJobVO.setResourceId(scheduleJob.getResourceId());
            if (groupInfo.get(scheduleJob.getResourceId()) != null) {
                batchJobVO.setResourceGroupName(groupInfo.get(scheduleJob.getResourceId()).getResourceName());
            }
            batchJobVO.setTaskType(taskShade.getTaskType());
            vos.add(batchJobVO);
        }
        return vos;
    }


    /**
     * 获取任务的状态统计信息
     *
     * @author toutian
     */
    public Map<String, Long> queryJobsStatusStatistics(QueryJobDTO vo) {

        if (vo.getType() == null) {
            throw new RdosDefineException("Type is required", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = createQuery(vo);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        if (AppType.DATASCIENCE.getType().equals(vo.getAppType())) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }
        //需要查询工作流的子节点
        batchJobDTO.setNeedQuerySonNode(true);
        String searchType = vo.getSearchType();
        changeSearchType(batchJobDTO, searchType);
        Map<String, Long> attachment = Maps.newHashMap();
        if (StringUtils.isNotBlank(vo.getTaskName()) || null!= vo.getOwnerId()) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(), vo.getTaskName(), vo.getAppType(), vo.getOwnerId(),vo.getProjectIds(),
                    batchJobDTO.getSearchType(), null, null);
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            } else {
                return attachment;
            }
        }
        List<StatusCount> statusCountList = scheduleJobDao.getJobsStatusStatistics(batchJobDTO);

        if(CollectionUtils.isEmpty(statusCountList)){
            return attachment;
        }
        long totalNum = 0;
        mergeStatusAndShow(statusCountList, attachment, totalNum);

        return attachment;
    }

    /**
     * 获取任务的状态统计信息 封装成 JobStatusVo 对象
     * @param vo
     * @return
     */
    public JobStatusVo queryJobsStatusStatisticsToVo(QueryJobDTO vo) {
        Map<String, Long> map = this.queryJobsStatusStatistics(vo);
        JobStatusVo jobStatusVo = JSON.parseObject(JSON.toJSONString(map), JobStatusVo.class);
        return jobStatusVo;
    }

    /**
     * @author newman
     * @Description 将数据库细分的任务状态合并展示给前端
     * @Date 2020-12-18 16:16
     * @param statusCountList:
     * @param attachment:
     * @param totalNum:
     * @return: void
     **/
    private void mergeStatusAndShow(List<StatusCount> statusCountList, Map<String, Long> attachment, long totalNum) {
        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusFailedDetail();
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            String statusName = RdosTaskStatus.getCode(entry.getKey());
            List<Integer> statuses = entry.getValue();
            long num = 0;
            for (StatusCount statusCount : statusCountList) {
                if (statuses.contains(statusCount.getStatus())) {
                    num += statusCount.getCount();
                }
            }
            if (!attachment.containsKey(statusName)) {
                attachment.put(statusName, num);
            } else {
                //上一个该状态的数量
                Long lastNum = attachment.getOrDefault(statusName,0L);
                attachment.put(statusName, num + lastNum);
            }
            totalNum += num;
        }
        attachment.putIfAbsent("ALL", totalNum);
    }


    private Map<Integer, List<Integer>> getStatusMap(Boolean splitFiledFlag) {
        Map<Integer, List<Integer>> statusMap;
        if (null != splitFiledFlag && splitFiledFlag) {
            statusMap = RdosTaskStatus.getStatusFailedDetailAndExpire();
        } else {
            statusMap = RdosTaskStatus.getCollectionStatus();
        }
        return statusMap;
    }


    private ScheduleJobDTO createQuery(QueryJobDTO vo) {

        ScheduleJobDTO batchJobDTO = new ScheduleJobDTO();
        this.createBaseQuery(vo, batchJobDTO);

        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = getStatusMap(vo.getSplitFiledFlag());
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                } else {
                    //不在失败状态拆分里
                    statues.add(Integer.parseInt(status));
                }
            }
            batchJobDTO.setJobStatuses(statues);
        }

        //任务名
        if (StringUtils.isNotBlank(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (StringUtils.isNotBlank(vo.getFillTaskName())) {
            batchJobDTO.setJobNameRightLike(vo.getFillTaskName() + "-");
        }

        //责任人
        if (null != vo.getOwnerId() && vo.getOwnerId() != 0) {
            batchJobDTO.setOwnerUserId(vo.getOwnerId());
        }
        //业务时间
        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        this.setCycDay(batchJobDTO, vo.getCycStartDay(), vo.getCycEndDay(), vo.getTenantId(), vo.getProjectId());

        //执行耗时
        if (null != vo.getExecTime()) {
            batchJobDTO.setExecTime(vo.getExecTime() * 1000);
        }
        // baseQuery里已经设置了分页，这里去掉

        if(vo.getExecStartDay()!=null){
            batchJobDTO.setExecStartDay(new Date(vo.getExecStartDay()));
        }
        if (vo.getExecEndDay()!=null) {
            batchJobDTO.setExecEndDay(new Date(vo.getExecEndDay()));
        }

        if(CollectionUtils.isNotEmpty(vo.getCalenderIds())) {
            batchJobDTO.setCalenderIds(vo.getCalenderIds());
        }

        if(vo.getExecEndTimeStart()!=null){
            batchJobDTO.setExecEndTimeStart(new Date(vo.getExecEndTimeStart()));
        }
        if (vo.getExecEndTimeEnd()!=null) {
            batchJobDTO.setExecEndTimeEnd(new Date(vo.getExecEndTimeEnd()));
        }
        return batchJobDTO;
    }

    private void createBaseQuery(QueryJobDTO vo, ScheduleJobDTO batchJobDTO) {

        batchJobDTO.setTenantId(vo.getTenantId());
        batchJobDTO.setProjectId(vo.getProjectId());
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));
        batchJobDTO.setExecTimeSort(vo.getExecTimeSort());
        batchJobDTO.setExecStartSort(vo.getExecStartSort());
        batchJobDTO.setExecEndSort(vo.getExecEndSort());
        batchJobDTO.setCycSort(vo.getCycSort());
        batchJobDTO.setRetryNumSort(vo.getRetryNumSort());
        batchJobDTO.setBusinessDateSort(vo.getBusinessDateSort());
        batchJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));
        batchJobDTO.setAppType(vo.getAppType());
        batchJobDTO.setBusinessType(vo.getBusinessType());
        batchJobDTO.setTypes(vo.getTypes());
        batchJobDTO.setResourceId(vo.getResourceId());
        batchJobDTO.setComputeType(vo.getComputeType());
        batchJobDTO.setResourceIds(vo.getResourceIds());
        batchJobDTO.setFillId(vo.getFillId());

        if (CollectionUtils.isNotEmpty(vo.getProjectIds())) {
            batchJobDTO.setProjectIds(vo.getProjectIds());
        }

        //调度类型
        if (vo.getType() != null) {
            batchJobDTO.setType(vo.getType());
        }

        if (StringUtils.isNotBlank(vo.getTaskName()) ||
                vo.getCycEndDay() != null ||
                vo.getCycStartDay() != null ||
                StringUtils.isNotBlank(vo.getJobStatuses()) ||
                StringUtils.isNotBlank(vo.getTaskType())) {
            //条件查询：针对工作流任务，查询全部父子节点
            batchJobDTO.setNeedQuerySonNode(true);
        } else {
            //无条件：只查询工作流父节点
            batchJobDTO.setNeedQuerySonNode(false);
        }

        //分页
        batchJobDTO.setPageQuery(true);
        //bugfix #19764 为对入参做处理
        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            batchJobDTO.setJobStatuses(Arrays.stream(vo.getJobStatuses().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(vo.getTaskIds())) {
            batchJobDTO.setTaskIds(vo.getTaskIds());
        }
        if (null != vo.getTaskId()) {
            if (null == batchJobDTO.getTaskIds()) {
                batchJobDTO.setTaskIds(new ArrayList<>());
            }
            batchJobDTO.getTaskIds().add(vo.getTaskId());
        }
    }

    private void setBizDay(ScheduleJobDTO batchJobDTO, Long bizStartDay, Long bizEndDay, Long tenantId, Long projectId) {
        if (bizStartDay != null && bizEndDay != null) {
            String bizStart = dayFormatterAll.print(getTime(bizStartDay * 1000, 0).getTime());
            String bizEnd = dayFormatterAll.print(getTime(bizEndDay * 1000, -1).getTime());
            batchJobDTO.setBizStartDay(bizStart);
            batchJobDTO.setBizEndDay(bizEnd);

            //设置调度始日期为业务开始日期的下一天
            batchJobDTO.setCycStartDay(dayFormatterAll.print(getTime(bizStartDay * 1000, -1).getTime()));
            batchJobDTO.setCycEndDay(dayFormatterAll.print(getTime(bizEndDay * 1000, -2).getTime()));
        }
    }

    private void setCycDay(ScheduleJobDTO batchJobDTO, Long cycStartDay, Long cycEndDay, Long tenantId, Long projectId) {
        if (cycStartDay != null && cycEndDay != null) {
            String cycStart = dayFormatterAll.print(getCycTime(cycStartDay * 1000).getTime());
            String cycEnd = dayFormatterAll.print(getCycTime(cycEndDay * 1000).getTime());
            batchJobDTO.setCycStartDay(cycStart);
            batchJobDTO.setCycEndDay(cycEnd);
        }
    }

    private Timestamp getTime(Long timestamp, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -day);
        return new Timestamp(calendar.getTimeInMillis());
    }


    private Timestamp getCycTime(Long timestamp) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return new Timestamp(calendar.getTimeInMillis());
    }

    private List<Integer> convertStringToList(String str) {
        List<Integer> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            String[] split = str.split(",");
            for (String sp : split) {
                if (StringUtils.isBlank(sp)) {
                    continue;
                }
                resultList.add(new Integer(sp));
            }
        }
        return resultList;
    }


    public List<ScheduleRunDetailVO> jobDetail( Long taskId,  Integer appType) {

        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(taskId, appType);

        if (task ==null ) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        PageQuery pageQuery = new PageQuery(1, 20, "business_date", Sort.DESC.name());
        List<Map<String, String>> jobs = scheduleJobDao.listTaskExeTimeInfo(task.getTaskId(), FINISH_STATUS, pageQuery,appType);
        List<ScheduleRunDetailVO> details = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (Map<String, String> job : jobs) {
                String execStartTimeObj = MathUtil.getString(job.get("execStartTime"));
                String execEndTimeObj = MathUtil.getString(job.get("execEndTime"));
                Long execTime = MathUtil.getLongVal(job.get("execTime"));

                ScheduleRunDetailVO runDetail = new ScheduleRunDetailVO();
                if (execTime == null || execTime == 0L) {
                    continue;
                }

                runDetail.setExecTime(execTime);
                runDetail.setStartTime(execStartTimeObj);
                runDetail.setEndTime(execEndTimeObj);

                runDetail.setTaskName(task.getName());
                details.add(runDetail);
            }
        }

        return details;
    }

    public Integer updateStatusAndLogInfoAndExecTimeById(String jobId, Integer status, String msg, Date execStartTime, Date execEndTime) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        scheduleJobExpandDao.updateLogInfoByJobId(jobId, msg,status);
        return updateLogAndThen.andThen((updateSize) ->
                        scheduleJobDao.updateStatusByJobId(jobId, status, null, execStartTime, execEndTime))
                .apply(jobId, new ExpandJob(msg, status, execStartTime, execEndTime));
    }

    public Integer updateStatusAndLogInfoById(String jobId, Integer status, String msg) {
        return updateLogAndThen.andThen((updateSize) ->
                        scheduleJobDao.updateStatusByJobId(jobId, status, null, null, null))
                .apply(jobId, new ExpandJob(msg, status, null, null));
    }

    public Integer updateStatusAndLogInfoByIds(List<String> jobIds, Integer status, String msg) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        for (String jobId : jobIds) {
            scheduleJobExpandDao.updateLogInfoByJobId(jobId, msg,status);
        }
        return scheduleJobDao.updateJobStatusByJobIds(jobIds, status);
    }

    public Integer updateStatusAndAppendLogInfoByIds(List<String> jobIds, Integer status, String msg) {
        for (String jobId : jobIds) {
            ScheduleJobExpand expand = scheduleJobExpandDao.getLogByJobId(jobId);
            String logInfo;
            if (StringUtils.isNotBlank(expand.getLogInfo())) {
                logInfo = expand.getLogInfo() +"==================\n"+ msg;
            } else {
                logInfo = msg;
            }

            if (StringUtils.isNotBlank(logInfo) && logInfo.length() > 5000) {
                logInfo = logInfo.substring(0, 5000) + "...";
            }

            scheduleJobExpandDao.updateLogInfoByJobId(jobId, logInfo,status);
        }
        return scheduleJobDao.updateJobStatusByJobIds(jobIds, status);
    }

    private BiFunction<String, ExpandJob, Integer> updateLogAndThen = (jobId, expandJob) -> {
        String msg = expandJob.msg;
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        return scheduleJobExpandDao.updateJobExpandByJobId(jobId, msg,expandJob.status,expandJob.execStartTime,expandJob.execEndTime);
    };

    public JobExecInfoVO getJobExecInfo(String jobId) {
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException(ErrorCode.JOB_ID_CAN_NOT_EMPTY);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        // 只获取运行中的任务的
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            return null;
        }
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
            JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId()
                    , jobClient.getTenantId(), jobClient.getEngineType(), null, jobClient.getUserId(), paramAction.getResourceId(), null, jobClient.getComponentVersion());
            return scheduleJobStruct.toJobExecInfoVO(dataSourceXOperator.getJobExecInfo(jobIdentifier, false, false));
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable e) {
            throw new RdosDefineException(String.format("job:%s getJobExecInfo error", jobId), ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    public Boolean isJobResourceMonitorEnable(String jobId) {
        return null;
    }

    public List<BlockJobVO> blockJobList(String jobId) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());

        if (scheduleJob == null) {
            return Lists.newArrayList();
        }

        if (EScheduleJobType.WORK_FLOW.getType().equals(scheduleJob.getTaskType()) && RdosTaskStatus.FAILED_STATUS.contains(scheduleJob.getStatus())) {
             // 工作流节点失败了
            List<ScheduleJob> scheduleJobs = scheduleJobDao.getSubJobsAndStatusByFlowId(jobId);
            List<String> jobIds = Lists.newArrayList();
            for (ScheduleJob job : scheduleJobs) {
                if (RdosTaskStatus.FAILED_STATUS.contains(job.getStatus())) {
                    jobIds.add(job.getJobId());
                }
            }

            return getBlockJobVOSByIds(jobIds);
        }

        List<ScheduleJobRelyCheck> scheduleJobRelyChecks = scheduleJobRelyCheckService.findByJobId(jobId);
        return getBlockJobVOSByRelyCheck(scheduleJobRelyChecks);
    }

    private List<BlockJobVO> getBlockJobVOSByRelyCheck(List<ScheduleJobRelyCheck> scheduleJobRelyChecks) {
        List<BlockJobVO> blockJobVOS = Lists.newArrayList();
        List<ScheduleJobRelyCheck> parentNoJob = scheduleJobRelyChecks.stream().filter(check -> check.getParentJobCheckStatus() != null
                && check.getParentJobCheckStatus().equals(JobCheckStatus.FATHER_NO_CREATED.getStatus())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(parentNoJob)) {
            blockJobVOS.addAll(getScheduleJobs(parentNoJob));
        }

        List<String> jobIds = scheduleJobRelyChecks.stream()
                .filter(check -> !(check.getParentJobCheckStatus() != null &&
                        check.getParentJobCheckStatus().equals(JobCheckStatus.FATHER_NO_CREATED.getStatus())))
                .map(ScheduleJobRelyCheck::getParentJobId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(jobIds)) {
            return blockJobVOS;
        }

        List<ScheduleJob> jobs = scheduleJobDao.getByJobIds(Lists.newArrayList(jobIds),
                IsDeletedEnum.NOT_DELETE.getType());

        Map<Integer, List<Long>> taskMaps = jobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                Collectors.mapping(ScheduleJob::getTaskId, Collectors.toList())));

        Map<Integer, Set<Long>> projectMaps = jobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                Collectors.mapping(ScheduleJob::getProjectId, Collectors.toSet())));

        Table<Integer, Long, ScheduleTaskShade> taskCache = HashBasedTable.create();

        for (Integer appType : taskMaps.keySet()) {
            List<Long> taskIds = taskMaps.get(appType);
            List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listByTaskIds(taskIds, null, appType);
            Set<Long> projectIds = projectMaps.get(appType);
            if (CollectionUtils.isEmpty(projectIds)) {
                projectIds = Sets.newHashSet();
            }
            for (ScheduleTaskShade shade : shades) {
                taskCache.put(appType, shade.getTaskId(), shade);
                projectIds.add(shade.getProjectId());
            }
            projectMaps.put(appType,projectIds);
        }

        Table<Integer, Long, AuthProjectVO> projectCache = HashBasedTable.create();

        for (Integer appType : projectMaps.keySet()) {
            Set<Long> projectIds = projectMaps.get(appType);
            List<AuthProjectVO> projects = projectService.findProjects(appType, Lists.newArrayList(projectIds));
            for (AuthProjectVO project : projects) {
                projectCache.put(appType, project.getProjectId(), project);
            }
        }


        for (ScheduleJob job : jobs) {
            ScheduleTaskShade scheduleTaskShade = taskCache.get(job.getAppType(), job.getTaskId());
            if (scheduleTaskShade != null) {
                AuthProjectVO authProjectVO = projectCache.get(scheduleTaskShade.getAppType(), scheduleTaskShade.getProjectId());
                BlockJobVO blockJobVO = buildBlockJob(scheduleTaskShade, authProjectVO, job);
                blockJobVOS.add(blockJobVO);
            }
        }

        return blockJobVOS;
    }

    private List<BlockJobVO> getScheduleJobs(List<ScheduleJobRelyCheck> parentNoJob) {
        List<String> parentNoJobIds = parentNoJob.stream().map(ScheduleJobRelyCheck::getJobId).collect(Collectors.toList());
        List<ScheduleJob> jobs = scheduleJobDao.getByJobIds(Lists.newArrayList(parentNoJobIds),
                IsDeletedEnum.NOT_DELETE.getType());

        if (CollectionUtils.isEmpty(jobs)) {
            return Lists.newArrayList();
        }

        List<String> jobkeys = jobs.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKeys(jobkeys, RelyTypeEnum.NORMAL.getType());
        List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());
        List<ScheduleJob> parentJobs = scheduleJobDao.listJobByJobKeys(parentJobKeys);
        List<String> selectJobKeys = parentJobs.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        List<String> noCreateJobs = parentJobKeys.stream().filter(jobkey -> !selectJobKeys.contains(jobkey)).collect(Collectors.toList());

        List<BlockJobVO> blockJobVOS = Lists.newArrayList();
        for (String noCreateJob : noCreateJobs) {
            BlockJobVO blockJobVO = new BlockJobVO();
            Pair<Long, String> pair = TaskUtils.dissectJobKeyReturnTaskIdAndCycTime(noCreateJob);
            if (pair == null) {
                continue;
            }

            ScheduleTaskShade shadeDaoOne = scheduleTaskShadeDao.getById(pair.getKey());

            if (shadeDaoOne == null) {
                continue;
            }

            blockJobVO.setName(shadeDaoOne.getName());
            blockJobVO.setCycTime(DateUtil.addTimeSplit(pair.getValue()));
            blockJobVO.setAppType(shadeDaoOne.getAppType());
            blockJobVO.setAppTypeName(AppType.getValue(shadeDaoOne.getAppType()).getName());
            blockJobVO.setProjectId(shadeDaoOne.getProjectId());
            AuthProjectVO authProjectVO = projectService.finProject(shadeDaoOne.getProjectId(), shadeDaoOne.getAppType());
            if (authProjectVO != null) {
                blockJobVO.setProjectName(authProjectVO.getProjectAlias());
            }

            blockJobVO.setTenantName(tenantService.getTenantName(shadeDaoOne.getDtuicTenantId()));
            blockJobVO.setParentJobCheckStatus(JobCheckStatus.FATHER_NO_CREATED.getStatus());
            ApiResponse<List<UICUserVO>> uicUser = uicUserApiClient
                    .getByUserIds(Lists.newArrayList(shadeDaoOne.getOwnerUserId()));
            Optional<UICUserVO> uicUserVO = uicUser.getData().stream().findFirst();
            uicUserVO.ifPresent(userVO -> blockJobVO.setOwnerUserName(userVO.getUserName()));
            blockJobVOS.add(blockJobVO);
        }

        return blockJobVOS;
    }

    private List<BlockJobVO> getBlockJobVOSByIds(List<String> jobIds) {
        List<ScheduleJob> jobs = scheduleJobDao.getByJobIds(jobIds, IsDeletedEnum.NOT_DELETE.getType());
        Map<Integer, List<Long>> taskMaps = jobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                Collectors.mapping(ScheduleJob::getTaskId, Collectors.toList())));

        Map<Integer, Set<Long>> projectMaps = jobs.stream().collect(Collectors.groupingBy(ScheduleJob::getAppType,
                Collectors.mapping(ScheduleJob::getProjectId, Collectors.toSet())));

        Table<Integer, Long, ScheduleTaskShade> taskCache = HashBasedTable.create();

        for (Integer appType : taskMaps.keySet()) {
            List<Long> taskIds = taskMaps.get(appType);
            List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listByTaskIds(taskIds, IsDeletedEnum.NOT_DELETE.getType(), appType);
            Set<Long> projectIds = projectMaps.get(appType);
            if (CollectionUtils.isEmpty(projectIds)) {
                projectIds = Sets.newHashSet();
            }
            for (ScheduleTaskShade shade : shades) {
                taskCache.put(appType,shade.getTaskId(),shade);
                projectIds.add(shade.getProjectId());
            }
            projectMaps.put(appType,projectIds);
        }

        Table<Integer, Long, AuthProjectVO> projectCache = HashBasedTable.create();

        for (Integer appType : projectMaps.keySet()) {
            Set<Long> projectIds = projectMaps.get(appType);
            List<AuthProjectVO> projects = projectService.findProjects(appType,Lists.newArrayList(projectIds));
            for (AuthProjectVO project : projects) {
                projectCache.put(appType,project.getProjectId(),project);
            }
        }

        List<BlockJobVO> blockJobVOS = Lists.newArrayList();
        for (ScheduleJob job : jobs) {
            ScheduleTaskShade scheduleTaskShade = taskCache.get(job.getAppType(), job.getTaskId());
            AuthProjectVO authProjectVO = projectCache.get(scheduleTaskShade.getAppType(), scheduleTaskShade.getProjectId());
            BlockJobVO blockJobVO = buildBlockJob(scheduleTaskShade, authProjectVO, job);
            blockJobVOS.add(blockJobVO);
        }
        return blockJobVOS;
    }

    @NotNull
    private BlockJobVO buildBlockJob( ScheduleTaskShade scheduleTaskShade,
                                      AuthProjectVO authProjectVO,
                                      ScheduleJob scheduleJob) {
        BlockJobVO blockJobVO = new BlockJobVO();
        blockJobVO.setJobId(scheduleJob.getJobId());
        blockJobVO.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
        blockJobVO.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus()));
        blockJobVO.setAppTypeName(AppType.getValue(scheduleJob.getAppType()).getName());
        blockJobVO.setAppType(scheduleJob.getAppType());

        if (scheduleTaskShade != null) {
            blockJobVO.setName(scheduleTaskShade.getName());
            ApiResponse<List<UICUserVO>> uicUser = uicUserApiClient
                    .getByUserIds(Lists.newArrayList(scheduleTaskShade.getOwnerUserId()));
            Optional<UICUserVO> uicUserVO = uicUser.getData().stream().findFirst();
            uicUserVO.ifPresent(userVO -> blockJobVO.setOwnerUserName(userVO.getUserName()));
            blockJobVO.setTenantName(tenantService.getTenantName(scheduleTaskShade.getDtuicTenantId()));
        }

        if (authProjectVO != null) {
            blockJobVO.setProjectName(authProjectVO.getProjectAlias());
            blockJobVO.setProjectId(authProjectVO.getProjectId());
        }

        return blockJobVO;
    }

    public BlockJobNumVO blockJobNum(String jobId) {
        List<ScheduleJobRelyCheck> relyChecks = scheduleJobRelyCheckService.findByJobId(jobId);
        List<String> parentJobIds = relyChecks.stream().map(ScheduleJobRelyCheck::getParentJobId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(parentJobIds)) {
            // 如果没有上游的阻塞节点，还有一个可能自身就是阻塞节点
            parentJobIds.add(jobId);
        }

        List<ScheduleJobRelyCheck> parentJobChecks = scheduleJobRelyCheckService.findByParentsJobIds(parentJobIds);
        Map<String, List<ScheduleJobRelyCheck>> map = parentJobChecks.stream().collect(Collectors.groupingBy(ScheduleJobRelyCheck::getJobId));
        int downstreamNum = 0;
        int upstreamNum = 0;

        List<ScheduleJobRelyCheck> upstreamList = Lists.newArrayList(relyChecks);
        // 上游节点数
        while (CollectionUtils.isNotEmpty(upstreamList)) {
            List<String> directParentJobIds = upstreamList.stream()
                    .map(ScheduleJobRelyCheck::getDirectParentJobId).collect(Collectors.toList());
            upstreamNum+=directParentJobIds.size();
            upstreamList.clear();

            for (String directParentJobId : directParentJobIds) {
                List<ScheduleJobRelyCheck> checks = map.get(directParentJobId);
                if (CollectionUtils.isNotEmpty(checks)) {
                    upstreamList.addAll(checks);
                }
            }
        }

        Map<String, List<ScheduleJobRelyCheck>> directJobMap = parentJobChecks.stream().collect(Collectors.groupingBy(ScheduleJobRelyCheck::getDirectParentJobId));
        // 下游节点数
        List<ScheduleJobRelyCheck> downstreamList = directJobMap.get(jobId);
        while (CollectionUtils.isNotEmpty(downstreamList)) {
            List<String> jobIds = downstreamList.stream().map(ScheduleJobRelyCheck::getJobId).collect(Collectors.toList());
            downstreamNum += jobIds.size();

            downstreamList.clear();

            for (String upJobId : jobIds) {
                List<ScheduleJobRelyCheck> checks = directJobMap.get(upJobId);
                if (CollectionUtils.isNotEmpty(checks)) {
                    downstreamList.addAll(checks);
                }
            }

        }


        BlockJobNumVO blockJobNumVO = new BlockJobNumVO();
        blockJobNumVO.setUpstreamNum(upstreamNum);
        blockJobNumVO.setDownstreamNum(downstreamNum);

        return blockJobNumVO;
    }

    public void deleteCurrentCheck(List<String> jobIds) {
        try {
            scheduleJobRelyCheckService.deleteByJobIds(jobIds);
            scheduleJobRelyCheckService.deleteByParentJobIds(jobIds);
        } catch (Exception e) {
            LOGGER.error("",e);
        }
    }

    public Long createFillData(ScheduleFillJobImmediatelyVO scheduleFillJobImmediatelyVO,String fillName,String yesterday) {
        // 生成补数据信息
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();
        ScheduleFillDataInfoVO vo = new ScheduleFillDataInfoVO();
        vo.setFillDataType(FillDataTypeEnum.BATCH.getType());
        vo.setTaskCustomParamVOList(scheduleFillJobImmediatelyVO.getTaskCustomParamVOList());
        scheduleFillDataJob.setFillDataInfo(JSON.toJSONString(vo));
        scheduleFillDataJob.setFillGeneratStatus(FillGeneratStatusEnum.FILL_FINISH.getType());
        scheduleFillDataJob.setFromDay(yesterday);
        scheduleFillDataJob.setToDay(yesterday);
        scheduleFillDataJob.setJobName(fillName);
        scheduleFillDataJob.setMaxParallelNum(0);
        scheduleFillDataJob.setNumberParallelNum(0);
        scheduleFillDataJob.setAppType(scheduleFillJobImmediatelyVO.getAppType());
        scheduleFillDataJob.setDtuicTenantId(scheduleFillJobImmediatelyVO.getDtuicTenantId());
        scheduleFillDataJob.setTenantId(scheduleFillJobImmediatelyVO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobImmediatelyVO.getUserId());
        scheduleFillDataJob.setProjectId(scheduleFillJobImmediatelyVO.getProjectId());
        scheduleFillDataJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setFillDataType(FillDataTypeEnum.BATCH.getType());
        scheduleFillDataJobDao.insert(scheduleFillDataJob);

        return scheduleFillDataJob.getId();
    }

    public String immediatelyFillJob(ScheduleFillJobImmediatelyVO scheduleFillJobImmediatelyVO) throws Exception {
        Integer appType = scheduleFillJobImmediatelyVO.getAppType();
        Long taskId = scheduleFillJobImmediatelyVO.getTaskId();

        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId, appType);

        if (taskShade == null) {
            throw new RdosDefineException(String.format("task:%s not fount,place save task:%s", taskId, taskId));
        }

        String localAddress = environmentContext.getLocalAddress();
        if (StringUtils.isNotBlank(localAddress)) {
            localAddress = localAddress.replace(".","").replace(":","");
        }
        String fillName = ("P_" + taskShade.getName() + "_" + System.currentTimeMillis())+"_"+localAddress+atomicLong.incrementAndGet();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        Long fillId = createFillData(scheduleFillJobImmediatelyVO,fillName,yesterday);
        Set<String> all = new HashSet<>();
        all.add(taskId + AbstractFillDataTask.FillDataConst.KEY_DELIMITER + appType);

        createEnhanceFillJob(all, all, Lists.newArrayList(), fillId, fillName, null, null, yesterday, yesterday,
                scheduleFillJobImmediatelyVO.getProjectId(), scheduleFillJobImmediatelyVO.getTenantId(),
                scheduleFillJobImmediatelyVO.getDtuicTenantId(), scheduleFillJobImmediatelyVO.getUserId(),
                EScheduleType.FILL_DATA, TaskRunOrderEnum.DEFAULT.getType(), CloseRetryEnum.OPEN.getType());

        List<String> jobIds = scheduleJobDao.listByFillId(fillId);
        return jobIds.get(0);
    }

    public Integer updateToDelete(List<String> taskIds, Integer appType) {
        return scheduleJobDao.updateToDeleted(taskIds,appType);
    }

    public List<String> queryDeletedJobs(Integer appType) {
        return scheduleJobDao.selectDeletedJobIds(appType);
    }

    public List<String> listJobIdsByAppType(Integer appType) {
        return scheduleJobDao.listJobIdsByAppType(appType);
    }

    public void deleteOperator(Long fillId) {
        List<String> jobIds = scheduleJobDao.listByFillId(fillId);

        if (CollectionUtils.isNotEmpty(jobIds)) {
            scheduleJobOperatorRecordDao.deleteByJobIds(jobIds);
        }
    }
    static class ExpandJob {
        private String msg;

        private Integer status;

        private Date execStartTime;

        private Date execEndTime;

        public ExpandJob(String msg, Integer status, Date execStartTime, Date execEndTime) {
            this.msg = msg;
            this.status = status;
            this.execStartTime = execStartTime;
            this.execEndTime = execEndTime;
        }
    }


    public Integer updateLogAndFinish(String jobId, String msg,Integer status) {
        return updateLogAndThen.andThen((updateSize) -> {
            scheduleJobDao.jobFinish(jobId, status);
            return 1;
        }).apply(jobId, new ExpandJob(msg, status, null, null));
    }

    public Integer updateStatusByJobId(String jobId, Integer status, Integer versionId) {
        scheduleJobExpandDao.updateJobStatus(jobId, status);
        return scheduleJobDao.updateStatusByJobId(jobId, status, versionId, null, null);
    }

    public Long startJob(ScheduleJob scheduleJob) throws Exception {
        sendTaskStartTrigger(scheduleJob);
        return scheduleJob.getId();
    }


    public Integer updateStatusWithExecTime(ScheduleJob updateJob) {
        if(null == updateJob || null == updateJob.getJobId() || null == updateJob.getAppType()){
            return 0;
        }
        ScheduleJob job = scheduleJobDao.getByJobId(updateJob.getJobId(), Deleted.NORMAL.getStatus());
        if (null != job.getExecStartTime() && null != updateJob.getExecEndTime()){
            updateJob.setExecTime((updateJob.getExecEndTime().getTime()-job.getExecStartTime().getTime())/1000);
        }
        return scheduleJobDao.updateStatusWithExecTime(updateJob);
    }

    public void testTrigger( String jobId) {
        ScheduleJob rdosJobByJobId = scheduleJobDao.getRdosJobByJobId(jobId);
        if (null != rdosJobByJobId) {
            try {
                this.sendTaskStartTrigger(rdosJobByJobId);
            } catch (Exception e) {
                LOGGER.error(" job  {} run fail with info is null",rdosJobByJobId.getJobId(),e);
            }
        }
    }

    /**
     * 触发 engine 执行指定task
     */
    public void sendTaskStartTrigger(ScheduleJob scheduleJob) throws Exception {

        if (null == scheduleJob.getTaskId() || null == scheduleJob.getAppType()) {
            throw new RdosDefineException("任务id和appType不能为空");
        }
        ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + scheduleJob.getTaskId());
        }
        if (UnnecessaryPreprocessJobService.preprocess(scheduleJob,batchTask)) {
            return;
        }
        String extInfoByTaskId = scheduleTaskShadeDao.getExtInfoByTaskId(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (StringUtils.isNotBlank(extInfoByTaskId)) {
            JSONObject extObject = JSONObject.parseObject(extInfoByTaskId);
            if (null != extObject ) {
                JSONObject actualTaskExtraInfo = TaskUtils.getActualTaskExtraInfo(extObject);
                if (Objects.nonNull(actualTaskExtraInfo)) {
                    ParamActionExt paramActionExt = actionService.paramActionExt(batchTask, scheduleJob, actualTaskExtraInfo);
                    if (paramActionExt != null) {
                        this.updateStatusByJobId(scheduleJob.getJobId(), RdosTaskStatus.SUBMITTING.getStatus(),batchTask.getVersionId());
                        actionService.start(paramActionExt);
                        return;
                    }
                }
            }
        }
        //额外信息为空 标记任务为失败
        this.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), "任务运行信息为空");
        scheduleJobGanttTimeService.ganttChartTime(scheduleJob.getJobId(), JobGanttChartEnum.JOB_SUBMIT_TIME);
        LOGGER.error(" job  {} run fail with info is null",scheduleJob.getJobId());
    }


    public String stopJob( long jobId, Integer appType,Long operateId) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDao.getOne(jobId);
        // 杀死工作流任务，已经强规则任务
        List<ScheduleJob> jobs = Lists.newArrayList(scheduleJob);
        getDependentJob(jobs);
        String result = "";
        for (ScheduleJob job : jobs) {
             stopJobByScheduleJob(appType, job,operateId);
        }
        return result;
    }

    private String stopJobByScheduleJob(  Integer appType, ScheduleJob scheduleJob,Long operateId) throws Exception {

        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        ScheduleTaskShade task = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Integer status = scheduleJob.getStatus();
        if (!checkJobCanStop(status)) {
            throw new RdosDefineException(ErrorCode.JOB_CAN_NOT_STOP);
        }

        jobStopDealer.addStopJobs(Lists.newArrayList(scheduleJob),operateId);
        return "";
    }

    public String stopJobByJobId( String jobId, Integer appType,Long operateId) throws Exception{
        if(StringUtils.isBlank(jobId)){
            return "";
        }
        LOGGER.info("stop job by jobId {}",jobId);
        ScheduleJob batchJob = scheduleJobDao.getByJobId(jobId,Deleted.NORMAL.getStatus());
        return stopJobByScheduleJob(appType, batchJob,operateId);
    }

    public void stopFillDataJobs( String fillDataJobName,  Long projectId,  Long dtuicTenantId,  Integer appType,Long operateId) throws Exception {
        //还未发送到engine部分---直接停止
        if (StringUtils.isBlank(fillDataJobName)) {
            return;
        }
        String likeName = fillDataJobName + "-%";
        //发送停止消息到engine
        scheduleJobDao.stopUnsubmitJob(likeName, null, null, RdosTaskStatus.CANCELED.getStatus());

        //查询出所有需要停止的任务
        Long startId = 0L;
        List<ScheduleJob> needStopIdList = scheduleJobDao.listNeedStopFillDataJob(startId,likeName, RdosTaskStatus.getCanStopStatus(),
                null, null,environmentContext.getStopFillPageSize());

        while (CollectionUtils.isNotEmpty(needStopIdList)) {
            //发送停止任务消息到engine
            //this.stopSubmittedJob(needStopIdList, dtuicTenantId, appType);
            jobStopDealer.addStopJobs(needStopIdList,operateId);
            Optional<ScheduleJob> max = needStopIdList.stream().max(Comparator.comparing(ScheduleJob::getId));
            if (max.isPresent()) {
                startId = max.get().getId();
            } else {
                LOGGER.warn("fill {} stop error",fillDataJobName);
                break;
            }

            needStopIdList = scheduleJobDao.listNeedStopFillDataJob(startId,likeName,
                    RdosTaskStatus.getCanStopStatus(), null, null,environmentContext.getStopFillPageSize());
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public int batchStopJobs(List<Long> jobIdList,Long operateId) {
        if (CollectionUtils.isEmpty(jobIdList)) {
            return 0;
        }
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.listByJobIds(jobIdList));

        // 查询规则任务
        getDependentJob(jobs);
        return jobStopDealer.addStopJobs(jobs,operateId);
    }

    private void getDependentJob(List<ScheduleJob> jobs) {
        List<ScheduleJob> all = Lists.newArrayList();
        for (ScheduleJob job : jobs) {
            // 查询所有规则任务
            List<ScheduleJob> taskRuleSonJob = this.getTaskRuleSonJob(job);
            if (CollectionUtils.isNotEmpty(taskRuleSonJob)) {
                all.addAll(taskRuleSonJob);
            }
        }
        // 去重
        this.distinctAddJobs(jobs, all);

        listByJobIdFillFlowSubJobs(jobs);
    }

    /**
     * 将 subs 加到 jobs 中，注意保证唯一性
     * @param jobs
     * @param subs
     */
    private void distinctAddJobs(List<ScheduleJob> jobs, List<ScheduleJob> subs) {
        if (CollectionUtils.isEmpty(subs)) {
            return;
        }
        Set<String> distinctJobIds = new HashSet<>(jobs.size());
        for (ScheduleJob job : jobs) {
            distinctJobIds.add(job.getJobId());
        }
        for (ScheduleJob scheduleJob : subs) {
            // 去重
            if (!distinctJobIds.add(scheduleJob.getJobId())) {
                continue;
            }
            jobs.add(scheduleJob);
        }
    }

    /**
     * jobSize 在负载均衡时 区分 scheduleType（正常调度 和 补数据）
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertJobList(Collection<ScheduleBatchJob> batchJobCollection, Integer scheduleType) {
        if (CollectionUtils.isEmpty(batchJobCollection)) {
            return;
        }

        Iterator<ScheduleBatchJob> batchJobIterator = batchJobCollection.iterator();

        //count%20 为一批
        //1: 批量插入BatchJob
        //2: 批量插入BatchJobJobList
        int count = 0;
        int jobBatchSize = environmentContext.getBatchJobInsertSize();
        int jobJobBatchSize = environmentContext.getBatchJobJobInsertSize();
        List<ScheduleJob> jobWaitForSave = Lists.newArrayList();
        List<ScheduleJobJob> jobJobWaitForSave = Lists.newArrayList();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(batchJobCollection.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            final int finalBatchNodeSize = nodeSize;
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                nodeSize--;
                count++;

                ScheduleBatchJob scheduleBatchJob = batchJobIterator.next();

                ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
                scheduleJob.setNodeAddress(nodeAddress);

                jobWaitForSave.add(scheduleJob);
                jobJobWaitForSave.addAll(scheduleBatchJob.getBatchJobJobList());

                LOGGER.debug("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}", count, batchJobCollection.size(), finalBatchNodeSize);
                if (count % jobBatchSize == 0 || count == (batchJobCollection.size() - 1) || jobJobWaitForSave.size() > jobJobBatchSize) {
                    persistJobs(jobWaitForSave, jobJobWaitForSave,jobJobBatchSize);
                    LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{} jobJobSize:{}", count, batchJobCollection.size(), finalBatchNodeSize, jobJobWaitForSave.size());
                }
            }
            LOGGER.info("insertJobList count:{} batchJobs:{} finalBatchNodeSize:{}",count, batchJobCollection.size(), finalBatchNodeSize);
            //结束前persist一次，flush所有jobs
            persistJobs(jobWaitForSave, jobJobWaitForSave,jobJobBatchSize);

        }
    }

    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = jobPartitioner.computeBatchJobSize(scheduleType, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkService.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }

    private void persistJobs(List<ScheduleJob> jobWaitForSave, List<ScheduleJobJob> jobJobWaitForSave,Integer jobJobBatchSize) {
        try {
            RetryUtil.executeWithRetry(() -> {
                if (jobWaitForSave.size() > 0) {
                    try {
                        scheduleJobDao.batchInsert(jobWaitForSave);
                    } catch (DuplicateKeyException e) {
                        LOGGER.error("!!!!! persistJobs job duplicate key error !!!! job {}", jobWaitForSave, e);
                        for (ScheduleJob scheduleJob : jobWaitForSave) {
                            scheduleJob.setJobId(actionService.generateUniqueSign());
                        }
                        scheduleJobDao.batchInsert(jobWaitForSave);
                    }
                    List<String> jobIds = jobWaitForSave.stream().map(ScheduleJob::getJobId).collect(Collectors.toList());
                    insertExpand(jobIds);
                    jobWaitForSave.clear();
                }
                if (jobJobWaitForSave.size() > 0) {
                    if (jobJobWaitForSave.size() > jobJobBatchSize) {
                        List<List<ScheduleJobJob>> partition = Lists.partition(jobJobWaitForSave, jobJobBatchSize);
                        for (int i = 0; i < partition.size(); i++) {
                            batchJobJobService.batchInsert(partition.get(i));
                            jobJobWaitForSave.removeAll(partition.get(i));
                        }
                    } else {
                        batchJobJobService.batchInsert(jobJobWaitForSave);
                    }
                    jobJobWaitForSave.clear();
                }
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("!!!!! persistJobs job error !!!! job {} jobjob {}", jobWaitForSave, jobJobWaitForSave, e);
            throw new RdosDefineException(e);
        } finally {
            if (jobWaitForSave.size() > 0) {
                jobWaitForSave.clear();
            }
            if (jobJobWaitForSave.size() > 0) {
                jobJobWaitForSave.clear();
            }
        }
    }

    private Integer insertExpand(List<String> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return 0;
        }
        List<ScheduleJobExpand> expands = Lists.newArrayList();
        for (String jobId : jobIds) {
            ScheduleJobExpand scheduleJobExpand = new ScheduleJobExpand();
            scheduleJobExpand.setJobId(jobId);
            scheduleJobExpand.setRetryTaskParams("");
            scheduleJobExpand.setJobExtraInfo("");
            scheduleJobExpand.setEngineLog("");
            scheduleJobExpand.setLogInfo("");
            expands.add(scheduleJobExpand);
        }

        return scheduleJobExpandDao.batchInsert(expands);
    }


    /**
     * 补数据的时候，选中什么业务日期，参数替换结果是业务日期+1天
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public String fillTaskData( String taskJsonStr,  String fillName,
                                Long fromDay,  Long toDay,
                                String beginTime,  String endTime,
                                Long projectId,  Long userId,
                                Long tenantId,
                                Boolean isRoot,  Integer appType,  Long dtuicTenantId,Boolean ignoreCycTime) throws Exception {

        if(StringUtils.isEmpty(taskJsonStr)){
            throw new RdosDefineException("(taskJsonStr 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }
        ArrayNode jsonNode = null;
        try {
            jsonNode = objMapper.readValue(taskJsonStr, ArrayNode.class);
        } catch (IOException e) {
            throw new RdosDefineException("takJsonStr格式错误");
        }
        //计算从fromDay--toDay之间的天数
        DateTime fromDateTime = new DateTime(fromDay * 1000L);
        DateTime toDateTime = new DateTime(toDay * 1000L);

        String fromDayStr = fromDateTime.toString(dayFormatter);
        String toDayStr = toDateTime.toString(dayFormatter);

        DateTime currDateTime = DateTime.now();
        currDateTime = currDateTime.withTime(0, 0, 0, 0);

        checkFillDataParams( fillName, projectId, toDateTime, currDateTime);

        Map<String, ScheduleBatchJob> addBatchMap = Maps.newLinkedHashMap();
        //存储fill_job name
        String currDayStr = currDateTime.toString(dayFormatter);
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobService.saveData(fillName, tenantId, projectId, currDayStr, fromDayStr, toDayStr, userId, appType, dtuicTenantId);
        AtomicInteger count = new AtomicInteger();
        for (; !toDateTime.isBefore(fromDateTime); ) {
            try {
                DateTime cycTime = fromDateTime.plusDays(1);
                String triggerDay = cycTime.toString(DAY_PATTERN);
                Map<String, ScheduleBatchJob> result;
                if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, beginTime, endTime, projectId, tenantId, isRoot, appType, scheduleFillDataJob.getId(),dtuicTenantId,count);
                } else {
                    result = jobGraphBuilder.buildFillDataJobGraph(jsonNode, fillName, false, triggerDay, userId, projectId, tenantId, isRoot, appType, scheduleFillDataJob.getId(),dtuicTenantId,count);
                }
                if (MapUtils.isEmpty(result)) {
                    continue;
                }

                if (BooleanUtils.isTrue(ignoreCycTime)) {
                    for (ScheduleBatchJob value : result.values()) {
                        ScheduleJob scheduleJob = value.getScheduleJob();
                        scheduleJob.setCycTime(DateTime.now().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));
                    }
                }
                insertJobList(result.values(), EScheduleType.FILL_DATA.getType());
                List<ScheduleJobOperatorRecord> operatorJobIds = result.values()
                        .stream()
                        .map(scheduleBatchJob -> {
                            ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                            record.setJobId(scheduleBatchJob.getJobId());
                            record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                            record.setOperatorType(OperatorType.FILL_DATA.getType());
                            record.setNodeAddress(scheduleBatchJob.getScheduleJob().getNodeAddress());
                            return record;
                        })
                        .collect(Collectors.toList());

                scheduleJobOperatorRecordDao.insertBatch(operatorJobIds);
                addBatchMap.putAll(result);

            } catch (RdosDefineException rde) {
                throw rde;
            } catch (Exception e) {
                LOGGER.error("", e);
                throw new RdosDefineException("build fill job error:" + e.getMessage(), ErrorCode.SERVER_EXCEPTION);
            } finally {
                fromDateTime = fromDateTime.plusDays(1);
            }
        }

        if (addBatchMap.size() == 0) {
            throw new RdosDefineException("Please check the specific date range selected", ErrorCode.NO_FILLDATA_TASK_IS_GENERATE);
        }

        return fillName;
    }


    /**
     * @author newman
     * @Description 校验补数据任务参数
     * @Date 2020-12-14 17:47
     * @param fillName:
     * @param projectId:
     * @param toDateTime:
     * @param currDateTime:
     * @return: void
     **/
    public void checkFillDataParams( String fillName, Long projectId, DateTime toDateTime, DateTime currDateTime) {

        if (fillName == null) {
            throw new RdosDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new RdosDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        if (!toDateTime.isBefore(currDateTime)) {
            throw new RdosDefineException("(业务日期开始时间不能晚于结束时间)", ErrorCode.INVALID_PARAMETERS);
        }

        //判断补数据的名字每个project必须是唯一的
        long fillId = scheduleFillDataJobService.getByName(fillName, projectId);
        if (fillId > 0) {
            throw new RdosDefineException("操作名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }


    public void checkEnhanceFillDataParams( String fillName, Long projectId, DateTime toDateTime, DateTime currDateTime) {

        if (fillName == null) {
            throw new RdosDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new RdosDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        //判断补数据的名字每个project必须是唯一的
        long fillId = scheduleFillDataJobService.getByName(fillName, projectId);
        if (fillId > 0) {
            throw new RdosDefineException("操作名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    /**
     * 先查询出所有的补数据名称
     * <p>
     * jobName dutyUserId userId 需要关联task表（防止sql慢） 其他情况不需要
     *
     * @param jobName
     * @param runDay
     * @param bizStartDay
     * @param bizEndDay
     * @param dutyUserId
     * @param projectId
     * @param appType
     * @param currentPage
     * @param pageSize
     * @param tenantId
     * @return
     */
    public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay,
                                                Long bizStartDay, Long bizEndDay, Long dutyUserId,
                                                Long projectId, Integer appType,
                                                Integer currentPage, Integer pageSize, Long tenantId,Long dtuicTenantId,Integer fillDataType) {
        final List<ScheduleTaskShade> taskList;
        ScheduleJobDTO batchJobDTO = new ScheduleJobDTO();
        if (!Strings.isNullOrEmpty(jobName)) {
            taskList = batchTaskShadeService.getTasksByName(projectId, jobName, appType);
            if (CollectionUtils.isEmpty(taskList)) {
                return PageResult.EMPTY_PAGE_RESULT;
            } else {
                batchJobDTO.setTaskIds(taskList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            }
        }
        //设置分页查询参数
        PageQuery<ScheduleJobDTO> pageQuery = getScheduleJobDTOPageQuery(runDay, bizStartDay, bizEndDay, dutyUserId, projectId, appType, currentPage, pageSize,
                tenantId, dtuicTenantId, batchJobDTO,fillDataType);
        pageQuery.getModel().setTenantId(tenantId);
        Integer totalCount = scheduleFillDataJobDao.countListFillJobByPageQuery(pageQuery);

        if (totalCount <= 0) {
            return new PageResult<>(null, 0, pageQuery);
        }

        List<ScheduleFillDataJob> fillJobList = scheduleFillDataJobDao.listFillJobByPageQuery(pageQuery);

        if(CollectionUtils.isEmpty(fillJobList)){
            return new PageResult<>(null, 0, pageQuery);
        }

        //内存中按照时间排序
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            fillJobList = fillJobList.stream().sorted((j1, j2) -> j2.getGmtCreate().compareTo(j1.getGmtCreate())).collect(Collectors.toList());
        }
        List<Map<String, Long>> statistics = new ArrayList<>();
        //查询补数据任务每个状态对应的个数
        if (CollectionUtils.isNotEmpty(fillJobList)) {
            List<Long> fillIds = fillJobList.stream().map(ScheduleFillDataJob::getId).collect(Collectors.toList());
            int type = FillDataTypeEnum.MANUAL.getType().equals(fillDataType) ? EScheduleType.MANUAL.getType() : EScheduleType.FILL_DATA.getType();
            statistics = scheduleJobDao.countByFillDataAllStatus(fillIds, projectId, tenantId, null, type);
        }

        List<ScheduleFillDataJobPreViewVO> resultContent = Lists.newArrayList();
        for (ScheduleFillDataJob fillJob : fillJobList) {
            ScheduleFillDataJobPreViewVO preViewVO = new ScheduleFillDataJobPreViewVO();
            preViewVO.setId(fillJob.getId());
            preViewVO.setFillDataJobName(fillJob.getJobName());
            preViewVO.setCreateTime(timeFormatter.print(fillJob.getGmtCreate().getTime()));
            preViewVO.setDutyUserId(fillJob.getCreateUserId());
            preViewVO.setFromDay(fillJob.getFromDay());
            preViewVO.setToDay(fillJob.getToDay());
            preViewVO.setProjectId(fillJob.getProjectId());
            //获取补数据执行进度
            if (!FillGeneratStatusEnum.FILL_FAIL.getType().equals(fillJob.getFillGeneratStatus())
                    && !FillGeneratStatusEnum.FILL_FAIL_LIMIT.getType().equals(fillJob.getFillGeneratStatus())) {
                this.setFillDataJobProgress(statistics, preViewVO);
            } else {
                preViewVO.setFinishedJobSum(0L);
                preViewVO.setAllJobSum(0L);
                preViewVO.setDoneJobSum(0L);
            }
            resultContent.add(preViewVO);
        }
        userService.fillFillDataJobUserName(resultContent);
        return new PageResult<>(resultContent, totalCount, pageQuery);
    }


    /**
     * @author newman
     * @Description 设置补数据的分页查询参数
     * @Date 2020-12-21 16:12
     * @param runDay:
     * @param bizStartDay:
     * @param bizEndDay:
     * @param dutyUserId:
     * @param projectId:
     * @param appType:
     * @param currentPage:
     * @param pageSize:
     * @param tenantId:
     * @param batchJobDTO:
     * @return: com.dtstack.engine.api.pager.PageQuery<com.dtstack.engine.api.dto.ScheduleJobDTO>
     **/
    private PageQuery<ScheduleJobDTO> getScheduleJobDTOPageQuery(Long runDay, Long bizStartDay, Long bizEndDay, Long dutyUserId, Long projectId, Integer appType, Integer currentPage, Integer pageSize,
                                                                 Long tenantId,Long dtuicTenantId, ScheduleJobDTO batchJobDTO,Integer fillDataType) {
        if (runDay != null) {
            batchJobDTO.setStartGmtCreate(new Timestamp(runDay * 1000L));
        }
        this.setBizDay(batchJobDTO, bizStartDay, bizEndDay, tenantId, projectId);
        if (dutyUserId != null) {
            batchJobDTO.setCreateUserId(dutyUserId);
        }
        batchJobDTO.setProjectId(projectId);
        batchJobDTO.setNeedQuerySonNode(true);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setOwnerUserId(dutyUserId);
        batchJobDTO.setDtuicTenantId(dtuicTenantId);
        batchJobDTO.setFillDataType(fillDataType);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_create", Sort.DESC.name());
        pageQuery.setModel(batchJobDTO);
        return pageQuery;
    }

    /**
     * 补数据的执行进度
     *
     * @param statistics
     * @param preViewVO
     */
    private void setFillDataJobProgress(List<Map<String, Long>> statistics, ScheduleFillDataJobPreViewVO
            preViewVO) {
        Map<Long, Long> statisticsMap = new HashMap<>();
        for (Map<String, Long> statistic : statistics) {
            Object fillId = statistic.get("fillId");
            long id = preViewVO.getId();
            if (((Integer) fillId).longValue() == id) {
                statisticsMap.put(statistic.get("status"), statistic.get("count"));
            }
        }

        Map<Integer, Long> resultMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : RdosTaskStatus.getCollectionStatus().entrySet()) {
            int showStatus = entry.getKey();
            long sum = 0;
            for (Integer value : entry.getValue()) {
                Long statusSum = statisticsMap.get(value);
                sum += statusSum == null ? 0L : statusSum;
            }
            resultMap.put(showStatus, sum);
        }

        Long unSubmit = resultMap.get(RdosTaskStatus.UNSUBMIT.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.UNSUBMIT.getStatus());
        Long running = resultMap.get(RdosTaskStatus.RUNNING.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.RUNNING.getStatus());
        running += resultMap.get(RdosTaskStatus.NOTFOUND.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.NOTFOUND.getStatus());
        Long finished = resultMap.get(RdosTaskStatus.FINISHED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FINISHED.getStatus());
        Long failed = resultMap.get(RdosTaskStatus.FAILED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FAILED.getStatus());
        Long waitEngine = resultMap.get(RdosTaskStatus.WAITENGINE.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.WAITENGINE.getStatus());
        Long submitting = resultMap.get(RdosTaskStatus.SUBMITTING.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.SUBMITTING.getStatus());
        Long canceled = resultMap.get(RdosTaskStatus.CANCELED.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.CANCELED.getStatus());
        Long frozen = resultMap.get(RdosTaskStatus.FROZEN.getStatus()) == null ? 0L : resultMap.get(RdosTaskStatus.FROZEN.getStatus());

        preViewVO.setFinishedJobSum(finished);
        preViewVO.setAllJobSum(unSubmit + running + finished + failed + waitEngine + submitting + canceled + frozen);
        preViewVO.setDoneJobSum(failed + canceled + frozen + finished);
    }

    /**
     * @param fillJobName
     * @return
     */
    @Deprecated
    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfoOld(QueryJobDTO vo,
                                                                             String fillJobName,
                                                                             Long dutyUserId) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(The supplementary data name cannot be empty)", ErrorCode.INVALID_PARAMETERS);
        }
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setJobNameRightLike(fillJobName + "-");

        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        if (dutyUserId != null && dutyUserId > 0) {
            batchJobDTO.setTaskCreateId(dutyUserId);
        }

        if (!Strings.isNullOrEmpty(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            for (String status : statuses) {
                List<Integer> statusList = RdosTaskStatus.getStatusFailedDetail().get(MathUtil.getIntegerVal(status));
                statues.addAll(statusList);
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "business_date", Sort.ASC.name());
        pageQuery.setModel(batchJobDTO);

        // 先查找符合条件的子节点
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
        batchJobDTO.setPageQuery(false);
        List<ScheduleJob> subScheduleJobs = scheduleJobDao.generalQuery(pageQuery);
        Set<String> matchFlowJobIds = subScheduleJobs.stream().map(ScheduleJob::getFlowJobId).collect(Collectors.toSet());
        List<String> subJobIds = subScheduleJobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toList());

        // 然后查找符合条件的其它任务
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setJobIds(matchFlowJobIds);

        //todo 优化查询次数
        batchJobDTO.setPageQuery(true);

        ScheduleFillDataJobDetailVO scheduleFillDataJobDetailVO = new ScheduleFillDataJobDetailVO();
        scheduleFillDataJobDetailVO.setFillDataJobName(fillJobName);

        batchJobDTO.setNeedQuerySonNode(CollectionUtils.isNotEmpty(batchJobDTO.getTaskTypes()) && batchJobDTO.getTaskTypes().contains(EScheduleJobType.WORK_FLOW.getVal()));
        int totalCount = scheduleJobDao.generalCount(batchJobDTO);
        if (totalCount > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobs);


            for (ScheduleJob scheduleJob : scheduleJobs) {
                if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(scheduleJob.getStatus())) {
                    scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
                }
                scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap,new HashMap<>()));
            }

            dealFlowWorkFillDataRecord(scheduleFillDataJobDetailVO.getRecordList(), subJobIds);
        } else if (subScheduleJobs.size() > 0) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Only_Workflow_SubNodes.getType());
            batchJobDTO.setPageQuery(true);
            batchJobDTO.setJobIds(null);
            batchJobDTO.setFlowJobId(null);
            //【2】 只查询工作流子节点
            totalCount = scheduleJobDao.generalCount(batchJobDTO);
            if (totalCount > 0) {
                subScheduleJobs = scheduleJobDao.generalQuery(pageQuery);

                Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subScheduleJobs);
                for (ScheduleJob scheduleJob : subScheduleJobs) {
                    if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(scheduleJob.getStatus())) {
                        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
                    }
                    scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap,new HashMap<>()));
                }
            }
        }

        return new PageResult<>(scheduleFillDataJobDetailVO, totalCount, pageQuery);
    }

    public PageResult<ScheduleFillDataJobDetailVO> getJobGetFillDataDetailInfo(String taskName, Long bizStartDay,
                                                                               Long bizEndDay, List<String> flowJobIdList,
                                                                               String fillJobName, Long dutyUserId,
                                                                               String searchType, Integer appType,
                                                                               Long projectId,Long dtuicTenantId,
                                                                               String execTimeSort, String execStartSort,
                                                                               String execEndSort, String cycSort,
                                                                               String businessDateSort, String retryNumSort,
                                                                               String taskType, String jobStatuses,
                                                                               Integer currentPage, Integer pageSize) throws Exception {
        QueryJobDTO vo = new QueryJobDTO();
        vo.setCurrentPage(currentPage);
        vo.setPageSize(pageSize);
        vo.setBizStartDay(bizStartDay);
        vo.setBizEndDay(bizEndDay);
        vo.setFillTaskName(fillJobName);
        vo.setSearchType(searchType);
        vo.setProjectId(projectId);
        vo.setDtuicTenantId(dtuicTenantId);
        vo.setTaskName(taskName);
        vo.setSplitFiledFlag(true);
        vo.setExecTimeSort(execTimeSort);
        vo.setExecStartSort(execStartSort);
        vo.setExecEndSort(execEndSort);
        vo.setCycSort(cycSort);
        vo.setBusinessDateSort(businessDateSort);
        vo.setRetryNumSort(retryNumSort);
        vo.setJobStatuses(jobStatuses);
        vo.setTaskType(taskType);
        return getScheduleFillDataJobDetailVOPageResult(flowJobIdList, fillJobName, dutyUserId, searchType, appType, vo);
    }

    public PageResult<ScheduleFillDataJobDetailVO> getFillDataDetailInfo( String queryJobDTO,
                                                                          List<String> flowJobIdList,
                                                                          String fillJobName,
                                                                          Long dutyUserId,  String searchType,
                                                                          Integer appType) throws Exception {
        if (Strings.isNullOrEmpty(fillJobName)) {
            throw new RdosDefineException("(The supplementary data name cannot be empty)", ErrorCode.INVALID_PARAMETERS);
        }

        QueryJobDTO vo = JSONObject.parseObject(queryJobDTO, QueryJobDTO.class);
        return getScheduleFillDataJobDetailVOPageResult(flowJobIdList, fillJobName, dutyUserId, searchType, appType, vo);
    }

    private PageResult<ScheduleFillDataJobDetailVO> getScheduleFillDataJobDetailVOPageResult(List<String> flowJobIdList, String fillJobName, Long dutyUserId, String searchType, Integer appType, QueryJobDTO vo) throws Exception {
        vo.setSplitFiledFlag(true);
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setAppType(appType);
        batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        batchJobDTO.setFillDataJobName(fillJobName);
        batchJobDTO.setNeedQuerySonNode(true);
        Long projectId = batchJobDTO.getProjectId();
        //跨租户、项目条件
        batchJobDTO.setProjectId(null);
        batchJobDTO.setTenantId(null);

        this.setBizDay(batchJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());

        if (dutyUserId != null && dutyUserId > 0) {
            batchJobDTO.setOwnerUserId(dutyUserId);
        }

        if (!Strings.isNullOrEmpty(vo.getTaskName())) {
            batchJobDTO.setTaskNameLike(vo.getTaskName());
        }

        if (!Strings.isNullOrEmpty(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            for (String status : statuses) {
                List<Integer> statusList = RdosTaskStatus.getStatusFailedDetail().get(MathUtil.getIntegerVal(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                }else{
                    statues.add(MathUtil.getIntegerVal(status));
                }
            }

            batchJobDTO.setJobStatuses(statues);
        }
        batchJobDTO.setTaskTypes(convertStringToList(vo.getTaskType()));

        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize());
        setPageQueryDefaultOrder(pageQuery, batchJobDTO);

        changeSearchType(batchJobDTO, searchType);
        pageQuery.setModel(batchJobDTO);


        batchJobDTO.setPageQuery(true);
        //根据有无条件来判断是否只查询父节点
        if (StringUtils.isNotEmpty(batchJobDTO.getTaskNameLike()) ||
                CollectionUtils.isNotEmpty(batchJobDTO.getJobStatuses()) ||
                CollectionUtils.isNotEmpty(batchJobDTO.getTaskTypes())) {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Full_Workflow_Job.getType());
        } else {
            batchJobDTO.setQueryWorkFlowModel(QueryWorkFlowModel.Eliminate_Workflow_SubNodes.getType());
        }

        ScheduleFillDataJobDetailVO scheduleFillDataJobDetailVO = new ScheduleFillDataJobDetailVO();
        scheduleFillDataJobDetailVO.setFillDataJobName(fillJobName);

        if (StringUtils.isNotBlank(vo.getTaskName()) || Objects.nonNull(vo.getOwnerId())) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(vo.getProjectId(), vo.getTaskName(),
                    appType, vo.getOwnerId(), vo.getProjectIds(), batchJobDTO.getSearchType(),null,null);
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                batchJobDTO.setTaskIds(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            } else {
                return new PageResult<>(scheduleFillDataJobDetailVO, 0, pageQuery);
            }
        }

        ScheduleFillDataJob byJobName = scheduleFillDataJobDao.getByJobName(batchJobDTO.getFillDataJobName(), projectId);

        if (byJobName != null) {
            batchJobDTO.setFillId(byJobName.getId());
        }

        Integer totalCount = scheduleJobDao.countByFillData(batchJobDTO);
        if (totalCount > 0) {

            List<ScheduleJob> scheduleJobListWithFillData = scheduleJobDao.queryFillData(pageQuery);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobListWithFillData);
            if (CollectionUtils.isNotEmpty(scheduleJobListWithFillData)) {
                List<Long> resourceIds = scheduleJobListWithFillData.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
                Map<Long, ResourceGroupDetail> groupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
                for (ScheduleJob job : scheduleJobListWithFillData) {
                    if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(job.getStatus())) {
                        job.setStatus(RdosTaskStatus.RUNNING.getStatus());
                    }
                    scheduleFillDataJobDetailVO.addRecord(transferBatchJob2FillDataRecord(job, flowJobIdList, taskShadeMap,groupDetailMap));
                }
                dealFlowWorkSubJobsInFillData(scheduleFillDataJobDetailVO.getRecordList(),groupDetailMap);
            }
        }

        return new PageResult<>(scheduleFillDataJobDetailVO, totalCount, pageQuery);
    }

    /**
     * 获取补数据实例工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    public ScheduleFillDataJobDetailVO.FillDataRecord getRelatedJobsForFillData( String jobId,  String query,
                                                                                 String fillJobName) throws Exception {

        QueryJobDTO vo = JSONObject.parseObject(query, QueryJobDTO.class);
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        Map<String, ScheduleEngineJob> engineJobMap = Maps.newHashMap();
        Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(Arrays.asList(scheduleJob));

        ScheduleFillDataJobDetailVO.FillDataRecord fillDataRecord = transferBatchJob2FillDataRecord(scheduleJob, null, taskShadeMap,new HashMap<>());

        vo.setSplitFiledFlag(true);
        //除去任务类型中的工作流类型的条件，用于展示下游节点
        if (StringUtils.isNotBlank(vo.getTaskType())) {
            vo.setTaskType(vo.getTaskType().replace(String.valueOf(EScheduleJobType.WORK_FLOW.getVal()), ""));
        }
        ScheduleJobDTO batchJobDTO = this.createQuery(vo);
        batchJobDTO.setFillDataJobName(fillJobName);

        if (scheduleJob != null) {
            if (EScheduleJobType.WORK_FLOW.getVal().intValue() == fillDataRecord.getTaskType().intValue()) {
                fillDataRecord.setRelatedRecords(getRelatedJobsForFillDataByQueryDTO(batchJobDTO, vo, jobId, engineJobMap, taskShadeMap));
            }
            return fillDataRecord;
        } else {
            throw new RdosDefineException("The instance object does not exist");
        }
    }

    /**
     * 获取指定工作流子节点（不包括工作流父节点）
     *
     * @param jobId        没有判断该job是否是工作流
     * @param taskShadeMap
     * @return
     * @throws Exception
     */
    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getOnlyRelatedJobsForFillData(String jobId, Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) {
        taskShadeMap = Optional.ofNullable(taskShadeMap).orElse(Maps.newHashMap());

        List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
        taskShadeMap.putAll(this.prepare(subJobs));
        List<ScheduleFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        List<Long> resourceIds = subJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
        Map<Long, ResourceGroupDetail> groupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
        for (ScheduleJob subJob : subJobs) {
            ScheduleFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap,groupDetailMap);
            fillDataRecord_subNodes.add(subNode);
        }
        return fillDataRecord_subNodes;
    }

    private List<ScheduleFillDataJobDetailVO.FillDataRecord> getRelatedJobsForFillDataByQueryDTO(ScheduleJobDTO queryDTO, QueryJobDTO vo, String jobId, Map<String, ScheduleEngineJob> engineJobMap,
                                                                                                 Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap) throws Exception {

        queryDTO.setPageQuery(false);
        queryDTO.setFlowJobId(jobId);
        queryDTO.setNeedQuerySonNode(true);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(vo.getCurrentPage(), vo.getPageSize(), "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(queryDTO);
        List<ScheduleJob> subJobs = scheduleJobDao.generalQuery(pageQuery);
        taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);

        List<ScheduleFillDataJobDetailVO.FillDataRecord> fillDataRecord_subNodes = new ArrayList<>();
        List<Long> resourceIds = subJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
        Map<Long, ResourceGroupDetail> groupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
        for (ScheduleJob subJob : subJobs) {
            ScheduleFillDataJobDetailVO.FillDataRecord subNode = transferBatchJob2FillDataRecord(subJob, null, taskShadeMap,groupDetailMap);
            fillDataRecord_subNodes.add(subNode);
        }
        return fillDataRecord_subNodes;
    }


    /**
     * 添加默认排序
     * 若查询条件中没有执行时间、执行开始时间、执行结束时间、计划时间、业务时间
     * 则默认添加业务时间ASC
     *
     * @param pageQuery
     * @param batchJobDTO
     */
    private void setPageQueryDefaultOrder(PageQuery pageQuery, ScheduleJobDTO batchJobDTO) {
        if (StringUtils.isBlank(batchJobDTO.getExecTimeSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecStartSort()) &&
                StringUtils.isBlank(batchJobDTO.getExecEndSort()) &&
                StringUtils.isBlank(batchJobDTO.getCycSort()) &&
                StringUtils.isBlank(batchJobDTO.getBusinessDateSort())) {
            pageQuery.setOrderBy(BUSINESS_DATE);
            pageQuery.setSort(Sort.ASC.name());
        }
    }


    private void dealFlowWorkSubJobsInFillData(List<ScheduleFillDataJobDetailVO.FillDataRecord> vos,Map<Long, ResourceGroupDetail> groupDetailMap) {
        Map<String, ScheduleFillDataJobDetailVO.FillDataRecord> record = Maps.newHashMap();
        Map<String, Integer> voIndex = Maps.newHashMap();
        vos.forEach(job -> voIndex.put(job.getJobId(), vos.indexOf(job)));
        List<ScheduleFillDataJobDetailVO.FillDataRecord> copy = Lists.newArrayList(vos);
        Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> iterator = vos.iterator();
        while (iterator.hasNext()) {
            ScheduleFillDataJobDetailVO.FillDataRecord jobVO = iterator.next();
            String flowJobId = jobVO.getFlowJobId();
            if (!"0".equals(flowJobId)) {
                if (record.containsKey(flowJobId)) {
                    ScheduleFillDataJobDetailVO.FillDataRecord flowVo = record.get(flowJobId);
                    flowVo.getRelatedRecords().add(jobVO);
                    iterator.remove();
                } else {
                    ScheduleFillDataJobDetailVO.FillDataRecord flowVO;
                    if (voIndex.containsKey(flowJobId)) {
                        flowVO = copy.get(voIndex.get(flowJobId));
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        iterator.remove();
                    } else {
                        ScheduleJob flow = scheduleJobDao.getByJobId(flowJobId, Deleted.NORMAL.getStatus());
                        if (flow == null) {
                            continue;
                        }
                        Map<Long, ScheduleTaskForFillDataDTO> shadeMap = this.prepare(Lists.newArrayList(flow));
                        flowVO = transferBatchJob2FillDataRecord(flow, null, shadeMap,groupDetailMap);
                        flowVO.setRelatedRecords(Lists.newArrayList(jobVO));
                        vos.set(vos.indexOf(jobVO), flowVO);
                    }
                    record.put(flowJobId, flowVO);
                }
            }
        }
    }

    private Map<Long, ScheduleTaskForFillDataDTO> prepareForFillDataDetailInfo(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return new HashMap<>();
        }
        Set<Long> taskIdSet = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toSet());
        Integer appType = scheduleJobs.get(0).getAppType();
        List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS = scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIdSet, null, appType);
        userService.fillScheduleTaskForFillDataDTO(scheduleTaskForFillDataDTOS);
        return scheduleTaskForFillDataDTOS.stream().collect(Collectors.toMap(ScheduleTaskForFillDataDTO::getTaskId, scheduleTaskForFillDataDTO -> scheduleTaskForFillDataDTO));

    }

    private void dealFlowWorkFillDataRecord
            (List<ScheduleFillDataJobDetailVO.FillDataRecord> records, List<String> subJobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<ScheduleJob> allSubJobs = new ArrayList<>();
            Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> it = records.iterator();
            while (it.hasNext()) {
                ScheduleFillDataJobDetailVO.FillDataRecord record = it.next();
                Integer type = record.getTaskType();
                if (EScheduleJobType.WORK_FLOW.getVal().intValue() == type) {
                    String jobId = record.getJobId();
                    List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
                    allSubJobs.addAll(subJobs);
                    if (CollectionUtils.isNotEmpty(subJobs)) {
                        Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(subJobs);
                        List<ScheduleFillDataJobDetailVO.FillDataRecord> subList = Lists.newArrayList();
                        for (ScheduleJob subJob : subJobs) {
                            if (subJobIds.contains(subJob.getJobId())) {
                                subList.add(transferBatchJob2FillDataRecord(subJob, null, taskShadeMap,null));
                            }
                        }
                        record.setRelatedRecords(subList);
                    }
                }
            }

            // 这里处理工作流里的任务
            Iterator<ScheduleFillDataJobDetailVO.FillDataRecord> itInternal = records.iterator();
            for (ScheduleJob subJob : allSubJobs) {
                while (itInternal.hasNext()) {
                    ScheduleFillDataJobDetailVO.FillDataRecord rec = itInternal.next();
                    if (subJob.getJobId().equalsIgnoreCase(rec.getJobId())) {
                        itInternal.remove();
                        break;
                    }
                }
            }
        }
    }


    /**
     * 转化batchjob任务为补数据界面所需格式
     *
     * @param scheduleJob
     * @param flowJobIdList 展开特定工作流子节点
     * @param taskShadeMap
     * @return
     * @throws Exception
     */
    private ScheduleFillDataJobDetailVO.FillDataRecord transferBatchJob2FillDataRecord(ScheduleJob scheduleJob, List<String> flowJobIdList,
                                                                                       Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap,
                                                                                       Map<Long, ResourceGroupDetail> groupDetailMap)  {
        String bizDayVO = scheduleJob.getBusinessDate();
        bizDayVO = bizDayVO.substring(0, 4) + "-" + bizDayVO.substring(4, 6) + "-" + bizDayVO.substring(6, 8);
        int status = scheduleJob.getStatus();

        String cycTimeVO = DateUtil.addTimeSplit(scheduleJob.getCycTime());
        String exeStartTimeVO = null;
        Timestamp exeStartTime = scheduleJob.getExecStartTime();
        if (exeStartTime != null) {
            exeStartTimeVO = timeFormatter.print(exeStartTime.getTime());
        }

        ScheduleTaskForFillDataDTO taskShade = taskShadeMap.get(scheduleJob.getTaskId());
        Integer taskType = 0;
        String taskName = "";
        if (taskShade != null) {
            taskType = taskShade.getTaskType();
            taskName = taskShade.getName();
        }
        ScheduleTaskVO batchTaskVO = new ScheduleTaskVO();
        String exeTime = DateUtil.getTimeDifference(scheduleJob.getExecTime() == null ? 0L : scheduleJob.getExecTime() * 1000);

        try {
            if (scheduleJob.getExecEndTime() == null) {
                exeTime = DateUtil.getTimeDifference(scheduleJob.getExecStartTime() == null ? 0L : (System.currentTimeMillis() - scheduleJob.getExecStartTime().getTime()));
            }
        } catch (Exception e) {
            exeTime =  DateUtil.getTimeDifference(0L);
        }

        Integer showStatus = RdosTaskStatus.getShowStatusWithoutStop(status);
        ScheduleFillDataJobDetailVO.FillDataRecord record = new ScheduleFillDataJobDetailVO.FillDataRecord(scheduleJob.getId(), bizDayVO, taskName,
                taskType, showStatus, cycTimeVO, exeStartTimeVO, exeTime, null);

        record.setJobId(scheduleJob.getJobId());
        record.setFlowJobId(scheduleJob.getFlowJobId());
        record.setIsRestart(scheduleJob.getIsRestart());
        record.setBusinessType(scheduleJob.getBusinessType());
        record.setResourceId(scheduleJob.getResourceId());
//        record.setTagIds(taskTags.get(scheduleJob.getTaskId()));
        ResourceGroupDetail groupDetail = groupDetailMap.computeIfAbsent(scheduleJob.getResourceId(), (resourceId) -> {
            Map<Long, ResourceGroupDetail> groupInfo = resourceGroupService.getGroupInfo(Lists.newArrayList(resourceId));
            return groupInfo.get(resourceId);
        });
        if (groupDetail != null) {
            record.setResourceGroupName(groupDetail.getResourceName());
        }

        // 判断taskType为2的，查出脏数据量，判断增加标识
        if (taskType == 2) {
            //fixme redmine[16670]暂时关闭脏数据读取，后续版本优化
            /*BatchServerLogVO batchServerLogVO = batchServerLogService.getLogsByJobId(batchJob.getJobId(),-1);
            if (batchServerLogVO.getSyncJobInfo() != null) {
                Float dirtyPercent = batchServerLogVO.getSyncJobInfo().getDirtyPercent();
                if (dirtyPercent != null && dirtyPercent > 0.0f) {
                    record.setIsDirty(1);
                }
            }*/
        }

        //展开特定工作流子节点
        if (EScheduleJobType.WORK_FLOW.getVal().equals(taskType) &&
                CollectionUtils.isNotEmpty(flowJobIdList) &&
                flowJobIdList.contains(scheduleJob.getJobId())) {
            record.setRelatedRecords(getOnlyRelatedJobsForFillData(scheduleJob.getJobId(), taskShadeMap));
        }

        if (null != taskShade) {
            batchTaskVO.setId(taskShade.getTaskId());
            batchTaskVO.setGmtModified(taskShade.getGmtModified());
            batchTaskVO.setName(taskShade.getName());
            batchTaskVO.setIsDeleted(taskShade.getIsDeleted());
            batchTaskVO.setProjectId(taskShade.getProjectId());
            batchTaskVO.setOwnerUserId(taskShade.getOwnerUserId());
            batchTaskVO.setCreateUserId(taskShade.getCreateUserId());
            batchTaskVO.setCreateUser(taskShade.getCreateUser());
            batchTaskVO.setOwnerUser(taskShade.getOwnerUser());
            if (null != taskShade.getOwnerUser()) {
                record.setDutyUserName(taskShade.getOwnerUser().getUserName());
            }
        }
        record.setBatchTask(batchTaskVO);
        record.setRetryNum(scheduleJob.getRetryNum());
        return record;
    }

    public List<RestartJobVO> getRootRestartJob(String jobKey, boolean isOnlyNextChild) {
        long curr = System.currentTimeMillis();
        ParsedJobKey parsedJobKey = parseJobKey(jobKey);
        if (parsedJobKey == null) {
            return Collections.emptyList();
        }
        ScheduleJob job = scheduleJobDao.getByJobKey(jobKey);
        if (job == null) {
            return Collections.emptyList();
        }

        //判断job 对应的task是否被删除
        ScheduleTaskShade task = batchTaskShadeService.getById(parsedJobKey.getTaskShadeId());
        Integer jobStatus = job.getStatus();
        jobStatus = jobStatus == null ? RdosTaskStatus.UNSUBMIT.getStatus() : jobStatus;

        String taskName = task == null ? null : task.getName();

        RestartJobVO restartJobVO = new RestartJobVO();
        restartJobVO.setJobId(job.getId());
        restartJobVO.setJobKey(job.getJobKey());
        restartJobVO.setJobStatus(jobStatus);
        restartJobVO.setCycTime(job.getCycTime());
        restartJobVO.setTaskType(job.getTaskType());
        restartJobVO.setTaskName(taskName);
        restartJobVO.setTaskId(job.getTaskId());

        Map<String, RestartJobVO> parentRestartJobs = new HashMap<>();
        parentRestartJobs.put(restartJobVO.getJobKey(), restartJobVO);
        Map<String, ParsedJobKey> parsedJobKeys = new HashMap<>();
        parsedJobKeys.put(parsedJobKey.getJobKey(), parsedJobKey);

        Integer maxLevel = environmentContext.getJobJobLevel();
        Integer maxSize = environmentContext.getJobJobSize();
        Set<String> seenJobKeys = new HashSet<>();
        getRestartChildJob(parentRestartJobs, seenJobKeys, parsedJobKeys, 1, isOnlyNextChild ? 2 : maxLevel, maxSize);

        LOGGER.info("getRootRestartJob level: " + (isOnlyNextChild ? 2 : maxLevel) +
                " result size: " + seenJobKeys.size() +
                " elapse time: " + (System.currentTimeMillis() - curr));
        return restartJobVO.getChilds();
    }


    /**
     * 获取重跑的数据节点信息
     *
     * @param parentRestartJobs, 本层所有节点。
     * @param parsedJobKeys, 所有已解析jobKeys。
     * @param level 第level层。
     * @param maxLevel 最大层数。
     * @return
     */
    private void getRestartChildJob(Map<String, RestartJobVO> parentRestartJobs,
                                    Set<String> seenJobKeys,
                                    Map<String, ParsedJobKey> parsedJobKeys,
                                    int level, int maxLevel, int maxSize) {
        if (parentRestartJobs.isEmpty() || level >= maxLevel || seenJobKeys.size() >= maxSize) {
            return;
        }

        List<ScheduleJobJob> scheduleJobJobs = batchJobJobService.getJobChild(parentRestartJobs.keySet());
        if (CollectionUtils.isEmpty(scheduleJobJobs)) {
            return;
        }

        scheduleJobJobs.forEach(jj ->
                parsedJobKeys.computeIfAbsent(jj.getJobKey(), this::parseJobKey));

        Set<String> childJobKeys = scheduleJobJobs.stream()
                .filter(jj -> {
                    if (seenJobKeys.contains(jj.getJobKey())) {
                        return false;
                    }
                    if (!parsedJobKeys.containsKey(jj.getJobKey())) {
                        return false;
                    }

                    ParsedJobKey parent = parsedJobKeys.get(jj.getParentJobKey());
                    ParsedJobKey child = parsedJobKeys.get(jj.getJobKey());
                    return child.getJobDay().equals(parent.getJobDay()) &&
                            !child.getTaskShadeId().equals(parent.getTaskShadeId());
                })
                .map(ScheduleJobJob::getJobKey)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(childJobKeys)) {
            LOGGER.warn("childJobKeys empty, but scheduleJobJobs size is greater than zero");
            return;
        }
        Set<Long> childTaskShadeIds = childJobKeys.stream()
                .map(parsedJobKeys::get)
                .map(ParsedJobKey::getTaskShadeId)
                .collect(Collectors.toSet());

        Map<Long, TaskName> childTasks = batchTaskShadeService.getNamesByIds(childTaskShadeIds);
        List<SimpleJob> childJobs = scheduleJobDao.listSimpleJobByJobKeys(childJobKeys);
        Map<String, RestartJobVO> childRestartJobs = new HashMap<>();
        for (SimpleJob childScheduleJob : childJobs) {

            //判断job 对应的task是否被删除
            TaskName task = childTasks.get(parsedJobKeys.get(childScheduleJob.getJobKey()).getTaskShadeId());
            Integer jobStatus = childScheduleJob.getStatus();
            jobStatus = jobStatus == null ? RdosTaskStatus.UNSUBMIT.getStatus() : jobStatus;

            String taskName = task == null ? null : task.getName();

            RestartJobVO restartJobVO = new RestartJobVO();
            restartJobVO.setJobId(childScheduleJob.getId());
            restartJobVO.setJobKey(childScheduleJob.getJobKey());
            restartJobVO.setJobStatus(jobStatus);
            restartJobVO.setCycTime(childScheduleJob.getCycTime());
            restartJobVO.setTaskType(childScheduleJob.getTaskType());
            restartJobVO.setTaskName(taskName);
            restartJobVO.setTaskId(childScheduleJob.getTaskId());
            childRestartJobs.put(childScheduleJob.getJobKey(), restartJobVO);
        }

        Map<String, Set<String>> parentToChilds = new HashMap<>();
        for (ScheduleJobJob scheduleJobJob : scheduleJobJobs) {
            String childJobKey = scheduleJobJob.getJobKey();
            if (!seenJobKeys.contains(childJobKey)) {
                String parentJobKey = scheduleJobJob.getParentJobKey();
                Set<String> childs = parentToChilds.computeIfAbsent(parentJobKey, k -> new HashSet<>());
                childs.add(childJobKey);
                seenJobKeys.add(childJobKey);
                if (seenJobKeys.size() >= maxSize) {
                    break;
                }
            }
        }

        for (String pk : parentToChilds.keySet()) {
            RestartJobVO parent = parentRestartJobs.get(pk);
            if (parent == null) {
                continue;
            }
            parent.setChilds(parentToChilds.get(pk).stream()
                    .map(childRestartJobs::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        level++;
        if (!childJobs.isEmpty() && level < maxLevel && seenJobKeys.size() < maxSize) {
            getRestartChildJob(childRestartJobs, seenJobKeys, parsedJobKeys, level, maxLevel, maxSize);
        }
    }

    public List<ScheduleJob> listByJobIds(List<String> jobIds) {
        return scheduleJobDao.listByJobIdList(jobIds, null);
    }

    public List<java.sql.Timestamp> findTopEndRunTimeByTaskIdsAndAppType(Long taskId, Integer appType, Integer num) {
        return scheduleJobDao.findTopEndRunTimeByTaskIdsAndAppType(taskId,appType,num);
    }

    public List<ScheduleJob> selectJobByTaskIdAndJobExecuteOrder(Long start, Long end, List<Long> taskIds, Integer appType,String cycTime) {
        return scheduleJobDao.selectJobByTaskIdAndJobExecuteOrder(start,end,taskIds,appType,cycTime);
    }

    public List<Integer> selectExecTimeByTaskIdAndAppType(Long taskId, Integer appType, Integer status, Integer maxBaselineJobNum) {
        if (taskId == null || appType == null) {
            return Lists.newArrayList();
        }

        return scheduleJobDao.selectExecTimeByTaskIdAndAppType(taskId,appType,status,maxBaselineJobNum);
    }

    public List<ScheduleJob> selectJobByTaskIdAndApptype(List<Long> taskIds,List<Integer> types, Integer appType, Long range) {
        if (CollectionUtils.isEmpty(taskIds) || appType == null) {
            return Lists.newArrayList();
        }

        return scheduleJobDao.selectJobByTaskIdAndApptype(taskIds,types,appType,range);
    }

    public void updateJobToRunning(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setStatus(RdosTaskStatus.RUNNING.getStatus());
        scheduleJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        scheduleJobDao.updateExecTimeAndStatusLogInfo(scheduleJob);
    }

    public ScheduleJob getNewestJob(long taskId, Integer appType, Integer type, Integer status) {
        return scheduleJobDao.getNewestTempJob(taskId, appType, type, status);
    }

    /**
     * 校验当天是否有可生成实例
     *
     * @param scheduleTaskShadeDTO 任务信息
     * @return 校验结果
     */
    public JobGraphBuildJudgeVO canBuildJobGraphToday(ScheduleTaskShadeDTO scheduleTaskShadeDTO) {
        JobGraphBuildJudgeVO errorVo = JobGraphBuildJudgeVO.createErrorVo("该任务当天剩余时间无可执行实例需要生成");
        try {
            Integer delayMinute = environmentContext.getJobBuildImmediatelyDelayMinute();
            CycTimeLimitDTO cycTimeLimitDTO = jobRichOperator.getCycTimeNowToNextDay(delayMinute);
            if (!cycTimeLimitDTO.isToday()) {
                return errorVo;
            }
            // 自定义调度日历，check多批次时间文本格式
            if (ObjectUtils.equals(ESchedulePeriodType.CALENDER.getVal(),scheduleTaskShadeDTO.getPeriodType())
                    && ObjectUtils.equals(CandlerBatchTypeEnum.MANY.getType(),ScheduleConfUtils.getCandlerBatchType(scheduleTaskShadeDTO.getScheduleConf()))){
                calenderService.checkIntervalTimesCondition(ScheduleConfUtils.getExpendTime(scheduleTaskShadeDTO.getScheduleConf()));
            }
            ScheduleCron scheduleCron = ScheduleFactory.parseFromJsonWithoutTask(
                    scheduleTaskShadeDTO.getScheduleConf(),
                    timeService,
                    scheduleTaskShadeDTO.getCalenderId(),
                    ScheduleConfUtils.getExpendTime(scheduleTaskShadeDTO.getScheduleConf()),
                    Optional.ofNullable(ScheduleConfUtils.getCandlerBatchType(scheduleTaskShadeDTO.getScheduleConf()))
                            .orElse(CandlerBatchTypeEnum.SINGLE.getType()));
            List<String> triggerTimeList = scheduleCron.getTriggerTime(cycTimeLimitDTO.getTriggerDay());
            if (CollectionUtils.isEmpty(triggerTimeList)) {
                return errorVo;
            }
            // 当天最后一次执行的触发时间如果 >= now + triggerTimeList 就表示可以生成实例
            DateTime lastTriggerTime = DateTime.parse(DateUtil.formatTimeToScheduleTrigger(triggerTimeList.get(triggerTimeList.size() - 1)), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
            DateTime cycStartTime = DateTime.parse(cycTimeLimitDTO.getStartTime(), DateTimeFormat.forPattern("yyyyMMddHHmmss"));
            if (lastTriggerTime.isAfter(cycStartTime)) {
                return JobGraphBuildJudgeVO.createSuccessVo();
            }
        } catch (Exception e) {
            LOGGER.error("judge job can build error", e);
            return JobGraphBuildJudgeVO.createErrorVo(ExceptionUtil.getErrorMessage(e));
        }
        return errorVo;
    }

    public FillDataTaskVO createFillDataTaskList(Long taskId, Integer appType, Integer level) {
        if (level == null || level > environmentContext.getJobJobLevel()) {
            level = environmentContext.getJobJobLevel();
        }
        Map<Integer, List<Long>> tasksInAppType = new HashMap<>();
        tasksInAppType.put(appType, Lists.newArrayList(taskId));
        List<FillDataTaskVO> fillDataTaskVO = getFillDataTaskVO(tasksInAppType, level, HashBasedTable.create(), new HashMap<>());
        if (CollectionUtils.isNotEmpty(fillDataTaskVO)) {
            return fillDataTaskVO.get(0);
        }
        return null;
    }

    private List<FillDataTaskVO> getFillDataTaskVO(Map<Integer, List<Long>> tasksInAppType, Integer level,
                                                   Table<Long, Integer, String> projectCache,
                                                   Map<Long, String> tenantCache) {
        if (level >= 0) {
            level = level - 1;
        } else {
            return null;
        }
        List<ScheduleTaskShade> scheduleTaskShades = tasksInAppType.keySet()
                .stream().map(m -> scheduleTaskShadeDao.listSimpleByTaskIds(tasksInAppType.get(m), Deleted.NORMAL.getStatus(), m))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        if (CollectionUtils.isEmpty(scheduleTaskShades)) {
            return null;
        }

        Set<String> taskKeys = scheduleTaskShades.stream()
                .map(t -> t.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + t.getAppType())
                .collect(Collectors.toSet());
        List<ScheduleTaskTaskShade> scheduleTaskTaskShades =
                scheduleTaskTaskShadeDao.listParentTaskKeys(Lists.newArrayList(taskKeys));
        //父任务的taskKey
        Map<String, List<FillDataTaskVO>> subFillDataTaskVOMapping = new HashMap<>();

        if (!CollectionUtils.isEmpty(scheduleTaskShades) && level >= 0) {
            Map<Integer, List<Long>> subTaskAppMapping = scheduleTaskTaskShades.stream()
                    .collect(Collectors.groupingBy(AppTenantEntity::getAppType,
                            Collectors.mapping(ScheduleTaskTaskShade::getTaskId, Collectors.toList())));
            // 一个子任务可能挂在多个父任务下
            Map<String, Set<String>> taskParentKeyMapping = scheduleTaskTaskShades.stream()
                    .collect(Collectors.groupingBy(t -> t.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + t.getAppType(),
                            Collectors.mapping(ScheduleTaskTaskShade::getParentTaskKey, Collectors.toSet())));

            List<FillDataTaskVO> subFillDataTaskVO = getFillDataTaskVO(subTaskAppMapping, level, projectCache, tenantCache);
            if (CollectionUtils.isNotEmpty(subFillDataTaskVO)) {
                for (FillDataTaskVO fillDataTaskVO : subFillDataTaskVO) {
                    Set<String> parentKeys = taskParentKeyMapping.get(fillDataTaskVO.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + fillDataTaskVO.getAppType());
                    if (CollectionUtils.isNotEmpty(parentKeys)) {
                        parentKeys.forEach(parentKey -> {
                            List<FillDataTaskVO> dataTaskVOS =
                                    subFillDataTaskVOMapping.computeIfAbsent(parentKey, k -> new ArrayList<>());
                            dataTaskVOS.add(fillDataTaskVO);
                        });
                    }
                }
            }
        }
        List<FillDataTaskVO> dataTaskVOS = new ArrayList<>(scheduleTaskShades.size());
        for (ScheduleTaskShade t : scheduleTaskShades) {
            FillDataTaskVO fillDataTaskVO = new FillDataTaskVO();
            fillDataTaskVO.setTaskId(t.getTaskId());
            fillDataTaskVO.setAppType(t.getAppType());
            fillDataTaskVO.setTaskType(t.getTaskType());
            fillDataTaskVO.setName(t.getName());
            fillDataTaskVO.setTenantName(tenantService.getTenantNameWithCache(tenantCache, t.getDtuicTenantId()));
            fillDataTaskVO.setProjectAlias(projectService.findProjectWithCache(projectCache, t.getProjectId(), t.getAppType()));
            List<FillDataTaskVO> fillDataTaskVOS = subFillDataTaskVOMapping.getOrDefault(t.getTaskId() + GlobalConst.TASK_KEY_SEPARATOR + t.getAppType(), Lists.newArrayList());
            fillDataTaskVO.setFillDataTaskVOList(fillDataTaskVOS);
            dataTaskVOS.add(fillDataTaskVO);
        }
        return dataTaskVOS;
    }

    /**
     * 根据运行次数统计相关信息
     *
     * @param jobId 实例 id
     * @return 运行记录相关信息
     */
    public List<JobRunCountVO> runNumList(String jobId) {
        List<ScheduleJobExpand> expands = scheduleJobExpandDao.getSimplifyExpandByJobId(jobId, environmentContext.getRunNumSize());
        if (CollectionUtils.isNotEmpty(expands)) {
            List<JobRunCountVO> jobRunCountVOS = scheduleJobStruct.toJobRunCountVO(expands);
            for (JobRunCountVO jobRunCountVO : jobRunCountVOS) {
                if (jobRunCountVO != null && jobRunCountVO.getStatus() != null) {
                    jobRunCountVO.setStatus(RdosTaskStatus.getShowStatusWithoutStop(jobRunCountVO.getStatus()));
                }
                if (jobRunCountVO != null && (jobRunCountVO.getExecTime() == null || jobRunCountVO.getExecTime().equals(0))) {
                    Long execTimeSecond = DateUtil.getExecTimeSecond(jobRunCountVO.getExecStartTime(), jobRunCountVO.getExecEndTime(), null, jobRunCountVO.getStatus());
                    jobRunCountVO.setExecTime(Objects.isNull(execTimeSecond) ? 0 : execTimeSecond.intValue());
                }
            }

            return jobRunCountVOS;
        }
        return Lists.newArrayList();
    }

    public List<ScheduleAlterReturnVO> alterPageList(ScheduleAlterPageVO pageVO) {
        List<Long> taskIds = pageVO.getTaskIds();
        String startCycTime = pageVO.getStartCycTime();
        String engCycTime = pageVO.getEngCycTime();
        Integer appType = pageVO.getAppType();

        if (CollectionUtils.isEmpty(taskIds)
                || StringUtils.isBlank(startCycTime)
                || StringUtils.isBlank(engCycTime)
                || appType == null) {

            return Lists.newArrayList();
        }

        Integer page = pageVO.getPage();
        Integer pageSize = pageVO.getPageSize();

        if (page == null || page <= 0) {
            page = 1;
        }

        if (pageSize == null || pageSize < 0 || pageSize > 2000) {
            pageSize = 2000;
        }

        List<ScheduleJob> scheduleJobs = scheduleJobDao.alterPageList(taskIds, startCycTime, engCycTime, appType, pageVO.getProjectId(), (page - 1) * pageSize, pageSize);
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return Lists.newArrayList();
        }

        List<ScheduleAlterReturnVO> scheduleAlterReturnVOS = Lists.newArrayList();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleAlterReturnVO scheduleAlterReturnVO = new ScheduleAlterReturnVO();

            scheduleAlterReturnVO.setAppType(scheduleJob.getAppType());
            scheduleAlterReturnVO.setStatus(scheduleJob.getStatus());
            scheduleAlterReturnVO.setCreateUserId(scheduleJob.getCreateUserId());
            scheduleAlterReturnVO.setExecEndTime(scheduleJob.getExecEndTime());
            scheduleAlterReturnVO.setExecStartTime(scheduleJob.getExecStartTime());
            scheduleAlterReturnVO.setJobId(scheduleJob.getJobId());
            scheduleAlterReturnVO.setType(scheduleJob.getType());
            scheduleAlterReturnVO.setTaskId(scheduleJob.getTaskId());
            scheduleAlterReturnVO.setIsRestart(scheduleJob.getIsRestart());
            scheduleAlterReturnVO.setCycTime(scheduleJob.getCycTime());
            scheduleAlterReturnVO.setProjectId(scheduleJob.getProjectId());
            scheduleAlterReturnVOS.add(scheduleAlterReturnVO);
        }

        return scheduleAlterReturnVOS;
    }

    private static class ParsedJobKey {

        private String jobKey;
        private String jobDay;
        private Long taskShadeId;

        public String getJobKey() {
            return jobKey;
        }

        public void setJobKey(String jobKey) {
            this.jobKey = jobKey;
        }

        public String getJobDay() {
            return jobDay;
        }

        public void setJobDay(String jobDay) {
            this.jobDay = jobDay;
        }

        public Long getTaskShadeId() {
            return taskShadeId;
        }

        public void setTaskShadeId(Long taskShadeId) {
            this.taskShadeId = taskShadeId;
        }
    }

    private ParsedJobKey parseJobKey(String jobKey) {
        String jobDay = getJobTriggerTimeFromJobKey(jobKey);
        Long taskShadeId = getTaskShadeIdFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(jobDay) || taskShadeId < 0) {
            return null;
        }

        ParsedJobKey res = new ParsedJobKey();
        res.setJobKey(jobKey);
        res.setJobDay(jobDay);
        res.setTaskShadeId(taskShadeId);
        return res;
    }

    /**
     * 此处获取的时候schedule_task_shade 的id 不是task_id
     * @param jobKey
     * @return
     */
    public Long getTaskShadeIdFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 2) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }

        String id = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(id);
        } catch (Exception e) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return -1L;
        }
    }

    public String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 1) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        String timeStr = strings[strings.length - 1];
        if (timeStr.length() < 8) {
            LOGGER.error("it's not a legal job key, str is {}.", jobKey);
            return "";
        }

        return timeStr.substring(0, 8);
    }


    private boolean checkJobCanStop(Integer status) {
        if (status == null) {
            return true;
        }
        return RdosTaskStatus.getCanStopStatus().contains(status);
    }



    /**
     * 根据工作流id获取子任务信息与任务状态
     *
     * @param jobId
     * @return
     */
    public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
        return scheduleJobDao.getSubJobsAndStatusByFlowId(jobId);
    }



    public List<String> listJobIdByTaskNameAndStatusList( String taskName,  List<Integer> statusList,  Long projectId, Integer appType) {
        ScheduleTaskShade task = batchTaskShadeService.getByName(projectId, taskName,appType,null);
        if (task != null) {
            return scheduleJobDao.listJobIdByTaskIdAndStatus(task.getTaskId(), task.getAppType() ,statusList);
        }
        return new ArrayList<>();
    }


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param projectId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap( List<String> jobIdList,  Long projectId) {

        if(CollectionUtils.isEmpty(jobIdList)){
            return Collections.EMPTY_MAP;
        }
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIdList, projectId);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            Map<String, ScheduleJob> jobMap = new HashMap<>();
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ScheduleJob flowJob = scheduleJobDao.getByJobId(scheduleJob.getFlowJobId(), Deleted.NORMAL.getStatus());
                jobMap.put(scheduleJob.getJobId(), flowJob);
            }
            return jobMap;
        }
        return new HashMap<>();
    }

    /**
     * 获取任务执息行信
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<Map<String, Object>> statisticsTaskRecentInfo( Long taskId,  Integer appType,  Long projectId,  Integer count,List<Integer> types) {

        return scheduleJobDao.listTaskExeInfo(taskId, projectId, count, appType,types);

    }

    /**
     * 获取任务执息行信 封装成 TaskExeInfoVo 集合
     *
     * @param taskId
     * @param appType
     * @param projectId
     * @param count
     * @return
     */
    public List<TaskExeInfoVo> statisticsTaskRecentInfoToVo(Long taskId, Integer appType, Long projectId, Integer count, List<Integer> types) {
        List<Map<String, Object>> maps = this.statisticsTaskRecentInfo(taskId, appType, projectId, count, types);
        List<TaskExeInfoVo> taskExeInfoVos = new LinkedList<>();
        try {
            for (Map<String, Object> map : maps) {
                for (String keyName : map.keySet()) {
                    map.computeIfPresent(keyName, (key, val) -> {
                        if (val instanceof LocalDateTime) {
                            LocalDateTime time = (LocalDateTime) val;
                            ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.systemDefault());
                            return zonedDateTime.toInstant().toEpochMilli();
                        }
                        return val;
                    });
                }
                taskExeInfoVos.add(PublicUtil.mapToObject(map, TaskExeInfoVo.class));
            }
        } catch (Exception e) {
            LOGGER.error("Get namespace error " + e.getMessage());
        }
        return taskExeInfoVos;
    }




    /**
     * 批量更新
     *
     * @param jobs
     */
    public Integer BatchJobsBatchUpdate( String jobs) {
        if (StringUtils.isBlank(jobs)) {
            return 0;
        }
        List<ScheduleJob> scheduleJobs = JSONObject.parseArray(jobs, ScheduleJob.class);
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return 0;
        }
        Integer updateSize = 0;
        for (ScheduleJob job : scheduleJobs) {
            if (null != job.getStatus() && StringUtils.isNotBlank(job.getJobId())) {
                //更新状态 日志信息也要更新
                scheduleJobExpandDao.updateLogInfoByJobId(job.getJobId(),"",job.getStatus());
            }

            if (RdosTaskStatus.UNSUBMIT.getStatus().equals(job.getStatus())) {
                job.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
            }

            updateSize += scheduleJobDao.update(job);

        }
        return updateSize;
    }

    /**
     * 把开始时间和结束时间置为null
     *
     * @param jobId
     * @return
     */
    public Integer updateTimeNull( String jobId){

        return scheduleJobDao.updateNullTime(jobId);
    }


    public ScheduleJob getById( Long id) {
        ScheduleJob scheduleJob = scheduleJobDao.getOne(id);
        if (scheduleJob != null) {
            encapsulationLog(scheduleJob.getJobId(), scheduleJob);
        }
        return scheduleJob;
    }

    public ScheduleJob getByJobId( String jobId,  Integer isDeleted) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, isDeleted);
        if (null!= scheduleJob) {
            // 实时没有ldap逻辑处理，所以这里要把实时排除掉
            if (!AppType.STREAM.getType().equals(scheduleJob.getAppType())) {
                scheduleJob.setSubmitUserName(getHadoopUserName(scheduleJob));
            }
            encapsulationLog(scheduleJob.getJobId(),scheduleJob);
        }
        return scheduleJob;
    }

    public ScheduleJob getSimpleByJobId( String jobId,  Integer isDeleted) {
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, isDeleted);
        return scheduleJob;
    }

    /**
     * 获取 taskId 和 jobId 对应的 map, key 为 taskId
     *
     * @param jobIds    jobId 集合
     * @param isDeleted 是否删除
     * @return map: taskId -> jobId
     */
    public Map<Long, String> getTaskIdJobIdMap(List<String> jobIds, Integer isDeleted) {
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getByJobIds(jobIds, isDeleted);
        return scheduleJobs.stream().collect(Collectors.toMap(ScheduleJob::getTaskId, ScheduleJob::getJobId));
    }

    private void encapsulationLog(String jobId, ScheduleJob scheduleJob) {
        try {
            ScheduleJobExpand expand = scheduleJobExpandDao.getLogByJobId(jobId);

            if (scheduleJob != null && expand != null) {
                scheduleJob.setLogInfo(expand.getLogInfo());
                scheduleJob.setEngineLog(expand.getEngineLog());
                scheduleJob.setRetryTaskParams(expand.getRetryTaskParams());
            }
        } catch (Exception e) {
            LOGGER.error("encapsulationLog error:{}", e.getMessage(), e);
        }
    }

    public Integer getJobStatus(String jobId){
        Integer status = scheduleJobDao.getStatusByJobId(jobId);
        if (Objects.isNull(status)) {
            throw new RdosDefineException("job not exist");
        }
        return status;
    }

    public Timestamp getJobExecStartTime(String jobId){
        return scheduleJobDao.getJobExecStartTime(jobId);
    }

    public List<ScheduleJob> getByIds( List<Long> ids) {

        if(CollectionUtils.isEmpty(ids)){
            return Collections.EMPTY_LIST;
        }
        return scheduleJobDao.listByJobIds(ids);
    }


    /**
     * 离线调用
     *
     * @param batchJob
     * @param isOnlyNextChild
     * @return
     */
    public List<ScheduleJob> getSameDayChildJob(String batchJob,
                                                boolean isOnlyNextChild) {
        ScheduleJob job = JSONObject.parseObject(batchJob, ScheduleJob.class);
        return getSameDayChildJob(job, isOnlyNextChild);
    }

    public List<ScheduleJob> getSameDayChildJob(ScheduleJob job,
                                                boolean isOnlyNextChild) {
        if (null == job ) {
            return Collections.emptyList();
        }

        Integer maxLevel = environmentContext.getJobJobLevel();
        Integer maxSize = environmentContext.getJobJobSize();
        Map<String, ScheduleJob> res = new HashMap<>();
        Map<String, ParsedJobKey> parsedJobKeys = new HashMap<>();
        parsedJobKeys.put(job.getJobKey(), parseJobKey(job.getJobKey()));
        long curr = System.currentTimeMillis();
        this.getAllChildJobWithSameDay(res, Collections.singletonList(job),
                parsedJobKeys, 1, isOnlyNextChild ? 2 : maxLevel, maxSize);

        LOGGER.info("getSameDayChildJob level: " + (isOnlyNextChild ? 2 : maxLevel) +
                " result size: " + res.size() +
                " elapse time: " + (System.currentTimeMillis() - curr));
        return new ArrayList<>(res.values());
    }

    /**
     * FIXME 注意不要出现死循环
     * 查询出指定job的所有关联的子job
     * 限定同一天并且不是自依赖
     *
     * @param outJobs, 输出所有子节点。
     * @param parentJobs, 本层所有节点。
     * @param parsedJobKeys, 所有已解析jobKeys。
     * @param level 第level层。
     * @param maxLevel 最大层数。
     * @return
     */
    private void getAllChildJobWithSameDay(Map<String, ScheduleJob> outJobs,
                                          List<ScheduleJob> parentJobs,
                                          Map<String, ParsedJobKey> parsedJobKeys,
                                          int level, int maxLevel, int maxSize) {

        if(parentJobs.isEmpty() || level >= maxLevel || outJobs.size() >= maxSize){
            return;
        }

        Set<String> parentJobKeys = parentJobs.stream()
                .map(ScheduleJob::getJobKey)
                .collect(Collectors.toSet());
        List<ScheduleJobJob> scheduleJobJobs = batchJobJobService.getJobChild(parentJobKeys);
        scheduleJobJobs.forEach(jj ->
                parsedJobKeys.computeIfAbsent(jj.getJobKey(), this::parseJobKey));
        if (CollectionUtils.isEmpty(scheduleJobJobs)) {
            return;
        }

        Set<String> childJobKeys = scheduleJobJobs.stream()
                .filter(jj -> {
                    if (outJobs.containsKey(jj.getJobKey())) {
                        return false;
                    }

                    if (!parsedJobKeys.containsKey(jj.getJobKey())) {
                        return false;
                    }

                    ParsedJobKey child = parsedJobKeys.get(jj.getJobKey());
                    ParsedJobKey parent = parsedJobKeys.get(jj.getParentJobKey());
                    return child.getJobDay().equals(parent.getJobDay()) &&
                            !child.getTaskShadeId().equals(parent.getTaskShadeId());
                })
                .map(ScheduleJobJob::getJobKey)
                .collect(Collectors.toSet());
        Set<Long> childTaskShadeIds = childJobKeys.stream()
                .map(parsedJobKeys::get)
                .map(ParsedJobKey::getTaskShadeId)
                .collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(childJobKeys)){
            return;
        }
        List<ScheduleJob> childJobs = scheduleJobDao.listJobByJobKeys(childJobKeys);
        Map<Long, ScheduleTaskShade> childTasks = batchTaskShadeService.getByIds(childTaskShadeIds);

        childJobs = childJobs.stream()
                .filter(j -> {
                    Long childTaskShadeId = parsedJobKeys.get(j.getJobKey()).getTaskShadeId();
                    return childTasks.containsKey(childTaskShadeId);
                })
                .collect(Collectors.toList());

        for (ScheduleJob childJob : childJobs) {
            outJobs.put(childJob.getJobKey(), childJob);
            if (outJobs.size() >= maxSize) {
                break;
            }
        }

        level++;
        if (!childJobs.isEmpty() && level < maxLevel && outJobs.size() < maxSize) {
            getAllChildJobWithSameDay(outJobs, childJobs, parsedJobKeys, level, maxLevel, maxSize);
        }
    }

    public Integer generalCount(ScheduleJobDTO query) {
        query.setPageQuery(false);
        return scheduleJobDao.generalCount(query);
    }

    public Integer generalCountWithMinAndHour(ScheduleJobDTO query) {
        query.setPageQuery(false);
        return scheduleJobDao.generalCountWithMinAndHour(query);
    }


    public List<ScheduleJob> generalQuery(PageQuery query) {
        return scheduleJobDao.generalQuery(query);
    }

    public List<ScheduleJob> generalQueryWithMinAndHour(PageQuery query) {
        return scheduleJobDao.generalQueryWithMinAndHour(query);
    }

    /**
     * 获取job最后一次执行
     *
     * @param taskId
     * @param time
     * @return
     */
    public ScheduleJob getLastSuccessJob(Long taskId, Timestamp time, Integer appType) {
        if (null == taskId || null == time || null == appType) {
            return null;
        }
        ScheduleJob job = scheduleJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, RdosTaskStatus.FINISHED.getStatus(), time, appType,null);
        if (job != null && StringUtils.isNotBlank(job.getJobId())) {
            encapsulationLog(job.getJobId(), job);
        }
        return job;
    }


    /**
     * 设置算法实验日志
     * 获取全部子节点日志
     *
     * @param status
     * @param taskType
     * @param jobId
     * @param logVo
     * @throws Exception
     */
    public ScheduleServerLogVO setAlogrithmLabLog( Integer status,  Integer taskType,  String jobId,
                                                   String info,  String logVo,  Integer appType) throws Exception {

        ScheduleServerLogVO scheduleServerLogVO = JSONObject.parseObject(logVo, ScheduleServerLogVO.class);
        if(!taskType.equals(EScheduleJobType.ALGORITHM_LAB.getVal())){
            return scheduleServerLogVO;
        }
        if (RdosTaskStatus.FAILED_STATUS.contains(status) ||
                RdosTaskStatus.FINISH_STATUS.contains(status)) {
            List<ScheduleJob> subJobs = scheduleJobDao.getSubJobsByFlowIds(Lists.newArrayList(jobId));
            if(CollectionUtils.isEmpty(subJobs)){
                return scheduleServerLogVO;
            }
            Map<String, String> subNodeDownloadLog = new HashMap<>(subJobs.size());
            StringBuilder subTaskLogInfo = new StringBuilder();
            List<Long> taskIds = subJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.listByTaskIds(taskIds, null, appType);
            if (CollectionUtils.isEmpty(taskShades)) {
                return scheduleServerLogVO;
            }
            Map<Long, ScheduleTaskShade> shadeMap = taskShades
                    .stream()
                    .collect(Collectors.toMap(ScheduleTaskShade::getTaskId, batchTaskShade -> batchTaskShade));
            for (ScheduleJob subJob : subJobs) {
                ScheduleTaskShade subTaskShade = shadeMap.get(subJob.getTaskId());
                if (null == subTaskShade ) {
                    continue;
                }
                setVirtualLog(subNodeDownloadLog, subTaskLogInfo, subJob, subTaskShade);
            }
            JSONObject infoObject = JSONObject.parseObject(info);
            infoObject.put("msg_info", subTaskLogInfo.toString());
            scheduleServerLogVO.setSubNodeDownloadLog(subNodeDownloadLog);
            scheduleServerLogVO.setLogInfo(infoObject.toJSONString());
        }
        return scheduleServerLogVO;
    }


    /**
     * @author newman
     * @Description 设置虚结点的日志信息
     * @Date 2020-12-21 16:49
     * @param subNodeDownloadLog:
     * @param subTaskLogInfo:
     * @param subJob:
     * @param subTaskShade:
     * @return: void
     **/
    private void setVirtualLog(Map<String, String> subNodeDownloadLog, StringBuilder subTaskLogInfo, ScheduleJob subJob, ScheduleTaskShade subTaskShade) {
        if (EScheduleJobType.VIRTUAL.getType().intValue() != subTaskShade.getTaskType()) {
            Long dtuicTenantId = subTaskShade.getDtuicTenantId();
            if (dtuicTenantId != null) {
                subNodeDownloadLog.put(subTaskShade.getName(), String.format(DOWNLOAD_LOG, subJob.getJobId(), subTaskShade.getTaskType()));
                ActionLogVO logInfoFromEngine = this.getLogInfoFromEngine(subJob.getJobId());
                if (null != logInfoFromEngine ) {
                    subTaskLogInfo.append(subTaskShade.getName()).
                            append("\n====================\n").
                            append(logInfoFromEngine.getLogInfo()).
                            append("\n====================\n").
                            append(logInfoFromEngine.getEngineLog()).
                            append("\n");
                }
            }
        }
    }


    /**
     * 获取日志
     *
     * @return
     */
    public ActionLogVO getLogInfoFromEngine(String jobId) {
        try {
            return actionService.log(jobId, ComputeType.BATCH.getType());
        } catch (Exception e) {
            LOGGER.error("Exception when getLogInfoFromEngine by jobId: {} and computeType: {}", jobId, ComputeType.BATCH.getType(), e);
        }
        return null;
    }


    /**
     * 周期实例列表
     * 分钟任务和小时任务 展开按钮显示
     * @return
     */
    public List<com.dtstack.engine.api.vo.ScheduleJobVO> minOrHourJobQuery(ScheduleJobDTO batchJobDTO) {
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(batchJobDTO.getCurrentPage(), batchJobDTO.getPageSize(), "gmt_modified", Sort.DESC.name());

        batchJobDTO.setPageQuery(false);
        batchJobDTO.setProjectId(null);
        pageQuery.setModel(batchJobDTO);

        Integer count = scheduleJobDao.minOrHourJobCount(batchJobDTO);
        if (count < 0) {
            return null;
        }

        List<ScheduleJob> scheduleJobs = scheduleJobDao.minOrHourJobQuery(pageQuery);
        Map<Long, ScheduleTaskForFillDataDTO> prepare = this.prepare(scheduleJobs);
        List<Long> resourceIds = scheduleJobs.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
        Map<Long, ResourceGroupDetail> groupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
        List<ScheduleJobVO> transfer = this.transfer(scheduleJobs, prepare,groupDetailMap);
        transfer.forEach(b -> b.setIsGroupTask(false));
        //处理工作流子节点
        try {
            this.dealFlowWorkSubJobs(transfer);
        } catch (Exception e) {
            throw new RdosDefineException("处理工作流字节点异常");
        }
        List<com.dtstack.engine.api.vo.ScheduleJobVO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(transfer)) {
            transfer.forEach(vo -> result.add(vo));
        }
        return result;
    }


    /**
     * 更新任务状态和日志
     *
     * @param jobId
     * @param status
     * @param logInfo
     */
    public void updateJobStatusAndLogInfo( String jobId,  Integer status,  String logInfo) {
        scheduleJobExpandDao.updateLogInfoByJobId(jobId,logInfo,status);
        scheduleJobDao.updateStatusByJobId(jobId, status,null,null,null);
    }


    /**
     * 生成当天单个任务实例
     *
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTodayTaskShade( Long taskId, Integer appType,String date) {
        try {
            //如果appType为空的话则为离线
            if (null == appType) {
                throw new RdosDefineException("appType不能为空");
            }
            ScheduleTaskShade testTask = batchTaskShadeService.getBatchTaskById(taskId, appType);
            if (null == testTask) {
                throw new RdosDefineException("任务不存在");
            }
            List<ScheduleTaskShade> taskShades = new ArrayList<>();
            taskShades.add(testTask);
            if (SPECIAL_TASK_TYPES.contains(testTask.getTaskType())) {
                //工作流算法实验 需要将子节点查询出来运行
                List<ScheduleTaskShade> flowWorkSubTasks = batchTaskShadeService.getFlowWorkSubTasks(testTask.getTaskId(), testTask.getAppType(),
                        null, null);
                if (CollectionUtils.isNotEmpty(flowWorkSubTasks)) {
                    taskShades.addAll(flowWorkSubTasks);
                }
            }
            if(StringUtils.isBlank(date)){
                date = new DateTime().toString("yyyy-MM-dd");
            }
            Map<String, String> flowJobId = new ConcurrentHashMap<>();
            List<ScheduleBatchJob> allJobs = new ArrayList<>();
            AtomicInteger count = new AtomicInteger();
            for (ScheduleTaskShade task : taskShades) {
                try {
                    List<ScheduleBatchJob> cronTrigger = jobGraphBuilder.buildJobRunBean(task, "cronTrigger", EScheduleType.NORMAL_SCHEDULE,
                            true, true, date, "cronJob" + "_" + task.getName(),
                            null, task.getProjectId(), task.getTenantId(),count);
                    allJobs.addAll(cronTrigger);
                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                        //工作流或算法实验
                        for (ScheduleBatchJob jobRunBean : cronTrigger) {
                            flowJobId.put(JobGraphUtils.buildFlowReplaceId(task.getTaskId(),jobRunBean.getCycTime(),task.getAppType()),jobRunBean.getJobId());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("生成当天单个任务实例异常,taskId:{}",task.getTaskId(), e);
                }
            }

            for (ScheduleBatchJob job : allJobs) {
                String flowIdKey = job.getScheduleJob().getFlowJobId();
                job.getScheduleJob().setFlowJobId(flowJobId.getOrDefault(flowIdKey, "0"));
            }
            sortAllJobs(allJobs);

            //需要保存BatchJob, BatchJobJob
            this.insertJobList(allJobs, EScheduleType.NORMAL_SCHEDULE.getType());

        } catch (Exception e) {
            LOGGER.error("createTodayTaskShadeForTest", e);
            throw new RdosDefineException("任务创建失败");
        }
    }

    /**
     * @author newman
     * @Description 对任务列表按照执行时间排序
     * @Date 2020-12-21 16:55
     * @param allJobs:
     * @return: void
     **/
    private void sortAllJobs(List<ScheduleBatchJob> allJobs) {
        allJobs.sort((ebj1, ebj2) -> {
            Long date1 = Long.valueOf(ebj1.getCycTime());
            Long date2 = Long.valueOf(ebj2.getCycTime());
            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            }
            return 0;
        });
    }

    public List<ScheduleJob> listByBusinessDateAndPeriodTypeAndStatusList(ScheduleJobDTO query) {
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        return scheduleJobDao.listByBusinessDateAndPeriodTypeAndStatusList(pageQuery);
    }

    /**
     * 根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType) {

        return scheduleJobDao.listJobByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    /**
     * 按批次根据cycTime和jobName获取，如获取当天的周期实例任务
     *
     * @param startId
     * @param preCycTime
     * @param preJobName
     * @param scheduleType
     * @param batchJobSize
     * @return
     */
    public List<ScheduleJob> listByCyctimeAndJobName( Long startId,  String preCycTime,  String preJobName,  Integer scheduleType,  Integer batchJobSize) {

        return scheduleJobDao.listJobByCyctimeAndJobNameBatch(startId, preCycTime, preJobName, scheduleType, batchJobSize);
    }

    public Integer countByCyctimeAndJobName( String preCycTime,  String preJobName,  Integer scheduleType) {
        return scheduleJobDao.countJobByCyctimeAndJobName(preCycTime, preJobName, scheduleType);
    }

    /**
     * 根据jobKey删除job jobjob记录
     *
     * @param jobKeyList
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobsByJobKey( List<String> jobKeyList) {

        if(CollectionUtils.isEmpty(jobKeyList)){
            return;
        }
        scheduleJobDao.deleteByJobKey(jobKeyList);
        scheduleJobJobDao.deleteByJobKey(jobKeyList);
    }


    /**
     * 分批获取batchJob中数据
     * schedule   接口
     * * @param dto
     *
     * @return
     */

    public List<ScheduleJob> syncBatchJob(QueryJobDTO dto) {
        if (null == dto || null == dto.getAppType()) {
            return new ArrayList<>();
        }
        if (null == dto.getCurrentPage()) {
            dto.setCurrentPage(1);
        }
        if (null == dto.getPageSize()) {
            dto.setPageSize(50);
        }
        if (null != dto.getProjectId() && -1L == dto.getProjectId()) {
            // 不采用默认值
            dto.setProjectId(null);
        }
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(dto.getCurrentPage(), dto.getPageSize(), "gmt_modified", Sort.DESC.name());
        ScheduleJobDTO query = this.createQuery(dto);
        pageQuery.setModel(query);
        return scheduleJobDao.syncQueryJob(pageQuery);
    }

    /**
     *
     * 根据taskId、appType 拿到对应的job集合
     * @param taskIds
     * @param appType
     */

    public List<ScheduleJob> listJobsByTaskIdsAndApptype( List<Long> taskIds, Integer appType){

        if(CollectionUtils.isEmpty(taskIds)){
            return Collections.EMPTY_LIST;
        }
        return scheduleJobDao.listJobsByTaskIdAndApptype(taskIds,appType);
    }


    /**
     * 生成指定日期的周期实例(需要数据库无对应记录)
     *
     * @param triggerDay
     */
    public void buildTaskJobGraphTest( String triggerDay) {
        CompletableFuture.runAsync(() -> jobGraphBuilder.buildTaskJobGraph(triggerDay));
    }

    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        if (id==null|| original==null|| update==null) {
            return Boolean.FALSE;
        }

        Integer integer = scheduleJobDao.updatePhaseStatusById(id, original.getCode(), update.getCode());

        if (integer != null && !integer.equals(0)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Long getListMinId(String left, String right) {
        // 如果没有时间限制, 默认返回0
        if (StringUtils.isAnyBlank(left,right)){
            return 0L;
        }
        // 如果当前时间范围没有数据, 返回NULL
        String minJobId = jobGraphTriggerDao.getMinJobIdByTriggerTime(left, right);
        if (StringUtils.isBlank(minJobId)){
            return 0L;
        }
        return Long.parseLong(minJobId);
    }

    public String getJobGraphJSON(String jobId) {
        String jobExtraInfo = scheduleJobExpandDao.getJobExtraInfo(jobId);
        JSONObject jobExtraObj = JSONObject.parseObject(jobExtraInfo);
        if (null != jobExtraObj) {
            return jobExtraObj.getString(JobResultConstant.JOB_GRAPH);
        }
        return "";
    }

    public void updateNotRuleResult(String jobId,Integer rule,String result) {
        LOGGER.info("updateNotRuleResult start jobId:{} , rule:{} result:{} ",jobId,rule,result);
        ScheduleJob job = scheduleJobDao.getByJobId(jobId, 0);

        JSONObject json = new JSONObject();
        json.put("jobId",jobId);
        json.put("result",result);
        if (job != null && EScheduleJobType.NOT_DO_TASK.getType().equals(job.getTaskType())) {
            Timestamp execStartTime = job.getExecStartTime();
            if (execStartTime == null) {
                execStartTime = new Timestamp(System.currentTimeMillis());
            }

            if (rule == 1) {
                json.put("msg_info","Application callback succeeded");
                updateStatusAndLogInfoAndExecTimeById(jobId, RdosTaskStatus.FINISHED.getStatus(), json.toJSONString(),execStartTime,new Date());
            } else if (rule == 2) {
                json.put("msg_info","Application callback failure");
                updateStatusAndLogInfoAndExecTimeById(jobId, RdosTaskStatus.FAILED.getStatus(), json.toJSONString(),execStartTime,new Date());
            }
        } else {
            LOGGER.info("updateNotRuleResult update  error jobId:{} , rule:{} result:{} ",jobId,rule,result);
            throw new RdosDefineException("job status error,so update failure");
        }
    }

    public void updateStatusByJobIdEqualsStatus(String jobId, Integer status, Integer status1) {
        scheduleJobDao.updateStatusByJobIdEqualsStatus(jobId,status,status1);
    }

    public List<ScheduleJob> listJobByJobKeys(List<String> parentJobKeys) {
        if (CollectionUtils.isNotEmpty(parentJobKeys)) {
            return scheduleJobDao.listJobByJobKeys(parentJobKeys);
        }
        return Lists.newArrayList();
    }

    /**
     * 根据父任务实例获取子任务实例
     * @param parentJobKeys
     * @return
     */
    public List<ScheduleJob> getSonJob(List<String> parentJobKeys) {
        List<ScheduleJob> sonJobList = Lists.newArrayList();
        for (String parentJobKey : parentJobKeys) {
            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(parentJobKey, RelyTypeEnum.NORMAL.getType());
            if (CollectionUtils.isEmpty(scheduleJobJobs)) {
                continue;
            }
            List<String> jobKeySon = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());

            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeySon);

            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                sonJobList.addAll(scheduleJobs);
            }
        }
        return sonJobList;
    }

    public Map<String, List<ScheduleJob>> getParentJobKeyMap(List<String> parentJobKeys) {
        Map<String, List<ScheduleJob>> parentAndSon = Maps.newHashMap();
        for (String parentJobKey : parentJobKeys) {
            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(parentJobKey,RelyTypeEnum.NORMAL.getType());
            List<String> jobKeySon = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());

            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeySon);

            if (CollectionUtils.isNotEmpty(scheduleJobs)) {
                parentAndSon.put(parentJobKey,scheduleJobs);
            }
        }
        return parentAndSon;
    }


    public void handleTaskRule(ScheduleJob scheduleJob,Integer bottleStatus) {
        String jobKey = scheduleJob.getJobKey();
        // 查询当前任务的所有父任务的运行状态
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(jobKey,RelyTypeEnum.NORMAL.getType());
        if (CollectionUtils.isNotEmpty(scheduleJobJobs)) {
            List<String> parentJobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());
            // 查询所有父任务
            List<ScheduleJob> scheduleJobs = this.listJobByJobKeys(parentJobKeys);
            // 查询所有父任务下的子任务关系
            Map<String,List<ScheduleJob>> parentAndSon = this.getParentJobKeyMap(parentJobKeys);

            for (ScheduleJob scheduleJobParent : scheduleJobs) {
                // 判断状态父任务的状态
                List<ScheduleJob> scheduleJobsSon = parentAndSon.get(scheduleJobParent.getJobKey());
                updateFatherStatus(scheduleJobParent,scheduleJob,scheduleJobsSon,bottleStatus);
            }

        }
    }

    public boolean hasTaskRule(ScheduleJob scheduleJob) {
        LOGGER.info("jobId:{} start hasRule",scheduleJob.getJobId());
        boolean hasTaskRule = Boolean.FALSE;
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(scheduleJob.getJobKey(),RelyTypeEnum.NORMAL.getType());

        List<String> jobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
        LOGGER.info("jobId:{} has child jobKey:{}" ,scheduleJob.getJobId(), jobKeys.toString());
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeys);

            for (ScheduleJob job : scheduleJobs) {
                // 如果查询出来任务状是冻结状态
                if (TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule())) {
                    ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeDao.getOne(job.getTaskId(), job.getAppType());
                    if (EScheduleStatus.PAUSE.getVal().equals(scheduleTaskShade.getScheduleStatus()) ||
                            EProjectScheduleStatus.PAUSE.getStatus().equals(scheduleTaskShade.getProjectScheduleStatus())) {
                        // 子任务已经冻结，该任务不受影响
                        continue;
                    }

                    // 存在强规则且非冻结状态
                    LOGGER.info("jobId {} exist rule task",job.getJobId());
                    hasTaskRule = Boolean.TRUE;
                    break;
                }
            }
        }
        return hasTaskRule;
    }

    private void updateFatherStatus(ScheduleJob fatherScheduleJob, ScheduleJob currentScheduleJob, List<ScheduleJob> sonScheduleJobs, Integer bottleStatus) {
        if (CollectionUtils.isEmpty(sonScheduleJobs)) {
            return;
        }
        if(!RdosTaskStatus.FAILED_STATUS.contains(bottleStatus) && !RdosTaskStatus.FINISH_STATUS.contains(bottleStatus)){
            return;
        }

         if (RdosTaskStatus.FAILED_STATUS.contains(bottleStatus)) {
            updateFatherStatus(fatherScheduleJob, RdosTaskStatus.FAILED.getStatus());
            updateFatherLog(fatherScheduleJob, currentScheduleJob, bottleStatus,RdosTaskStatus.FAILED.getStatus(),null);
        } else if (RdosTaskStatus.FINISH_STATUS.contains(bottleStatus)) {
            // 当前任务执行成功,判断父任务下其他子任务是否有强规则任务
            List<ScheduleJob> jobs = sonScheduleJobs.stream().filter(job -> TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule()) && !job.getJobKey().equals(currentScheduleJob.getJobKey())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(jobs)) {
                List<ScheduleJob> noFinishJobs = jobs.stream().filter(job -> !RdosTaskStatus.FINISH_STATUS.contains(job.getStatus())).collect(Collectors.toList());
                // noFinishJobs集合是空的，没有未完成的状态，更新父节点
                if (CollectionUtils.isEmpty(noFinishJobs)) {
                    updateFatherStatus(fatherScheduleJob, RdosTaskStatus.FINISHED.getStatus());
                    updateFatherLog(fatherScheduleJob, currentScheduleJob, bottleStatus,RdosTaskStatus.FINISHED.getStatus(),sonScheduleJobs);
                }
            } else {
                updateFatherStatus(fatherScheduleJob, RdosTaskStatus.FINISHED.getStatus());
                updateFatherLog(fatherScheduleJob, currentScheduleJob, bottleStatus,RdosTaskStatus.FINISHED.getStatus(),sonScheduleJobs);
            }
        }
    }

    private void updateFatherLog(ScheduleJob fatherScheduleJob, ScheduleJob currentScheduleJob, Integer bottleStatus,Integer status,List<ScheduleJob> jobs) {
        try {
            String nameByDtUicTenantId = tenantService.getTenantName(currentScheduleJob.getDtuicTenantId());
            AuthProjectVO authProjectVO = projectService.finProject(currentScheduleJob.getProjectId(), currentScheduleJob.getAppType());
            String logInfo = scheduleJobExpandDao.getLogInfoByJobId(fatherScheduleJob.getJobId());
            boolean addLog = false;
            String log = "";

            List<ScheduleJob> jobList = null;
            if (CollectionUtils.isNotEmpty(jobs)) {
                jobList = jobs.stream().filter(job -> TaskRuleEnum.STRONG_RULE.getCode().equals(job.getTaskRule())).collect(Collectors.toList());
            }

            if (RdosTaskStatus.FAILED_STATUS.contains(bottleStatus)) {
                // 当前强任务执行失败，执行更新成失败
                log = getLog(logInfo, currentScheduleJob, nameByDtUicTenantId, authProjectVO);
            } else if (RdosTaskStatus.FINISH_STATUS.contains(bottleStatus)) {
                addLog = true;
                if (CollectionUtils.isNotEmpty(jobList)) {
                    StringBuilder sb = new StringBuilder();
                    for (ScheduleJob job : jobList) {
                        String tenantName = tenantService.getTenantName(job.getDtuicTenantId());
                        AuthProjectVO projectVO = projectService.finProject(job.getProjectId(), job.getAppType());
                        sb.append(String.format(LOG_TEM, job.getJobName(), "校验通过",
                                StringUtils.isBlank(tenantName) ? "" : tenantName, projectVO == null ? "" : projectVO.getProjectAlias())).append("\n");
                    }

                    log = sb.toString();
                } else {
                    log = String.format(LOG_TEM, currentScheduleJob.getJobName(), "校验通过",
                            StringUtils.isBlank(nameByDtUicTenantId) ? "" : nameByDtUicTenantId, authProjectVO == null ? "" : authProjectVO.getProjectAlias());
                }
            }
            if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(fatherScheduleJob.getStatus())) {
                updateLogInfoById(fatherScheduleJob.getJobId(), addLog ? addLog(logInfo, log) : log,status);
            } else {
                updateLogInfoById(fatherScheduleJob.getJobId(), addLog ? addLog(logInfo, log) : log, null);
            }
        } catch (Exception e) {
            LOGGER.error("updateFatherLog {} log error {}", fatherScheduleJob.getJobId(), e);
        }
    }

    private void updateFatherStatus(ScheduleJob fatherScheduleJob, Integer status) {
        if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(fatherScheduleJob.getStatus())) {
            this.updateStatusByJobId(fatherScheduleJob.getJobId(), status,fatherScheduleJob.getVersionId());
        }
    }

    public void updateLogInfoById(String jobId, String msg,Integer bottleStatus) {
        if (StringUtils.isNotBlank(msg) && msg.length() > 5000) {
            msg = msg.substring(0, 5000) + "...";
        }
        scheduleJobExpandDao.updateLogInfoByJobId(jobId,msg,bottleStatus);
    }

    private String getLog(String fatherLogInfo,ScheduleJob currentScheduleJob,String nameByDtUicTenantId,AuthProjectVO project) {
        // %s: %s(所属租户：%s,所属项目：%s)
        String addLog = LOG_TEM;

        boolean isRule = Boolean.FALSE;
        // 如果工作流任务，查询是否有null任务
        List<ScheduleJob> subJobsAndStatusByFlowId = this.getSubJobsAndStatusByFlowId(currentScheduleJob.getJobId());
        List<ScheduleJob> jobs = subJobsAndStatusByFlowId.stream().filter(job -> EScheduleJobType.NOT_DO_TASK.getType().equals(job.getTaskType())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(jobs)) {
            // 有空任务
            for (ScheduleJob job : jobs) {
                if (RdosTaskStatus.FAILED_STATUS.contains(job.getStatus())) {
                    // 存在空任务失败的情况
                    String logInfo1 = "运行失败";
                    try {
                        JSONObject jsonObject = JSON.parseObject(scheduleJobExpandDao.getLogInfoByJobId(job.getJobId()));
                        logInfo1 = jsonObject.getString("result");
                    } catch (Exception e) {
                        LOGGER.error("log json parseObject error:",e);
                    }
                    addLog = String.format(addLog, currentScheduleJob.getJobName(), logInfo1, StringUtils.isBlank(nameByDtUicTenantId)?"":nameByDtUicTenantId,project==null? "":project.getProjectAlias());
                    isRule = Boolean.TRUE;
                    break;
                }
            }
        }

        if (!isRule) {
            addLog = String.format(addLog, currentScheduleJob.getJobName(), "运行失败", nameByDtUicTenantId, project.getProjectAlias());
        }

        return addLog(fatherLogInfo, addLog);


    }

    private String addLog(String logInfo, String addLog) {
        try {
            JSONObject jsonObject = JSON.parseObject(logInfo);
            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConst.RULE_LOG_FILED);

            if (jsonArray != null) {
                jsonArray.add(addLog);
            } else {
                jsonArray = new JSONArray();
                List<String> ruleLogList = Lists.newArrayList();
                ruleLogList.add(addLog);
                jsonArray.add(ruleLogList);
            }

            jsonObject.put(GlobalConst.RULE_LOG_FILED,jsonArray);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logInfo+= "===============================================================\n";
            logInfo+=addLog;
            return logInfo;
        }
    }


    public List<ScheduleJobBeanVO> findTaskRuleJobById(Long id) {
        // 查询 jobId 的所有子节点
        ScheduleJob scheduleJob = getJobById(id);

        List<ScheduleJobBeanVO> vos = Lists.newArrayList();
        if (scheduleJob == null) {
            throw new RdosDefineException("job not exist,please checking jobId");
        }

        // 查询该任务下所有的规则任务
        List<ScheduleJob> scheduleJobs = getTaskRuleSonJob(scheduleJob);

        buildScheduleJobBeanVOs(vos,scheduleJobs);
        return vos;
    }

    public List<ScheduleJob> getTaskRuleSonJob(ScheduleJob scheduleJob) {
        List<ScheduleJob> scheduleJobs = Lists.newArrayList();
        List<ScheduleJobJob> jobJobs = scheduleJobJobDao.listByParentJobKey(scheduleJob.getJobKey(),RelyTypeEnum.NORMAL.getType());
        if (CollectionUtils.isNotEmpty(jobJobs)) {
            List<String> jobKeys = jobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
            List<ScheduleJob> jobs = scheduleJobDao.listJobByJobKeys(jobKeys);

            for (ScheduleJob job : jobs) {
                if (!TaskRuleEnum.NO_RULE.getCode().equals(job.getTaskRule())) {
                    scheduleJobs.add(job);
                }
            }
        }
        return scheduleJobs;
    }

    private ScheduleJobBeanVO buildScheduleJobBeanVO(ScheduleJob job) {
        ScheduleJobBeanVO vo = new ScheduleJobBeanVO();
        if (job != null) {
            BeanUtils.copyProperties(job, vo);
        }
        return vo;
    }

    private void buildScheduleJobBeanVOs(List<ScheduleJobBeanVO> vos, List<ScheduleJob> subJobsAndStatusByFlowId) {
        if (CollectionUtils.isNotEmpty(subJobsAndStatusByFlowId)) {
            for (ScheduleJob scheduleJob : subJobsAndStatusByFlowId) {
                vos.add(buildScheduleJobBeanVO(scheduleJob));
            }
        }
    }

    public ScheduleDetailsVO findTaskRuleJob(String jobId) {
        // 查询 jobId 的所有子节点
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
        List<ScheduleJob> taskRuleSonJob = getTaskRuleSonJob(scheduleJob);
        ScheduleDetailsVO vo = new ScheduleDetailsVO();

        buildScheduleJobDetailsVO(vo, scheduleJob,Boolean.TRUE);

        List<ScheduleDetailsVO> vos = Lists.newArrayList();
        for (ScheduleJob job : taskRuleSonJob) {
            ScheduleDetailsVO voSon = new ScheduleDetailsVO();
            buildScheduleJobDetailsVO(voSon, job,Boolean.FALSE);
            vos.add(voSon);
        }

        vo.setScheduleDetailsVOList(vos);
        return vo;
    }

    private void buildScheduleJobDetailsVO(ScheduleDetailsVO vo, ScheduleJob scheduleJob,Boolean isSelectWaitReason) {
        if (scheduleJob != null) {
            if(ESchedulePeriodType.CALENDER.getVal() == scheduleJob.getPeriodType()){
               Long calenderId = scheduleJob.getCalenderId();
               if(null == calenderId){
                   calenderId = calenderService.getCalenderIdByTaskId(scheduleJob.getTaskId(), scheduleJob.getAppType());
               }
                if (calenderId != null) {
                    ConsoleCalender calender = calenderService.findById(calenderId);
                    vo.setCalenderName(null == calender ? "" : calender.getCalenderName());
                }
            }
            vo.setAppType(scheduleJob.getAppType());
            ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOneIncludeDelete(scheduleJob.getTaskId(), scheduleJob.getAppType());
            if (taskShade != null) {
                vo.setAppType(taskShade.getAppType());
                vo.setName(taskShade.getName());
                vo.setTaskRule(taskShade.getTaskRule());
                vo.setTaskType(taskShade.getTaskType());
                vo.setScheduleStatus(taskShade.getScheduleStatus());
                vo.setProjectScheduleStatus(taskShade.getProjectScheduleStatus());
                vo.setGmtCreate(DateUtil.getDate(taskShade.getGmtCreate(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setTaskId(taskShade.getTaskId());
                vo.setOwnerUserId(taskShade.getOwnerUserId());
                vo.setPeriodType(taskShade.getPeriodType());
                vo.setTaskDesc(taskShade.getTaskDesc());
                vo.setScheduleConf(taskShade.getScheduleConf());
                vo.setTaskIsDeleted(taskShade.getIsDeleted());
                AuthProjectVO authProjectVO = projectService.finProject(taskShade.getProjectId(), taskShade.getAppType());
                if (authProjectVO != null) {
                    vo.setProjectName(authProjectVO.getProjectName());
                    vo.setProjectAlias(authProjectVO.getProjectAlias());
                }

                String tenantName = tenantService.getTenantName(taskShade.getDtuicTenantId());
                vo.setTenantName(tenantName);

                Long resourceId = taskShade.getResourceId();
                Optional<ResourceGroup> resourceGroupOptional = resourceGroupService.getResourceGroup(resourceId);
                if (resourceGroupOptional.isPresent()) {
                    ResourceGroup resourceGroup = resourceGroupOptional.get();
                    vo.setResourceName(resourceGroup.getName());
                }
            } else {
                vo.setName(scheduleJob.getJobName());
            }

            vo.setJobId(scheduleJob.getJobId());
            vo.setBusinessDate(TimeUtils.getOnlyDate(scheduleJob.getBusinessDate()));
            vo.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
            vo.setExecEndTime(DateUtil.getDate(scheduleJob.getExecEndTime(),DateUtil.STANDARD_DATETIME_FORMAT));
            vo.setExecStartTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            vo.setStatus(RdosTaskStatus.getShowStatusWithoutStop(scheduleJob.getStatus()));
            vo.setRetryNum(scheduleJob.getRetryNum());

            // 计算运行次数
            Integer count = scheduleJobOperateService.count(scheduleJob.getJobId());
            if (count > 0) {
                vo.setRunNum(count+1);
            } else {
                if (RdosTaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus())) {
                    vo.setRunNum(1);
                } else {
                    vo.setRunNum(0);
                }
            }

            // 判断等待原因
            if (isSelectWaitReason) {
                try {
                    vo.setWaitReason(selectWaitReason(scheduleJob,taskShade));
                } catch (Exception e) {
                    LOGGER.error("selectWaitReason error",e);
                }
            }

            // 自定义运行参数
            List<ScheduleJobParam> jobParams = scheduleJobParamDao.selectByJobId(scheduleJob.getJobId());
            if (CollectionUtils.isNotEmpty(jobParams)) {
                List<ScheduleTaskParamShade> paramShadeList = paramStruct.jobParamToTaskParams(jobParams);
                TaskCustomParamVO taskCustomParamVO = new TaskCustomParamVO();
                taskCustomParamVO.setTaskId(scheduleJob.getTaskId());
                taskCustomParamVO.setTaskParamsToReplace(JSONArray.toJSONString(paramShadeList));
                vo.setTaskCustomParamVO(taskCustomParamVO);
            }

            // 标签信息
            List<ScheduleTaskTag> tagByTaskIds = scheduleTaskTagService.findTagByTaskIds(Sets.newHashSet(scheduleJob.getTaskId()), scheduleJob.getAppType());
            vo.setTagIds(tagByTaskIds.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList()));

            Integer priority = scheduleTaskPriorityService.selectPriorityByTaskId(taskShade.getTaskId(), taskShade.getAppType());
            vo.setPriority(priority);
        }
    }

    private String selectWaitReason(ScheduleJob scheduleJob, ScheduleTaskShade taskShade) throws ParseException {
        // 判断是否等待状态
        if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus())) {
            return "";
        }

        Pair<String, String> cycTimeLimitEndNow = jobRichOperator.getCycTimeLimitEndNow(true);

        long startTime = Long.parseLong(cycTimeLimitEndNow.getLeft());
        long endTime = Long.parseLong(cycTimeLimitEndNow.getRight());

        String cycTime = scheduleJob.getCycTime();
        long cycTimeLong = Long.parseLong(cycTime);

        if (cycTimeLong < startTime) {
            return "实例已超24小时";
        }

        if (cycTimeLong > endTime) {
            return "实例未到计划时间";
        }

        ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey(),RelyTypeEnum.NORMAL.getType());
        scheduleBatchJob.setJobJobList(scheduleJobJobs);

        JobCheckRunInfo checkRunInfo = jobStatusSummitCheckOperator.checkJobStatusMeetSubmissionConditions(scheduleJob.getStatus(), taskShade, scheduleBatchJob, true);

        if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH) {
            return "实例未到计划时间";
        }

        if (checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT) {
            return "";
        }

        if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED ||
                checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED) {
            return "上游实例未运行结束";
        }

        if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION) {
            return "上游实例存在失败";
        }

        if (checkRunInfo.getStatus() == JobCheckStatus.CONSOLE_JOB_RESTRICT) {
            return checkRunInfo.getExtInfo();
        }

        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            return "符合调度条件";
        }

        return "";
    }

    /**
     * 异步重跑任务
     *
     * @param id           需要重跑任务的id
     * @param justRunChild 是否只重跑当前
     * @param setSuccess   是否置成功 true的时候 justRunChild 也为true
     * @param subJobIds    重跑当前任务 subJobIds不为空
     * @return
     */
    public boolean syncRestartJob(Long id, Boolean justRunChild, Boolean setSuccess, List<Long> subJobIds) {
        String key = "syncRestartJob" + id;
        if (redisTemplate.hasKey(key)) {
            LOGGER.info("syncRestartJob  {}  is doing ", key);
            return false;
        }
        String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex((int) environmentContext.getForkJoinResultTimeOut() * 2);
            return commands.set(key, "-1", setParams);
        });
        if(StringUtils.isBlank(execute)){
            return false;
        }
        if (BooleanUtils.isTrue(justRunChild) && null != id) {
            if (null == subJobIds) {
                subJobIds = new ArrayList<>();
            }
            subJobIds.add(id);
        }
        CompletableFuture.runAsync(new RestartRunnable(id, justRunChild, setSuccess, subJobIds, scheduleJobDao, scheduleTaskShadeDao,
                scheduleJobJobDao, environmentContext, key, redisTemplate,this,scheduleJobExpandDao));
        return true;
    }

    /**
     * 异步重跑任务
     *
     * @return
     */
    public boolean restartJob(RestartType restartType, List<String> jobIds,Long operateId) {
        String key = "syncRestartJob_" + JSON.toJSONString(jobIds);
        if (redisTemplate.hasKey(key)) {
            LOGGER.info("syncRestartJob  {}  is doing ", key);
            return false;
        }

        String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex((int) environmentContext.getForkJoinResultTimeOut() * 2);
            return commands.set(key, "-1", setParams);
        });

        if(StringUtils.isBlank(execute)){
            return false;
        }

        CompletableFuture.runAsync(new RestartJobRunnable(jobIds,restartType, scheduleJobDao, scheduleTaskShadeDao,
                scheduleJobJobDao, environmentContext, key, redisTemplate,this,scheduleJobOperateService,operateId));

        return true;
    }


    public Integer stopJobByConditionTotal(ScheduleJobKillJobVO vo) {
        ScheduleJobDTO query = createKillQuery(vo);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        return scheduleJobDao.generalCount(query);
    }

    public Integer stopJobByCondition(ScheduleJobKillJobVO vo) {
        ScheduleJobDTO query = createKillQuery(vo);
        PageQuery<ScheduleJobDTO> pageQuery = new PageQuery<>(query);
        pageQuery.setModel(query);
        query.setPageQuery(false);
        int count = scheduleJobDao.generalCount(query);
        if (count > 0) {
            int pageSize = 50;
            int stopSize = 0;
            if (count > pageSize) {
                //分页查询
                int pageCount = (count / pageSize) + (count % pageSize == 0 ? 0 : 1);
                stopSize = count;
                query.setPageQuery(true);
                for (int i = 0; i < pageCount; i++) {
                    PageQuery<ScheduleJobDTO> finalQuery = new PageQuery<>(query);
                    finalQuery.setModel(query);
                    finalQuery.setPageSize(pageSize);
                    finalQuery.setPage(i + 1);
                    CompletableFuture.runAsync(() -> {
                        try {
                            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(finalQuery);
                            listByJobIdFillFlowSubJobs(scheduleJobs);
                            jobStopDealer.addStopJobs(scheduleJobs,vo.getUserId());
                        } catch (Exception e) {
                            LOGGER.info("stopJobByCondition  {}  error ", JSONObject.toJSONString(finalQuery));
                        }
                    });
                }
            } else {
                List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);
                listByJobIdFillFlowSubJobs(scheduleJobs);
                stopSize = jobStopDealer.addStopJobs(scheduleJobs,vo.getUserId());
            }
            return stopSize;
        }
        return 0;
    }

    /**
     * 填充jobs中的工作流和算法类型任务的子任务
     *
     * @param scheduleJobs
     */
    private void listByJobIdFillFlowSubJobs(List<ScheduleJob> scheduleJobs) {
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return;
        }
        List<String> flowJobIds = scheduleJobs
                .stream()
                .filter(s -> SPECIAL_TASK_TYPES.contains(s.getTaskType()))
                .map(ScheduleJob::getJobId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(flowJobIds)) {
            return;
        }
        List<ScheduleJob> flowSubJobIds = scheduleJobDao.getWorkFlowSubJobId(flowJobIds);
        //将子任务实例加入
        this.distinctAddJobs(scheduleJobs, flowSubJobIds);
    }


    private ScheduleJobDTO createKillQuery(ScheduleJobKillJobVO vo) {
        ScheduleJobDTO ScheduleJobDTO = new ScheduleJobDTO();
        ScheduleJobDTO.setTenantId(vo.getTenantId());
        ScheduleJobDTO.setProjectId(vo.getProjectId());
        ScheduleJobDTO.setType(EScheduleType.NORMAL_SCHEDULE.getType());
        ScheduleJobDTO.setTaskPeriodId(convertStringToList(vo.getTaskPeriodId()));
        ScheduleJobDTO.setAppType(vo.getAppType());
        //筛选任务名称
        ScheduleJobDTO.setTaskIds(vo.getTaskIds());
        setBizDay(ScheduleJobDTO, vo.getBizStartDay(), vo.getBizEndDay(), vo.getTenantId(), vo.getProjectId());
        ScheduleJobDTO.setOwnerUserId(vo.getOwnerUserId());
        ScheduleJobDTO.setOwnerUserIds(vo.getOwnerUserIds());
        //任务状态
        if (StringUtils.isNotBlank(vo.getJobStatuses())) {
            List<Integer> statues = new ArrayList<>();
            String[] statuses = vo.getJobStatuses().split(",");
            // 根据失败状态拆分标记来确定具体是哪一个状态map
            Map<Integer, List<Integer>> statusMap = this.getStatusMap(false);
            for (String status : statuses) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                } else {
                    statues.add(Integer.parseInt(status));
                }
            }
            ScheduleJobDTO.setJobStatuses(statues);
        } else {
            ScheduleJobDTO.setJobStatuses(RdosTaskStatus.getCanStopStatus());
        }
        return ScheduleJobDTO;
    }

    /**
     * 根据规则转换时间
     * @return [{"paramReplace":[{"bdp.system.cyctime":"20200810000000","timeType":"1"}],"jobId":"8f6f5127"}]
     */
    public List<ScheduleJobRuleTimeVO> getJobsRuleTime(List<ScheduleJobRuleTimeVO> jobList) {
        Map<String, List<ScheduleJobRuleTimeVO.RuleTimeVO>> jobRuleMap = jobList.stream().collect(Collectors.toMap(ScheduleJobRuleTimeVO::getJobId,ScheduleJobRuleTimeVO::getParamReplace));
        List<ScheduleJob> scheduleJobList = scheduleJobDao.listByJobIdList(jobRuleMap.keySet(),null);

        List<ScheduleJobRuleTimeVO> results=new ArrayList<>(scheduleJobList.size());
        for (ScheduleJob scheduleJob : scheduleJobList) {
            List<ScheduleJobRuleTimeVO.RuleTimeVO> ruleArray = jobRuleMap.get(scheduleJob.getJobId());
            ScheduleJobRuleTimeVO curJobRuleTime = new ScheduleJobRuleTimeVO();
            curJobRuleTime.setJobId(scheduleJob.getJobId());
            List<ScheduleJobRuleTimeVO.RuleTimeVO> paramReplace = new ArrayList<>(ruleArray.size());

            for (ScheduleJobRuleTimeVO.RuleTimeVO ruleTimeVO : ruleArray) {
                ScheduleJobRuleTimeVO.RuleTimeVO currentRule=new ScheduleJobRuleTimeVO.RuleTimeVO();
                currentRule.setParamName(ruleTimeVO.getParamName());
                currentRule.setTimeType(ruleTimeVO.getTimeType());
                currentRule.setType(ruleTimeVO.getType());
                currentRule.setParamValue(jobParamReplace.convertParam(ruleTimeVO.getType(),
                        ruleTimeVO.getParamName(),ruleTimeVO.getParamValue(), scheduleJob.getCycTime()));
                paramReplace.add(currentRule);
            }
            curJobRuleTime.setParamReplace(paramReplace);
            results.add(curJobRuleTime);
        }
        return results;
    }

    /**
     * 移除满足条件的job 操作记录
     * @param jobIds
     * @param records
     */
    public void removeOperatorRecord(Collection<String> jobIds, Collection<ScheduleJobOperatorRecord> records) {
        Map<String, ScheduleJobOperatorRecord> recordMap = records.stream().collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, k -> k));
        for (String jobId : jobIds) {
            ScheduleJobOperatorRecord record = recordMap.get(jobId);
            if (null == record) {
                continue;
            }

            EngineJobCache cache = engineJobCacheDao.getOne(jobId);
            if (cache != null && cache.getGmtCreate().after(record.getGmtCreate())) {
                //has submit to cache
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] stage:[{}] type:[{}]", record.getJobId(), record.getId(), cache.getGmtCreate(), cache.getStage(), record.getOperatorType());
            }
            ScheduleJob scheduleJob = scheduleJobDao.getByJobId(jobId, Deleted.NORMAL.getStatus());
            if (null == scheduleJob) {
                LOGGER.info("schedule job is null ,remove schedule:[{}] operator record:[{}] type:[{}] ", record.getJobId(), record.getId(), record.getOperatorType());
                scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
            } else if (scheduleJob.getGmtModified().after(record.getGmtCreate())) {
                if (RdosTaskStatus.STOPPED_STATUS.contains(scheduleJob.getStatus()) || RdosTaskStatus.RUNNING.getStatus().equals(scheduleJob.getStatus())) {
                    //has running or finish
                    scheduleJobOperatorRecordDao.deleteByJobIdAndType(record.getJobId(), record.getOperatorType());
                    LOGGER.info("remove schedule:[{}] operator record:[{}] time: [{}] status:[{}] type:[{}]", record.getJobId(), record.getId(), scheduleJob.getGmtModified(), scheduleJob.getStatus(), record.getOperatorType());
                }
            }
        }
    }

    public Integer updateFlowJob(String placeholder, String flowJob) {
        if (StringUtils.isBlank(placeholder)) {
            return 0;
        }

        return scheduleJobDao.updateFlowJob(placeholder, flowJob);
    }

    public String getJobExtraInfoOfValue(String jobId, String key){
        String jobExtraInfo = scheduleJobExpandDao.getJobExtraInfo(jobId);
        JSONObject jobExtraObj = JSONObject.parseObject(jobExtraInfo);
        if (null != jobExtraObj) {
            return jobExtraObj.getString(key);
        }
        return "";
    }

    public String getJobExtraInfoOfValueById(Long expendId, String key){
        String jobExtraInfo = scheduleJobExpandDao.getJobExtraInfoById(expendId);
        JSONObject jobExtraObj = JSONObject.parseObject(jobExtraInfo);
        if (null != jobExtraObj) {
            return jobExtraObj.getString(key);
        }
        return "";
    }

    public OperatorVO restartJobAndResume(List<Long> jobIdList, Boolean runCurrentJob) {
        final OperatorVO<String> batchOperatorVO = new OperatorVO<>();

        final int successNum = 0;
        int failNum = 0;

        for (final Object idStr : jobIdList) {
            try {
                final Long id = com.dtstack.dtcenter.common.util.MathUtil.getLongVal(idStr);
                if (id == null) {
                    throw new RdosDefineException("convert id: " + idStr + " exception.", ErrorCode.SERVER_EXCEPTION);
                }

                final List<Long> subJobIds = new ArrayList<>();
                if (org.apache.commons.lang.BooleanUtils.isTrue(runCurrentJob)){
                    subJobIds.add(id);
                }

                this.syncRestartJob(id, false, false, subJobIds);
            } catch (final Exception e) {
                LOGGER.error("", e);
                failNum++;
            }
        }

        batchOperatorVO.setSuccessNum(successNum);
        batchOperatorVO.setFailNum(failNum);
        batchOperatorVO.setDetail("");
        return batchOperatorVO;
    }


    public Long fillData(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO, FillLimitationDTO fillLimitationDTO) {
        String fillName = scheduleFillJobParticipateVO.getFillName();
        ScheduleFillDataInfoVO fillDataInfo = scheduleFillJobParticipateVO.getFillDataInfo();

        // 生成schedule_fill_data_job数据
        ScheduleFillDataJob fillDataJob = buildScheduleFillDataJob(scheduleFillJobParticipateVO,fillLimitationDTO);
        scheduleFillDataJobDao.insert(fillDataJob);

        // 提交任务
        fillDataThreadPoolExecutor.submit(
                new FillDataRunnable(fillDataJob.getId()
                        , fillName
                        , fillDataInfo.getFillDataType()
                        , fillDataInfo.getIgnoreCycTime()
                        , fillDataInfo.getProjects()
                        , fillDataInfo.getTaskIds()
                        , fillDataInfo.getWhitelist()
                        , fillDataInfo.getBlacklist()
                        , fillDataInfo.getRootTaskId()
                        , scheduleFillJobParticipateVO.getStartDay()
                        , scheduleFillJobParticipateVO.getEndDay()
                        , scheduleFillJobParticipateVO.getBeginTime()
                        , scheduleFillJobParticipateVO.getEndTime()
                        , scheduleFillJobParticipateVO.getProjectId()
                        , scheduleFillJobParticipateVO.getTenantId()
                        , scheduleFillJobParticipateVO.getDtuicTenantId()
                        , scheduleFillJobParticipateVO.getUserId()
                        , FillDataTypeEnum.MANUAL.getType().equals(fillDataInfo.getFillDataType()) ? EScheduleType.MANUAL : EScheduleType.FILL_DATA
                        , applicationContext)
        );
        return fillDataJob.getId();
    }


    public Long enhanceFillData(ScheduleFillJobParticipateEnhanceVO scheduleFillJobParticipateEnhanceVO,FillLimitationDTO fillLimitationDTO) {
        String fillName = scheduleFillJobParticipateEnhanceVO.getFillName();
        ScheduleFillDataInfoEnhanceVO fillDataInfo = scheduleFillJobParticipateEnhanceVO.getFillDataInfo();

        ScheduleFillDataJob fillDataJob = buildScheduleFillDataJob(scheduleFillJobParticipateEnhanceVO,fillLimitationDTO);
        scheduleFillDataJobDao.insert(fillDataJob);


        fillDataThreadPoolExecutor.submit(
                new FillDataEnhanceRunnable(fillDataJob.getId()
                        , fillName
                        , fillDataInfo.getFillDataType()
                        , fillDataInfo.getChooseTaskInfo()
                        , fillDataInfo.getConditionInfo()
                        , fillDataInfo.getRelyInfo()
                        , fillDataInfo.getWhitelist()
                        , fillDataInfo.getBlacklist()
                        , scheduleFillJobParticipateEnhanceVO.getStartDay()
                        , scheduleFillJobParticipateEnhanceVO.getEndDay()
                        , scheduleFillJobParticipateEnhanceVO.getBeginTime()
                        , scheduleFillJobParticipateEnhanceVO.getEndTime()
                        , scheduleFillJobParticipateEnhanceVO.getProjectId()
                        , scheduleFillJobParticipateEnhanceVO.getTenantId()
                        , scheduleFillJobParticipateEnhanceVO.getDtuicTenantId()
                        , scheduleFillJobParticipateEnhanceVO.getUserId()
                        , scheduleFillJobParticipateEnhanceVO.getTaskRunOrder()
                        , scheduleFillJobParticipateEnhanceVO.getCloseRetry()
                        , FillDataTypeEnum.MANUAL.getType().equals(fillDataInfo.getFillDataType()) ? EScheduleType.MANUAL : EScheduleType.FILL_DATA
                        , applicationContext)
        );
        return fillDataJob.getId();
    }

    private ScheduleFillDataJob buildScheduleFillDataJob(ScheduleFillJobParticipateEnhanceVO scheduleFillJobParticipateEnhanceVO,FillLimitationDTO fillLimitationDTO) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();

        scheduleFillDataJob.setFillDataInfo(JSON.toJSONString(scheduleFillJobParticipateEnhanceVO.getFillDataInfo()));
        scheduleFillDataJob.setFillGeneratStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
        scheduleFillDataJob.setFromDay(scheduleFillJobParticipateEnhanceVO.getStartDay());
        scheduleFillDataJob.setToDay(scheduleFillJobParticipateEnhanceVO.getEndDay());
        scheduleFillDataJob.setJobName(scheduleFillJobParticipateEnhanceVO.getFillName());
        scheduleFillDataJob.setMaxParallelNum(environmentContext.getOpenFillLimitation() && scheduleFillJobParticipateEnhanceVO.getMaxParallelNum() == 0 ? fillLimitationDTO.getMaxParallelNum():scheduleFillJobParticipateEnhanceVO.getMaxParallelNum());
        scheduleFillDataJob.setAppType(scheduleFillJobParticipateEnhanceVO.getAppType());
        scheduleFillDataJob.setDtuicTenantId(scheduleFillJobParticipateEnhanceVO.getDtuicTenantId());
        scheduleFillDataJob.setTenantId(scheduleFillJobParticipateEnhanceVO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobParticipateEnhanceVO.getUserId());
        scheduleFillDataJob.setProjectId(scheduleFillJobParticipateEnhanceVO.getProjectId());
        scheduleFillDataJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setNumberParallelNum(0);
        if (scheduleFillJobParticipateEnhanceVO.getTaskRunOrder() == null) {
            scheduleFillDataJob.setTaskRunOrder(0);
        } else {
            scheduleFillDataJob.setTaskRunOrder(scheduleFillJobParticipateEnhanceVO.getTaskRunOrder());
        }
        scheduleFillDataJob.setFillDataType(scheduleFillJobParticipateEnhanceVO.getFillDataInfo().getFillDataType());
        return scheduleFillDataJob;
    }

    private ScheduleFillDataJob buildScheduleFillDataJob(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO,FillLimitationDTO fillLimitationDTO) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();

        scheduleFillDataJob.setFillDataInfo(JSON.toJSONString(scheduleFillJobParticipateVO.getFillDataInfo()));
        scheduleFillDataJob.setFillGeneratStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
        scheduleFillDataJob.setFromDay(scheduleFillJobParticipateVO.getStartDay());
        scheduleFillDataJob.setToDay(scheduleFillJobParticipateVO.getEndDay());
        scheduleFillDataJob.setJobName(scheduleFillJobParticipateVO.getFillName());
        scheduleFillDataJob.setMaxParallelNum(environmentContext.getOpenFillLimitation() && scheduleFillJobParticipateVO.getMaxParallelNum() == 0 ? fillLimitationDTO.getMaxParallelNum() : scheduleFillJobParticipateVO.getMaxParallelNum());
        scheduleFillDataJob.setAppType(scheduleFillJobParticipateVO.getAppType());
        scheduleFillDataJob.setDtuicTenantId(scheduleFillJobParticipateVO.getDtuicTenantId());
        scheduleFillDataJob.setTenantId(scheduleFillJobParticipateVO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobParticipateVO.getUserId());
        scheduleFillDataJob.setProjectId(scheduleFillJobParticipateVO.getProjectId());
        scheduleFillDataJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setNumberParallelNum(0);
        scheduleFillDataJob.setTaskRunOrder(0);
        scheduleFillDataJob.setFillDataType(scheduleFillJobParticipateVO.getFillDataInfo().getFillDataType());
        return scheduleFillDataJob;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createFillJob(Set<String> all, Set<String> run, List<String> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long projectId, Long tenantId, Long dtuicTenantId,
                              Long userId, Boolean ignoreCycTime,EScheduleType scheduleType) throws Exception {
        Date startDate = DateUtil.parseDate(startDay, DateUtil.DATE_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(endDay, DateUtil.DATE_FORMAT, Locale.CHINA);

        DateTime startTime = new DateTime(startDate);
        DateTime finishTime = new DateTime(endDate);
        AtomicInteger count = new AtomicInteger(0);
        while (startTime.getMillis() <= finishTime.getMillis()) {
            startTime = startTime.plusDays(1);
            String triggerDay = startTime.toString(DateUtil.DATE_FORMAT);
            jobGraphBuilder.buildFillDataJobGraph(fillName, fillId, all, run, blackTaskKeyList, triggerDay,
                    beginTime, endTime, projectId, tenantId, dtuicTenantId, userId, ignoreCycTime,scheduleType,null,null,count);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEnhanceFillJob(Set<String> all, Set<String> run, List<String> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long projectId, Long tenantId, Long dtuicTenantId,
                              Long userId,EScheduleType scheduleType,Integer taskRunOrder,Integer closeRetry) throws Exception {
        Date startDate = DateUtil.parseDate(startDay, DateUtil.DATE_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(endDay, DateUtil.DATE_FORMAT, Locale.CHINA);

        DateTime startTime = new DateTime(startDate);
        DateTime finishTime = new DateTime(endDate);
        AtomicInteger count = new AtomicInteger(0);
        while (startTime.getMillis() <= finishTime.getMillis()) {
            startTime = startTime.plusDays(1);
            String triggerDay = startTime.toString(DateUtil.DATE_FORMAT);
            jobGraphBuilder.buildFillDataJobGraph(fillName, fillId, all, run,
                    blackTaskKeyList, triggerDay, beginTime, endTime,
                    projectId, tenantId, dtuicTenantId, userId, null,scheduleType,
                    taskRunOrder,closeRetry,count);
        }
    }

    public PageResult<ScheduleFillDataJobDetailVO> fillDataJobList(FillDataJobListVO vo) {
        Long fillId = vo.getFillId();
        ScheduleFillDataJob scheduleFillDataJob = scheduleFillDataJobDao.getById(fillId);

        if (scheduleFillDataJob == null) {
            throw new RdosDefineException("fillId:" + fillId + " does not exist");
        }

        ScheduleFillDataJobDetailVO dataJobDetailVO = new ScheduleFillDataJobDetailVO();
        Integer totalCount = 0;

        // 判断补数据是否完成
        if (FillGeneratStatusEnum.REALLY_GENERATED.getType().equals(scheduleFillDataJob.getFillGeneratStatus())) {
            dataJobDetailVO.setFillGeneratStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
            dataJobDetailVO.setMsg(FillGeneratStatusEnum.REALLY_GENERATED.getName());
            return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
        } else if (FillGeneratStatusEnum.FILL_FAIL.getType().equals(scheduleFillDataJob.getFillGeneratStatus())) {
            dataJobDetailVO.setFillGeneratStatus(FillGeneratStatusEnum.FILL_FAIL.getType());
            dataJobDetailVO.setMsg(scheduleFillDataJob.getFillDataFailure());
            return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
        } else if (FillGeneratStatusEnum.FILL_FAIL_LIMIT.getType().equals(scheduleFillDataJob.getFillGeneratStatus())) {
            dataJobDetailVO.setFillGeneratStatus(FillGeneratStatusEnum.FILL_FAIL.getType());
            dataJobDetailVO.setMsg(String.format(FillGeneratStatusEnum.FILL_FAIL_LIMIT.getName(),environmentContext.getFillDataCreateLimitSize()));
            return new PageResult<>(vo.getCurrentPage(), vo.getPageSize(), totalCount, dataJobDetailVO);
        }

        FillDataQueryDTO fillDataQueryDTO = parameterHandler(vo);
        Set<Long> allTaskIds = Sets.newHashSet();
        if (StringUtils.isNotBlank(vo.getTaskName())) {
            List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByNameLikeWithSearchType(null, CheckUtils.handlerStr(vo.getTaskName()), vo.getAppType(), null, null, SearchTypeEnum.getTypeByName(vo.getSearchType()),null,null);
            if (CollectionUtils.isNotEmpty(batchTaskShades)) {
                allTaskIds.addAll(batchTaskShades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList()));
            } else {
                return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
            }
        }

        List<Long> tagIds = vo.getTagIds();
        if (CollectionUtils.isNotEmpty(tagIds)) {
            List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(tagIds,vo.getAppType());

            if (CollectionUtils.isEmpty(tagsByTagIds)) {
                return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
            }

            Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));

            boolean flg = Boolean.TRUE;
            for (Long task : tagMap.keySet()) {
                List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(task);
                List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());

                // tagIds 是否是taskTagIds的子集
                if (ListUtil.isSubList(taskTagIds,tagIds)) {
                    allTaskIds.add(task);
                    flg= Boolean.FALSE;
                }
            }

            if (flg) {
                return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
            }
        }

        fillDataQueryDTO.setTaskIds(Lists.newArrayList(allTaskIds));
        // 查询数据
        totalCount = scheduleJobDao.pageFillDateCount(fillDataQueryDTO);
        if (totalCount > 0) {
            List<ScheduleJob> scheduleJobListWithFillData = scheduleJobDao.pageFillDate(fillDataQueryDTO);

            Map<Long, ScheduleTaskForFillDataDTO> taskShadeMap = this.prepareForFillDataDetailInfo(scheduleJobListWithFillData);
            if (CollectionUtils.isNotEmpty(scheduleJobListWithFillData)) {
                List<Long> resourceIds = scheduleJobListWithFillData.stream().map(ScheduleJob::getResourceId).collect(Collectors.toList());
                List<Long> taskIds = scheduleJobListWithFillData.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());

                Map<Long, ResourceGroupDetail> resourceGroupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
                List<ScheduleTaskTag> tags = scheduleTaskTagService.findTagByTaskIds(Sets.newHashSet(taskIds), vo.getAppType());
                Map<Long, List<Long>> taskTags = tags.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId,
                        Collectors.mapping(ScheduleTaskTag::getTagId, Collectors.toList())));


                for (ScheduleJob job : scheduleJobListWithFillData) {
                    if (RdosTaskStatus.RUNNING_TASK_RULE.getStatus().equals(job.getStatus())) {
                        job.setStatus(RdosTaskStatus.RUNNING.getStatus());
                    }
                    ScheduleFillDataJobDetailVO.FillDataRecord fillDataRecord = transferBatchJob2FillDataRecord(job, vo.getFlowJobIdList(), taskShadeMap, resourceGroupDetailMap);
                    fillDataRecord.setTagIds(taskTags.get(job.getTaskId()));
                    dataJobDetailVO.addRecord(fillDataRecord);
                }
                dealFlowWorkSubJobsInFillData(dataJobDetailVO.getRecordList(),resourceGroupDetailMap);
            }
        }

        dataJobDetailVO.setFillGeneratStatus(FillGeneratStatusEnum.FILL_FINISH.getType());
        dataJobDetailVO.setFillDataJobName(scheduleFillDataJob.getJobName());
        return new PageResult<>(vo.getCurrentPage(),vo.getPageSize(),totalCount,dataJobDetailVO);
    }

    private FillDataQueryDTO parameterHandler( FillDataJobListVO vo) {
        FillDataQueryDTO fillDataQueryDTO = scheduleJobStruct.toFillDataQueryDTO(vo);

        // 处理封装时间
        Long bizEndDay = vo.getBizEndDay();
        Long bizStartDay = vo.getBizStartDay();
        if (bizEndDay != null && bizStartDay != null) {
            fillDataQueryDTO.setCycStart(dayFormatterAll.print(getTime(bizStartDay * 1000,-1).getTime()));
            fillDataQueryDTO.setCycEnd(dayFormatterAll.print(getTime(bizEndDay * 1000,-2).getTime()));
        }

        // 处理任务类型
        String taskType = vo.getTaskType();
        if (StringUtils.isNotBlank(taskType)) {
            String[] taskTypeArray = taskType.split(",");
            fillDataQueryDTO.setTaskTypeList(Lists.newArrayList(taskTypeArray));
        }

        // 处理状态类型
        String jobStatuses = vo.getJobStatuses();
        if (StringUtils.isNotBlank(jobStatuses)) {
            List<Integer> statues = new ArrayList<>();
            String[] jobStatusesArray = jobStatuses.split(",");
            Map<Integer, List<Integer>> statusMap = getStatusMap(vo.getSplitFiledFlag());
            for (String status : jobStatusesArray) {
                List<Integer> statusList = statusMap.get(new Integer(status));
                if (CollectionUtils.isNotEmpty(statusList)) {
                    statues.addAll(statusList);
                } else {
                    //不在失败状态拆分里
                    statues.add(Integer.parseInt(status));
                }
            }
            fillDataQueryDTO.setJobStatusesList(statues);
        }

        // 设置查询部署类型
        fillDataQueryDTO.setFillTypes(Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(),FillJobTypeEnum.RUN_JOB.getType()));
        fillDataQueryDTO.setResourceIds(vo.getResourceIds());
        return fillDataQueryDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchRestartScheduleJob(Map<String,String> resumeBatchJobs) {
        if (MapUtils.isNotEmpty(resumeBatchJobs)) {
            List<String> restartJobId = new ArrayList<>(resumeBatchJobs.size());
            resumeBatchJobs.entrySet()
                    .stream()
                    .sorted(Comparator.nullsFirst(Map.Entry.comparingByValue(Comparator.nullsFirst(String::compareTo))))
                    .forEachOrdered(v -> {
                        if (null!= v && StringUtils.isNotBlank(v.getKey())) {
                            restartJobId.add(v.getKey());
                        }
                    });
            List<List<String>> partition = Lists.partition(restartJobId, environmentContext.getRestartOperatorRecordMaxSize());
            List<List<String>> partitionRecord = Lists.newArrayList();
            for (List<String> scheduleJobs : partition) {
                HashSet<String> jobIds = new HashSet<>(scheduleJobs);
                List<Integer> status = Lists.newArrayList(RdosTaskStatus.STOPPED_STATUS);
                status.add(RdosTaskStatus.UNSUBMIT.getStatus());
                HashSet<String> newJobIds = scheduleJobDao.listJobIdByJobIdAndStatus(jobIds, status);

                if (CollectionUtils.isNotEmpty(newJobIds)) {
                    scheduleJobGanttTimeService.batchClear(newJobIds);

                    // 进行一次任务诊断并保存记录
                    List<String> jobIdList = Lists.newArrayList(newJobIds);
                    insertExpandRestart(jobIdList);
                    scheduleJobDao.updateJobStatusAndPhaseStatus(jobIdList, RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus(), environmentContext.getLocalAddress(), null);
                    scheduleJobRelyCheckService.deleteByJobIds(jobIdList);
                    LOGGER.info("reset job {}", jobIdList);
                    partitionRecord.add(jobIdList);
                }
            }

            for (List<String> scheduleJobs : partitionRecord) {
                Set<ScheduleJobOperatorRecord> records = new HashSet<>(scheduleJobs.size());
                //更新任务为重跑任务--等待调度器获取并执行
                for (String jobId : scheduleJobs) {
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobId);
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.RESTART.getType());
                    record.setNodeAddress(environmentContext.getLocalAddress());
                    records.add(record);
                }
                scheduleJobOperatorRecordDao.insertBatch(records);
            }
        }
    }

    /**
     * 添加重跑记录到 expand 表中
     *
     * @param jobIds 重跑的实例 id
     */
    private void insertExpandRestart(List<String> jobIds) {
        List<ScheduleJobExpand> scheduleJobExpands = scheduleJobExpandDao.getSimplifyExpandByJobIds(jobIds);

        if (CollectionUtils.isEmpty(scheduleJobExpands)) {
            return;
        }
        Map<String, List<ScheduleJobExpand>> expandMaps = scheduleJobExpands.stream().collect(Collectors.groupingBy(ScheduleJobExpand::getJobId));
        List<ScheduleJobExpand> insertExpands = Lists.newArrayList();

        for (Map.Entry<String, List<ScheduleJobExpand>> entry : expandMaps.entrySet()) {
            List<ScheduleJobExpand> scheduleJobExpandList = entry.getValue();
            ScheduleJobExpand scheduleJobExpand = getMaxExpand(scheduleJobExpandList);
            ScheduleJobExpand insertExpand = new ScheduleJobExpand();
            insertExpand.setRunNum(scheduleJobExpand.getRunNum() + 1);
            insertExpand.setJobId(scheduleJobExpand.getJobId());
            insertExpand.setRetryTaskParams("");
            insertExpand.setJobExtraInfo("");
            insertExpand.setEngineLog("");
            insertExpand.setLogInfo("");
            insertExpands.add(insertExpand);
        }

        for (String jobId : jobIds) {
            try {
                List<JobDiagnosisInformationVO> diagnosis = jobDiagnosisChain.diagnosis(jobId);
                String diagnosisJson = JSON.toJSONString(diagnosis);
                scheduleJobExpandDao.updateJobDiagnosisInfo(jobId, diagnosisJson);
            } catch (Exception e) {
                LOGGER.error("get job diagnosis error, jobId: {}", jobId, e);
            }
        }

        if (CollectionUtils.isNotEmpty(insertExpands)) {
            scheduleJobExpandDao.batchInsertIgnore(insertExpands);
        }
    }

    /**
     * 获取最近的重跑记录
     *
     * @param scheduleJobExpandList 所有重跑记录
     * @return 最近的重跑记录
     */
    public ScheduleJobExpand getMaxExpand(List<ScheduleJobExpand> scheduleJobExpandList) {
        return scheduleJobExpandList.stream().max(Comparator.comparing(ScheduleJobExpand::getRunNum)).orElse(null);
    }


    public List<RestartJobInfoVO> restartJobInfo(String jobKey, Long parentTaskId, Integer level) {
        if (level==null) {
            level = 1;
        }
        List<ScheduleJobJob> scheduleJobJobList = batchJobJobService.getJobChild(jobKey);
        List<String> jobKeyList = Lists.newArrayList();
        List<RestartJobInfoVO> batchJobList = Lists.newArrayList();

        String parentJobDayStr = getJobTriggerTimeFromJobKey(jobKey);
        if (Strings.isNullOrEmpty(parentJobDayStr)
                || CollectionUtils.isEmpty(scheduleJobJobList)) {
            return batchJobList;
        }

        for (ScheduleJobJob scheduleJobJob : scheduleJobJobList) {
            //排除自依赖
            String childJobKey = scheduleJobJob.getJobKey();
            Long taskShadeIdFromJobKey = getTaskShadeIdFromJobKey(childJobKey);
            ScheduleTaskShade taskShade = batchTaskShadeService.getById(taskShadeIdFromJobKey);
            if (null != taskShade && taskShade.getTaskId().equals(parentTaskId)) {
                continue;
            }

            //排除不是同一天执行的
            if (!parentJobDayStr.equals(getJobTriggerTimeFromJobKey(childJobKey))) {
                continue;
            }

            jobKeyList.add(scheduleJobJob.getJobKey());
        }

        if (CollectionUtils.isEmpty(jobKeyList)) {
            return batchJobList;
        }

        List<ScheduleJob> jobList = scheduleJobDao.listJobByJobKeys(jobKeyList);
        if (CollectionUtils.isNotEmpty(jobList)) {
            for (ScheduleJob childScheduleJob : jobList) {

                //判断job 对应的task是否被删除
                ScheduleTaskShade jobRefTask = batchTaskShadeService.getBatchTaskById(childScheduleJob.getTaskId(), childScheduleJob.getAppType());
                Integer jobStatus = childScheduleJob.getStatus();
                jobStatus = jobStatus == null ? RdosTaskStatus.UNSUBMIT.getStatus() : jobStatus;

                String taskName = jobRefTask == null ? null : jobRefTask.getName();

                RestartJobInfoVO restartJobVO = new RestartJobInfoVO();
                restartJobVO.setId(childScheduleJob.getId());
                restartJobVO.setJobId(childScheduleJob.getJobId());
                restartJobVO.setJobKey(childScheduleJob.getJobKey());
                restartJobVO.setJobStatus(jobStatus);
                restartJobVO.setCycTime(childScheduleJob.getCycTime());
                restartJobVO.setTaskType(childScheduleJob.getTaskType());
                restartJobVO.setTaskName(taskName);
                restartJobVO.setTaskId(childScheduleJob.getTaskId());
                restartJobVO.setScheduleStatus(jobRefTask.getScheduleStatus());

                if (level > 0) {
                    List<RestartJobInfoVO> restartJobInfoVOS = restartJobInfo(childScheduleJob.getJobKey(), childScheduleJob.getTaskId(), level - 1);
                    restartJobVO.setChilds(restartJobInfoVOS);
                }
                batchJobList.add(restartJobVO);
            }
        }

        return batchJobList;
    }

    public PageResult queryStreamJobs(PageQuery<ScheduleJobDTO> pageQuery) {
        int count = scheduleJobDao.generalCount(pageQuery.getModel());
        if (count > 0) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.generalQuery(pageQuery);
            List convertList;
            List<Long> taskShadeIds = scheduleJobs.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listSimpleByTaskIds(taskShadeIds, Deleted.NORMAL.getStatus(), pageQuery.getModel().getAppType());
            Map<Long, ScheduleTaskShade> taskShadeMapping = scheduleTaskShades.stream()
                    .collect(Collectors.groupingBy(ScheduleTaskShade::getTaskId, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));

            Stream<Long> modifyStream = scheduleTaskShades.stream().map(ScheduleTask::getModifyUserId);
            Stream<Long> ownStream = scheduleTaskShades.stream().map(ScheduleTask::getOwnerUserId);

            List<User> userInfo = userService.findUserWithFill(Stream.concat(modifyStream,ownStream).collect(Collectors.toSet()));
            Map<Long, User> userMapping = userInfo.stream()
                    .collect(Collectors.groupingBy(User::getDtuicUserId, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));

            convertList = scheduleJobs.stream().filter(s -> taskShadeMapping.get(s.getTaskId()) != null).map(s -> {
                ScheduleTaskShade taskShade = taskShadeMapping.get(s.getTaskId());
                User modifyUser = userMapping.get(taskShade.getModifyUserId());
                User ownUser = userMapping.get(taskShade.getOwnerUserId());
                return scheduleJobStruct.toStreamJob(s,taskShade,ownUser,modifyUser);
            }).collect(Collectors.toList());
            return new PageResult<>(pageQuery.getPage(), pageQuery.getPageSize(), count, convertList);
        }
        return new PageResult<>(pageQuery.getPage(), pageQuery.getPageSize(), count, null);
    }

    public List<StatusCount> queryStreamJobsStatusStatistics(ScheduleJobDTO scheduleJobDTO) {
        return scheduleJobDao.getJobsStatusStatistics(scheduleJobDTO);
    }

    public void addOrUpdateScheduleJob(ScheduleJob scheduleJob) {
        ScheduleJob dbJob = scheduleJobDao.getByJobId(scheduleJob.getJobId(), null);
        if (null == dbJob) {
            scheduleJobDao.insert(scheduleJob);
        } else {
            scheduleJob.setStatus(dbJob.getStatus());
            scheduleJobDao.update(scheduleJob);
        }
    }

    /**
     * @param jobIds
     * @return
     */
    public List<ScheduleSqlTextDTO> querySqlText(List<String> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Collections.emptyList();
        }
        List<ScheduleJob> jobs = scheduleJobDao.listByJobIdList(jobIds, null);
        List<ScheduleSqlTextDTO> result = new ArrayList<>(jobs.size());
        for (ScheduleJob scheduleJob : jobs) {
            ScheduleTaskShade taskShade = null;
            String sqlText;
            String engineType = null;
            String jobId = scheduleJob.getJobId();
            if (EScheduleType.TEMP_JOB.getType().equals(scheduleJob.getType())) {
                //临时运行
                ScheduleSqlTextTemp sqlTextTemp = sqlTextTempDao.selectByJobId(jobId);
                if (null == sqlTextTemp) {
                    LOGGER.warn("can not find sqlTextTemp, jobId:{}", jobId);
                    continue;
                }
                sqlText = sqlTextTemp.getSqlText();
                engineType = sqlTextTemp.getEngineType();
            } else {
                Long taskId = scheduleJob.getTaskId();
                if (taskId == -1L) {
                    // 临时运行 taskId 都是 -1
                    LOGGER.warn("job error, jobId:{}", jobId);
                    continue;
                }
                taskShade = scheduleTaskShadeDao.getOneByTaskIdAndAppType(taskId, scheduleJob.getAppType());
                if (null == taskShade) {
                    LOGGER.warn("task shade not exists, jobId:{}", jobId);
                    continue;
                }
                String extraInfo = taskShade.getExtraInfo();
                JSONObject jsonObject = JSONObject.parseObject(extraInfo);

                JSONObject taskInfoJson = Optional.ofNullable(TaskUtils.getActualTaskExtraInfo(jsonObject)).orElse(new JSONObject());

                // 区分数据同步任务
                if (EScheduleJobType.SYNC.getType().equals(taskShade.getTaskType())) {
                    sqlText = taskInfoJson.getString("job");
                } else {
                    sqlText = taskInfoJson.getString("sqlText");

                    List<ScheduleTaskParamShade> totalTaskParamShade = new ArrayList<>();
                    actionService.fillConsoleParam(taskInfoJson, scheduleJob.getTaskId(), scheduleJob.getAppType(), jobId);
                    paramService.convertGlobalToParamType(JSONObject.toJSONString(taskInfoJson), (globalTaskParams, selfTaskParams) -> {
                        List<ScheduleTaskParamShade> globalTaskShades = paramStruct.BOtoTaskParams(globalTaskParams);
                        totalTaskParamShade.addAll(globalTaskShades);
                        totalTaskParamShade.addAll(selfTaskParams);
                    });

                    //统一替换下sql
                    sqlText = jobParamReplace.paramReplace(sqlText, totalTaskParamShade, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
                }
            }
            ScheduleSqlTextDTO sqlTextDTO = new ScheduleSqlTextDTO();
            sqlTextDTO.setSqlText(sqlText);
            sqlTextDTO.setJobId(scheduleJob.getJobId());
            sqlTextDTO.setVersionId(scheduleJob.getVersionId());
            sqlTextDTO.setEngineType(engineType);
            result.add(sqlTextDTO);
        }
        return result;
    }

    public ScheduleJob getJobRangeByCycTimeLimitOne(Long taskId, Integer appType, boolean isAfter, String cycTime, Integer scheduleType,Long fillId) {
        List<ScheduleJob> jobRangeByCycTimeByLimit = scheduleJobDao.getJobRangeByCycTimeByLimit(taskId, isAfter, cycTime, appType, scheduleType, 1,fillId);
        if (CollectionUtils.isNotEmpty(jobRangeByCycTimeByLimit)) {
            return jobRangeByCycTimeByLimit.get(0);
        }
        return null;
    }


    public void insertIgnoreOperator(String jobId, OperatorType operatorType, String nodeAddress) {
        ScheduleJobOperatorRecord operatorRecord = new ScheduleJobOperatorRecord();
        operatorRecord.setJobId(jobId);
        operatorRecord.setOperatorType(operatorType.getType());
        operatorRecord.setNodeAddress(nodeAddress);
        operatorRecord.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
        scheduleJobOperatorRecordDao.insertIgnore(operatorRecord);
    }

    private long getTimeOutParam(EScheduleJobType scheduleJobType, ScheduleTaskShade batchTask) {
        Long timeout = environmentContext.getTaskTimeout();
        switch (scheduleJobType) {
            case NOT_DO_TASK:
                String scheduleConf = batchTask.getScheduleConf();
                JSONObject confJson = JSONObject.parseObject(scheduleConf);
                if (confJson != null && confJson.getLong(GlobalConst.TASK_RULE_TIMEOUT) != null) {
                    timeout = confJson.getLong(GlobalConst.TASK_RULE_TIMEOUT);
                }
                return timeout;
            case EVENT:
                JSONObject sqlText = JSONObject.parseObject(batchTask.getSqlText());
                if (sqlText != null && sqlText.getLong(GlobalConst.TIMEOUT) != null) {
                    //配置单位分钟
                    timeout = sqlText.getLong(GlobalConst.TIMEOUT);
                    timeout = timeout * 60 * 1000;
                }
                return timeout;
            default:
                return timeout;
        }
    }

    public boolean isTimeOut(ScheduleJob scheduleJob, ScheduleTaskShade batchTask, Consumer<Long> dealTimeOut) {
        EScheduleJobType scheduleJobType = EScheduleJobType.getEJobType(scheduleJob.getTaskType());
        if (scheduleJobType == null) {
            throw new RdosDefineException("task type is null");
        }
        long timeout = getTimeOutParam(scheduleJobType, batchTask);
        Timestamp execStartTime = scheduleJob.getExecStartTime();

        if (execStartTime == null) {
            // 在尝试获取一次时间
            execStartTime = getJobExecStartTime(scheduleJob.getJobId());
            if (execStartTime == null) {
                throw new RdosDefineException("not find execStartTime");
            }
        }

        long time = execStartTime.getTime();
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - time) > timeout) {
            dealTimeOut.accept(currentTimeMillis - time);
            return true;
        }
        return false;
    }

    public JSONObject getSqlText(boolean isTemp, Long taskId, Integer appType, String jobId) {
        if (!isTemp) {
            ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeDao.getOne(taskId, appType);
            if (scheduleTaskShade == null) {
                return null;
            }
            return JSONObject.parseObject(scheduleTaskShade.getSqlText());
        } else {
            ScheduleSqlTextTemp scheduleSqlTextTemp = sqlTextTempDao.selectByJobId(jobId);
            if (null == scheduleSqlTextTemp) {
                return null;
            }
            return JSONObject.parseObject(scheduleSqlTextTemp.getSqlText());
        }
    }



    /**
     * 递归查找当前jobId下的子任务
     *
     * @param jobId           jobId
     * @param isOnlyNextChild 是否只查询一级子任务
     * @return key：子任务 jobId, value：执行时间
     */
    public Map<String, String> getAllChildJobWithSameDayByForkJoin(String jobId, boolean isOnlyNextChild) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
        ForkJoinJobTask forkJoinJobTask = new ForkJoinJobTask(jobId, results, scheduleJobDao, scheduleJobJobDao, isOnlyNextChild,scheduleJobOperateService);
        ForkJoinTask<Map<String, String>> submit = forkJoinPool.submit(forkJoinJobTask);
        try {
            return submit.get(environmentContext.getForkJoinResultTimeOut(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("get all child job {} error ", jobId, e);
        }
        return null;
    }

    /**
     * 获取可以运行的子 taskId 集合
     *
     * @param jobId 实例 id
     * @return 可以运行的子 taskId 集合
     */
    public List<Long> getRunSubTaskId(String jobId) {
        String runSubTaskId = scheduleJobExpandDao.getRunSubTaskIdByJobId(jobId);
        if (StringUtils.isNotBlank(runSubTaskId)) {
            return Arrays.stream(runSubTaskId.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<ScheduleJob> getJobRangeByCycTime(Long taskId, String startTime, String endTime, Integer appType) {
        return scheduleJobDao.getJobRangeByCycTime(taskId, startTime, endTime, appType);
    }
    public List<String> getRunningTaskLogUrl(RollingJobLogParam rollingJobLogParam) {
        String jobId = rollingJobLogParam.getJobId();
        if (StringUtils.isEmpty(jobId)) {
            throw new RdosDefineException(ErrorCode.JOB_ID_CAN_NOT_EMPTY);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        Integer status = scheduleJob.getStatus();
        if (!RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(scheduleJob.getTaskId(), scheduleJob.getAppType());
            if (batchTask != null) {
                EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(batchTask.getTaskParams(), batchTask.getComputeType(), EngineType.Flink.name(), scheduleJob.getDtuicTenantId());
                if (eDeployMode == EDeployMode.PERJOB) {
                    return Collections.emptyList();
                }
                // 非 perjob 模式可以获取到日志 -- session 模式实际上只要 session 不 kill, runlog 都是能获取的
                return getRunningTaskLogUrlIfNotPerjob(scheduleJob, batchTask, eDeployMode);
            } else {
                // 临时运行离线没提交任务的兼容处理
                Integer taskType = scheduleJob.getTaskType();
                if (!EScheduleJobType.SYNC.getType().equals(taskType)) {
                    return Collections.emptyList();
                }
                Integer deployMode = rollingJobLogParam.getDeployMode();
                if (deployMode == null) {
                    throw new RdosDefineException("sync deployMode empty");
                }
                EDeployMode eDeployMode = EDeployMode.getByType(deployMode);
                if (eDeployMode != EDeployMode.SESSION) {
                    return Collections.emptyList();
                }
                ScheduleTaskShade mockBatchTask = new ScheduleTaskShade();
                mockBatchTask.setEngineType(com.dtstack.dtcenter.common.enums.EngineType.Flink.getVal());
                mockBatchTask.setOwnerUserId(scheduleJob.getCreateUserId());
                mockBatchTask.setResourceId(scheduleJob.getResourceId());
                return getRunningTaskLogUrlIfNotPerjob(scheduleJob, mockBatchTask, eDeployMode);
            }
        }
        // perjob 只获取运行中的任务的 log—url
        String engineJobId = scheduleJob.getEngineJobId();
        if (StringUtils.isEmpty(engineJobId)) {
            throw new RdosDefineException(String.format("job:%s engineJobId is empty ", jobId), ErrorCode.JOB_CACHE_NOT_EXIST);
        }
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            throw new RdosDefineException(String.format("job:%s not exist in job cache table ", jobId), ErrorCode.JOB_CACHE_NOT_EXIST);
        }

        JobClient jobClient = null;
        JobIdentifier jobIdentifier = null;
        ClientTypeEnum clientType = null;
        String engineType = engineJobCache.getEngineType();

        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            clientType = ClientTypeEnum.getClientTypeEnum(paramAction.getClientType());
            Map<String, Object> pluginInfo = paramAction.getPluginInfo();
            jobIdentifier = new JobIdentifier(engineJobId, scheduleJob.getApplicationId(), jobId, scheduleJob.getDtuicTenantId(), engineType,
                    taskParamsService.parseDeployTypeByTaskParams(paramAction.getTaskParams(), engineJobCache.getComputeType(), engineJobCache.getEngineType(), scheduleJob.getDtuicTenantId()).getType(),
                    paramAction.getUserId(), paramAction.getResourceId(), MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo), paramAction.getComponentVersion());
            jobIdentifier.setProjectId(paramAction.getProjectId());
            jobIdentifier.setAppType(paramAction.getAppType());
            jobIdentifier.setTaskType(paramAction.getTaskType());
            jobIdentifier.setClassArgs(paramAction.getExeArgs());
            jobIdentifier.setArchiveFsDir(getJobExtraInfoOfValue(jobIdentifier.getTaskId(), JobResultConstant.ARCHIVE));
            jobClient = JobClientUtil.conversionJobClient(paramAction);
            return operatorDistributor.getOperator(clientType, engineType).getRollingLogBaseInfo(jobIdentifier);
        } catch (RdosDefineException e) {
            throw e;
        } catch (Throwable e) {
            if (jobClient != null) {
                RdosTaskStatus jobStatus = operatorDistributor.getOperator(clientType, engineType).getJobStatus(jobIdentifier, null);
                Integer statusCode = jobStatus.getStatus();
                if (RdosTaskStatus.getStoppedStatus().contains(statusCode)) {
                    throw new RdosDefineException(String.format("job:%s had stopped ", jobId), ErrorCode.INVALID_TASK_STATUS, e);
                }
            }
            throw new RdosDefineException(String.format("job:%s getRunningTaskLogUrl error", jobId), ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    public List<JobStopProcessVO> listStopProcess(List<String> jobIds) {
        List<ScheduleJobOperatorRecord> stopRecords = scheduleJobOperatorRecordDao.listOperatorByJobIdsAndType(jobIds, Lists.newArrayList(OperatorType.STOP.getType(), OperatorType.CONSOLE_STOP.getType()));
        List<ScheduleJob> simpleInfoByJobIds = scheduleJobDao.getSimpleInfoByJobIds(jobIds);
        Map<String, Integer> jobMap = simpleInfoByJobIds.stream()
                .collect(Collectors.toMap(ScheduleJob::getJobId, ScheduleJob::getStatus));
        Map<String, ScheduleJobOperatorRecord> stopMap = stopRecords.stream()
                .collect(Collectors.toMap(ScheduleJobOperatorRecord::getJobId, Function.identity()));
        return jobMap.keySet().stream().map(s -> {
            JobStopProcessVO stopProcessVO = new JobStopProcessVO();
            ScheduleJobOperatorRecord operatorRecord = stopMap.get(s);
            stopProcessVO.setContinueStop(null != operatorRecord);
            if (null != operatorRecord) {
                stopProcessVO.setStopTime(operatorRecord.getGmtCreate());
                String hasTry = redisTemplate.opsForValue().get(GlobalConst.STOP + s);
                stopProcessVO.setHasRetry(NumberUtils.toInt(hasTry, 0));
            }
            stopProcessVO.setJobId(s);
            stopProcessVO.setMaxRetry(environmentContext.getJobStoppedRetry());
            stopProcessVO.setStatus(jobMap.get(s));
            return stopProcessVO;
        }).collect(Collectors.toList());
    }

    @PostConstruct
    public void refreshEngineJobCacheTaskName() {
        // 刷新名称
        List<String> jobIds = engineJobCacheDao.getJobIdWithTaskNameIsNull();
        for (String jobId : jobIds) {
            try {
                String taskName = convertTaskName(jobId);
                engineJobCacheDao.updateTaskName(jobId, taskName);
                LOGGER.info("update engineJobCache taskName as {}, jobId is :{}", taskName, jobId);
            } catch (Exception e) {
                LOGGER.error("refresh engine job cache task name error, jobId is :{}", jobId);
            }
        }
    }

    /**
     * 转换 jobName 为子应用任务名称
     *
     * @param jobId        实例 id
     * @return 子应用任务名称
     */
    public String convertTaskName(String jobId) {
        ScheduleJob scheduleJob = getByJobId(jobId, Deleted.NORMAL.getStatus());
        if (Objects.isNull(scheduleJob)) {
            return "notExists_" + jobId;
        }
        return convertTaskName(scheduleJob.getJobName(), scheduleJob.getAppType(), scheduleJob.getProjectId(), scheduleJob.getJobId(), scheduleJob.getType());
    }

    /**
     * 转换 jobName 为子应用任务名称
     *
     * @param jobName      任务名称
     * @param appType      应用类型
     * @param projectId    项目 id
     * @param jobId        实例 id
     * @param scheduleType 调度类型
     * @return 子应用任务名称
     */
    public String convertTaskName(String jobName, Integer appType, Long projectId, String jobId, Integer scheduleType) {
        if (Objects.isNull(scheduleType)) {
            return jobName;
        }
        EScheduleType scheduleTypeEnum = EScheduleType.getScheduleType(scheduleType);
        try {
            switch (scheduleTypeEnum) {
                case TEMP_JOB:
                    // 判断是否是 stream
                    if (AppType.STREAM.getType().equals(appType)) {
                        // 查询项目名称
                        AuthProjectVO project = workSpaceProjectService.finProject(projectId, AppType.STREAM.getType());
                        return jobName.substring(project.getProjectName().length() + 1, jobName.length() - (jobId.length() + 1));
                    } else {
                        return "-";
                    }
                case MANUAL:
                case FILL_DATA:
                    // 切分中划线
                    String[] splitArr = jobName.split("-");
                    if (splitArr.length > 2) {
                        return splitArr[splitArr.length - 2];
                    }
                    return jobName;
                case NORMAL_SCHEDULE:
                    return jobName.substring(JobGraphBuilder.CRON_JOB_NAME.length() + 1, jobName.length() - (ScheduleCron.TRIGGER_TIME_LENGTH + 1));
                default:
                    return jobName;
            }
        } catch (Exception e) {
            LOGGER.error("convert task name error, jobName: {}, scheduleType: {}, jobId: {}", jobId, scheduleType, jobId);
            return jobName;
        }
    }

    /**
     * 针对非 perjob 模式获取日志
     * @param scheduleJob
     * @param batchTask
     * @param eDeployMode
     * @return
     */
    private List<String> getRunningTaskLogUrlIfNotPerjob(ScheduleJob scheduleJob, ScheduleTaskShade batchTask, EDeployMode eDeployMode) {
        JobIdentifier jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getJobId(),
                scheduleJob.getDtuicTenantId(),
                com.dtstack.dtcenter.common.enums.EngineType.getEngineType(batchTask.getEngineType()).getEngineName(),
                eDeployMode.getType(),
                batchTask.getOwnerUserId(), batchTask.getResourceId(), null, batchTask.getComponentVersion());
        jobIdentifier.setProjectId(scheduleJob.getProjectId());
        jobIdentifier.setAppType(scheduleJob.getAppType());
        jobIdentifier.setTaskType(scheduleJob.getTaskType());
        jobIdentifier.setClassArgs(batchTask.getExeArgs());
        jobIdentifier.setArchiveFsDir(getJobExtraInfoOfValue(scheduleJob.getJobId(), JobResultConstant.ARCHIVE));
        return enginePluginsOperator.getRollingLogBaseInfo(jobIdentifier);
    }
}
