package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.JobGraphTriggerDao;
import com.dtstack.engine.po.JobGraphTrigger;

import java.sql.Timestamp;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-24 09:17
 */
public class JobGraphTriggerServiceMock {
    @MockInvoke(targetClass = JobGraphTriggerDao.class)
    Integer insert(JobGraphTrigger jobGraphTrigger) {
        return 1;
    }

    @MockInvoke(targetClass = JobGraphTriggerDao.class)
    JobGraphTrigger getByTriggerTimeAndTriggerType(  Timestamp timestamp,   int triggerType) {
        JobGraphTrigger jobGraphTrigger = new JobGraphTrigger();
        jobGraphTrigger.setTriggerTime(timestamp);
        jobGraphTrigger.setTriggerType(triggerType);
        return jobGraphTrigger;
    }

}
