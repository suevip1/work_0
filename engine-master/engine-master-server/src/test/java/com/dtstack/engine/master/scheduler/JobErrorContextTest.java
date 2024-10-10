package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.JobCheckStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class JobErrorContextTest {

    private JobErrorContext context = JobErrorContext.newInstance();

    @Test
    public void newInstance() {
        context.jobCheckStatus(JobCheckStatus.NO_TASK);
        context.taskResourceErrMsg("");
        context.frozenReason("");
        context.parentTaskShade(new ScheduleTaskShade());
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221215174100");
        scheduleJob.setStatus(4);
        context.parentJob(scheduleJob);
        context.parentTaskName("");
        context.acquireParentTaskName();
        context.toString();
    }

}