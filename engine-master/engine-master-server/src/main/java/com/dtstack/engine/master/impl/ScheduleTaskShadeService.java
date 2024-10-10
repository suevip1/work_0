package com.dtstack.engine.master.impl;

import cn.hutool.core.stream.SimpleCollector;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.enums.*;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.DependencyTaskVo;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.*;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.*;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.dto.CalenderTaskDTO;
import com.dtstack.engine.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.enums.SearchTypeEnum;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.mapstruct.ScheduleTaskShadeStruct;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.JobStartTriggerDispatcher;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.master.utils.CheckUtils;
import com.dtstack.engine.master.utils.ListUtil;
import com.dtstack.engine.master.utils.TaskUtils;
import com.dtstack.engine.po.*;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.result.UICUserListResult;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Service
public class ScheduleTaskShadeService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskShadeService.class);

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Autowired
    private WorkSpaceProjectService projectService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleTaskCommitMapper scheduleTaskCommitMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleTaskShadeStruct scheduleTaskShadeStruct;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ScheduleTaskProjectParamDao scheduleTaskProjectParamDao;

    @Autowired
    private ScheduleProjectParamService scheduleProjectParamService;

    @Autowired
    private ScheduleTaskWorkflowParamService scheduleTaskWorkflowParamService;

    @Autowired
    private ScheduleTaskChainParamDao scheduleTaskChainParamDao;

    @Autowired
    private AlertAlarmService alertAlarmService;

    @Autowired
    private BaselineTaskService baselineTaskService;

    @Autowired
    private BaselineTaskTaskDao baselineTaskTaskDao;

    @Autowired
    private ScheduleJobBuildRecordService scheduleJobBuildRecordService;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    @Autowired
    private ParamStruct paramStruct;

    @Autowired
    private ScheduleTaskParamDao scheduleTaskParamDao;

    @Autowired
    private ScheduleTaskTagService scheduleTaskTagService;

    @Autowired
    private AlertAlarmDao alertAlarmDao;

    @Autowired
    private AlertAlarmRuleDao alertAlarmRuleDao;

    @Autowired
    private ScheduleTaskPriorityService scheduleTaskPriorityService;

    @Autowired
    private ResourceGroupGrantDao resourceGroupGrantDao;

    @Autowired
    private ScheduleTaskCalenderDao scheduleTaskCalenderDao;

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO) {

        //保存batch_task_shade
        ScheduleTaskShade old = scheduleTaskShadeDao.getOneIncludeDelete(batchTaskShadeDTO.getTaskId(),batchTaskShadeDTO.getAppType());
        checkComponentVersion(batchTaskShadeDTO);
        checkAgentLabelResourceIfNeed(batchTaskShadeDTO);

        // shade移除expendTime，统一存到schedule_task_calender中
        batchTaskShadeDTO.setScheduleConf(ScheduleConfUtils.removeKey(batchTaskShadeDTO.getScheduleConf(),ScheduleConfUtils.EXPEND_TIME));

        if (old != null) {
            checkBaseline(batchTaskShadeDTO, old);
            this.checkTenantHasBindCluster(batchTaskShadeDTO.getDtuicTenantId());
            //更新提交时间
            batchTaskShadeDTO.setGmtModified(new Timestamp(System.currentTimeMillis()));
            scheduleTaskShadeDao.update(batchTaskShadeDTO);
            this.updateJobWhenUpdateTask(batchTaskShadeDTO, old);
        } else {
            if (null == batchTaskShadeDTO.getProjectScheduleStatus()) {
                batchTaskShadeDTO.setProjectScheduleStatus(EProjectScheduleStatus.NORMAL.getStatus());
            }
            if (null == batchTaskShadeDTO.getNodePid()) {
                batchTaskShadeDTO.setNodePid(0L);
            }
            if (null == batchTaskShadeDTO.getDtuicTenantId() || batchTaskShadeDTO.getDtuicTenantId() <= 0) {
                throw new RdosDefineException("租户dtuicTenantId 不能为空");
            }

            this.checkTenantHasBindCluster(batchTaskShadeDTO.getDtuicTenantId());
            if (null == batchTaskShadeDTO.getFlowId()) {
                batchTaskShadeDTO.setFlowId(0L);
            }

            if (null == batchTaskShadeDTO.getTaskRule()) {
                batchTaskShadeDTO.setTaskRule(0);
            }
            scheduleTaskShadeDao.insert(batchTaskShadeDTO);
        }

        scheduleTaskTagService.addOrUpdateTaskTag(batchTaskShadeDTO.getTaskId(),
                batchTaskShadeDTO.getAppType(),batchTaskShadeDTO.getTagIds());

        if (batchTaskShadeDTO.getCalenderId() != null) {
            ConsoleCalender calender = calenderService.findById(batchTaskShadeDTO.getCalenderId());
            if (null == calender || Deleted.DELETED.getStatus().equals(calender.getIsDeleted())) {
                throw new RdosDefineException(ErrorCode.CALENDER_IS_NULL);
            }
            calenderService.addOrUpdateTaskCalender(batchTaskShadeDTO.getTaskId(), batchTaskShadeDTO.getAppType(), batchTaskShadeDTO.getCalenderId(), batchTaskShadeDTO.getExpendTime(),batchTaskShadeDTO.getCandlerBatchType());
        }else {
            // fix:任务从自定义调度改回周期调度，清理自定义调度和任务关联
            calenderService.deleteTask(Lists.newArrayList(batchTaskShadeDTO.getTaskId()),batchTaskShadeDTO.getAppType());
        }

        // 插入立即生成实例记录
        if (JobBuildType.IMMEDIATELY.getType().equals(batchTaskShadeDTO.getJobBuildType()) && Objects.nonNull(batchTaskShadeDTO.getFlowId()) && batchTaskShadeDTO.getFlowId() == 0L) {
            ScheduleJobBuildRecord scheduleJobBuildRecord = new ScheduleJobBuildRecord();
            scheduleJobBuildRecord.setAppType(batchTaskShadeDTO.getAppType());
            scheduleJobBuildRecord.setScheduleType(EScheduleType.NORMAL_SCHEDULE.getType());
            scheduleJobBuildRecord.setJobBuildType(JobBuildType.IMMEDIATELY.getType());
            scheduleJobBuildRecord.setJobBuildStatus(JobBuildStatus.CREATE.getStatus());
            scheduleJobBuildRecord.setTaskId(batchTaskShadeDTO.getTaskId());
            scheduleJobBuildRecordService.insert(scheduleJobBuildRecord);
        }
    }

    /**
     * 离线 dtScript agent 任务校验节点标签是否被授权
     * @param taskShade
     */
    private void checkAgentLabelResourceIfNeed(ScheduleTaskShadeDTO taskShade) {
        if (!AppType.RDOS.getType().equals(taskShade.getAppType())) {
            return;
        }
        if (!JobStartTriggerDispatcher.AGENT_JOB_TYPE.contains(taskShade.getTaskType())) {
            return;
        }
        Long resourceId = taskShade.getResourceId();
        if (resourceId == null) {
            throw new RdosDefineException(ErrorCode.RESOURCE_GROUP_NOT_FOUND);
        }
        List<ResourceGroupDetail> resourceGroups = resourceGroupGrantDao.listAccessedResourceDetailByProjectId(taskShade.getProjectId(), taskShade.getAppType(), EComponentType.DTSCRIPT_AGENT.getTypeCode());
        List<Long> resourceGroupIds = resourceGroups.stream().map(ResourceGroupDetail::getResourceId).collect(Collectors.toList());
        // 没找到授权记录
        if (!resourceGroupIds.contains(resourceId)) {
            throw new RdosDefineException(ErrorCode.RESOURCE_GROUP_NOT_GRANTED);
        }
    }

    /**
     * 校验租户绑定集群
     * @param dtuicTenantId
     */
    private void checkTenantHasBindCluster(Long dtuicTenantId) {
        Long clusterId = tenantService.getClusterIdByDtUicTenantId(dtuicTenantId);
        if (clusterId == null) {
            throw new RdosDefineException(ErrorCode.TENANT_NOT_BIND);
        }
    }

    private void checkBaseline(ScheduleTaskShadeDTO batchTaskShadeDTO, ScheduleTaskShade old) {
        List<Long> baseTaskTaskIds = baselineTaskTaskDao.selectByTaskIds(Lists.newArrayList(batchTaskShadeDTO.getTaskId())
                , batchTaskShadeDTO.getAppType());
        List<BaselineTaskDTO> baselineDTOS = baselineTaskService.getBaselineTaskByIds(baseTaskTaskIds);
        String allBaseTaskNames = Joiner.on(",").join(baselineDTOS.stream().map(BaselineTaskDTO::getName).collect(Collectors.toList()));
        if (baseTaskTaskIds.size() > 0) {
            if (batchTaskShadeDTO.getPeriodType() != null && !old.getPeriodType().equals(batchTaskShadeDTO.getPeriodType())) {
                throw new RdosDefineException(String.format("任务%s 已绑定基线告警,不允许变动调度类型,调度周期天修改为%s,原来:%s,关联基线任务:%s", old.getName()
                        ,ESchedulePeriodType.getMsg(batchTaskShadeDTO.getPeriodType()),ESchedulePeriodType.getMsg(old.getPeriodType()),allBaseTaskNames));
            }
            //  日历调度check单批次任务是不是加到多批次，或者多批次加到单批次任务
            if (batchTaskShadeDTO.getPeriodType() != null && ObjectUtils.equals(ESchedulePeriodType.CALENDER.getVal(),old.getPeriodType())){
                Integer newCandlerBatchType  = batchTaskShadeDTO.getCandlerBatchType();
                ScheduleTaskCalender oldTaskCalender = scheduleTaskCalenderDao.findByTaskId(old.getTaskId(), old.getAppType());
                Integer oldCandlerBatchType = oldTaskCalender.getCandlerBatchType();
                if (!ObjectUtils.equals(oldCandlerBatchType,newCandlerBatchType)){
                    throw new RdosDefineException(String.format("任务%s 已绑定基线告警,不允许变动批次类型,调度批次修改为%s,原来:%s,关联基线任务:%s", old.getName()
                            ,CandlerBatchTypeEnum.getMsg(newCandlerBatchType),CandlerBatchTypeEnum.getMsg(oldCandlerBatchType),allBaseTaskNames));
                }
                boolean expandTimeChanged = calenderService.checkExpandTimeChanged(batchTaskShadeDTO.getExpendTime(), oldTaskCalender.getExpandTime());
                if (expandTimeChanged){
                    throw new RdosDefineException(String.format("任务已绑定基线告警，自定义日历调度多批次时间不能改动,原批次[%s]",oldTaskCalender.getExpandTime()));
                }

            }
        }
    }

    private void updateJobWhenUpdateTask(ScheduleTaskShadeDTO batchTaskShadeDTO, ScheduleTaskShade old) {
        scheduleJobDao.updateResourceIdByTaskIdAndOldResourceIdAndStatus(batchTaskShadeDTO.getResourceId(), batchTaskShadeDTO.getTaskId(), batchTaskShadeDTO.getAppType(), old.getResourceId(), RdosTaskStatus.UNSUBMIT.getStatus());
        scheduleJobDao.updateByTaskIdAndVersionId(batchTaskShadeDTO.getVersionId(), batchTaskShadeDTO.getTaskId(), batchTaskShadeDTO.getAppType(), old.getVersionId(), RdosTaskStatus.UNSUBMIT.getStatus());
    }

    private void checkComponentVersion(ScheduleTaskShadeDTO batchTaskShadeDTO) {
        if (StringUtils.isBlank(batchTaskShadeDTO.getComponentVersion())) {
            return;
        }
        EComponentType eComponentType = ComponentVersionUtil.TASK_COMPONENT.get(batchTaskShadeDTO.getTaskType());
        if (eComponentType == null) {
            return;
        }
        String componentVersion = batchTaskShadeDTO.getComponentVersion();
        List<Component> components = componentService.getComponents(batchTaskShadeDTO.getDtuicTenantId(), eComponentType);
        if (CollectionUtils.isEmpty(components)) {
            throw new RdosDefineException(ErrorCode.COMPONENT_VERSION_SUPPORT);
        }
        Optional<Component> componentOptional = components.stream()
                .filter(c -> componentVersion.equalsIgnoreCase(c.getHadoopVersion())).findAny();
        if (!componentOptional.isPresent()) {
            throw new RdosDefineException(ErrorCode.COMPONENT_VERSION_SUPPORT);
        }
    }

    /**
     * web 接口
     * task删除时触发同步清理
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long taskId, long modifyUserId, Integer appType, Integer taskType) {
        if (EScheduleJobType.GROUP.getType().equals(taskType)) {
            ScheduleTaskShade groupTask = scheduleTaskShadeDao.getOne(taskId, appType);
            if (groupTask == null) {
                return;
            }
            LOGGER.info("delete task in group {} appType {}", groupTask.getTaskId(), appType);
            scheduleTaskShadeDao.deleteByFlowId(groupTask.getTaskId(), modifyUserId, appType);
        }
        scheduleTaskShadeDao.delete(taskId, modifyUserId, appType);
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId, appType);
        baselineTaskService.deleteTaskOfBaseline(Lists.newArrayList(taskId),appType);
        alertAlarmService.deleteAlarmTask(Lists.newArrayList(taskId),appType, AlarmRuleBusinessTypeEnum.TASK);
        clearTaskBoundInfo(Lists.newArrayList(taskId), appType);
    }

    private void clearTaskBoundInfo(List<Long> taskIds, Integer appType) {
        calenderService.deleteTask(taskIds, appType);
        paramService.deleteTaskParamByTaskId(taskIds, appType);
        scheduleTaskProjectParamDao.removeByTasks(taskIds, appType);
        scheduleTaskChainParamDao.deleteByTasks(taskIds, appType);
    }

    public List<NotDeleteTaskVO> getNotDeleteTask(Long taskId, Integer appType) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.getChildTaskByOtherPlatform(taskId, appType, environmentContext.getListChildTaskLimit());
        return buildNotDeleteTaskVO( shades,appType);

    }

    public List<NotDeleteTaskVO> buildNotDeleteTaskVO(List<ScheduleTaskShade> shades,Integer appType) {
        List<NotDeleteTaskVO> notDeleteTaskVOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(shades)) {
            List<Long> tenantIds = shades.stream().map(ScheduleTaskShade::getDtuicTenantId).collect(Collectors.toList());

            Map<Long, TenantDeletedVO> detailVOMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);
            for (ScheduleTaskShade shade : shades) {
                NotDeleteTaskVO notDeleteTaskVO = new NotDeleteTaskVO();
                notDeleteTaskVO.setAppType(shade.getAppType());
                AuthProjectVO authProjectVO = projectService.finProject(shade.getProjectId(), shade.getAppType());
                if (authProjectVO != null) {
                    notDeleteTaskVO.setProjectAlias(authProjectVO.getProjectAlias());
                    notDeleteTaskVO.setProjectName(authProjectVO.getProjectName());
                }
                TenantDeletedVO tenant = detailVOMap.get(shade.getDtuicTenantId());
                if (tenant != null) {
                    notDeleteTaskVO.setTenantName(tenant.getTenantName());
                }

                notDeleteTaskVO.setTaskName(shade.getName());
                notDeleteTaskVOS.add(notDeleteTaskVO);
            }
        }
        return notDeleteTaskVOS;
    }

    /**
     * 获取所有需要需要生成调度的task 没有sqlText字段
     */
    public List<ScheduleTaskShade> listTaskByStatus(Long startId, Integer submitStatus, Integer projectSubmitStatus, Integer batchTaskSize,Collection<Long> projectIds,Integer appType,Integer taskGroup) {
        return scheduleTaskShadeDao.listTaskByStatus(startId, submitStatus, projectSubmitStatus, batchTaskSize,projectIds,appType,taskGroup);
    }

    public Integer countTaskByStatus(Integer submitStatus, Integer projectSubmitStatus, Collection<Long> projectIds, Integer appType, Integer taskGroup) {
        return scheduleTaskShadeDao.countTaskByStatus(submitStatus, projectSubmitStatus, projectIds, appType, taskGroup);
    }

    /**
     * 根据任务类型查询已提交到task服务的任务数
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    public ScheduleTaskShadeCountTaskVO countTaskByType( Long tenantId, Long dtuicTenantId,
                                                Long projectId,  Integer appType,
                                                List<Integer> taskTypes){
        List<ScheduleTaskShadeCountTaskVO> ScheduleTaskShadeCountTaskVOs = scheduleTaskShadeDao.countTaskByType(tenantId, dtuicTenantId, Lists.newArrayList(projectId), appType, taskTypes, AppType.DATASCIENCE.getType() == appType ? 0L : null);
        if (CollectionUtils.isEmpty(ScheduleTaskShadeCountTaskVOs)) {
            return new ScheduleTaskShadeCountTaskVO();
        }
        return ScheduleTaskShadeCountTaskVOs.get(0);
    }


    public List<ScheduleTaskShadeCountTaskVO> countTaskByTypes( Long tenantId, Long dtuicTenantId,
                                                List<Long> projectIds,  Integer appType,
                                                List<Integer> taskTypes){

        return scheduleTaskShadeDao.countTaskByType(tenantId, dtuicTenantId, projectIds, appType, taskTypes, AppType.DATASCIENCE.getType() == appType ? 0L : null);
    }

    public Map<Long, ScheduleTaskShade> getByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }

        List<ScheduleTaskShade> tasks = scheduleTaskShadeDao.listByIds(ids);
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyMap();
        }

        return tasks.stream().collect(Collectors.toMap(
                ScheduleTaskShade::getId,
                Function.identity()
        ));
    }

    public Map<Long, TaskName> getNamesByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }

        List<TaskName> tasks = scheduleTaskShadeDao.listNameByIds(ids);
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyMap();
        }

        return tasks.stream().collect(Collectors.toMap(
                TaskName::getId,
                Function.identity()
        ));
    }

    /**
     * 根据任务id获取对应的taskShade
     * @param taskIds
     * @return
     */
    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listByTaskIds(taskIds, Deleted.NORMAL.getStatus(),appType);
    }

    public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds,Integer isDelete, Integer appType) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listByTaskIds(taskIds, isDelete,appType);
    }

    /**
     * ps- 省略了一些大字符串 如 sql_text、task_params
     *
     * @param taskIdArray
     * @return
     */
    public List<ScheduleTaskShade> getSimpleTaskRangeAllByIds(List<Long> taskIdArray, Integer appType) {
        if (CollectionUtils.isEmpty(taskIdArray)) {
            return Collections.EMPTY_LIST;
        }

        return scheduleTaskShadeDao.listSimpleByTaskIds(taskIdArray, null,appType);
    }

    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<ScheduleTaskShade> getTasksByName( Long projectId,
                                                   String name,  Integer appType) {

        if (StringUtils.isNotBlank(name)) {
            name = CheckUtils.handlerStr(name);
        }

        return scheduleTaskShadeDao.listByNameLikeWithSearchType(projectId, name,appType,null,null, SearchTypeEnum.FUZZY.getType(),null,null);
    }

    public ScheduleTaskShade getByName( long projectId,
                                        String name,  Integer appType, Long flowId) {
        //如果appType没传那就默认为ide
        if (null == appType){
            appType = 1;
        }
        return scheduleTaskShadeDao.getByName(projectId, name,appType,flowId);
    }

    public void updateTaskName(long taskId,  String taskName,Integer appType) {
        scheduleTaskShadeDao.updateTaskName(taskId, taskName,appType);
    }


    /**
     * jobKey 格式：cronTrigger_taskId_time
     *
     * @param jobKey
     * @return
     */
    public String getTaskNameByJobKey(String jobKey,Integer appType) {
        String[] jobKeySplit = jobKey.split("_");
        if (jobKeySplit.length < 3) {
            return "";
        }

        String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
        Long taskShadeId = MathUtil.getLongVal(taskIdStr);
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getById(taskShadeId);
        if (taskShade == null) {
            return "";
        }

        return taskShade.getName();
    }


    /**
     * 获取工作流中的最顶层的子节点
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskShade getWorkFlowTopNode(Long taskId,Integer appType) {
        if (taskId != null) {
            return scheduleTaskShadeDao.getWorkFlowTopNode(taskId,appType);
        } else {
            return null;
        }
    }

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto) {
        PageQuery<ScheduleTaskShadeDTO> query = new PageQuery<>(dto.getPageIndex(),dto.getPageSize(),"gmt_modified",dto.getSort());
        query.setModel(dto);
        Integer count = scheduleTaskShadeDao.simpleCount(dto);
        List<ScheduleTaskShadeVO> data = new ArrayList<>();
        if (count > 0) {
            List<ScheduleTaskShade> taskShades = scheduleTaskShadeDao.simpleQuery(query);
            taskShades = fillScheduleConf(taskShades);
            for (ScheduleTaskShade taskShade : taskShades) {
                ScheduleTaskShadeVO taskShadeVO = new ScheduleTaskShadeVO();
                BeanUtils.copyProperties(taskShade,taskShadeVO);
                taskShadeVO.setId(taskShade.getTaskId());
                taskShadeVO.setTaskName(taskShade.getName());
                taskShadeVO.setTaskType(taskShade.getTaskType());
                taskShadeVO.setGmtModified(taskShade.getGmtModified());
                taskShadeVO.setIsDeleted(taskShade.getIsDeleted());
                data.add(taskShadeVO);
            }
        }
        return new PageResult<>(data, count, query);
    }


    public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {

        if (null == taskId || null == appType) {
            throw new RdosDefineException("taskId或appType不能为空");
        }
        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId, appType);
        if (taskShade == null || Deleted.DELETED.getStatus().equals(taskShade.getIsDeleted())) {
            return null;
        }
        return taskShade;
    }




    public ScheduleTaskShadePageVO queryTasksByCondition(FindTaskVO findTaskVO) {
        Long tenantId = findTaskVO.getTenantId();
        Long dtTenantId = findTaskVO.getUicTenantId();
        Long projectId = findTaskVO.getProjectId();
        String name = findTaskVO.getName();
        Long ownerId = findTaskVO.getOwnerId();
        Long startTime = findTaskVO.getStartTime();
        Long endTime = findTaskVO.getEndTime();
        Integer scheduleStatus = findTaskVO.getScheduleStatus();
        String taskTypeList = findTaskVO.getTaskTypeList();
        String periodTypeList = findTaskVO.getPeriodTypeList();
        Integer currentPage = findTaskVO.getCurrentPage();
        Integer pageSize = findTaskVO.getPageSize();
        String searchType = findTaskVO.getSearchType();
        Integer appType = findTaskVO.getAppType();
        List<Long> ownerIds = findTaskVO.getOwnerIds();
        List<Long> resourceIds = findTaskVO.getResourceIds();
        List<Long> calenderIds = findTaskVO.getCalenderIds();

        ScheduleTaskShadeDTO batchTaskDTO = new ScheduleTaskShadeDTO();
        boolean queryAll = false;
        if (StringUtils.isNotBlank(name) ||
                CollectionUtils.isNotEmpty(batchTaskDTO.getResourceIds()) ||
                CollectionUtils.isNotEmpty(batchTaskDTO.getTaskTypeList()) ||
                CollectionUtils.isNotEmpty(batchTaskDTO.getPeriodTypeList())) {
            queryAll = true;
            batchTaskDTO.setFlowId(null);
        } else {
            //过滤掉任务流中的子任务
            batchTaskDTO.setFlowId(0L);
        }
        if (CollectionUtils.isNotEmpty(calenderIds)) {
            List<Long> taskIds = calenderService.getAllTaskByCalenderId(calenderIds, appType);
            if (CollectionUtils.isEmpty(taskIds)) {
                ScheduleTaskShadePageVO scheduleTaskShadeTaskVO = new ScheduleTaskShadePageVO();
                scheduleTaskShadeTaskVO.setPageResult(new PageResult<>(0, 0, 0, 0, new ArrayList<>()));
                return scheduleTaskShadeTaskVO;
            }
            batchTaskDTO.setQueryTaskIds(taskIds);
        }
        setBatchTaskDTO(tenantId,dtTenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList,
                periodTypeList, searchType, batchTaskDTO,appType, ownerIds,resourceIds);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(batchTaskDTO);
        ScheduleTaskShadePageVO scheduleTaskShadeTaskVO = new ScheduleTaskShadePageVO();
        int publishedTasks = scheduleTaskShadeDao.countPublishToProduce(projectId,appType);
        scheduleTaskShadeTaskVO.setPublishedTasks(publishedTasks);
        int count = scheduleTaskShadeDao.generalCount(batchTaskDTO);
        if(count<=0){
            scheduleTaskShadeTaskVO.setPageResult(new PageResult<>(new ArrayList<>(),count,pageQuery));
            return scheduleTaskShadeTaskVO;
        }
        List<ScheduleTaskShade> batchTasks = scheduleTaskShadeDao.generalQuery(pageQuery);
        List<ScheduleTaskVO> vos = new ArrayList<>(batchTasks.size());

        for (ScheduleTaskShade batchTask : batchTasks) {
            com.dtstack.engine.master.vo.ScheduleTaskVO scheduleTaskVO = new com.dtstack.engine.master.vo.ScheduleTaskVO(batchTask, true);

            vos.add(scheduleTaskVO);
        }
        if (queryAll) {
            vos = dealFlowWorkSubTasks(vos,appType);
        } else {
            //默认不查询全部工作流子节点
            //vos = dealFlowWorkTasks(vos);
        }
        List<Long> dbResourceIds = batchTasks.stream().map(ScheduleTask::getResourceId).collect(Collectors.toList());
        resourceGroupService.fillTaskGroupInfo(dbResourceIds,vos);


        userService.fillUser(vos);
        PageResult<List<ScheduleTaskVO>> pageResult = new PageResult<>(vos, count, pageQuery);
        scheduleTaskShadeTaskVO.setPageResult(pageResult);
        return scheduleTaskShadeTaskVO;
    }

    public ScheduleTaskShadePageVO queryTasks(Long tenantId,
                                              Long dtTenantId,
                                              Long projectId,
                                              String name,
                                              Long ownerId,
                                              Long startTime,
                                              Long endTime,
                                              Integer scheduleStatus,
                                              String taskTypeList,
                                              String periodTypeList,
                                              Integer currentPage,
                                              Integer pageSize,
                                              String  searchType,
                                              Integer appType,
                                              List<Long> resourceIds){
        FindTaskVO findTaskVO = new FindTaskVO();
        findTaskVO.setTenantId(tenantId);
        findTaskVO.setUicTenantId(dtTenantId);
        findTaskVO.setProjectId(projectId);
        findTaskVO.setName(name);
        findTaskVO.setOwnerId(ownerId);
        findTaskVO.setStartTime(startTime);
        findTaskVO.setEndTime(endTime);
        findTaskVO.setScheduleStatus(scheduleStatus);
        findTaskVO.setTaskTypeList(taskTypeList);
        findTaskVO.setPeriodTypeList(periodTypeList);
        findTaskVO.setCurrentPage(currentPage);
        findTaskVO.setPageSize(pageSize);
        findTaskVO.setSearchType(searchType);
        findTaskVO.setAppType(appType);
        findTaskVO.setResourceIds(resourceIds);

        return this.queryTasksByCondition(findTaskVO);
    }

    /**
     * @author newman
     * @Description 设置分页任务查询参数
     * @Date 2020-12-21 18:12
     * @param tenantId:
     * @param projectId:
     * @param name:
     * @param ownerId:
     * @param startTime:
     * @param endTime:
     * @param scheduleStatus:
     * @param taskTypeList:
     * @param periodTypeList:
     * @param searchType:
     * @param batchTaskDTO:
     * @return: void
     **/
    private void setBatchTaskDTO(Long tenantId,Long dtTenantId, Long projectId, String name, Long ownerId, Long startTime, Long endTime, Integer scheduleStatus, String taskTypeList, String periodTypeList, String searchType, ScheduleTaskShadeDTO batchTaskDTO,Integer appType, List<Long> ownerIds,List<Long> resourceIds) {
        batchTaskDTO.setTenantId(tenantId);
        batchTaskDTO.setDtuicTenantId(dtTenantId);
        batchTaskDTO.setAppType(appType);
        batchTaskDTO.setProjectId(projectId);
        batchTaskDTO.setSubmitStatus(ESubmitStatus.SUBMIT.getStatus());
        batchTaskDTO.setTaskTypeList(convertStringToList(taskTypeList));
        batchTaskDTO.setPeriodTypeList(convertStringToList(periodTypeList));
        batchTaskDTO.setAppType(appType);
        batchTaskDTO.setResourceIds(resourceIds);
        if (StringUtils.isNotBlank(name)) {
            batchTaskDTO.setFuzzName(name);
        }
        if (null != ownerId && ownerId != 0) {
            batchTaskDTO.setOwnerUserId(ownerId);
        }
        batchTaskDTO.setOwnerUserIds(ownerIds);
        if (null != startTime && null != endTime) {
            batchTaskDTO.setStartGmtModified(new Timestamp(startTime * 1000));
            batchTaskDTO.setEndGmtModified(new Timestamp(endTime * 1000));
        }
        if (scheduleStatus != null) {
            batchTaskDTO.setScheduleStatus(scheduleStatus);
        }
        if (StringUtils.isEmpty(searchType) || "fuzzy".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(1);
        } else if ("precise".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(2);
        } else if ("front".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(3);
        } else if ("tail".equalsIgnoreCase(searchType)) {
            batchTaskDTO.setSearchType(4);
        } else {
            batchTaskDTO.setSearchType(1);
        }
    }

    private List<ScheduleTaskVO> dealFlowWorkSubTasks(List<ScheduleTaskVO> vos, Integer appType) {
        Map<Long, ScheduleTaskVO> record = Maps.newHashMap();
        Map<Long, Integer> voIndex = Maps.newHashMap();
        vos.forEach(task -> voIndex.put(task.getId(), vos.indexOf(task)));
        Iterator<ScheduleTaskVO> iterator = vos.iterator();
        List<ScheduleTaskVO> vosCopy = new ArrayList<>(vos);
        while (iterator.hasNext()) {
            ScheduleTaskVO vo = iterator.next();
            Long flowId = vo.getFlowId();
            if (flowId > 0) {
                if (record.containsKey(flowId)) {
                    ScheduleTaskVO flowVo = record.get(flowId);
                    flowVo.getRelatedTasks().add(vo);
                    iterator.remove();
                } else {
                    ScheduleTaskVO flowVo;
                    if (voIndex.containsKey(flowId)) {
                        flowVo = vosCopy.get(voIndex.get(flowId));
                        flowVo.setRelatedTasks(Lists.newArrayList(vo));
                        iterator.remove();
                        record.put(flowId, flowVo);
                    } else {
                        ScheduleTaskShade flow = scheduleTaskShadeDao.getOne(flowId, appType);
                        if (flow != null) {
                            flowVo = new com.dtstack.engine.master.vo.ScheduleTaskVO(flow, true);
                            flowVo.setRelatedTasks(Lists.newArrayList(vo));
                            vos.set(vos.indexOf(vo), flowVo);
                            record.put(flowId, flowVo);
                        }
                    }
                }
            }
        }
        return vos;
    }


    private List<Integer> convertStringToList(String str) {
        if(StringUtils.isBlank(str)){
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(",")).map(Integer::valueOf).collect(Collectors.toList());
    }


    /**
     * 冻结任务
     * @param taskIdList
     * @param scheduleStatus
     * @param appType
     */
    public void frozenTask(List<Long> taskIdList, int scheduleStatus,
                           Integer appType) {
        scheduleTaskShadeDao.batchUpdateTaskScheduleStatus(taskIdList, scheduleStatus, appType);
    }


    /**
     * 查询工作流下子节点
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask( Long taskId, Integer appType,List<Integer> taskTypes,Long ownerId) {

        ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOne(taskId,appType);
        if (taskShade == null) {
            return null;
        }

        ScheduleTaskVO vo = new com.dtstack.engine.master.vo.ScheduleTaskVO(taskShade, true);
        String tenantName = tenantService.getTenantName(taskShade.getDtuicTenantId());
        vo.setTenantName(tenantName);
        AuthProjectVO authProjectVO = projectService.finProject(taskShade.getProjectId(), taskShade.getAppType());
        if (authProjectVO != null) {
            vo.setProjectAlias(authProjectVO.getProjectAlias());
            vo.setProjectName(authProjectVO.getProjectName());
        }
        if (EScheduleJobType.WORK_FLOW.getVal().equals(vo.getTaskType()) || EScheduleJobType.GROUP.getVal().equals(vo.getTaskType())) {
            List<ScheduleTaskShade> subtasks = this.getFlowWorkSubTasks(vo.getTaskId(),appType,taskTypes,ownerId);
            fillScheduleConf(subtasks);
            if (CollectionUtils.isNotEmpty(subtasks)) {
                List<Long> resourceIds = subtasks.stream().map(ScheduleTaskShade::getResourceId).collect(Collectors.toList());
                Map<Long, ResourceGroupDetail> groupDetailMap = resourceGroupService.getGroupInfo(resourceIds);
                List<ScheduleTaskVO> list = subtasks.stream().map(task -> {
                    com.dtstack.engine.master.vo.ScheduleTaskVO scheduleTaskVO = new com.dtstack.engine.master.vo.ScheduleTaskVO(task, true);
                    if (authProjectVO != null) {
                        scheduleTaskVO.setProjectAlias(authProjectVO.getProjectAlias());
                        scheduleTaskVO.setProjectName(authProjectVO.getProjectName());
                    }
                    scheduleTaskVO.setTenantName(tenantName);
                    ResourceGroupDetail groupDetail = groupDetailMap.get(scheduleTaskVO.getResourceId());
                    if (groupDetail != null) {
                        scheduleTaskVO.setResourceGroupName(groupDetail.getResourceName());
                    }
                    return scheduleTaskVO;
                }).collect(Collectors.toList());
                vo.setRelatedTasks(list);
            }
        } else {
            Optional<ResourceGroup> resourceGroup = resourceGroupService.getResourceGroup(vo.getResourceId());
            resourceGroup.ifPresent(resource->{
                vo.setResourceId(resource.getId());
                vo.setResourceGroupName(resource.getName());
            });
        }
        return vo;
    }


    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId,  Integer appType,List<Integer> taskTypes,Long ownerId) {
        ScheduleTaskShadeDTO batchTaskShadeDTO = new ScheduleTaskShadeDTO();
        batchTaskShadeDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        batchTaskShadeDTO.setFlowId(taskId);
        batchTaskShadeDTO.setAppType(appType);
        batchTaskShadeDTO.setTaskTypeList(taskTypes);
        batchTaskShadeDTO.setOwnerUserId(ownerId);
        PageQuery<ScheduleTaskShadeDTO> pageQuery = new PageQuery<>(batchTaskShadeDTO);
        return scheduleTaskShadeDao.generalQuery(pageQuery);
    }


    public ScheduleTaskShade findTaskId( Long taskId, Integer isDeleted,  Integer appType) {
        if(null == taskId){
            return null;
        }
        List<ScheduleTaskShade> batchTaskShades = scheduleTaskShadeDao.listByTaskIds(Lists.newArrayList(taskId), isDeleted,appType);
        if(CollectionUtils.isEmpty(batchTaskShades)){
            return null;
        }
        return fillScheduleConf(batchTaskShades.get(0));
    }

    /**
     *
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple 不查询sql
     * @return
     */
    public List<ScheduleTaskShade> findTaskIds( List<Long> taskIds, Integer isDeleted,  Integer appType,  boolean isSimple) {
        if(CollectionUtils.isEmpty(taskIds)){
            return null;
        }
        if(isSimple){
            return scheduleTaskShadeDao.listSimpleByTaskIds(taskIds,isDeleted,appType);
        }
        return  scheduleTaskShadeDao.listByTaskIds(taskIds, isDeleted,appType);
    }

    /**
     * 填充自定义日历调度配置
     */
    public List<ScheduleTaskShade> fillScheduleConf(List<ScheduleTaskShade> allShades) {
        if (CollectionUtils.isEmpty(allShades)){
            return allShades;
        }
        List<ScheduleTaskShade> calendarShades = allShades.stream().filter(a -> ObjectUtils.equals(ESchedulePeriodType.CALENDER.getVal(), a.getPeriodType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(calendarShades)){
            return allShades;
        }
        Map<Integer, List<ScheduleTaskShade>> shadesGroup = calendarShades.stream().collect(Collectors.groupingBy(ScheduleTaskShade::getAppType));
        for (Map.Entry<Integer,List<ScheduleTaskShade>> shadeGroup:shadesGroup.entrySet()){
            List<ScheduleTaskShade> shades = shadeGroup.getValue();
            List<Long> shadeTaskIds = shades.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskCalender> calenders = scheduleTaskCalenderDao.findByTaskIds(shadeTaskIds, shadeGroup.getKey());
            Map<Long, ScheduleTaskCalender> calenderGroup = calenders.stream().collect(Collectors.toMap(ScheduleTaskCalender::getTaskId,Function.identity(),(a,b)->a));
            shades.forEach(s->{
                ScheduleTaskCalender scheduleTaskCalender = calenderGroup.get(s.getTaskId());
                if (null == scheduleTaskCalender){
                    LOGGER.error("not found calendar expandTime:taskId {} appType {}",s.getTaskId(),s.getAppType());
                    return;
                }
                s.setScheduleConf(ScheduleConfUtils.pubKeyIfAbsent(s.getScheduleConf(),ScheduleConfUtils.EXPEND_TIME,scheduleTaskCalender.getExpandTime()));
            });
        }
        return allShades;
    }
    public ScheduleTaskShade fillScheduleConf(ScheduleTaskShade shade){
        if (null == shade){
            return null;
        }
        return fillScheduleConf(Lists.newArrayList(shade)).get(0);
    }



    /**
     * 保存任务提交engine的额外信息
     * 备注：提交后 taskParamsToReplace 只保存了自定义参数
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void info(Long taskId, Integer appType, String info) {
        JSONObject commitInfo = JSONObject.parseObject(info);
        if (commitInfo != null) {
            paramService.convertGlobalToParamType(info, (globalTaskParams, otherTaskParams) -> {
                if (CollectionUtils.isNotEmpty(globalTaskParams)) {
                    // 全局参数
                    paramService.addOrUpdateTaskParam(taskId, appType, globalTaskParams);
                    List<ScheduleTaskParamShade> globalTaskShades = paramStruct.BOtoTaskParams(globalTaskParams);
                    // 移除同名参数
                    scheduleProjectParamService.removeIdenticalProjectParam(taskId, appType, globalTaskShades);
                    scheduleTaskWorkflowParamService.removeIdenticalWorkflowParam(taskId, appType, globalTaskShades);
                } else {
                    scheduleTaskParamDao.deleteByTaskIds(Lists.newArrayList(taskId), appType);
                }
                // 项目参数
                List<ScheduleTaskParamShade> projectParams = otherTaskParams.stream().filter(p -> EParamType.PROJECT.getType().equals(p.getType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(projectParams)) {
                    scheduleProjectParamService.addOrUpdateTaskProjectParams(taskId, appType, projectParams);
                    // 移除同名参数
                    paramService.removeIdenticalParam(taskId, appType, projectParams);
                    scheduleTaskWorkflowParamService.removeIdenticalWorkflowParam(taskId, appType, projectParams);
                } else {
                    scheduleTaskProjectParamDao.removeByTask(taskId, appType);
                }

                // 工作流参数
                List<ScheduleTaskParamShade> workflowParams = otherTaskParams.stream().filter(p -> EParamType.WORK_FLOW.getType().equals(p.getType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(workflowParams)) {
                    scheduleTaskWorkflowParamService.addOrUpdateTaskWorkflowParams(taskId, appType, workflowParams);
                    // 移除同名参数
                    paramService.removeIdenticalParam(taskId, appType, workflowParams);
                    scheduleProjectParamService.removeIdenticalProjectParam(taskId, appType, workflowParams);
                } else {
                    scheduleTaskWorkflowParamService.removeTaskWorkflowParams(taskId, appType);
                }

                // 自定义参数
                List<ScheduleTaskParamShade> selfTaskParams = otherTaskParams.stream().filter(p -> EParamType.isSelfParam(p.getType())).collect(Collectors.toList());
                commitInfo.put(GlobalConst.taskParamToReplace, JSONObject.toJSONString(selfTaskParams));
            }, (infoStr,selfTaskParams) -> {
                List<ScheduleTaskParam> scheduleTaskParams = scheduleTaskParamDao.findByTaskId(taskId, appType);
                if (CollectionUtils.isNotEmpty(scheduleTaskParams)) {
                    scheduleTaskParamDao.deleteByTaskIds(Lists.newArrayList(taskId), appType);
                }
            });

        }
        String taskExtraInfo = Optional.ofNullable(commitInfo).map(JSONAware::toJSONString).orElse(null);
        scheduleTaskShadeDao.updateTaskExtInfo(taskId, appType, taskExtraInfo);
        this.saveTaskChainParamIfNeed(taskId, appType, info);
    }

    public List<Map<String, Object>> listDependencyTask( List<Long> taskId, String name,  Long projectId) {

        return scheduleTaskShadeDao.listDependencyTask(projectId, name, taskId);
    }

    /**
     * 获取依赖任务列表 ， 封装到 DependencyTaskVo 集合
     * @param taskId
     * @param name
     * @param projectId
     * @return
     */
    public List<DependencyTaskVo> listDependencyTaskToVo(List<Long> taskId, String name, Long projectId) {
        List<Map<String, Object>> maps = this.listDependencyTask(taskId, name, projectId);
        List<DependencyTaskVo> dependencyTaskVos = new LinkedList<>();
        try {
            dependencyTaskVos = PublicUtil.mapListToVoList(maps, DependencyTaskVo.class);
        }catch (Exception e){
            LOGGER.error("Get namespace error " + e.getMessage());
        }
        return dependencyTaskVos;
    }

    public List<Map<String, Object>> listByTaskIdsNotIn( List<Long> taskId,  Integer appType,  Long projectId) {
        return scheduleTaskShadeDao.listByTaskIdsNotIn(projectId, taskId);
    }

    /**
     * 查询不包含的 taskId 里所有的id的 任务 ， 封装成 DependencyTaskVo 集合对象
     * @param taskId
     * @param appType
     * @param projectId
     * @return
     */
    public List<DependencyTaskVo> listByTaskIdsNotInToVo( List<Long> taskId,  Integer appType,  Long projectId) {
        List<Map<String, Object>> maps = this.listByTaskIdsNotIn(taskId, appType, projectId);
        List<DependencyTaskVo> dependencyTaskVos = new LinkedList<>();
        try {
            dependencyTaskVos = PublicUtil.mapListToVoList(maps,DependencyTaskVo.class);
        }catch (Exception e){
            LOGGER.error("Get namespace error " + e.getMessage());
        }
        return dependencyTaskVos;
    }

    public ScheduleTaskShade getById(Long id ){
        return scheduleTaskShadeDao.getById(id);
    }


    /**
     * @author zyd
     * @Description 校验任务资源参数限制
     * @Date 2020/10/20 2:55 下午
     * @param dtuicTenantId: uic租户id
     * @param taskType: 任务类型
     * @param resourceParams: 任务资源参数
     * @return: java.util.List<java.lang.String>
     **/
    public List<String> checkResourceLimit(Long dtuicTenantId, Integer taskType, String resourceParams,Long taskId) {

        TenantResource tenantResource = tenantResourceDao.selectByUicTenantIdAndTaskType(dtuicTenantId,taskType);
        List<String> exceedMessage = new ArrayList<>();
        if(null == tenantResource){
            return exceedMessage;
        }
        try {
            Properties taskProperties = PublicUtil.stringToProperties(resourceParams);
            String resourceLimit = tenantResource.getResourceLimit();
            JSONObject jsonObject = JSONObject.parseObject(resourceLimit);
            if(EScheduleJobType.SPARK_SQL.getType().equals(taskType) || EScheduleJobType.SPARK.getType().equals(taskType)
                    || EScheduleJobType.SPARK_PYTHON.getType().equals(taskType)){
                //spark类型的任务
                //2.1 为driver.cores 2.4 + 为spark.driver.cores
                String driverCores = Optional.ofNullable(taskProperties.getProperty("driver.cores")).orElse(taskProperties.getProperty("spark.driver.cores"));
                Integer driverCoresLimit = jsonObject.getInteger("driver.cores");
                if(StringUtils.isNotBlank(driverCores) && driverCoresLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(driverCores) > driverCoresLimit){
                        //driver核数超过限制
                        LOGGER.error("spark type task，task{} driverCores:{} (restrict:{})",taskId,driverCores,driverCoresLimit);
                        exceedMessage.add("driverCores: "+driverCores+" (restrict: "+driverCoresLimit+")");
                    }
                }
                String driverMemory = Optional.ofNullable(taskProperties.getProperty("driver.memory")).orElse(taskProperties.getProperty("spark.driver.memory"));
                Integer driverMemoryLimit = jsonObject.getInteger("driver.memory");
                if(StringUtils.isNotBlank(driverMemory) && driverMemoryLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(driverMemory) > driverMemoryLimit){
                        //driver内存大小超过限制
                        LOGGER.error("spark type task，task{} driverMemory:{} (restrict:{})",taskId,driverMemory,driverMemoryLimit);
                        exceedMessage.add("driverMemory: "+driverMemory+" (restrict: "+driverMemoryLimit+")");
                    }
                }
                String executorInstances = Optional.ofNullable(taskProperties.getProperty("executor.instances")).orElse(taskProperties.getProperty("spark.executor.instances"));
                Integer executorInstancesLimit = jsonObject.getInteger("executor.instances");
                if(StringUtils.isNotBlank(executorInstances) && executorInstancesLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(executorInstances) > executorInstancesLimit){
                        //executor实例数超过限制
                        LOGGER.error("spark type task，task{} executorInstances:{} (restrict:{})",taskId,executorInstances,executorInstancesLimit);
                        exceedMessage.add("executorInstances: "+executorInstances+" (restrict: "+executorInstancesLimit+")");
                    }
                }
                String executorCores = Optional.ofNullable(taskProperties.getProperty("executor.cores")).orElse(taskProperties.getProperty("spark.executor.cores"));
                Integer executorCoresLimit = jsonObject.getInteger("executor.cores");
                if(StringUtils.isNotBlank(executorCores) && executorCoresLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(executorCores) > executorCoresLimit){
                        //executor核数超过限制
                        LOGGER.error("spark type task，task{} executorCores:{} (restrict:{})",taskId,executorCores,executorCoresLimit);
                        exceedMessage.add("executorCores: "+executorCores+" (restrict: "+executorCoresLimit+")");
                    }
                }
                String executorMemory = Optional.ofNullable(taskProperties.getProperty("executor.memory")).orElse(taskProperties.getProperty("spark.executor.memory"));
                Integer executorMemoryLimit = jsonObject.getInteger("executor.memory");
                if(StringUtils.isNotBlank(executorMemory) && executorMemoryLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(executorMemory) > executorMemoryLimit){
                        //executor核数超过限制
                        LOGGER.error("spark type task，task{} executorMemory:{} (restrict:{})",taskId,executorMemory,executorMemoryLimit);
                        exceedMessage.add("executorMemory: "+executorMemory+" (restrict: "+executorMemoryLimit+")");
                    }
                }
            }else if(EScheduleJobType.SYNC.getType().equals(taskType)){
                //flink，数据同步类型任务
                String jobManagerMemory = taskProperties.getProperty("jobmanager.memory.mb");
                Integer jobManagerMemoryLimit = jsonObject.getInteger("jobmanager.memory.mb");
                if(StringUtils.isNotBlank(jobManagerMemory) && jobManagerMemoryLimit !=null){
                    if(UnitConvertUtil.getNormalizedMem(jobManagerMemory) > jobManagerMemoryLimit){
                        //工作管理器内存大小超过限制
                        LOGGER.error("flink data synchronization type tasks，task{} jobManagerMemory:{} (restrict:{})",taskId,jobManagerMemory,jobManagerMemoryLimit);
                        exceedMessage.add("jobManagerMemory: "+jobManagerMemory+" (restrict: "+jobManagerMemoryLimit+")");
                    }
                }
                String taskManagerMemory = taskProperties.getProperty("taskmanager.memory.mb");
                Integer taskManagerMemoryLimit = jsonObject.getInteger("taskmanager.memory.mb");
                if(StringUtils.isNotBlank(taskManagerMemory) && taskManagerMemoryLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(taskManagerMemory) > taskManagerMemoryLimit){
                        //任务管理器内存大小超过限制
                        LOGGER.error("flink data synchronization type tasks，task{} taskManagerMemory:{} (restrict:{})",taskId,taskManagerMemory,taskManagerMemoryLimit);
                        exceedMessage.add("taskManagerMemory: "+taskManagerMemory+" (restrict: "+taskManagerMemoryLimit+")");
                    }
                }
            }else if(EScheduleJobType.PYTHON.getType().equals(taskType) || EScheduleJobType.SHELL.getType().equals(taskType)){
                //dtscript类型的任务
                String workerMemory = taskProperties.getProperty("dtscript.worker.memory");
                Integer workerMemoryLimit = jsonObject.getInteger("dtscript.worker.memory");
                if(StringUtils.isNotBlank(workerMemory) && workerMemoryLimit!=null){
                    if(UnitConvertUtil.getNormalizedMem(workerMemory) > workerMemoryLimit){
                        //工作内存大小超过限制
                        LOGGER.error("dtscript data synchronization type tasks，task{} workerMemory:{} (restrict:{})",taskId,workerMemory,workerMemoryLimit);
                        exceedMessage.add("workerMemory: "+workerMemory+" (restrict: "+workerMemoryLimit+")");
                    }
                }
                String workerCores = taskProperties.getProperty("dtscript.worker.cores");
                Integer workerCoresLimit = jsonObject.getInteger("dtscript.worker.cores");
                if(StringUtils.isNotBlank(workerCores) && workerCoresLimit!=null ){
                    if(UnitConvertUtil.getNormalizedMem(workerCores) > workerCoresLimit){
                        //工作核数超过限制
                        LOGGER.error("dtscript data synchronization type tasks，task{} workerCores:{} (restrict:{})",taskId,workerCores,workerCoresLimit);
                        exceedMessage.add("workerCores: "+workerCores+" (restrict: "+workerCoresLimit+")");
                    }
                }
                String workerNum = taskProperties.getProperty("dtscript.worker.num");
                Integer workerNumLimit = jsonObject.getInteger("dtscript.worker.num");
                if(StringUtils.isNotBlank(workerNum) && workerNumLimit!=null ){
                    if(UnitConvertUtil.getNormalizedMem(workerNum) > workerNumLimit){
                        //worker数量超过限制
                        LOGGER.error("dtscript data synchronization type tasks，task{} workerNum:{} (restrict:{})",taskId,workerNum,workerNumLimit);
                        exceedMessage.add("workerNum: "+workerNum+" (restrict: "+workerNumLimit+")");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("ScheduleTaskShadeService.checkResourceLimit error:", e);
            throw new RdosDefineException("Check task resource parameter is abnormal");
        }
        return exceedMessage;
    }

    public String addOrUpdateBatchTask(List<ScheduleTaskShadeDTO> batchTaskShadeDTOs, String commitId) {
        if (CollectionUtils.isEmpty(batchTaskShadeDTOs)) {
            return null;
        }

        if (batchTaskShadeDTOs.size() > environmentContext.getMaxBatchTask()) {
            throw new RdosDefineException("The number of tasks added or modified in batch cannot exceed:" + environmentContext.getMaxBatchTask());
        }

        if (StringUtils.isBlank(commitId)) {
            LOGGER.info("commitId未传，自动生成commitId");
            commitId = UUID.randomUUID().toString();
        }

        try {
            List<ScheduleTaskCommit> scheduleTaskCommits = Lists.newArrayList();
            for (ScheduleTaskShadeDTO batchTaskShadeDTO : batchTaskShadeDTOs) {
                checkSubmitTaskCron(batchTaskShadeDTO);
                ScheduleTaskCommit scheduleTaskCommit = new ScheduleTaskCommit();
                scheduleTaskCommit.setAppType(batchTaskShadeDTO.getAppType());
                scheduleTaskCommit.setCommitId(commitId);
                scheduleTaskCommit.setExtraInfo(batchTaskShadeDTO.getExtraInfo());
                scheduleTaskCommit.setIsCommit(0);
                scheduleTaskCommit.setTaskId(batchTaskShadeDTO.getTaskId());
                scheduleTaskCommit.setTaskJson(JSONObject.toJSONString(batchTaskShadeDTO));
                scheduleTaskCommits.add(scheduleTaskCommit);

            }

            if (CollectionUtils.isNotEmpty(scheduleTaskCommits)) {
                if (batchTaskShadeDTOs.size() > environmentContext.getMaxBatchTaskInsert()) {
                    List<List<ScheduleTaskCommit>> partition = Lists.partition(scheduleTaskCommits, environmentContext.getMaxBatchTaskInsert());
                    for (List<ScheduleTaskCommit> scheduleTaskShadeDTOS : partition) {
                        scheduleTaskCommitMapper.insertBatch(scheduleTaskShadeDTOS);
                    }
                } else {
                    scheduleTaskCommitMapper.insertBatch(scheduleTaskCommits);
                }
                LOGGER.info("Submit task commitId:{}",commitId);
                return commitId;
            }

            return null;
        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
            return null;
        }
    }

    public void infoCommit(Long taskId, Integer appType, String info,String commitId) {
        if (StringUtils.isNotBlank(commitId)){
            scheduleTaskCommitMapper.updateTaskExtInfo(taskId, appType, info,commitId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public Boolean taskCommit(String commitId) {
        LOGGER.info("submit task commitId:{}",commitId);
        Long minId = scheduleTaskCommitMapper.findMinIdOfTaskCommitByCommitId(commitId);

        if (minId == null) {
            return Boolean.FALSE;
        }

        List<ScheduleTaskCommit> scheduleTaskCommits = scheduleTaskCommitMapper.findTaskCommitByCommitId(minId,commitId,environmentContext.getMaxBatchTaskSplInsert());
        while (CollectionUtils.isNotEmpty(scheduleTaskCommits)) {
            // 保存任务
            try {
                for (ScheduleTaskCommit scheduleTaskCommit : scheduleTaskCommits) {
                    String taskJson = scheduleTaskCommit.getTaskJson();
                    String extraInfo = scheduleTaskCommit.getExtraInfo();
                    ScheduleTaskShadeDTO scheduleTaskShadeDTO = JSONObject.parseObject(taskJson, ScheduleTaskShadeDTO.class);
                    addOrUpdate(scheduleTaskShadeDTO);
                    String info = getInfo(extraInfo);
                    info(scheduleTaskShadeDTO.getTaskId(),scheduleTaskShadeDTO.getAppType(),info);
                    scheduleTaskCommitMapper.updateTaskCommit(scheduleTaskCommit.getId());
                    clearHistoryCommit(scheduleTaskCommit);
                    minId = scheduleTaskCommit.getId();
                }

                scheduleTaskCommits = scheduleTaskCommitMapper.findTaskCommitByCommitId(minId,commitId,environmentContext.getMaxBatchTaskSplInsert());
            } catch (Exception e) {
                LOGGER.error(ExceptionUtil.getErrorMessage(e));
                throw new RdosDefineException(e.getMessage());
            }
        }

        return Boolean.TRUE;
    }

    private void clearHistoryCommit(ScheduleTaskCommit scheduleTaskCommit) {
        try {
            // 清理失败不能影响提交流程
            Long taskId = scheduleTaskCommit.getTaskId();
            Integer appType = scheduleTaskCommit.getAppType();
            List<ScheduleTaskCommit> taskByTaskIdAndAppType = scheduleTaskCommitMapper.findTaskByTaskIdAndAppType(taskId, appType, environmentContext.getReserveDay());

            if (CollectionUtils.isNotEmpty(taskByTaskIdAndAppType)) {
                List<String> commitIds = taskByTaskIdAndAppType.stream().map(ScheduleTaskCommit::getCommitId).collect(Collectors.toList());
                scheduleTaskCommitMapper.deleteByCommitIds(commitIds);
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtil.getErrorMessage(e));
        }
    }

    private String getInfo(String extraInfo) {
        JSONObject extInfo = JSONObject.parseObject(extraInfo);

        if (extInfo == null) {
            return null;
        }

        JSONObject actualTaskExtraInfo = TaskUtils.getActualTaskExtraInfo(extInfo);
        return Optional.ofNullable(actualTaskExtraInfo).map(JSONAware::toJSONString).orElse(null);
    }

    public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(String name,
                                                                      String tenantName,
                                                                      Integer appType,
                                                                      Long uicTenantId,
                                                                      List<Long> projectIds,
                                                                      Integer limit,
                                                                      Integer projectScheduleStatus) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (StringUtils.isNotBlank(name)) {
            name = handlerStr(name);
        }

        if (limit == null) {
            limit = environmentContext.getFuzzyProjectByProjectAliasLimit();
        }

        if (StringUtils.isBlank(name) && StringUtils.isBlank(tenantName)) {
            return buildTypeVo(null,null);
        }

        List<ScheduleTaskShade> tasks = scheduleTaskShadeDao.findFuzzyTaskNameByCondition(name, appType, uicTenantId,null, projectIds,limit,projectScheduleStatus);

        return buildTypeVo(tasks,null);
    }

    private String handlerStr(String name) {
        name = name.replaceAll("%", "\\%");
        name = name.replaceAll("'", "");
        name = name.replaceAll("_", "\\_");
        return name;
    }

    private List<ScheduleTaskShadeTypeVO> buildTypeVo(List<ScheduleTaskShade> tasks,Map<Long, String> collect) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }

        List<ScheduleTaskShadeTypeVO> vos = Lists.newArrayList();
        List<Long> tenantIds = tasks.stream().map(ScheduleTaskShade::getTenantId).collect(Collectors.toList());
        Map<Integer, List<Long>> groupProjectIdsByAppType = tasks.stream()
                .collect(
                        Collectors.groupingBy(ScheduleTaskShade::getAppType,
                                new SimpleCollector<>(ArrayList::new,(a,b)-> Optional.ofNullable(b.getProjectId()).ifPresent(a::add),
                                        (a, b)-> a,
                                        Sets.newHashSet(Collector.Characteristics.UNORDERED)))
                );
        Table<Integer, Long, AuthProjectVO> projectGroup = projectService.getProjectGroupAppType(groupProjectIdsByAppType);
        Map<Long, TenantDeletedVO> tenantMap = tenantService.listAllTenantByDtUicTenantIds(tenantIds);
        for (ScheduleTaskShade task : tasks) {
            ScheduleTaskShadeTypeVO vo = new ScheduleTaskShadeTypeVO();
            vo.setId(task.getId());
            vo.setProjectId(task.getProjectId());
            vo.setTaskId(task.getTaskId());
            vo.setAppType(task.getAppType());
            vo.setName(task.getName());
            vo.setDtuicTenantId(task.getDtuicTenantId());
            vo.setTaskType(task.getTaskType());
            vo.setEngineType(task.getEngineType());
            vo.setComputeType(task.getComputeType());
            vo.setOwnerUserId(task.getOwnerUserId());
            vo.setPeriodType(task.getPeriodType());

            TenantDeletedVO tenant = tenantMap.get(task.getDtuicTenantId());
            if (null != tenant){
                vo.setTenantName(tenant.getTenantName());
            }
            AuthProjectVO authProjectVO = projectGroup.get(task.getAppType(),task.getProjectId());
            if (authProjectVO != null) {
                vo.setProjectName(authProjectVO.getProjectName());
                vo.setProjectAlias(authProjectVO.getProjectAlias());
            }

            if (collect != null) {
                vo.setOwnerUserName(collect.get(task.getOwnerUserId()));
            }

            vos.add(vo);

        }
        return vos;
    }

    public List<ScheduleTaskTaskShade> getTaskOtherPlatformByProjectId(Long projectId, Integer appType, Integer listChildTaskLimit) {
        return scheduleTaskTaskShadeService.getTaskOtherPlatformByProjectId(projectId,appType,listChildTaskLimit);
    }

    public ScheduleDetailsVO findTaskRuleTask(Long taskId, Integer appType) {

        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (taskId == null) {
            throw new RdosDefineException("taskId must be passed");
        }

        ScheduleTaskShade shadeDaoOne = scheduleTaskShadeDao.getOne(taskId, appType);

        if (shadeDaoOne == null) {
            throw new RdosDefineException("task not exist");
        }

        ScheduleDetailsVO vo = buildScheduleDetailsVO(shadeDaoOne);
        List<ScheduleDetailsVO> vos =Lists.newArrayList();
        build(taskId, appType, vos);
        vo.setScheduleDetailsVOList(vos);
        return vo;
    }

    private void build(Long taskId, Integer appType, List<ScheduleDetailsVO> vos) {
        List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listTaskRuleTask(taskId, appType);

        for (ScheduleTaskShade taskShade : scheduleTaskShades) {
            if (!TaskRuleEnum.NO_RULE.getCode().equals(taskShade.getTaskRule())) {
                ScheduleDetailsVO voSon = buildScheduleDetailsVO(taskShade);
                if (voSon != null) {
                    vos.add(voSon);
                }
            }
        }
    }

    private ScheduleDetailsVO buildScheduleDetailsVO(ScheduleTaskShade taskShade) {
        if (taskShade != null) {
            ScheduleDetailsVO vo = new ScheduleDetailsVO();
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

            // 查询租户
            String tenantName = tenantService.getTenantName(taskShade.getDtuicTenantId());
            vo.setTenantName(tenantName);

            // 查询项目
            AuthProjectVO authProjectVO = projectService.finProject(taskShade.getProjectId(), taskShade.getAppType());
            if (authProjectVO != null) {
                vo.setProjectName(authProjectVO.getProjectName());
                vo.setProjectAlias(authProjectVO.getProjectAlias());
            }

            // 查询资源组
            Long resourceId = taskShade.getResourceId();
            Optional<ResourceGroup> resourceGroupOptional = resourceGroupService.getResourceGroup(resourceId);
            if (resourceGroupOptional.isPresent()) {
                ResourceGroup resourceGroup = resourceGroupOptional.get();
                vo.setResourceName(resourceGroup.getName());
            }
            if (null != taskShade.getPeriodType() && ESchedulePeriodType.CALENDER.getVal() == taskShade.getPeriodType()) {
                List<CalenderTaskDTO> calender = calenderService.getCalenderByTasks(Lists.newArrayList(taskShade.getTaskId()), taskShade.getAppType());
                if (!CollectionUtils.isEmpty(calender)) {
                    vo.setCalenderId(calender.get(0).getCalenderId());
                    vo.setCalenderName(calender.get(0).getCalenderName());
                }
            }

            List<ScheduleTaskTag> tags = scheduleTaskTagService.findTagByTaskIds(Sets.newHashSet(taskShade.getTaskId()), taskShade.getAppType());
            vo.setTagIds(tags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList()));

            Integer priority = scheduleTaskPriorityService.selectPriorityByTaskId(taskShade.getTaskId(), taskShade.getAppType());
            vo.setPriority(priority);
            return vo;
        }
        return null;
    }

    public List<ScheduleTaskShade> findChildTaskRuleByTaskId(Long taskId, Integer appType) {
        if (appType == null) {
            throw new RdosDefineException("appType must be passed");
        }

        if (taskId == null) {
            throw new RdosDefineException("taskId must be passed");
        }
        List<ScheduleTaskShade> taskShades = Lists.newArrayList();
        List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listTaskRuleTask(taskId, appType);

        for (ScheduleTaskShade taskShade : scheduleTaskShades) {
            if (!TaskRuleEnum.NO_RULE.getCode().equals(taskShade.getTaskRule())) {
                if (!EScheduleStatus.PAUSE.getVal().equals(taskShade.getScheduleStatus())
                        && !EProjectScheduleStatus.PAUSE.getStatus().equals(taskShade.getProjectScheduleStatus())) {
                    taskShades.add(taskShade);
                }
            }
        }

        return taskShades;
    }

    /**
     * 按照appType和taskId分组查询
     * @param groupByAppMap 分组数据
     * @return
     */
    public Map<Integer,List<ScheduleTaskShade>> listTaskShadeByIdAndType(Map<Integer,Set<Long>> groupByAppMap){
        if (MapUtils.isEmpty(groupByAppMap)){
            throw new RdosDefineException("taskId或appType不能为空");
        }
        Map<Integer,List<ScheduleTaskShade>> scheduleTaskShadeMap=new HashMap<>(groupByAppMap.size());
        for (Map.Entry<Integer, Set<Long>> entry : groupByAppMap.entrySet()) {
            scheduleTaskShadeMap.put(entry.getKey(),scheduleTaskShadeDao.listByTaskIds(entry.getValue(), Deleted.NORMAL.getStatus(), entry.getKey()));
        }
        return scheduleTaskShadeMap;
    }

    /**
     * 校验cron表达式
     * @return
     */
    public CronExceptionVO checkCronExpression(String cron, Long minPeriod) {
        String[] timeFields = cron.split("\\s+");
        if (timeFields.length != 6) {
            return new CronExceptionVO(CronExceptionVO.CHECK_EXCEPTION, "illegal param of cron str:" + cron);
        }

        CronExpression cronExpression = null;
        try {
            CronExpression.validateExpression(cron);
            cronExpression = new CronExpression(cron);
        } catch (Exception e) {
            return new CronExceptionVO(CronExceptionVO.CHECK_EXCEPTION, ExceptionUtil.getErrorMessage(e));
        }
        minPeriod *= 1000;
        // 第一次执行的时间
        Date curRunTime = cronExpression.getNextValidTimeAfter(new Date()), nextRunTime;
        Date startDateTime = new Date(curRunTime.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().atStartOfDay().plusSeconds(-1L).toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());

        Date endTDateTime = new Date(curRunTime.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        while (curRunTime.after(startDateTime) && curRunTime.before(endTDateTime)) {
            nextRunTime = cronExpression.getNextValidTimeAfter(curRunTime);
            if (nextRunTime.getTime() - minPeriod < curRunTime.getTime()) {
                return new CronExceptionVO(CronExceptionVO.PERIOD_EXCEPTION, String.format("%s run too frequency and min period = %sS", cron, minPeriod / 1000));
            }
            curRunTime = nextRunTime;
        }
        return null;
    }

    /**
     * 指定范围内最近多少条运行时间
     *
     * @param startDate 开始
     * @param endDate   结束
     * @param cron      cron
     * @param num       条数
     * @return 运行数据
     */
    public List<String> recentlyRunTime(String startDate, String endDate, String cron, int num) {
        String[] timeFields = cron.split("\\s+");
        if (timeFields.length != 6) {
            throw new RdosDefineException("illegal param of cron str:" + cron);
        }
        CronExpression cronExpression;
        try {
            cronExpression = new CronExpression(cron);
        } catch (Exception e) {
            throw new RdosDefineException("illegal cron expression");
        }
        List<String> recentlyList = new ArrayList<>(num);
        Date nowDate = new Date();
        Date start = DateUtil.parseDate(startDate, DateUtil.DATE_FORMAT);
        // 当前时间在开始时间后,以下一天开始的时间为起始时间
        if (nowDate.after(start)) {
            start = new Date(nowDate.toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                    .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        } else {
            start = new Date(start.getTime() - 1000);
        }
        Date end = new Date(DateUtil.parseDate(endDate, DateUtil.DATE_FORMAT).toInstant().atOffset(DateUtil.DEFAULT_ZONE)
                .toLocalDate().plusDays(1).atStartOfDay().toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());

        Date curDate = cronExpression.getNextValidTimeAfter(start);
        while (num-- > 0 && curDate.before(end) && curDate.after(start)) {
            recentlyList.add(DateUtil.getDate(curDate, DateUtil.STANDARD_DATETIME_FORMAT));
            curDate = cronExpression.getNextValidTimeAfter(curDate);
        }
        return recentlyList;
    }

    private void checkSubmitTaskCron(ScheduleTaskShadeDTO taskShade) {
        //手动任务没有调度属性
        if (ETaskGroupEnum.MANUAL.getType().equals(taskShade.getTaskGroup())) {
            return;
        }
        // 优先使用periodType
        if (taskShade.getPeriodType() != null &&
                taskShade.getPeriodType() != ESchedulePeriodType.CUSTOM.getVal()) {
            return;
        }
        // 没有periodType再去反序列化
        JSONObject scheduleConf = JSON.parseObject(taskShade.getScheduleConf());
        if (Objects.isNull(scheduleConf)) {
            throw new RdosDefineException("empty schedule conf");
        }
        // 非自定义调度
        if (scheduleConf.getInteger("periodType") != ESchedulePeriodType.CUSTOM.getVal()) {
            return;
        }
        String cron = scheduleConf.getString("cron");
        CronExceptionVO cronExceptionVO = checkCronExpression(cron, 300L);
        if (Objects.nonNull(cronExceptionVO)) {
            throw new RdosDefineException(cronExceptionVO.getErrMessage());
        }

    }

    public List<TaskTypeVO> getTaskType() {
        EScheduleJobType[] values = EScheduleJobType.values();
        List<TaskTypeVO> taskTypeVOS = Lists.newArrayList();
        for (EScheduleJobType value : values) {
            TaskTypeVO vo = new TaskTypeVO();
            vo.setCode(value.getType());
            vo.setName(value.getName());
            vo.setEnumName(value.name());
            taskTypeVOS.add(vo);
        }
        return taskTypeVOS;
    }

    public List<ScheduleTaskShadeDTO> findTaskKeyByProjectId(Long projectId, Integer appType, List<Integer> taskTypes, List<Long> taskIds, Integer taskGroup) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.findTaskKeyByProjectId(projectId, appType, taskTypes, taskGroup,taskIds);
        List<ScheduleTaskShadeDTO> shadeDTOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(shades)) {
            for (ScheduleTaskShade shade : shades) {
                ScheduleTaskShadeDTO shadeDTO = scheduleTaskShadeStruct.toScheduleTaskShadeDTO(shade);
                shadeDTOS.add(shadeDTO);
            }
        }

        return shadeDTOS;
    }

    public List<ScheduleTaskShade> findSimpleTaskByProjectId(Long projectId, Integer appType,Integer taskGroup) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listSimpleByNodePIdsAndAppTypeAndProjectIdAndTaskIds(null,null, projectId, appType,taskGroup);
        return Optional.ofNullable(shades).orElse(Lists.newArrayList());
    }

    public List<ScheduleTaskShade> findSimpleTaskByNodePIds(List<Long> nodePIds, Long projectId, Integer appType,Integer taskGroup) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listSimpleByNodePIdsAndAppTypeAndProjectIdAndTaskIds(null,nodePIds, projectId, appType,taskGroup);
        return Optional.ofNullable(shades).orElse(Lists.newArrayList());
    }

    public List<ScheduleTaskShade> findSimpleTaskByTaskIds(List<Long> taskIds, Integer appType, Integer taskGroup) {
        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.listSimpleByNodePIdsAndAppTypeAndProjectIdAndTaskIds(taskIds,null, null, appType,taskGroup);
        return Optional.ofNullable(shades).orElse(Lists.newArrayList());
    }

    public List<ScheduleTaskShade> listByIdsAndTaskName(List<Long> ids,String taskName) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return scheduleTaskShadeDao.listByIdsAndTaskName(ids,taskName);
        }
        return Lists.newArrayList();
    }

    public List<ScheduleTaskShadeTypeVO> findTask(FindTaskVO findTaskVO) {
        if (findTaskVO.getAppType() == null) {
            throw new RdosDefineException("appType must be passed");
        }

        String name = findTaskVO.getName();
        if (StringUtils.isNotBlank(name)) {
            name = handlerStr(name);
        }

        if (null == findTaskVO.getTaskGroup()) {
            findTaskVO.setTaskGroup(ETaskGroupEnum.NORMAL_SCHEDULE.getType());
        }

        findTaskVO.setName(name);

        Integer limit = findTaskVO.getLimit();
        if (limit == null) {
            findTaskVO.setLimit(environmentContext.getFuzzyProjectByProjectAliasLimit());
        }

        List<Long> tenantIds = Lists.newArrayList();
        String ownerUserName = findTaskVO.getOwnerUserName();
        Map<Long, String> collect = Maps.newHashMap();
        if (StringUtils.isNotBlank(ownerUserName)) {

            ApiResponse<List<UICUserListResult>> currentUserByName = uicUserApiClient.findCurrentUserByName(ownerUserName);

            if (currentUserByName.success) {
                List<UICUserListResult> data = currentUserByName.getData();
                tenantIds.addAll(data.stream().map(UICUserListResult::getUserId).collect(Collectors.toList()));
                collect = data.stream().collect(Collectors.toMap(UICUserListResult::getUserId, UICUserListResult::getUserName));

                findTaskVO.setUserIds(tenantIds);
            }
        }

        List<ScheduleTaskShade> tasks = scheduleTaskShadeDao.listByFindTask(findTaskVO);
        return buildTypeVo(tasks,collect);
    }

   public List<ScheduleTaskShade> listByNameLikeWithSearchType(Long projectId, String name, Integer appType,
                                                        Long ownerId,List<Long> projectIds,Integer searchType,List<String> versions,Integer computeType){
        return scheduleTaskShadeDao.listByNameLikeWithSearchType(projectId,name,appType,ownerId,projectIds,searchType,versions,computeType);
    }

    public List<String> getTaskNameByJobKeys(List<String> relyJobParentKeys) {
        List<Long> ids = Lists.newArrayList();
        for (String relyJobParentKey : relyJobParentKeys) {
            String[] jobKeySplit = relyJobParentKey.split("_");
            if (jobKeySplit.length < 3) {
               continue;
            }

            String taskIdStr = jobKeySplit[jobKeySplit.length - 2];
            Long taskShadeId = MathUtil.getLongVal(taskIdStr);

            ids.add(taskShadeId);
        }

        List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeDao.listByIds(ids);

        if (CollectionUtils.isNotEmpty(scheduleTaskShades)) {
            return scheduleTaskShades.stream().map(ScheduleTaskShade::getName).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public void batchUpdateScheduleConf(List<Long> taskIds, long modifyUserId, Integer appType, String scheduleConf,Integer periodType) {
        scheduleTaskShadeDao.updateScheduleConf(taskIds,appType,modifyUserId,scheduleConf,periodType);
    }

    public List<ScheduleTaskShade> getTasksByFlowId(Long taskId, Integer appType) {
       return scheduleTaskShadeDao.getTasksByFlowId(taskId,appType);
    }

    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public OfflineReturnVO offline(List<Long> taskIds, Integer appType) {
        OfflineReturnVO vo = new OfflineReturnVO();

        // 判断选择的taskIds中有没有工作流任务，或者是工作流子任务
        List<ScheduleTaskShade> scheduleTaskShadeList = scheduleTaskShadeDao.listByTaskIds(taskIds, IsDeletedEnum.NOT_DELETE.getType(), appType);

        List<ScheduleTaskShade> offlineTaskIds = Lists.newArrayList(scheduleTaskShadeList);
        for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShadeList) {
            if (!scheduleTaskShade.getFlowId().equals(0L) && !taskIds.contains(scheduleTaskShade.getFlowId())) {
                vo.setSuccess(Boolean.FALSE);
                vo.setMsg("“工作流中的子节点不可单独下线”，此批次全部任务下线失败");
                return vo;
            }

            Integer taskType = scheduleTaskShade.getTaskType();
            if (EScheduleJobType.WORK_FLOW.getType().equals(taskType) || EScheduleJobType.ALGORITHM_LAB.getVal().equals(taskType)) {
                // 说明是工作流任务
                List<ScheduleTaskShade> subTasks = getFlowWorkSubTasks(scheduleTaskShade.getTaskId(), scheduleTaskShade.getAppType(), null, null);
                offlineTaskIds.addAll(subTasks);
            }
        }

        // 下线任务
        Set<Long> taskSets = Sets.newHashSet();
        Set<String> taskKeySets = Sets.newHashSet();
        for (ScheduleTaskShade offlineTaskId : offlineTaskIds) {
            taskSets.add(offlineTaskId.getTaskId());
            taskKeySets.add(offlineTaskId.getTaskId() + "-" + offlineTaskId.getAppType());
        }

        scheduleTaskShadeDao.deleteTombstoneByTaskIds(taskSets, appType);
        scheduleTaskTaskShadeService.deleteTombstoneByTaskKeys(taskKeySets);

        baselineTaskService.deleteTaskOfBaseline(taskIds,appType);
        alertAlarmService.deleteAlarmTask(taskIds,appType,AlarmRuleBusinessTypeEnum.TASK);
        // 任务下线，从管理的黑名单里去掉
        alertAlarmService.deleteAlarmTask(taskIds,appType,AlarmRuleBusinessTypeEnum.BLACK_LIST);
        clearTaskBoundInfo(taskIds,appType);
        vo.setSuccess(Boolean.TRUE);
        return vo;
    }

    /**
     * 保存任务上下游配置参数「taskParamsToReplace」
     * 备注：内部调用，由外层控制事务
     * @param taskId
     * @param appType
     * @param info
     */
    private void saveTaskChainParamIfNeed(Long taskId, Integer appType, String info) {
        if (!AppType.RDOS.getType().equals(appType)) {
            return;
        }
        if (StringUtils.isEmpty(info)) {
            return;
        }
        JSONObject infoJson = JSONObject.parseObject(info);
        String taskParamsToReplaceStr = infoJson.getString(TaskConstant.TASK_PARAMS_TO_REPLACE);
        if (StringUtils.isEmpty(taskParamsToReplaceStr)) {
            // 清掉旧数据
            scheduleTaskChainParamDao.deleteInOutParamsByTaskShade(taskId, appType);
            return;
        }
        List<ScheduleTaskParamShade> taskParamShades = JSONObject.parseArray(taskParamsToReplaceStr, ScheduleTaskParamShade.class);
        if (CollectionUtils.isEmpty(taskParamShades)) {
            return;
        }
        // 是否包含任务上下游入出参配置项
        boolean containsInOutParam = JobChainParamHandler.containsInOutParam(taskParamShades);
        if (!containsInOutParam) {
            // 清掉旧数据
            scheduleTaskChainParamDao.deleteInOutParamsByTaskShade(taskId, appType);
            return;
        }
        ScheduleTaskShade oneTask = scheduleTaskShadeDao.getOneByTaskIdAndAppType(taskId, appType);
        if (oneTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        // 是否支持
        if (!JobChainParamHandler.isSupport(oneTask.getTaskType())) {
            return;
        }
        // 校验配置项参数合法性
        JobChainParamHandler.validateTaskParamShade(taskParamShades, oneTask);
        // 筛掉非上下游参数
        taskParamShades.removeIf(p -> !EParamType.isChainParam(p.getType()));
        // 先删后插
        scheduleTaskChainParamDao.deleteInOutParamsByTaskShade(taskId, appType);
        scheduleTaskChainParamDao.batchSave(JobChainParamHandler.trans2TaskChainParams(taskParamShades));
    }

    public List<ScheduleTaskShade> queryTasksByNames(List<String> taskNames, Integer appType, Long projectId) {
        return scheduleTaskShadeDao.listByTaskNames(taskNames, appType, projectId);
    }

    public Long findMinIdByTaskType(List<Integer> taskTypes,Integer taskGroup,List<Long> taskIds) {
        return scheduleTaskShadeDao.findMinIdByTaskType(taskTypes,taskIds,taskGroup);
    }

    public List<ScheduleTaskShadeDTO> findTaskByTaskType(Long minId, List<Integer> taskTypes,Integer taskGroup,List<Long> taskIds, Integer fillDataLimitSize,Boolean isEquals) {
        if (minId == null) {
            return Lists.newArrayList();
        }

        if (fillDataLimitSize == null) {
            fillDataLimitSize = 2000;
        }

        List<ScheduleTaskShade> shades = scheduleTaskShadeDao.findTaskByTaskType(minId, taskTypes,taskIds, fillDataLimitSize,taskGroup,isEquals);
        List<ScheduleTaskShadeDTO> shadeDTOS = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(shades)) {
            for (ScheduleTaskShade shade : shades) {
                ScheduleTaskShadeDTO shadeDTO = scheduleTaskShadeStruct.toScheduleTaskShadeDTO(shade);
                shadeDTOS.add(shadeDTO);
            }
        }

        return shadeDTOS;
    }

    /**
     * 批量修改任务责任人
     *
     * @param taskIdList  任务ID
     * @param ownerUserId 责任人
     * @param appType
     */
    public void updateTaskOwner(List<Long> taskIdList, Long ownerUserId, Integer appType) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }
        scheduleTaskShadeDao.batchUpdateTaskOwnerUserId(taskIdList, ownerUserId, appType);
    }

    public ScheduleTaskShade getSimpleByTaskIdAndAppType(Long taskId, Integer appType) {
        return scheduleTaskShadeDao.getSimpleByTaskIdAndAppType(taskId, appType);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteTag(Long tagId,Integer appType) {
        List<Long> alarmIds = alertAlarmRuleDao.selectAlarmByBusinessTypeAndBusinessId(AlarmRuleBusinessTypeEnum.TAG_LIST.getCode()
                , Lists.newArrayList(tagId));
        if (CollectionUtils.isEmpty(alarmIds)) {
            return scheduleTaskTagService.deleteTagByTagId(tagId);
        }

        // 重新计算标签下的任务
        List<AlertAlarmRule> alertAlarmRules = alertAlarmRuleDao.selectByAlarmIds(alarmIds, null);
        List<Long> ruleTagIds = alertAlarmRules.stream()
                .filter(alertAlarmRule -> AlarmRuleBusinessTypeEnum.TAG_LIST.getCode().equals(alertAlarmRule.getBusinessType())
                        && alertAlarmRule.getBusinessId().equals(tagId))
                .map(AlertAlarmRule::getId)
                .collect(Collectors.toList());
        Map<Long, List<AlertAlarmRule>> alertMap = alertAlarmRules.stream().
                collect(Collectors.groupingBy(AlertAlarmRule::getAlertAlarmId));

        List<AlertAlarmRule> insertRule = Lists.newArrayList();
        List<Long> ruleIds = Lists.newArrayList(ruleTagIds);
        for (Long alertAlarmId : alertMap.keySet()) {
            List<AlertAlarmRule> rules = alertMap.get(alertAlarmId);
            AlertAlarm alertAlarm = alertAlarmDao.selectByPrimaryKey(alertAlarmId);
            List<AlertAlarmRule> alarmRules = rules.stream().filter(rule ->
                            AlarmRuleBusinessTypeEnum.TAG_LIST.getCode().equals(rule.getBusinessType()) && !rule.getBusinessId().equals(tagId))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(alarmRules)) {
                // 说明告警仅设置了一个标签，删除标签直接删除告警
                alertAlarmRuleDao.deleteByAlertId(alertAlarmId);
                alertAlarmDao.deleteByPrimaryKey(alertAlarmId);
            } else {
                List<Long> tagIds = alarmRules.stream().map(AlertAlarmRule::getBusinessId).collect(Collectors.toList());
                List<ScheduleTaskTag> tagsByTagIds = scheduleTaskTagService.findTagsByTagIds(tagIds,appType);


                List<Long> allTasks = Lists.newArrayList();
                Map<Long, List<ScheduleTaskTag>> tagMap = tagsByTagIds.stream().collect(Collectors.groupingBy(ScheduleTaskTag::getTaskId));

                for (Long taskId : tagMap.keySet()) {
                    List<ScheduleTaskTag> scheduleTaskTags = tagMap.get(taskId);
                    List<Long> taskTagIds = scheduleTaskTags.stream().map(ScheduleTaskTag::getTagId).collect(Collectors.toList());

                    // tagIds 是否是taskTagIds的子集
                    if (ListUtil.isSubList(taskTagIds,tagIds)) {
                        allTasks.add(taskId);
                    }
                }
                List<Long> blackTaskIds = rules.stream().filter(rule ->
                                AlarmRuleBusinessTypeEnum.BLACK_LIST.getCode().equals(rule.getBusinessType()))
                        .map(AlertAlarmRule::getBusinessId).collect(Collectors.toList());

                List<ScheduleTaskShade> shades = this.findSimpleTaskByTaskIds(allTasks,appType,
                        alertAlarmService.convertAlertTypeToETaskGroupEnum(alertAlarm.getAlarmType()));
                List<ScheduleTaskShade> scheduleTaskShades = alertAlarmService.filterAndCollectTasks(shades, blackTaskIds);

                // 需要delete的任务
                List<Long> ids = rules.stream().filter(rule ->
                                AlarmRuleBusinessTypeEnum.TASK.getCode().equals(rule.getBusinessType()))
                        .map(AlertAlarmRule::getId).collect(Collectors.toList());
                ruleIds.addAll(ids);

                for (ScheduleTaskShade scheduleTaskShade : scheduleTaskShades) {
                    insertRule.add(alertAlarmService.buildAlertAlarmRule(alertAlarmId,scheduleTaskShade.getTaskId(),AlarmRuleBusinessTypeEnum.TASK));
                }
            }

            if (CollectionUtils.isNotEmpty(ruleIds)) {
                alertAlarmRuleDao.deleteByIds(ruleIds);
            }

            if (CollectionUtils.isNotEmpty(insertRule)) {
                alertAlarmRuleDao.batchInsert(insertRule);
            }

        }

        return scheduleTaskTagService.deleteTagByTagId(tagId);
    }

    public List<Long> selectTagByTask(Long taskId, Integer appType) {
        if (taskId == null || appType == null) {
            return Lists.newArrayList();
        }
        return scheduleTaskTagService.selectTagByTask(taskId,appType);
    }

    public List<TaskInfoVO> findTaskNames(List<TaskKeyVO> taskKeyVOS) {
        List<TaskInfoVO> vos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskKeyVOS)) {
            Map<Integer, List<Long>> listMap = taskKeyVOS.stream().collect(Collectors.groupingBy(TaskKeyVO::getAppType,Collectors.mapping(TaskKeyVO::getTaskId,Collectors.toList())));
            for (Integer appType : listMap.keySet()) {
                List<Long> taskIds = listMap.get(appType);
                List<ScheduleTaskForFillDataDTO> dtos = scheduleTaskShadeDao.listSimpleTaskByTaskIds(taskIds, null, appType);

                dtos.forEach(dto-> vos.add(buildTaskInfo(dto)));
            }
        }
        return vos;
    }

    private TaskInfoVO buildTaskInfo(ScheduleTaskForFillDataDTO dto) {
        TaskInfoVO taskInfoVO = new TaskInfoVO();
        taskInfoVO.setDeleted(dto.getIsDeleted());
        taskInfoVO.setTaskId(dto.getTaskId());
        taskInfoVO.setAppType(dto.getAppType());
        taskInfoVO.setName(dto.getName());
        return taskInfoVO;
    }
}
