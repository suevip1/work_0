package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.alibaba.testable.processor.annotation.EnablePrivateAccess;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleJobJobServiceMock;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

@MockWith(ScheduleJobJobServiceMock.class)
@EnablePrivateAccess
public class ScheduleJobJobServiceTest {
    ScheduleJobJobService scheduleJobJobService = new ScheduleJobJobService();

    @Test
    public void testDisplayOffSpringNew() throws Exception {
        scheduleJobJobService.displayOffSpringNew(1L, 2);
    }

    @Test
    public void testDisplayOffSpring() throws Exception {
        scheduleJobJobService.displayOffSpring(1L, 0);
    }


    @Test
    public void testDisplayOffSpringWorkFlow() throws Exception {
        scheduleJobJobService.displayOffSpringWorkFlow(2L, 1);
    }

    @Test
    public void testDisplayForefathersNew() throws Exception {
        scheduleJobJobService.displayForefathersNew(1L, 0);
    }

    @Test
    public void testDisplayForefathers() throws Exception {
        scheduleJobJobService.displayForefathers(1L, 0);
    }


    @Test
    public void testParentNode() throws Exception {
        scheduleJobJobService.parentNode("jobId", "taskName");
    }

    @Test
    public void testRemoveRely() throws Exception {
        scheduleJobJobService.removeRely("relyJobKey", Collections.singletonList("String"), 1L);
    }

    @Test
    public void testFindParentJob() throws Exception {
        scheduleJobJobService.findParentJob("jobId");
    }
}
