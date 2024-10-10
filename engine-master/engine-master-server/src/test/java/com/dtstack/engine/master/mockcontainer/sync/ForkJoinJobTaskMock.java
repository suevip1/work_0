package com.dtstack.engine.master.mockcontainer.sync;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import org.assertj.core.util.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-19 19:27
 */
public class ForkJoinJobTaskMock {

    public static final String JOB_KEY = "cronTrigger_64783_20220719000000";

    @MockInvoke(targetClass = ScheduleJobDao.class)
    List<ScheduleJob> listJobByJobKeys(Collection<String> jobKeys) {
        ScheduleJob job = new ScheduleJob();
        job.setJobKey("cronTrigger_64789_20220719000000");
        job.setCycTime("20220719000000");
        job.setJobId("bJobId");
        return Lists.newArrayList(job);
    }

    @MockInvoke(targetClass = ScheduleJobDao.class)
    ScheduleJob getRdosJobByJobId(String jobId) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setJobKey(JOB_KEY);
        return scheduleJob;
    }

    @MockInvoke(targetClass = ScheduleJobJobDao.class)
    List<ScheduleJobJob> listByParentJobKey(String jobKey, Integer relyType) {
        if (JOB_KEY.equals(jobKey)) {
            ScheduleJobJob jobJob = new ScheduleJobJob();
            jobJob.setAppType(1);
            jobJob.setParentJobKey(jobKey);
            jobJob.setJobKey("cronTrigger_64789_20220719000000");
            jobJob.setParentAppType(1);
            return Lists.newArrayList(jobJob);
        }
        return Collections.emptyList();
    }

}
