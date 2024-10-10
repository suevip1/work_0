package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Param;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Auther: dazhi
 * @Date: 2022/6/29 11:32 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobExecutorTriggerMock extends AbstractJobExecutorMock {

    @MockInvoke(targetClass = ScheduleJobDao.class)
    Integer countTasksByCycTimeTypeAndAddress(String nodeAddress, Integer scheduleType, String cycStartTime, String cycEndTime) {
        return 10;
    }

    @MockInvoke(targetClass = JobRichOperator.class)
    public Pair<String, String> getCycTimeLimit() {
        String startTime = "1";
        String endTime = "2";
        return new ImmutablePair<>(startTime, endTime);
    }
}
