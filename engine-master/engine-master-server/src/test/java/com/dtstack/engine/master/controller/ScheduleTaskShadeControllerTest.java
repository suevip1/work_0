package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.CronExceptionVO;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.api.vo.task.TaskTypeVO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MockWith(ScheduleTaskShadeControllerTest.ScheduleTaskShadeControllerMock.class)
public class ScheduleTaskShadeControllerTest {

    private static final ScheduleTaskShadeController scheduleTaskShadeController = new ScheduleTaskShadeController();

    static class ScheduleTaskShadeControllerMock {

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(String name,
                                                                          String tenantName,
                                                                          Integer appType,
                                                                          Long uicTenantId,
                                                                          List<Long> projectIds,
                                                                          Integer limit,
                                                                          Integer projectScheduleStatus) {
            return new
                    ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO) {
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public String addOrUpdateBatchTask(List<ScheduleTaskShadeDTO> batchTaskShadeDTOs, String commitId) {
            return "";
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void infoCommit(Long taskId, Integer appType, String info, String commitId) {

        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public Boolean taskCommit(String commitId) {
            return true;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void info(Long taskId, Integer appType, String info) {}

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void deleteTask(Long taskId, long modifyUserId, Integer appType, Integer taskType) {}

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<NotDeleteTaskVO> getNotDeleteTask(Long taskId, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShade> getTasksByName(Long projectId, String name, Integer appType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskShade getByName(long projectId, String name, Integer appType, Long flowId) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void updateTaskName(long taskId, String taskName, Integer appType) {

        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskShadePageVO queryTasks(Long tenantId, Long dtTenantId, Long projectId, String name, Long ownerId, Long startTime, Long endTime, Integer scheduleStatus, String taskTypeList, String periodTypeList, Integer currentPage, Integer pageSize, String searchType, Integer appType, List<Long> resourceIds) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public void frozenTask(List<Long> taskIdList, int scheduleStatus, Integer appType) {
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskVO dealFlowWorkTask(Long taskId, Integer appType, List<Integer> taskTypes, Long ownerId) {
            return new ScheduleTaskVO();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShade> getFlowWorkSubTasks(Long taskId, Integer appType, List<Integer> taskTypes, Long ownerId) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShade> findTaskIds(List<Long> taskIds, Integer isDeleted, Integer appType, boolean isSimple) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskShade findTaskId(Long taskId, Integer isDeleted, Integer appType) {
            return new ScheduleTaskShade();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<Map<String, Object>> listDependencyTask(List<Long> taskId, String name, Long projectId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<Map<String, Object>> listByTaskIdsNotIn(List<Long> taskId, Integer appType, Long projectId) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleTaskShadeCountTaskVO countTaskByType(Long tenantId, Long dtuicTenantId, Long projectId, Integer appType, List<Integer> taskTypes) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShade> getTaskByIds(List<Long> taskIds, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShadeCountTaskVO> countTaskByTypes(Long tenantId, Long dtuicTenantId, List<Long> projectIds, Integer appType, List<Integer> taskTypes) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<String> checkResourceLimit(Long dtuicTenantId, Integer taskType, String resourceParams, Long taskId) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(String name, Integer appType, Long uicTenantId, List<Long> projectIds, Integer limit, Integer projectScheduleStatus) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public ScheduleDetailsVO findTaskRuleTask(Long taskId, Integer appType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public CronExceptionVO checkCronExpression(String cron, Long minPeriod) {
            return new CronExceptionVO();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<String> recentlyRunTime(String startDate, String endDate, String cron, int num) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskShadeService.class)
        public List<TaskTypeVO> getTaskType() {
            return new ArrayList<>();
        }
    }


    @Test
    public void addOrUpdate() {
        scheduleTaskShadeController.addOrUpdate(null);
    }

    @Test
    public void addOrUpdateBatchTask() {
        scheduleTaskShadeController.addOrUpdateBatchTask(null,null);
    }

    @Test
    public void infoCommit() {
        scheduleTaskShadeController.infoCommit(1L,1,"","");
    }

    @Test
    public void taskCommit() {
        scheduleTaskShadeController.taskCommit("");
    }

    @Test
    public void info() {
        scheduleTaskShadeController.info(1L,1,"");
    }

    @Test
    public void deleteTask() {
        scheduleTaskShadeController.deleteTask(1L,1L,1);
    }

    @Test
    public void getNotDeleteTask() {
        scheduleTaskShadeController.getNotDeleteTask(1L,1);
    }

    @Test
    public void getTasksByName() {
        scheduleTaskShadeController.getTasksByName(1L,"",1);
    }

    @Test
    public void getByName() {
        scheduleTaskShadeController.getByName(1L,"",1,1L);
    }

    @Test
    public void updateTaskName() {
        scheduleTaskShadeController.updateTaskName(1L,"",1);
    }

    @Test
    public void pageQuery() {
        scheduleTaskShadeController.pageQuery(new ScheduleTaskShadeDTO());
    }

    @Test
    public void newPageQuery() {
        scheduleTaskShadeController.newPageQuery(new ScheduleTaskShadeDTO());
    }

    @Test
    public void getBatchTaskById() {
        scheduleTaskShadeController.getBatchTaskById(1L,1);
    }

    @Test
    public void queryTasks() {
        scheduleTaskShadeController.queryTasks(1L,1L,1L,"",1L,1L,null,null,null,null,null,null,null,null,null);
    }

    @Test
    public void frozenTask() {
        scheduleTaskShadeController.frozenTask(null,1,1);
    }

    @Test
    public void dealFlowWorkTask() {
        scheduleTaskShadeController.dealFlowWorkTask(1L,1,null,1L);
    }

    @Test
    public void getFlowWorkSubTasks() {
        scheduleTaskShadeController.getFlowWorkSubTasks(1L,1,null,1L);
    }

    @Test
    public void findTaskId() {
        scheduleTaskShadeController.findTaskId(1L,1,1);
    }

    @Test
    public void findTaskIds() {
        scheduleTaskShadeController.findTaskIds(null,1,1,true);
    }

    @Test
    public void listDependencyTask() {
        scheduleTaskShadeController.listDependencyTask(null,1,"",1L);
    }

    @Test
    public void listByTaskIdsNotIn() {
        scheduleTaskShadeController.listByTaskIdsNotIn(null,1,1L);
    }

    @Test
    public void countTaskByType() {
        scheduleTaskShadeController.countTaskByType(1L,1L,1L,1,null);
    }

    @Test
    public void getTaskByIds() {
        scheduleTaskShadeController.getTaskByIds(null,1);
    }

    @Test
    public void countTaskByTypes() {
        scheduleTaskShadeController.countTaskByTypes(1L,1L,null,1,null);
    }

    @Test
    public void checkResourceLimit() {
        scheduleTaskShadeController.checkResourceLimit(1L,1,"");
    }

    @Test
    public void findFuzzyTaskNameByCondition() {
        scheduleTaskShadeController.findFuzzyTaskNameByCondition("",1,1L,1L);
    }

    @Test
    public void findFuzzyTaskNameByProjectIds() {
        scheduleTaskShadeController.findFuzzyTaskNameByProjectIds("","",1,1L,null,1);
    }

    @Test
    public void findTaskRuleTask() {
        scheduleTaskShadeController.findTaskRuleTask(null,1);
    }

    @Test
    public void checkCronExpression() {
        scheduleTaskShadeController.checkCronExpression("",1L);
    }

    @Test
    public void recentlyRunTime() {
        scheduleTaskShadeController.recentlyRunTime("","","",1);
    }

    @Test
    public void getTaskType() {
        scheduleTaskShadeController.getTaskType();
    }
}