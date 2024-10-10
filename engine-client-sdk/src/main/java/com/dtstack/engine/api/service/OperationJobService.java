package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.SchedulePeriodVO;
import com.dtstack.engine.api.vo.job.JobPageVO;
import com.dtstack.engine.api.vo.job.JobReturnPageVO;
import com.dtstack.engine.api.vo.job.StatisticsJobVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName OperationJobService
 * @date 2022/7/19 3:17 PM
 */
public interface OperationJobService extends DtInsightServer {

    @RequestLine("POST /node/sdk/operation/job/page")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<JobReturnPageVO>>> page(JobPageVO vo);

    @RequestLine("POST /node/sdk/operation/job/queryJobsStatusStatistics")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<StatisticsJobVO>> queryJobsStatusStatistics(JobPageVO vo);

    @RequestLine("POST /node/sdk/operation/job/displayPeriods")
    ApiResponse<List<SchedulePeriodVO>> displayPeriods(@Param("jobId") String jobId,
                                                       @Param("directType") Integer directType,
                                                       @Param("level") Integer level);

    @RequestLine("POST /node/sdk/operation/job/workflow/pageQuery")
    ApiResponse<List<JobReturnPageVO>> workflowPage(JobPageVO vo);

    @Deprecated
    @RequestLine("POST /node/sdk/operation/job/workflow/page")
    ApiResponse<List<JobReturnPageVO>> workflowPage(@Param("jobId") String jobId);
}
