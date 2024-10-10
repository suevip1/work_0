package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IYarn;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoOverView;
import com.dtstack.dtcenter.loader.dto.trinoResource.TrinoResouceDTO;
import com.dtstack.dtcenter.loader.dto.yarn.YarnResourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.AppTenantEntity;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.master.enums.EModuleAppTypeMapping;
import com.dtstack.engine.master.faced.sdk.PublicServiceUicSubProductModuleApiClientSdkFaced;
import com.dtstack.engine.po.ConsoleTenantComponent;
import com.dtstack.engine.po.EngineJobCache;
import com.dtstack.engine.po.JobGraphTrigger;
import com.dtstack.engine.api.domain.ScheduleDict;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobOperatorRecord;
import com.dtstack.engine.dto.GroupOverviewDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.console.ConsoleJobInfoVO;
import com.dtstack.engine.api.vo.console.ConsoleJobVO;
import com.dtstack.engine.api.vo.project.ProjectNameVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.ConsoleTenantComponentDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.JobGraphTriggerDao;
import com.dtstack.engine.dao.ScheduleDictDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.bo.GroupOverviewBO;
import com.dtstack.engine.master.config.TaskResourceBeanConfig;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.mapstruct.PlugInStruct;
import com.dtstack.engine.master.mapstruct.ProjectStruct;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.queue.GroupPriorityQueue;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.dtstack.engine.master.utils.JobClientUtil;
import com.dtstack.engine.master.vo.AddOrUpdateTenantComponentVO;
import com.dtstack.engine.master.vo.AlertConfigVO;
import com.dtstack.engine.master.vo.GroupDetailPageVO;
import com.dtstack.engine.master.vo.GroupOverviewVO;
import com.dtstack.engine.master.vo.JobGenerationVO;
import com.dtstack.engine.master.vo.NotYetKilledJobCountVO;
import com.dtstack.engine.master.vo.ProductNameVO;
import com.dtstack.engine.master.vo.QueryNotYetKilledJobCountVO;
import com.dtstack.engine.master.vo.StopJobResult;
import com.dtstack.engine.master.vo.TaskTypeResourceTemplateVO;
import com.dtstack.engine.master.vo.UserNameVO;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UIcUserTenantRelApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicTenantApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.SubProductModuleVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 对接数栈控制台
 * <p>
 * 代码engine中内存队列的类型名字
 * <p>
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/18
 */
