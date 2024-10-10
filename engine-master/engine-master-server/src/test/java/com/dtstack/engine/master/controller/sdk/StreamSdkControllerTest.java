package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleJobDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.StreamJobQueryParam;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dto.StatusCount;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.StreamTaskService;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@MockWith(StreamSdkControllerMock.class)
public class StreamSdkControllerTest {
    StreamSdkController streamSdkController = new StreamSdkController();

    @Test
    public void testTotalSize() throws Exception {
        Long result = streamSdkController.totalSize("jobId");
        Assert.assertEquals(Long.valueOf(1), result);
    }

    @Test
    public void testQueryJobs() throws Exception {
        StreamJobQueryParam streamJobQueryParam = new StreamJobQueryParam();
        streamJobQueryParam.setAppType(2);
        streamJobQueryParam.setCurrentPage(1);
        streamJobQueryParam.setPageSize(2);
        streamJobQueryParam.setTaskName("test");
        streamJobQueryParam.setProjectId(1L);
        streamSdkController.queryJobs(streamJobQueryParam);
    }

    @Test
    public void testQueryJobsStatusStatistics() throws Exception {
        StreamJobQueryParam streamJobQueryParam = new StreamJobQueryParam();
        streamJobQueryParam.setAppType(2);
        streamJobQueryParam.setCurrentPage(1);
        streamJobQueryParam.setPageSize(2);
        streamJobQueryParam.setTaskName("test");
        streamJobQueryParam.setProjectId(1L);
        streamSdkController.queryJobsStatusStatistics(streamJobQueryParam);
    }
}

class StreamSdkControllerMock {

    @MockInvoke(targetClass = ScheduleTaskShadeService.class)
    public List<ScheduleTaskShade> listByNameLikeWithSearchType(Long projectId, String name, Integer appType,
                                                                Long ownerId, List<Long> projectIds, Integer searchType, List<String> versions, Integer computeType) {

        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setName(name);
        scheduleTaskShade.setProjectId(projectId);
        return Lists.newArrayList(scheduleTaskShade);

    }

    @MockInvoke(targetClass = StreamTaskService.class)
    public Long getTotalSize(String jobId) {
        return 1L;
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public List<StatusCount> queryStreamJobsStatusStatistics(ScheduleJobDTO scheduleJobDTO) {
        StatusCount statusCount = new StatusCount();
        statusCount.setStatus(RdosTaskStatus.RUNNING.getStatus());
        statusCount.setCount(1);
        return Lists.newArrayList(statusCount);
    }

    @MockInvoke(targetClass = ScheduleJobService.class)
    public PageResult queryStreamJobs(PageQuery<ScheduleJobDTO> pageQuery) {
        return PageResult.EMPTY_PAGE_RESULT;
    }
}