package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataJobListVO;
import com.dtstack.engine.api.vo.schedule.job.FillDataListVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillDataInfoVO;
import com.dtstack.engine.api.vo.schedule.job.ScheduleFillJobParticipateVO;
import com.dtstack.engine.master.impl.ScheduleJobService;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

@MockWith(FillDataSdkControllerMock.class)
public class FillDataSdkControllerTest {
    FillDataSdkController fillDataSdkController = new FillDataSdkController();

    @Test
    public void testFillData() throws Exception {
        ScheduleFillJobParticipateVO scheduleFillJobParticipateVO = new ScheduleFillJobParticipateVO();
        scheduleFillJobParticipateVO.setStartDay("20220101");
        scheduleFillJobParticipateVO.setEndDay("20220102");
        scheduleFillJobParticipateVO.setFillDataInfo(new ScheduleFillDataInfoVO());
        fillDataSdkController.fillData(scheduleFillJobParticipateVO);
    }

    @Test
    public void testFillDataList() throws Exception {
        fillDataSdkController.fillDataList(new FillDataListVO());
    }

    @Test
    public void testFillDataJobList() throws Exception {
        fillDataSdkController.fillDataJobList(new FillDataJobListVO());
    }
}

class FillDataSdkControllerMock {


    @MockInvoke(targetClass = ScheduleJobService.class)
    public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay,
                                                                                    Long bizStartDay, Long bizEndDay, Long dutyUserId,
                                                                                    Long projectId, Integer appType,
                                                                                    Integer currentPage, Integer pageSize, Long tenantId,Long dtuicTenantId,Integer fillDataType) {
        return null;

    }


        @MockInvoke(targetClass = ScheduleJobService.class)
    public void checkFillDataParams(String fillName, Long projectId, DateTime toDateTime, DateTime currDateTime) {

    }

        @MockInvoke(targetClass = ScheduleJobService.class)
    public PageResult<List<ScheduleFillDataJobPreViewVO>> getFillDataJobInfoPreview(String jobName, Long runDay,
                                                                                    Long bizStartDay, Long bizEndDay, Long dutyUserId,
                                                                                    Long projectId, Integer appType,
                                                                                    Integer currentPage, Integer pageSize, Long tenantId, Long dtuicTenantId) {
        return PageResult.EMPTY_PAGE_RESULT;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public PageResult<ScheduleFillDataJobDetailVO> fillDataJobList(FillDataJobListVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;

    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public Long fillData(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO) {
        return 1L;
    }
}