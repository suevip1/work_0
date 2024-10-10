package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.dao.ScheduleJobHistoryDao;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.ScheduleJobHistory;
import org.assertj.core.util.Lists;

import java.util.List;

public class ScheduleJobHistoryServiceTestMock extends BaseMock {

    @MockInvoke(targetClass = ScheduleJobHistoryDao.class)
    Integer selectJobIdByCount(PageQuery<ScheduleJobHistory> pageQuery){
        return 1;
    }


    @MockInvoke(targetClass = ScheduleJobHistoryDao.class)
    List<ScheduleJobHistory> selectByJobId(PageQuery<ScheduleJobHistory> pageQuery){
        ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
        scheduleJobHistory.setJobId("test");
        return Lists.newArrayList(scheduleJobHistory);
    }


    @MockInvoke(targetClass = ScheduleJobHistoryDao.class)
    void updateByApplicationId( String applicationId,  Integer appType) {

    }

    @MockInvoke(targetClass = ScheduleJobHistoryDao.class)
    Integer insert(ScheduleJobHistory history) {
        return 1;
    }

}
