package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.mockcontainer.impl.JobStopDealerMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 2:28 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobStopDealerMock.class)
public class JobStopDealerTest {

    JobStopDealer jobStopDealer = new JobStopDealer();


    @Test
    public void addStopJobs() throws Exception {
        List<ScheduleJob> jobs = Lists.newArrayList();
        ScheduleJob scheduleJob1 = new ScheduleJob();
        scheduleJob1.setStatus(0);
        scheduleJob1.setJobId("1");

        ScheduleJob scheduleJob2 = new ScheduleJob();
        scheduleJob2.setStatus(4);
        scheduleJob2.setJobId("2");

        ScheduleJob scheduleJob3 = new ScheduleJob();
        scheduleJob3.setTaskType(10);
        scheduleJob3.setStatus(4);
        scheduleJob3.setJobId("3");

        jobs.add(scheduleJob1);
        jobs.add(scheduleJob2);
        jobs.add(scheduleJob3);
        jobStopDealer.addStopJobs(jobs,-1L);
        jobStopDealer.afterPropertiesSet();
        Thread.sleep(5000L);
        jobStopDealer.destroy();
    }

}