@Service
public class ConsoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleService.class);

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobOperatorRecordDao engineJobStopRecordDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private JobGraphTriggerDao jobGraphTriggerDao;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleDictDao dictDao;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private PlugInStruct plugInStruct;

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UIcUserTenantRelApiClient uIcUserTenantRelApiClient;

    @Autowired
    private UicTenantApiClient uicTenantApiClient;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ConsoleTenantComponentDao consoleTenantComponentDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private WorkSpaceProjectService workSpaceProjectService;

    @Autowired
    private ProjectStruct projectStruct;

    @Autowired
    private ClusterTenantDao clusterTenantDao;

    @Autowired
    private PublicServiceUicSubProductModuleApiClientSdkFaced publicServiceUicSubProductModuleApiClientSdkFaced;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    @Autowired
    private UserGroupService userGroupService;

    /**
     * 需要校验 url 格式的数据源列表
     */
    private static final List<Integer> URL_CHECK_COMPONENT_LIST = Lists.newArrayList(
            EComponentType.HIVE_SERVER.getTypeCode(),
            EComponentType.SPARK_THRIFT.getTypeCode(),
            EComponentType.TIDB_SQL.getTypeCode(),
            EComponentType.MYSQL.getTypeCode(),
            EComponentType.IMPALA_SQL.getTypeCode()
    );

    public Boolean finishJob(String jobId, Integer status) {
        if (!RdosTaskStatus.isStopped(status)) {
            LOGGER.warn("Job status：" + status + " is not stopped status");
            return false;
        }
        shardCache.updateLocalMemTaskStatus(jobId, status);
        engineJobCacheDao.delete(jobId);
        scheduleJobDao.updateJobStatus(jobId, status);
        LOGGER.info("jobId:{} update job status:{}, job is finished.", jobId, status);
        return true;
    }

    public List<String> nodeAddress() {
        try {
            return zkService.getAliveBrokersChildren();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public ConsoleJobVO searchJob(String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        String jobId = null;
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobName);
        if (scheduleJob != null) {
            jobId = scheduleJob.getJobId();
        }
        if (jobId == null) {
            return null;
        }
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            ApiResponse<UICTenantVO> tenantDetail = uicTenantApiClient.findTenantById(scheduleJob.getDtuicTenantId());
            String tenantName = tenantDetail.getData() == null ? "" : tenantDetail.getData().getTenantName();
            ConsoleJobInfoVO consoleJobInfoVO = this.fillJobInfo(paramAction, scheduleJob, engineJobCache, tenantName);
            ConsoleJobVO vo = new ConsoleJobVO();
            vo.setTheJob(consoleJobInfoVO);
            vo.setNodeAddress(engineJobCache.getNodeAddress());
            vo.setTheJobIdx(1);
            return vo;
        } catch (Exception e) {
            LOGGER.error("searchJob error:", e);
        }
        return null;
    }

    public List<String> listNames( String jobName) {
        try {
            Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
            return engineJobCacheDao.listNames(jobName);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public List<String> jobResources() {
        return engineJobCacheDao.getJobResources();
    }

    /**
     * 根据计算引擎类型显示任务
     */
    public List<GroupOverviewVO> overview(String nodeAddress,
                                          String clusterName,
                                          Long tenantId,
                                          UserDTO user,
                                          Boolean isAllAuth) {
        Preconditions.checkNotNull(clusterName, "clusterName can not be null.");
        List<Long> tenantIds = Lists.newArrayList();
        List<GroupOverviewVO> groupOverviewVOS = Lists.newArrayList();
        if (!isAllAuth) {
            tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
        }

        List<Long> clusterTenantIds = clusterTenantDao.getTenantIdsByClusterName(clusterName);
        if (CollectionUtils.isNotEmpty(tenantIds)) {
            tenantIds.retainAll(clusterTenantIds);
        }

        if (tenantId != null) {
            if (!tenantIds.contains(tenantId)) {
                return groupOverviewVOS;
            } else {
                List<GroupOverviewDTO> groupOverviewDTOS = engineJobCacheDao.getGroupOverviews(nodeAddress, Lists.newArrayList(tenantId));
                return processGroupOverviews(groupOverviewDTOS, nodeAddress);
            }
        } else {
            List<GroupOverviewDTO> groupOverviewDTOS = engineJobCacheDao.getGroupOverviews(nodeAddress, tenantIds);
            return processGroupOverviews(groupOverviewDTOS, nodeAddress);
        }
    }

    private List<GroupOverviewVO> processGroupOverviews(List<GroupOverviewDTO> groupOverviewDTOS, String nodeAddress) {
        List<GroupOverviewVO> vos = new ArrayList<>();

        if (CollectionUtils.isEmpty(groupOverviewDTOS)) {
            return vos;
        }

        // 先根据 jobResource 聚合一下，合并 stage
        Collection<GroupOverviewBO> groupByJobResourceOverviewBOs = groupByJobResource(groupOverviewDTOS);

        for (GroupOverviewBO bo: groupByJobResourceOverviewBOs) {
            GroupOverviewVO vo  = new GroupOverviewVO();
            vo.setJobResource(bo.getJobResource());
            vo.setNotYetKilledJobCount(getNotYetKilledJobCount(bo.getJobResource(), nodeAddress));
            vo.setOverviewContents(getOverviewContent(bo.getGroupOverviewDTOs()));
            vos.add(vo);
        }

        return vos;
    }

    private List<GroupOverviewVO.OverviewContent> getOverviewContent(List<GroupOverviewDTO> groupOverviewDTOs) {
        List<GroupOverviewVO.OverviewContent> contents = new ArrayList<>();
        if (CollectionUtils.isEmpty(groupOverviewDTOs)) {
            return contents;
        }

        for (GroupOverviewDTO dto:groupOverviewDTOs) {
            GroupOverviewVO.OverviewContent content = new GroupOverviewVO.OverviewContent();
            Integer stage = dto.getStage();
            content.setStage(EJobCacheStage.getStage(stage));
            content.setWaitTime(dto.getWaitTime());
            content.setJobSize(dto.getJobSize());
            contents.add(content);
        }

        return contents;
    }

    private Integer getNotYetKilledJobCount(String jobResource, String nodeAddress) {
        return getNotYetKilledJobCount(jobResource, nodeAddress, null);
    }

    private Integer getNotYetKilledJobCount(String jobResource,
                                            String nodeAddress,
                                            Integer stage) {

        List<String> existJobCacheIds =
                engineJobCacheDao.listJobIdByJobResourceAndNodeAddressAndStages(jobResource, nodeAddress, stage);

        return getNotYetKilledJobCount(existJobCacheIds);
    }

    private Integer getNotYetKilledJobCount(List<String> existJobCacheIds) {
        List<String> stopJobCacheIds = filterStopJobOperateRecord(existJobCacheIds);
        if (CollectionUtils.isEmpty(stopJobCacheIds)) {
            return null;
        }
        return stopJobCacheIds.size();
    }


    private Collection<GroupOverviewBO> groupByJobResource(List<GroupOverviewDTO> groupOverviewDTOS) {

        Map<String, GroupOverviewBO> helper = new HashMap<>();

        for (GroupOverviewDTO dto: groupOverviewDTOS) {
            String jobResource = dto.getJobResource();

            GroupOverviewBO groupOverviewBO = helper.get(jobResource);
            if (Objects.nonNull(groupOverviewBO)) {
                groupOverviewBO.getGroupOverviewDTOs().add(dto);
            } else {
                GroupOverviewBO bo = new GroupOverviewBO();
                bo.setJobResource(jobResource);
                bo.setGroupOverviewDTOs(Lists.newArrayList(dto));
                helper.put(jobResource, bo);
            }
        }

        return helper.values();
    }

    private List<String> filterStopJobOperateRecord(List<String> existJobCacheIds) {
        if (CollectionUtils.isEmpty(existJobCacheIds)) {
            return existJobCacheIds;
        }
        return engineJobStopRecordDao.listConsoleStopJobIdsByJobIds(existJobCacheIds);
    }

    public PageResult groupDetail(String jobResource,
                                  String nodeAddress,
                                  Integer stage,
                                  Integer pageSize,
                                  Integer currentPage,
                                  UserDTO user,
                                  String taskName,
                                  Long projectId,
                                  Boolean isAllAuth) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(stage, "parameters of stage is required");
        Preconditions.checkArgument(currentPage != null && currentPage > 0, "parameters of currentPage is required");
        Preconditions.checkArgument(pageSize != null && pageSize > 0, "parameters of pageSize is required");
        List<Long> tenantIds = Lists.newArrayList();
        if (!isAllAuth) {
            tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
        }

        if (StringUtils.isBlank(nodeAddress)) {
            nodeAddress = null;
        }
        GroupDetailPageVO data = new GroupDetailPageVO();
        List<Map<String, Object>> pageData = new ArrayList<>();
        Long count = 0L;
        int start = (currentPage - 1) * pageSize;
        try {
            count = engineJobCacheDao.countByJobResource(jobResource, stage, nodeAddress, projectId, taskName,tenantIds);
            if (count > 0) {
                List<EngineJobCache> engineJobCaches = engineJobCacheDao.listByJobResource(jobResource, stage, nodeAddress, start, pageSize, projectId, taskName,tenantIds);
                List<String> jobIds = engineJobCaches.stream().map(EngineJobCache::getJobId).collect(Collectors.toList());
                List<ScheduleJob> rdosJobByJobIds = scheduleJobDao.getRdosJobByJobIds(jobIds);
                Integer appType = rdosJobByJobIds.stream().findFirst().map(AppTenantEntity::getAppType).orElse(null);
                Map<String, ScheduleJob> scheduleJobMap = rdosJobByJobIds.stream().collect(Collectors.toMap(ScheduleJob::getJobId, u -> u));
                List<Long> dtuicTenantIds = rdosJobByJobIds.stream().map(ScheduleJob::getDtuicTenantId).collect(Collectors.toList());
                List<Long> projectIds = rdosJobByJobIds.stream().map(ScheduleJob::getProjectId).distinct().collect(Collectors.toList());

                Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(dtuicTenantIds);
                // 填充项目名称信息
                Map<Long, AuthProjectVO> projectMap = workSpaceProjectService.findProjects(appType, projectIds).stream().collect(Collectors.toMap(AuthProjectVO::getProjectId, v -> v, (v1, v2) -> v2));
                Map<String,String> pluginInfoCache = new HashMap<>();
                for (EngineJobCache engineJobCache : engineJobCaches) {
                    Map<String, Object> theJobMap = PublicUtil.objectToMap(engineJobCache);
                    ScheduleJob scheduleJob = scheduleJobMap.getOrDefault(engineJobCache.getJobId(), new ScheduleJob());
                    // 补充租户信息
                    TenantDeletedVO tenant = tenantMap.get(scheduleJob.getDtuicTenantId());
                    // 补充项目信息
                    AuthProjectVO project = projectMap.get(scheduleJob.getProjectId());
                    this.fillJobInfo(theJobMap, scheduleJob, engineJobCache, tenant == null ? "" : tenant.getTenantName(), Objects.isNull(project) ? "" : project.getProjectAlias(), pluginInfoCache);
                    pageData.add(theJobMap);
                }
                // 分页数据
                data.setPageData(pageData);
                // 还未杀死的 job 数量
                data.setNotYetKilledJobCount(getNotYetKilledJobCount(jobIds));
            }
        } catch (Exception e) {
            LOGGER.error("groupDetail error", e);
        }
        PageQuery pageQuery = new PageQuery<>(currentPage, pageSize);
        return new PageResult<>(data,count.intValue(),pageQuery);
    }


    /**
     * 根据条件获取可选的项目列表
     *
     * @param jobResource 资源队列
     * @param projectName 项目名称模糊搜索
     * @param stage       任务阶段
     * @param nodeAddress 调度地址
     * @return 项目列表
     */
    public List<ProjectNameVO> getProjectOptions(String jobResource, String projectName, Integer stage, String nodeAddress) {
        List<ProjectNameVO> result = Lists.newArrayList();
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(stage, "parameters of stage is required");
        List<String> jobIds = engineJobCacheDao.listJobIdByJobResourceAndNodeAddressAndStages(jobResource, nodeAddress, stage);
        if (CollectionUtils.isEmpty(jobIds)) {
            return result;
        }
        List<Long> projectIds = scheduleJobDao.getDistinctProjectByJobIds(jobIds);
        if (CollectionUtils.isEmpty(projectIds)) {
            return result;
        }

        Integer appType = getAppType(jobResource);
        if (Objects.isNull(appType)) {
            return result;
        }

        List<AuthProjectVO> projects = workSpaceProjectService.findProjects(appType, projectIds);
        if (CollectionUtils.isEmpty(projects)) {
            LOGGER.warn("get projects from public service is empty, appType: {}, projectIds: {}", appType, projects);
            return result;
        }
        List<AuthProjectVO> filterProjects = projects.stream()
                .filter(projectVo -> !StringUtils.isNotBlank(projectName) || StringUtils.containsIgnoreCase(projectVo.getProjectAlias(), projectName))
                .collect(Collectors.toList());
        return projectStruct.toProjectNameVo(filterProjects);
    }

    public Integer getAppType(String jobResource) {
        EngineJobCache resource = engineJobCacheDao.getOneByJobResource(jobResource);
        if (Objects.isNull(resource)) {
            return null;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getByJobId(resource.getJobId(), Deleted.NORMAL.getStatus());
        return Objects.isNull(scheduleJob) ? null : scheduleJob.getAppType();
    }

    private void fillJobInfo(Map<String, Object> theJobMap, ScheduleJob scheduleJob, EngineJobCache engineJobCache, String tenantName, String projectName, Map<String,String> pluginInfoCache) {
        theJobMap.put("status", scheduleJob.getStatus());
        theJobMap.put("execStartTime", scheduleJob.getExecStartTime());
        theJobMap.put("generateTime", engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        theJobMap.put("waitTime", waitTime);
        theJobMap.put("waitReason", engineJobCache.getWaitReason());
        theJobMap.put("tenantName", tenantName);
        theJobMap.put("projectName", projectName);
        theJobMap.put("scheduleType", scheduleJob.getType());
        theJobMap.put("hasKillPermission", true);
        String jobInfo = (String) theJobMap.get("jobInfo");
        JSONObject jobInfoJSON = JSONObject.parseObject(jobInfo);
        if (null == jobInfoJSON) {
            jobInfoJSON = new JSONObject();
        }

        if (!jobInfoJSON.containsKey(ConfigConstant.PLUGIN_INFO)) {
            JSONObject pluginInfo = pluginInfoManager.buildTaskPluginInfo(
                    scheduleJob.getProjectId(),
                    scheduleJob.getAppType(),
                    scheduleJob.getTaskType(),
                    jobInfoJSON.getString("taskParams"),
                    engineJobCache.getEngineType(),
                    null == tenantName ? -1L : scheduleJob.getDtuicTenantId(),
                    jobInfoJSON.getLong("userId"),
                    scheduleJob.getResourceId(),
                    jobInfoJSON.getString("componentVersion"));

            jobInfoJSON.put(ConfigConstant.PLUGIN_INFO, pluginInfo);
            theJobMap.put("jobInfo", jobInfoJSON);
        }

        Integer appType = scheduleJob.getAppType();
        Long taskId = scheduleJob.getTaskId();
        Integer priority = scheduleTaskPriorityService.selectPriorityByTaskId(taskId, appType);
        theJobMap.put("priority",priority);
    }

    private ConsoleJobInfoVO fillJobInfo(ParamAction paramAction, ScheduleJob scheduleJob, EngineJobCache engineJobCache, String tenant) {
        ConsoleJobInfoVO infoVO = new ConsoleJobInfoVO();
        infoVO.setStatus(scheduleJob.getStatus());
        infoVO.setExecStartTime(scheduleJob.getExecStartTime());
        infoVO.setGenerateTime(engineJobCache.getGmtCreate());
        long currentTime = System.currentTimeMillis();
        String waitTime = DateUtil.getTimeDifference(currentTime - engineJobCache.getGmtCreate().getTime());
        infoVO.setWaitTime(waitTime);
        infoVO.setTenantName(tenant);
        infoVO.setParamAction(paramAction);
        return infoVO;
    }

    public Boolean jobStick( String jobId) {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");

        try {
            EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
            if(null == engineJobCache){
                return false;
            }
            //只支持DB、PRIORITY两种调整顺序
            if (EJobCacheStage.DB.getStage() == engineJobCache.getStage()
                    || EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = JobClientUtil.conversionJobClient(paramAction);
                jobClient.setCallBack((jobStatus) -> {
                    jobDealer.updateJobStatus(jobClient.getTaskId(), jobStatus);
                });

                Long minPriority = engineJobCacheDao.minPriorityByStage(engineJobCache.getJobResource(), Lists.newArrayList(EJobCacheStage.PRIORITY.getStage()), engineJobCache.getNodeAddress());
                minPriority = minPriority == null ? 0 : minPriority;
                jobClient.setPriority(minPriority - 1);

                if (EJobCacheStage.PRIORITY.getStage() == engineJobCache.getStage()) {
                    //先将队列中的元素移除，重复插入会被忽略
                    GroupPriorityQueue groupPriorityQueue = jobDealer.getGroupPriorityQueue(engineJobCache.getJobResource());
                    groupPriorityQueue.remove(jobClient);
                }
                return jobDealer.addGroupPriorityQueue(engineJobCache.getJobResource(), jobClient, false, false);
            }
        } catch (Exception e) {
            LOGGER.error("jobStick error:", e);
        }
        return false;
    }

    public void stopJob(String jobId, Integer isForce){
        stopJob(jobId, isForce, false);
    }

    public void stopJob(String jobId, Integer isForce, boolean isTimeout){
        Preconditions.checkArgument(StringUtils.isNotBlank(jobId), "parameters of jobId is required");
        List<String> alreadyExistJobIds = engineJobStopRecordDao.listStopRecordByJobIds(Lists.newArrayList(jobId));
        if (alreadyExistJobIds.contains(jobId)) {
            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
            return;
        }

        ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
        stopRecord.setJobId(jobId);
        stopRecord.setForceCancelFlag(isForce);
        stopRecord.setOperatorType(isTimeout ? OperatorType.TIMEOUT_STOP.getType() : OperatorType.STOP.getType());
        engineJobStopRecordDao.insert(stopRecord);

    }


    public StopJobResult stopJobOfAuth(String jobResource,
                                       String nodeAddress,
                                       List<String> jobIdList,
                                       Integer isForce,
                                       Boolean isAllAuth,
                                       UserDTO user) throws Exception {
        List<Long> tenantIds = Lists.newArrayList();
        StopJobResult stopAllJobResult = new StopJobResult();
        if (!isAllAuth) {
            tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
        }

        if (CollectionUtils.isNotEmpty(jobIdList)) {
            // 批量杀死任务
            List<ScheduleJob> scheduleJobs = scheduleJobDao.listByJobIdList(jobIdList, null);

            for (ScheduleJob scheduleJob : scheduleJobs) {
                if (CollectionUtils.isNotEmpty(tenantIds) && !tenantIds.contains(scheduleJob.getTenantId())) {
                    throw new RdosDefineException("无权限杀死任务");
                }
            }

            Integer count = stopJobList(null, null, null, jobIdList, tenantIds, isForce, user);
            stopAllJobResult.setNotYetKilledJobCount(count);
        } else {
            Integer count = 0;
            for (EJobCacheStage eJobCacheStage : EJobCacheStage.values()) {
                count += stopJobList(jobResource, nodeAddress, eJobCacheStage.getStage(), Lists.newArrayList(), tenantIds, isForce, user);
            }
            stopAllJobResult.setNotYetKilledJobCount(count);
        }
        return stopAllJobResult;
    }


    /**
     * 作废 新接口 stopJobOfAuth
     * @param jobId
     * @throws Exception
     */
    @Deprecated
    public void stopJob( String jobId) throws Exception {
        stopJob(jobId , ForceCancelFlag.YES.getFlag());
    }


    /**
     * 作废 新接口 stopJobOfAuth
     */
    @Deprecated
    public StopJobResult stopAll(String jobResource,
                                 String nodeAddress,
                                 UserDTO user) throws Exception {
        StopJobResult stopAllJobResult = new StopJobResult();
        AtomicReference<Integer> allCount = new AtomicReference<>(0);
        for (EJobCacheStage eJobCacheStage : EJobCacheStage.values()) {
            StopJobResult stopJobResult = stopJobList(jobResource, nodeAddress, eJobCacheStage.getStage(), null, user);
            Optional.ofNullable(stopJobResult).map(StopJobResult::getNotYetKilledJobCount).map(c -> allCount.updateAndGet(v -> v + c));
        }
        stopAllJobResult.setNotYetKilledJobCount(allCount.get());
        return stopAllJobResult;
    }

    public Integer stopJobList(String jobResource,
                            String nodeAddress,
                            Integer stage,
                            List<String> jobIdList,
                            List<Long> tenantIds,
                            Integer isForce,
                            UserDTO user){
        if (CollectionUtils.isNotEmpty(jobIdList)) {
            //杀死指定jobIdList的任务

            if (EJobCacheStage.unSubmitted().contains(stage)) {
                Integer deleted = engineJobCacheDao.deleteByJobIds(jobIdList);
                Integer updated = scheduleJobDao.updateJobStatusByJobIds(jobIdList, RdosTaskStatus.CANCELED.getStatus());
                LOGGER.info("delete job size:{}, update job size:{}, deal jobIds:{}", deleted, updated, jobIdList);
                if (user != null && StringUtils.isNotEmpty(user.getUserName())) {
                    scheduleJobExpandService.asyncAddKillLog(jobIdList, user.getUserName(), new Date(),RdosTaskStatus.CANCELED.getStatus());
                }
            } else {
                List<String> alreadyExistJobIds = engineJobStopRecordDao.listStopRecordByJobIds(jobIdList);
                List<String> sendStopJobIds = new ArrayList<>();
                for (String jobId : jobIdList) {
                    if (alreadyExistJobIds.contains(jobId)) {
                        LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobId);
                        continue;
                    }

                    ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
                    stopRecord.setJobId(jobId);
                    stopRecord.setForceCancelFlag(isForce);
                    stopRecord.setOperatorType(OperatorType.CONSOLE_STOP.getType());
                    engineJobStopRecordDao.insert(stopRecord);
                    sendStopJobIds.add(jobId);
                }
                if (CollectionUtils.isNotEmpty(sendStopJobIds)) {
                    if (user != null && StringUtils.isNotEmpty(user.getUserName())) {
                        scheduleJobExpandService.asyncAddKillLog(jobIdList, user.getUserName(), new Date(),null);
                    }
                }
            }
            return jobIdList.size();
        } else {
            int allKillCount = 0;
            //根据条件杀死所有任务
            Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
            Preconditions.checkNotNull(stage, "parameters of stage is required");

            if (StringUtils.isBlank(nodeAddress)) {
                nodeAddress = null;
            }

            long startId = 0L;
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, nodeAddress, stage,tenantIds, jobResource);
                allKillCount += jobCaches.size();
                if (CollectionUtils.isEmpty(jobCaches)) {
                    //两种情况：
                    //1. 可能本身没有jobcaches的数据
                    //2. master节点已经为此节点做了容灾
                    break;
                }
                List<String> jobIds = new ArrayList<>(jobCaches.size());
                for (EngineJobCache jobCache : jobCaches) {
                    startId = jobCache.getId();
                    jobIds.add(jobCache.getJobId());
                }

                if (EJobCacheStage.unSubmitted().contains(stage)) {
                    Integer deleted = engineJobCacheDao.deleteByJobIds(jobIds);
                    Integer updated = scheduleJobDao.updateJobStatusByJobIds(jobIds, RdosTaskStatus.CANCELED.getStatus());
                    LOGGER.info("delete job size:{}, update job size:{}, query job size:{}, jobIds:{}", deleted, updated, jobCaches.size(), jobIds);
                    if (user != null && StringUtils.isNotEmpty(user.getUserName())) {
                        scheduleJobExpandService.asyncAddKillLog(jobIds, user.getUserName(), new Date(),RdosTaskStatus.CANCELED.getStatus());
                    }
                } else {
                    //已提交的任务需要发送请求杀死，走正常杀任务的逻辑
                    List<String> alreadyExistJobIds = engineJobStopRecordDao.listStopRecordByJobIds(jobIds);
                    List<String> sendStopJobIds = new ArrayList<>();
                    for (EngineJobCache jobCache : jobCaches) {
                        startId = jobCache.getId();
                        if (alreadyExistJobIds.contains(jobCache.getJobId())) {
                            LOGGER.info("jobId:{} ignore insert stop record, because is already exist in table.", jobCache.getJobId());
                            continue;
                        }

                        ScheduleJobOperatorRecord stopRecord = new ScheduleJobOperatorRecord();
                        stopRecord.setJobId(jobCache.getJobId());
                        stopRecord.setForceCancelFlag(isForce);
                        stopRecord.setOperatorType(OperatorType.CONSOLE_STOP.getType());
                        engineJobStopRecordDao.insert(stopRecord);
                        sendStopJobIds.add(jobCache.getJobId());
                    }
                    if (CollectionUtils.isNotEmpty(sendStopJobIds)) {
                        if (user != null && StringUtils.isNotEmpty(user.getUserName())) {
                            scheduleJobExpandService.asyncAddKillLog(jobIdList, user.getUserName(), new Date(),null);
                        }
                    }
                }
            }
            return allKillCount;
        }
    }

    /**
     * 作废 新接口 stopJobOfAuth
     */
    @Deprecated
    public StopJobResult stopJobList( String jobResource,
                             String nodeAddress,
                             Integer stage,
                             List<String> jobIdList,
                             UserDTO user) throws Exception {
        if (null != user.getRootUser() && 1 == user.getRootUser()) {
            // 管理员
            if (!environmentContext.getOpenAdminKillPermission()) {
                Integer stopCount = stopJobList(jobResource, nodeAddress, stage, jobIdList, null, ForceCancelFlag.YES.getFlag(), user);
                StopJobResult stopJobResult = new StopJobResult();
                stopJobResult.setNotYetKilledJobCount(Optional.ofNullable(stopCount).orElse(0));
                return stopJobResult;
            }
        }

        List<Long> tenantIds = uIcUserTenantRelApiClient.findTenantByUserIdNoLimit(user.getDtuicUserId()).getData();
        if (CollectionUtils.isEmpty(tenantIds)) {
            throw new RdosDefineException("无权限杀死全部任务");
        }

        List<EngineJobCache> engineJobCaches = engineJobCacheDao.findByJobResourceNoTenant(jobResource, tenantIds);
        if (engineJobCaches.size() > 0) {
            throw new RdosDefineException("无权限杀死全部任务");
        }
        Integer stopCount = stopJobList(jobResource, nodeAddress, stage, jobIdList, null, ForceCancelFlag.YES.getFlag(), user);
        StopJobResult stopJobResult = new StopJobResult();
        stopJobResult.setNotYetKilledJobCount(Optional.ofNullable(stopCount).orElse(0));
        return stopJobResult;
    }

    public ClusterResource clusterResources(String clusterName, Map<Integer, String> componentVersionMap, Long dtuicTenantId) {
        if (StringUtils.isEmpty(clusterName)) {
            return new ClusterResource();
        }

        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        JSONObject yarnInfo = clusterService.getYarnInfo(cluster.getId(),dtuicTenantId,null);
        if(MapUtils.isEmpty(yarnInfo)){
            return null;
        }
        String typeName = yarnInfo.getString(ConfigConstant.TYPE_NAME_KEY);
        try {
            Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.YARN);
            yarnInfo.put(ConfigConstant.DATASOURCE_TYPE, dataSourceCodeByDiceName);
            IYarn yarn = ClientCache.getYarn(dataSourceCodeByDiceName);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(yarnInfo.toJSONString(), dtuicTenantId);
            YarnResourceDTO yarnResource = yarn.getYarnResource(sourceDTO);
            return plugInStruct.yarnResourceDTOtoClusterResource(yarnResource);
        } catch (Exception e) {
            LOGGER.error("getResources error: ", e);
            throw new RdosDefineException("getResources error.", e);
        }
    }


    /**
     * 获取trino资源组
     * @param clusterId
     * @param dtuicTenantId
     * @return
     */
    public List<TrinoResouceDTO> clusterTrinoResources(Long clusterId,Long dtuicTenantId) {


        JSONObject trinoInfo = clusterService.getTrinoInfo(clusterId,dtuicTenantId,null);
        if(MapUtils.isEmpty(trinoInfo)){
            return null;
        }
        try {
            Integer dataType = DataSourceType.TRINO.getVal();
            trinoInfo.put(ConfigConstant.DATASOURCE_TYPE, dataType);
            IClient client = ClientCache.getClient(dataType);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(trinoInfo.toJSONString(), dtuicTenantId);
            return client.getAllTrinoResourceGroup(sourceDTO);
        } catch (Exception e) {
            LOGGER.error("getTrinoResources error: ", e);
            throw new RdosDefineException("getTrinoResources error.", e);
        }
    }

    /**
     * 获取trino资源组
     * @param clusterId
     * @param dtuicTenantId
     * @return
     */
    public TrinoOverView getTrinoOverView(Long clusterId,Long dtuicTenantId) {


        JSONObject trinoInfo = clusterService.getTrinoInfo(clusterId,dtuicTenantId,null);
        if(MapUtils.isEmpty(trinoInfo)){
            return null;
        }
        try {
            Integer dataType = DataSourceType.TRINO.getVal();
            trinoInfo.put(ConfigConstant.DATASOURCE_TYPE, dataType);
            IClient client = ClientCache.getClient(dataType);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(trinoInfo.toJSONString(), dtuicTenantId);
            return client.getTrinoOverView(sourceDTO);
        } catch (Exception e) {
            LOGGER.error("getTrinoOverView error: ", e);
            throw new RdosDefineException("getTrinoOverView error.", e);
        }
    }

    /**
     * 绑定用户组到trino资源组或解除绑定
     * @param clusterId
     * @param dtuicTenantId
     * @param bindType 0绑定，1解除绑定，2换绑
     * @return
     */
    public Boolean bindTrinoResource(Long clusterId,Long dtuicTenantId,String resourceName,List<Long> groupIds,Integer bindType) {


        JSONObject trinoInfo = clusterService.getTrinoInfo(clusterId,dtuicTenantId,null);
        if(MapUtils.isEmpty(trinoInfo)){
            return null;
        }
        //获取ldap用户组
        List<String> ldapGroupNames = userGroupService.getLdapUserGroupByGroupIds(groupIds, dtuicTenantId);
        try {
            Integer dataType = DataSourceType.TRINO.getVal();
            trinoInfo.put(ConfigConstant.DATASOURCE_TYPE, dataType);
            IClient client = ClientCache.getClient(dataType);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(trinoInfo.toJSONString(), dtuicTenantId);
            for (String ldapGroupName : ldapGroupNames) {
                return client.bindResourceGroup(sourceDTO,Arrays.asList(resourceName.split("-")),ldapGroupName,bindType);
            }
        } catch (Exception e) {
            LOGGER.error("bindTrinoResources error: ", e);
            throw new RdosDefineException("bindTrinoResources error.", e);
        }
        return true;
    }

    /**
    * @author zyd
    * @Description 获取任务类型及对应的资源模板
    * @Date 8:13 下午 2020/10/14
    * @Param []
    * @retrun java.util.List<com.dtstack.engine.master.vo.TaskTypeResourceTemplate>
    **/
    public List<TaskTypeResourceTemplateVO> getTaskResourceTemplate() {

        return TaskResourceBeanConfig.templateList;
    }

    public List<JobGenerationVO> listJobGenerationRecord(Integer status) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime end = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime start = end.minusDays(30);
        List<JobGraphTrigger> triggers = jobGraphTriggerDao.listByTriggerTime(
                Timestamp.from(start.toInstant()), Timestamp.from(end.toInstant()));
        Map<ZonedDateTime, List<JobGraphTrigger>> triggerMap = new HashMap<>();
        for (JobGraphTrigger trigger : triggers) {
            ZonedDateTime triggerDay = ZonedDateTime.ofInstant(
                    trigger.getTriggerTime().toInstant(), ZoneId.systemDefault()
            ).truncatedTo(ChronoUnit.DAYS);

            List<JobGraphTrigger> triggersInOneDay = triggerMap.computeIfAbsent(triggerDay, k -> new ArrayList<>());
            triggersInOneDay.add(trigger);
        }

        List<JobGenerationVO> res = new ArrayList<>();
        // 判断第二天实例是否已生成，已生成就显示第二天，否则仍显示前一天
        boolean tomorrowIsOk = CollectionUtils.isNotEmpty(jobGraphTriggerDao.listByTriggerTime(Timestamp.from(end.toInstant()), Timestamp.from(end.toInstant())));
        ZonedDateTime executeDay = tomorrowIsOk ? end : end.minusDays(1);
        // 从实际调度时间开始
        JobGraphTrigger startTrigger = jobGraphTriggerDao.getStartTriggerTime();
        if (startTrigger != null){
            ZonedDateTime startTime = ZonedDateTime.ofInstant(
                    startTrigger.getTriggerTime().toInstant(), ZoneId.systemDefault()
            ).truncatedTo(ChronoUnit.DAYS);
            // start 如果早于实际调度开始时间，就重新赋值 start 为实际调度开始时间
            start = start.isBefore(startTime) ? startTime : start;
        }

        while (!executeDay.isBefore(start)) {
            List<JobGraphTrigger> triggersInOneDay = triggerMap.get(executeDay);
            if (CollectionUtils.isEmpty(triggersInOneDay)) {
                JobGenerationVO vo = new JobGenerationVO();
                vo.setDay(Date.from(executeDay.toInstant()));
                vo.setStatus(0);
                res.add(vo);
            } else {
                for (JobGraphTrigger t : triggersInOneDay) {
                    JobGenerationVO vo = new JobGenerationVO();
                    // 周期实例执行日期
                    vo.setDay(Date.from(executeDay.toInstant()));
                    // 周期实例执行时间
                    vo.setDayTime(Date.from(t.getTriggerTime().toInstant()));
                    // 周期实例生成时间
                    vo.setCreateTime(Date.from(t.getGmtCreate().toInstant()));
                    // 状态
                    vo.setStatus(1);
                    res.add(vo);
                }
            }
            executeDay = executeDay.minusDays(1);
        }
        if (status == null) {
            return res;
        } else if (status.equals(0)) {
            return res.stream().filter(t -> t.getStatus().equals(0)).collect(Collectors.toList());
        } else {
            return res.stream().filter(t -> t.getStatus().equals(1)).collect(Collectors.toList());
        }
    }


    public List<UserNameVO> getUser(Long dtUicTenantId, List<Long> userIds) {
        //获取uic下该租户所有用户
        List<Map<String, Object>> uicUsers = dtUicUserConnect.getAllUicUsers(environmentContext.getPublicServiceNode(), "RDOS", dtUicTenantId, environmentContext.getSdkToken());
        if (CollectionUtils.isEmpty(uicUsers)) {
            return new ArrayList<>(0);
        }
        List<UserNameVO> userNameVOS = uicUsers.stream()
                .map(m -> {
                    UserNameVO vo = new UserNameVO();
                    Long userId = ((Number) m.get("userId")).longValue();
                    vo.setUserId(userId);
                    vo.setUserName(m.get("userName") == null ? "" : m.get("userName").toString());
                    return vo;
                })
                .collect(Collectors.toList());
        List<Long> getUserInfo = userNameVOS.stream().map(UserNameVO::getUserId).collect(Collectors.toList());
        userIds.removeAll(getUserInfo);
        if(!userIds.isEmpty()){
            userNameVOS.addAll(getUserByIds(userIds));
        }
        return userNameVOS;
    }

    public List<UserNameVO> getUser(String userName, List<Long> userIds) {

        ApiResponse<List<UICUserListResult>> allUsers = uicUserApiClient.findCurrentUserByName(userName);
        if(CollectionUtils.isEmpty(allUsers.getData())){
            return new ArrayList<>(0);
        }
        List<UserNameVO> userNameVOS = new ArrayList<>();
        for (UICUserListResult datum : allUsers.getData()) {
            UserNameVO vo = new UserNameVO();
            Long userId = datum.getUserId();
            vo.setUserId(userId);
            vo.setUserName(datum.getUserName());
            userNameVOS.add(vo);
            userIds.remove(userId);
        }
        if(!userIds.isEmpty()){
            userNameVOS.addAll(getUserByIds(userIds));
        }
        return userNameVOS;
    }

    private List<UserNameVO> getUserByIds(List<Long> userIds) {
        ApiResponse<List<UICUserVO>> users = uicUserApiClient.getByUserIds(userIds);
        if (null == users || CollectionUtils.isEmpty(users.getData())) {
            return new ArrayList<>();
        }
        return users.getData().stream().map(u -> {
            UserNameVO vo = new UserNameVO();
            vo.setUserId(u.getUserId());
            vo.setUserName(u.getUserName());
            return vo;
        }).collect(Collectors.toList());
    }


    public AlertConfigVO getAlertConfig() {
        AlertConfigVO vo = null;
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.ALERT_CONFIG.type);
        if (!CollectionUtils.isEmpty(dicts)) {
            String config = dicts.get(0).getDictValue();
            vo = JSONObject.parseObject(config, AlertConfigVO.class);
        }
        return vo;
    }

    public AlertConfigVO saveAlertConfig(AlertConfigVO vo) {
        List<ScheduleDict> dicts = dictDao.listDictByType(DictType.ALERT_CONFIG.type);
        if (CollectionUtils.isEmpty(dicts)) {
            ScheduleDict dict = new ScheduleDict();
            dict.setDictCode("alert_config");
            dict.setDictName("alertConfig");
            dict.setType(DictType.ALERT_CONFIG.type);
            dict.setDictValue(JSONObject.toJSONString(vo));
            dict.setDataType("STRING");
            dictDao.insert(dict);
        } else {
            ScheduleDict dict = dicts.get(0);
            dict.setDictValue(JSONObject.toJSONString(vo));
            dictDao.updateValue(dict);
        }
        return vo;
    }

    public List<ProductNameVO> getProducts() {
        AppType[] types = AppType.values();
        List<ProductNameVO> res = new ArrayList<>();
        for (AppType type : types) {
            // 目前质量5.3开始已经合并到资产了,多集群管理-资源组授权-添加授权项目，这边下拉框获取到的质量选择获取不到项目的
            if (type == AppType.DQ) {
                continue;
            }
            ProductNameVO vo = new ProductNameVO();
            vo.setType(type.name());
            vo.setName(type.getName());
            res.add(vo);
        }
        return res;
    }

    /**
     * 从业务中心获取用户配置的模块
     * @return
     */
    public List<ProductNameVO> getModules() {
        List<SubProductModuleVO> moduleList = publicServiceUicSubProductModuleApiClientSdkFaced.getModuleList();
        List<ProductNameVO> res = new ArrayList<>();
        for (SubProductModuleVO moduleVO : moduleList) {
            EModuleAppTypeMapping enumByModuleCode = EModuleAppTypeMapping.getEnumByModuleCode(moduleVO.getModuleCode());
            if (enumByModuleCode == null) {
                continue;
            }
            ProductNameVO vo = new ProductNameVO();
            vo.setType(enumByModuleCode.name());
            vo.setName(enumByModuleCode.getAppName());
            res.add(vo);
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTenantComponent(List<AddOrUpdateTenantComponentVO> vos) {
        for (AddOrUpdateTenantComponentVO vo : vos) {
            Long tenantId = vo.getTenantId();
            Long clusterId = tenantService.getClusterIdByDtUicTenantId(tenantId);

            Integer componentTypeCode = vo.getComponentTypeCode();
            String componentConfig = vo.getComponentConfig();

            List<ConsoleTenantComponent> exists = consoleTenantComponentDao.getByTenantIdAndClusterIdAndComponentTypeCode(tenantId, clusterId, componentTypeCode);
            if (CollectionUtils.isNotEmpty(exists)) {
                ConsoleTenantComponent exist = exists.get(0);
                exist.setComponentConfig(componentConfig);
                consoleTenantComponentDao.update(exist);
                return;
            }

            EComponentType componentType = EComponentType.getByCode(componentTypeCode);

            ConsoleTenantComponent newOne = new ConsoleTenantComponent();
            newOne.setTenantId(tenantId);
            newOne.setClusterId(clusterId);
            newOne.setComponentName(componentType.getName());
            newOne.setComponentTypeCode(componentType.getTypeCode());
            newOne.setComponentConfig(componentConfig);
            consoleTenantComponentDao.insert(newOne);
        }
    }

    public List<NotYetKilledJobCountVO> getNotYetKilledJob(List<QueryNotYetKilledJobCountVO> queryNotYetKilledJobCountVOS) {
        List<NotYetKilledJobCountVO> result = new ArrayList<>();
        for (QueryNotYetKilledJobCountVO queryVO : queryNotYetKilledJobCountVOS) {
            NotYetKilledJobCountVO vo = new NotYetKilledJobCountVO();

            Integer notYetKilledJobCount = getNotYetKilledJobCount(
                    queryVO.getJobResource(),
                    queryVO.getNodeAddress(),
                    queryVO.getStage());

            vo.setJobResource(queryVO.getJobResource());
            vo.setNotYetKilledJobCount(notYetKilledJobCount);
            result.add(vo);
        }
        return result;
    }

    public List<Integer> getUrlCheckList() {
        return URL_CHECK_COMPONENT_LIST;
    }
}
