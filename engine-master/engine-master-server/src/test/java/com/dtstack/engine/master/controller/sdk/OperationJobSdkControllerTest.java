package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.SchedulePeriodVO;
import com.dtstack.engine.api.vo.job.JobPageVO;
import com.dtstack.engine.api.vo.job.JobReturnPageVO;
import com.dtstack.engine.api.vo.job.StatisticsJobVO;
import com.dtstack.engine.master.impl.OperationJobService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@MockWith(OperationJobSdkControllerMock.class)
public class OperationJobSdkControllerTest {

    private OperationJobSdkController controller = new OperationJobSdkController();

    @Test
    public void page() {
        controller.page(new JobPageVO());
    }

    @Test
    public void queryJobsStatusStatistics() {
        controller.queryJobsStatusStatistics(new JobPageVO());
    }

    @Test
    public void displayPeriods() {
        controller.displayPeriods("ada",1,1);
    }

    @Test
    public void workflowPage() {
        controller.workflowPage("adc");
    }

}

class OperationJobSdkControllerMock {



    @MockInvoke(targetClass = OperationJobService.class)
    public PageResult<List<JobReturnPageVO>> page(JobPageVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;
    }

    @MockInvoke(targetClass = OperationJobService.class)
    public List<StatisticsJobVO> queryJobsStatusStatistics(JobPageVO vo) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = OperationJobService.class)
    public List<SchedulePeriodVO> displayPeriods(String jobId, Integer directType, Integer level) {
        return new ArrayList<>();
    }

    @MockInvoke(targetClass = OperationJobService.class)
    public List<JobReturnPageVO> workflowPage(String jobId) {
        return new ArrayList<>();
    }

}

