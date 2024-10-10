package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.vo.FindParentJobVO;
import com.dtstack.engine.api.vo.job.JobViewVO;
import com.dtstack.engine.api.vo.job.ParentNodesVO;
import com.dtstack.engine.api.vo.job.RelyResultVO;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

@MockWith(ScheduleJobJobSdkControllerMock.class)
public class ScheduleJobJobSdkControllerTest {
    ScheduleJobJobSdkController scheduleJobJobSdkController = new ScheduleJobJobSdkController();

    @Test
    public void testParentNode() throws Exception {
        scheduleJobJobSdkController.parentNode("jobId", "taskName");
    }

    @Test
    public void testRemoveRely() throws Exception {
        scheduleJobJobSdkController.removeRely("relyJobKey", Collections.singletonList("String"), 1L);
    }

    @Test
    public void testFindParentJob() throws Exception {
        scheduleJobJobSdkController.findParentJob("jobId");
    }

    @Test
    public void testView() throws Exception {
        scheduleJobJobSdkController.view(Collections.singletonList("String"), 0, 0, false);
    }
}

class ScheduleJobJobSdkControllerMock {
    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public JobViewVO view(List<String> jobIds, Integer level, Integer directType) {
        return null;

    }

    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public FindParentJobVO findParentJob(String jobId) {
        return null;
    }

    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public RelyResultVO removeRely(String relyJobKey, List<String> relyJobParentKeys, Long operateId) {
        return null;

    }

    @MockInvoke(targetClass = ScheduleJobJobService.class)
    public ParentNodesVO parentNode(String jobId, String taskName) {
        return null;
    }
}