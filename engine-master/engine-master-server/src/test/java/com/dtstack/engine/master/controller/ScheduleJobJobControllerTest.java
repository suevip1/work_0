package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import org.junit.Test;

@MockWith(ScheduleJobJobControllerTest.ScheduleJobJobControllerMock.class)
public class ScheduleJobJobControllerTest {

    private static final ScheduleJobJobController controller = new ScheduleJobJobController();

    static class ScheduleJobJobControllerMock {

        @MockInvoke(targetClass = EnvironmentContext.class)
        public Boolean getUseOptimize(){
            return true;
        }

        @MockInvoke(targetClass = ScheduleJobJobService.class)
        public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringNew( Long jobId,
                                                                               Integer level) throws Exception
        {
            return null;
        }

        @MockInvoke(targetClass = ScheduleJobJobService.class)
        public com.dtstack.engine.master.vo.ScheduleJobVO displayOffSpringWorkFlow( Long jobId, Integer appType) throws Exception {
            return new ScheduleJobVO();
        }

        @MockInvoke(targetClass = ScheduleJobJobService.class)
        public com.dtstack.engine.master.vo.ScheduleJobVO displayForefathersNew( Long jobId,  Integer level) throws Exception {
            return new ScheduleJobVO();
        }
    }

    @Test
    public void displayOffSpring() throws Exception {
        controller.displayOffSpring(1L,1);
    }

    @Test
    public void displayOffSpringWorkFlow() throws Exception {
        controller.displayOffSpringWorkFlow(1L,1);
    }

    @Test
    public void displayForefathers() throws Exception {
        controller.displayForefathers(1L,1);
    }
}