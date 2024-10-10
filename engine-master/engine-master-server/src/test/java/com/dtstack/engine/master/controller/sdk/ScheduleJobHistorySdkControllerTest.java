package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.schedule.job.ScheduleJobHistoryVO;
import com.dtstack.engine.master.impl.ScheduleJobHistoryService;
import com.dtstack.engine.master.mapstruct.ScheduleJobHistoryStruct;
import com.dtstack.engine.po.ScheduleJobHistory;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

@MockWith(ScheduleJobHistorySdkControllerMock.class)
public class ScheduleJobHistorySdkControllerTest {

    ScheduleJobHistorySdkController scheduleJobHistorySdkController = new ScheduleJobHistorySdkController();

    @Test
    public void testQueryJobHistory() throws Exception {
        try {
            scheduleJobHistorySdkController.queryJobHistory("jobId", 10);
        } catch (Exception e) {
        }
    }
}

class ScheduleJobHistorySdkControllerMock {

    @MockInvoke(targetClass = ScheduleJobHistoryService.class)
    public PageResult<List<ScheduleJobHistory>> pageByJobId(String jobId, Integer pageSize, Integer pageNo) {
        return new PageResult(1, 1, 1, 1, Lists.newArrayList(new ScheduleJobHistory()));
    }

    @MockInvoke(targetClass = ScheduleJobHistoryStruct.class)
    public ScheduleJobHistoryVO toJobHistoryVO(ScheduleJobHistory jobHistory) {
        ScheduleJobHistoryVO scheduleJobHistoryVO = new ScheduleJobHistoryVO();
        scheduleJobHistoryVO.setJobId(jobHistory.getJobId());
        return scheduleJobHistoryVO;
    }
}
