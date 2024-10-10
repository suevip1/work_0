package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.mockcontainer.impl.ScheduleJobHistoryServiceTestMock;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

@MockWith(value = ScheduleJobHistoryServiceTestMock.class)
public class ScheduleJobHistoryServiceTest {

    ScheduleJobHistoryService scheduleJobHistoryService = new ScheduleJobHistoryService();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInsertScheduleJobHistory() throws Exception {
        JobClient jobClient = new JobClient();
        jobClient.setAppType(AppType.STREAM.getType());
        scheduleJobHistoryService.insertScheduleJobHistory(jobClient, "applicationId");
    }

    @Test
    public void testUpdateScheduleJobHistoryTime() throws Exception {
        JobClient jobClient = new JobClient();
        jobClient.setAppType(AppType.STREAM.getType());
        scheduleJobHistoryService.updateScheduleJobHistoryTime("applicationId", 0);
    }

    @Test
    public void testPageByJobId() throws Exception {
        PageResult<List<ScheduleJobHistory>> result = scheduleJobHistoryService.pageByJobId("jobId", 1, 0);
        Assert.assertNotNull(result);
    }
}
