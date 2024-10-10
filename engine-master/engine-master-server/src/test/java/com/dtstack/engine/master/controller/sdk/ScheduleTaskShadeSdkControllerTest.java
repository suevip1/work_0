package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.param.QueryTaskParam;
import com.dtstack.engine.api.vo.DependencyTaskVo;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.FindTaskVO;
import com.dtstack.engine.api.vo.task.OfflineReturnVO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@MockWith(ScheduleTaskShadeSdkControllerMock.class)
public class ScheduleTaskShadeSdkControllerTest {
    ScheduleTaskShadeSdkController scheduleTaskShadeSdkController = new ScheduleTaskShadeSdkController();

    @Test
    public void testFindTask() throws Exception {
        scheduleTaskShadeSdkController.findTask(new FindTaskVO());
    }

    @Test
    public void testDeleteTaskByTaskType() throws Exception {
        scheduleTaskShadeSdkController.deleteTaskByTaskType(1L, 0L, 0, 0);
    }

    @Test
    public void testBatchUpdateSchedule() throws Exception {
        scheduleTaskShadeSdkController.batchUpdateSchedule(Collections.singletonList(1L), 0L, 1, "{\"selfReliance\":false, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}");
    }

    @Test
    public void testQueryTasksByCondition() throws Exception {
        scheduleTaskShadeSdkController.queryTasksByCondition(new FindTaskVO());
    }

    @Test
    public void testOffline() throws Exception {
        scheduleTaskShadeSdkController.offline(Collections.singletonList(1L), 0);
    }

    @Test
    public void testListDependencyTask() throws Exception {
        scheduleTaskShadeSdkController.listDependencyTask(Collections.singletonList(1L), 0, "name", 1L);
    }

    @Test
    public void testListByTaskIdsNotIn() throws Exception {
        scheduleTaskShadeSdkController.listByTaskIdsNotIn(Collections.singletonList(1L), 0, 1L);
    }

    @Test
    public void testQueryTasks() throws Exception {
        scheduleTaskShadeSdkController.queryTasks(new QueryTaskParam());
    }
}

class ScheduleTaskShadeSdkControllerMock {
    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public void batchUpdateScheduleConf(List<Long> taskIds, long modifyUserId, Integer appType, String scheduleConf, Integer periodType) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public void deleteTask(Long taskId, long modifyUserId, Integer appType, Integer taskType) {
        return;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public ScheduleTaskShadePageVO queryTasksByCondition(FindTaskVO findTaskVO) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public OfflineReturnVO offline(List<Long> taskIds, Integer appType) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<DependencyTaskVo> listDependencyTaskToVo(List<Long> taskId, String name, Long projectId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<DependencyTaskVo> listByTaskIdsNotInToVo(List<Long> taskId, Integer appType, Long projectId) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
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
                                              String searchType,
                                              Integer appType,
                                              List<Long> resourceIds) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShadeTypeVO> findTask(FindTaskVO findTaskVO) {
        return new ArrayList<>();
    }
}