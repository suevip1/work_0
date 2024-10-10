package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.master.mockcontainer.impl.JobChainParmaCleanerMock;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

@MockWith(JobChainParmaCleanerMock.class)
public class JobChainParamCleanerTest {
    JobChainParamCleaner jobChainParamCleaner = new JobChainParamCleaner();


    @Test
    public void testAsyncCleanJobOutputProcessedParamsIfNeed() throws Exception {
        jobChainParamCleaner.asyncCleanJobOutputProcessedParamsIfNeed(new ArrayList<>());
    }


    @Test
    public void testAsyncCleanLastCycJobOutputParamsIfNeed() throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskId(-1L);
        jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
    }

    @Test
    public void testCleanLastCycJobOutputParamsIfNeed() throws Exception {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTaskId(100L);
        jobChainParamCleaner.cleanLastCycJobOutputParamsIfNeed(scheduleJob);
    }

    @Test
    public void testCleanNotUsedOutputParams() throws Exception {
        jobChainParamCleaner.cleanNotUsedOutputParams();
    }

    @Test
    public void testScheduledCleanNotUsedOutputParams() throws Exception {
        jobChainParamCleaner.scheduledCleanNotUsedOutputParams();
    }

    @Test
    public void testCleanUpCycAndPatchJobOutputParams() throws Exception {
        try {
            jobChainParamCleaner.cleanUpCycAndPatchJobOutputParams(null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
