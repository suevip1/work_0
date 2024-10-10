package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.mockcontainer.impl.BatchFlowWorkJobMock;
import org.junit.Assert;
import org.junit.Test;

@MockWith(BatchFlowWorkJobMock.class)
public class BatchFlowWorkJobServiceTest {

    BatchFlowWorkJobService batchFlowWorkJobService = new BatchFlowWorkJobService();

    @Test
    public void testCheckRemoveAndUpdateFlowJobStatus() throws Exception {
        boolean result = batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(new ScheduleBatchJob(new ScheduleJob()));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testBatchUpdateFlowSubJobStatus() throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        batchFlowWorkJobService.batchUpdateFlowSubJobStatus(scheduleJob, RdosTaskStatus.FROZEN.getStatus());
    }
}