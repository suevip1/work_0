package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.task.SaveTaskRefResultVO;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import org.junit.Test;

@MockWith(ScheduleTaskRefShadeControllerTest.ScheduleTaskRefShadeControllerMock.class)
public class ScheduleTaskRefShadeControllerTest {

    private ScheduleTaskRefShadeController controller = new ScheduleTaskRefShadeController();

    @Test
    public void saveTaskRefList() {
        controller.saveTaskRefList("{}","");
    }

    static class ScheduleTaskRefShadeControllerMock {

        @MockInvoke(targetClass = ScheduleTaskRefShadeService.class)
        public SaveTaskRefResultVO saveTaskRefList(String taskRefListJsonStr, String commitId) {
            return new SaveTaskRefResultVO();
        }
    }
}