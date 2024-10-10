package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.mockcontainer.impl.JobGraphTriggerServiceMock;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-24 09:14
 */
@MockWith(JobGraphTriggerServiceMock.class)
public class JobGraphTriggerServiceTest {
    JobGraphTriggerService jobGraphTriggerService = new JobGraphTriggerService();

    @Test
    public void getTriggerByDate() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        jobGraphTriggerService.getTriggerByDate(timestamp, EScheduleType.NORMAL_SCHEDULE.getType());
    }

    @Test
    public void checkHasBuildJobGraph() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        jobGraphTriggerService.checkHasBuildJobGraph(timestamp);
    }

    @Test
    public void addJobTrigger() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        jobGraphTriggerService.addJobTrigger(timestamp, 0L);
    }

}