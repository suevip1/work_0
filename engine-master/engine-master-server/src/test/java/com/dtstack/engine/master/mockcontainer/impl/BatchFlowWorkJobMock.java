package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.google.common.collect.Lists;

import java.util.List;

public class BatchFlowWorkJobMock {

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer updateStatusByJobId(String jobId, Integer status, Integer versionId) {
        return 1;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public boolean updatePhaseStatusById(Long id, JobPhaseStatus original, JobPhaseStatus update) {
        return true;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Integer updateStatusWithExecTime(ScheduleJob updateJob) {
        return 1;

    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<ScheduleJob> getSubJobsAndStatusByFlowId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());

        ScheduleJob failJob = new ScheduleJob();
        failJob.setStatus(RdosTaskStatus.FAILED.getStatus());

        ScheduleJob parentFailJob = new ScheduleJob();
        parentFailJob.setStatus(RdosTaskStatus.PARENTFAILED.getStatus());
        return Lists.newArrayList(scheduleJob, failJob, parentFailJob);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public void updateLogInfoById(String jobId, String msg) {

    }
}
