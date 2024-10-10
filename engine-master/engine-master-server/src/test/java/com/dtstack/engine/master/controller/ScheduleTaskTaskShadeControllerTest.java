package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.task.SaveTaskTaskVO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(ScheduleTaskTaskShadeControllerTest.ScheduleTaskTaskShadeControllerMock.class)
public class ScheduleTaskTaskShadeControllerTest {

    private static final ScheduleTaskTaskShadeController controller = new ScheduleTaskTaskShadeController();

    static class ScheduleTaskTaskShadeControllerMock {

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public void clearDataByTaskId( Long taskId,Integer appType) {

        }

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public SaveTaskTaskVO saveTaskTaskList(String taskLists, String commitId) {
            return new SaveTaskTaskVO();
        }

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public List<ScheduleTaskTaskShade> getAllParentTask(Long taskId, Integer appType) {
            return new ArrayList<>();
        }

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public com.dtstack.engine.master.vo.ScheduleTaskVO displayOffSpring( Long taskId,
                                                                            Long projectId,
                                                                            Integer level,
                                                                            Integer directType, Integer appType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public com.dtstack.engine.master.vo.ScheduleTaskVO getAllFlowSubTasks( Long taskId,  Integer appType) {
            return null;
        }

        @MockInvoke(targetClass = ScheduleTaskTaskShadeService.class)
        public ScheduleTaskVO listGroupTask(Long taskId, Integer appType) {
            return null;
        }
    }

    @Test
    public void clearDataByTaskId() {
        controller.clearDataByTaskId(1L, AppType.RDOS.getType());
    }

    @Test
    public void saveTaskTaskList() {
        controller.saveTaskTaskList("","");
    }

    @Test
    public void getAllParentTask() {
        controller.getAllParentTask(1L,AppType.RDOS.getType());
    }

    @Test
    public void displayOffSpring() {
        controller.displayOffSpring(1L,1L,1,1,1);
    }

    @Test
    public void getAllFlowSubTasks() {
        controller.getAllFlowSubTasks(1L,1);
    }

    @Test
    public void listGroupTask() {
        controller.listGroupTask(1L,1);
    }
}