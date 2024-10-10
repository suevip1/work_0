package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.task.FindTaskVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.dao.BaselineTaskTaskDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskChainParamDao;
import com.dtstack.engine.dao.ScheduleTaskCommitMapper;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.dto.TaskName;
import com.dtstack.engine.master.dto.BaselineTaskDTO;
import com.dtstack.engine.master.impl.AlertAlarmService;
import com.dtstack.engine.master.impl.BaselineTaskService;
import com.dtstack.engine.master.impl.CalenderService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ParamService;
import com.dtstack.engine.master.impl.ResourceGroupService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.WorkSpaceProjectService;
import com.dtstack.engine.master.mapstruct.ScheduleTaskShadeStruct;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.scheduler.parser.ESchedulePeriodType;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ResourceGroup;
import com.dtstack.engine.po.ResourceGroupDetail;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import com.dtstack.engine.po.ScheduleTaskCommit;
import com.dtstack.engine.po.TenantResource;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import org.apache.ibatis.annotations.Param;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ScheduleTaskShadeServiceMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    public List<ScheduleTaskShade> findTaskKeyByProjectId(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("taskTypes") List<Integer> taskTypes, @Param("taskGroup") Integer taskGroup) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> findFuzzyTaskNameByCondition(@Param("name") String name,
                                                         @Param("appType") Integer appType,
                                                         @Param("uicTenantId") Long uicTenantId,
                                                         @Param("tenantIds") List<Long> tenantIds,
                                                         @Param("projectIds") List<Long> projectIds,
                                                         @Param("fuzzyProjectByProjectAliasLimit") Integer fuzzyProjectByProjectAliasLimit,
                                                         @Param("projectScheduleStatus") Integer projectScheduleStatus) {
        return new ArrayList<>();
    }



    @MockInvoke(targetClass = AlertAlarmService.class)
    public void deleteAlarmTask(List<Long> alertAlarmBusinessIds, Integer appType, AlarmRuleBusinessTypeEnum alarmRuleBusinessTypeEnum) {

    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public void deleteTaskOfBaseline(List<Long> taskIds, Integer appType) {

    }

    @MockInvoke(targetClass = CalenderService.class)
    public void addOrUpdateTaskCalender(Long taskId, Integer appType, Long calenderId, String expendTime) {

    }


    @MockInvoke(targetClass = ScheduleJobDao.class)
    public void updateByTaskIdAndVersionId(@Param("versionId")Integer versionId, @Param("taskId")Long taskId, @Param("appType")Integer appType, @Param("oldVersionId")Integer oldVersionId, @Param("status")Integer status) {

    }

    @MockInvoke(targetClass = BaselineTaskService.class)
    public List<BaselineTaskDTO> getBaselineTaskByIds(List<Long> baselineTaskIds) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = BaselineTaskTaskDao.class)
    List<Long> selectByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType") Integer appType) {
        return new ArrayList<>();
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShadeCountTaskVO> countTaskByType(Long tenantId, Long dtuicTenantId, List<Long> projectIds, Integer appType, List<Integer> taskTypes,
                                                       Long flowId) {
        ScheduleTaskShadeCountTaskVO scheduleTaskShade = new ScheduleTaskShadeCountTaskVO();
        scheduleTaskShade.setProjectId("1");
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<TaskName> listNameByIds(Collection<Long> ids) {
        TaskName taskName = new TaskName();
        taskName.setName("test");
        return Lists.newArrayList(taskName);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listSimpleByTaskIds(Collection<Long> taskIds, Integer isDeleted, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ResourceGroupService.class)
    public Optional<ResourceGroup> getResourceGroup(Long resourceId) {
        return Optional.empty();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> findFuzzyTaskNameByCondition(String name, Integer appType, Long uicTenantId, List<Long> projectIds, Integer fuzzyProjectByProjectAliasLimit, Integer projectScheduleStatus) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByIds(Collection<Long> ids) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    String getExtInfoByTaskId(Long taskId, Integer appType, String commitId) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    Long findMinIdOfTaskCommitByCommitId(String commitId) {
        return 1L;
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    List<ScheduleTaskCommit> findTaskCommitByCommitId(Long minId, String commitId, Integer limit) {
        if (null != minId && minId == 1L) {
            ScheduleTaskCommit scheduleTaskCommit = new ScheduleTaskCommit();
            scheduleTaskCommit.setCommitId(commitId);
            scheduleTaskCommit.setAppType(1);
            ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
            scheduleTaskShade.setTaskId(100L);
            scheduleTaskShade.setAppType(1);
            scheduleTaskShade.setName("test");
            scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
            scheduleTaskCommit.setTaskJson(JSONObject.toJSONString(scheduleTaskShade));
            scheduleTaskCommit.setExtraInfo("{}");
            scheduleTaskCommit.setId(2L);
            return Lists.newArrayList(scheduleTaskCommit);
        }
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    Boolean updateTaskCommit(Long id) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    Boolean updateTaskExtInfo(Long taskId, Integer appType, String info, String commitId) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleTaskCommitMapper.class)
    Boolean insertBatch(List<ScheduleTaskCommit> scheduleTaskCommits) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeStruct.class)
    ScheduleTaskShadeDTO toScheduleTaskShadeDTO(ScheduleTaskShade shade){
        return new ScheduleTaskShadeDTO();
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public Integer deleteTombstoneByTaskKeys(Collection<String> jobKeys) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer deleteTombstoneByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("appType") Integer appType) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByTaskIds(Collection<Long> taskIds, Integer isDeleted, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);

    }

    @MockInvoke(targetClass = TenantResourceDao.class)
    TenantResource selectByUicTenantIdAndTaskType(Long dtUicTenantId, Integer taskType) {
        TenantResource resource = new TenantResource();
        resource.setTaskType(taskType);
        if (EScheduleJobType.SPARK_SQL.getType().equals(taskType)) {
            resource.setResourceLimit("{\"executor.memory\":\"23\",\"driver.cores\":\"89\",\"executor.cores\":\"23333\"}");
        }
        if (EScheduleJobType.SHELL.getType().equals(taskType)) {
            resource.setResourceLimit("{\"worker.num\":\"1\",\"worker.memory\":\"2\",\"worker.cores\":\"3\"}");
        }
        return resource;
    }

    @MockInvoke(targetClass = ScheduleTaskChainParamDao.class)
    int batchSave(List<ScheduleTaskChainParam> list) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskChainParamDao.class)
    int deleteByTaskShade(Long taskId, Integer appType) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOneByTaskIdAndAppType(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return scheduleTaskShade;

    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> getChildTaskByOtherPlatform(Long taskId, Integer appType, Integer limit) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = TenantService.class)
    public Map<Long, TenantDeletedVO> listAllTenantByDtUicTenantIds(Collection<Long> ids) {
        TenantDeletedVO tenantDeletedVO = new TenantDeletedVO();
        tenantDeletedVO.setTenantName("test");
        tenantDeletedVO.setTenantId(1L);
        Map<Long, TenantDeletedVO> map = new HashMap<>();
        map.put(1L, tenantDeletedVO);
        return map;
    }

    @MockInvoke(targetClass = WorkSpaceProjectService.class)
    public AuthProjectVO finProject(Long projectId, Integer appType) {
        AuthProjectVO authProjectVO = new AuthProjectVO();
        authProjectVO.setProjectName("test");
        return authProjectVO;
    }


    @MockInvoke(targetClass = ParamService.class)
    public void deleteTaskParamByTaskId(List<Long> taskId, Integer appType) {
        return;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public void deleteTask(List<Long> taskIds, Integer appType) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer deleteByTaskId(long taskId, Integer appType) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer delete(long taskId, long modifyUserId, Integer appType) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void updateTaskExtInfo(long taskId, Integer appType, String extraInfo) {

    }


    @MockInvoke(targetClass = ParamService.class)
    public void convertGlobalToParamType(String info, BiConsumer<List<ConsoleParam>, List<ScheduleTaskParamShade>> convertConsumer) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer countPublishToProduce(Long projectId, Integer appType) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> generalQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShade.setFlowId(1L);
        return Lists.newArrayList(scheduleTaskShade);
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listTaskRuleTask(Long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setPeriodType(ESchedulePeriodType.WEEK.getVal());
        return Lists.newArrayList(scheduleTaskShade);

    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> listByFindTask(FindTaskVO findTaskVO) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> findTaskKeyByProjectId(Long projectId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        scheduleTaskShade.setFlowId(1L);
        scheduleTaskShade.setPeriodType(ESchedulePeriodType.WEEK.getVal());
        return Lists.newArrayList(scheduleTaskShade);
    }


    @MockInvoke(targetClass = TenantService.class)
    public String getTenantName(Long tenantId) {
        return "";
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer generalCount(Object model) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer simpleCount(ScheduleTaskShadeDTO pageQuery) {
        return 1;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    String getExtInfoByTaskId(long taskId, Integer appType) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    List<ScheduleTaskShade> simpleQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(100L);
        scheduleTaskShade.setAppType(1);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return Lists.newArrayList(scheduleTaskShade);
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    void deleteByFlowId(Long flowId, long modifyUserId, Integer appType) {
        return;
    }


    @MockInvoke(targetClass = ResourceGroupService.class)
    public Map<Long, ResourceGroupDetail> getGroupInfo(List<Long> resourceIds) {
        return new HashMap<>();
    }

    @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
    public void clearDataByTaskId(Long taskId, Integer appType) {
        return;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOne(long taskId, Integer appType) {
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setPeriodType(ESchedulePeriodType.WEEK.getVal());
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        if (taskId == -1L) {
            scheduleTaskShade.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        }
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    ScheduleTaskShade getOneIncludeDelete(Long taskId, Integer appType) {
        if (taskId < 0L) {
            return null;
        }
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setTaskId(taskId);
        scheduleTaskShade.setAppType(appType);
        scheduleTaskShade.setName("test");
        scheduleTaskShade.setTaskType(EScheduleJobType.SPARK_SQL.getType());
        return scheduleTaskShade;
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    void updateResourceIdByTaskIdAndOldResourceIdAndStatus(Long handOver, Long taskId, Integer appType, Long resourceId, Integer status) {
        return;
    }


    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer insert(ScheduleTaskShade batchTaskShade) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeDao.class)
    Integer update(ScheduleTaskShade batchTaskShade) {
        return 1;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public List<Component> getComponents(Long uicTenantId, EComponentType componentType) {
        Component component = new Component();
        component.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        component.setVersionName("2.1");
        component.setHadoopVersion("2.1");
        return Lists.newArrayList(component);
    }

    @MockInvoke(targetClass = CalenderService.class)
    public ConsoleCalender findById(long calenderId) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setCalenderName("test");
        consoleCalender.setId(1L);
        return consoleCalender;
    }

    @MockInvoke(targetClass = CalenderService.class)
    public void addOrUpdateTaskCalender(Long taskId, Integer appType, Long calenderId) {
        return;
    }
}
