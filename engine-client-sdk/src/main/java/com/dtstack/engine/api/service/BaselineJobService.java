package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.BaselineBlockJobRecordVO;
import com.dtstack.engine.api.vo.alert.BaselineJobConditionVO;
import com.dtstack.engine.api.vo.alert.BaselineJobPageVO;
import com.dtstack.engine.api.vo.alert.BaselineViewVO;
import com.dtstack.engine.api.vo.job.JobReturnPageVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/19 4:27 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface BaselineJobService extends DtInsightServer {

    @RequestLine("POST /node/sdk/baseline/job/page")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<BaselineJobPageVO>>> page(BaselineJobConditionVO vo);

    @RequestLine("POST /node/sdk/baseline/job/getBaselineJobInfo")
    ApiResponse<List<String>> getBaselineJobInfo(@Param("baselineJobId") Long baselineJobId);

    @RequestLine("POST /node/sdk/baseline/job/baselineJobGraph")
    ApiResponse<List<BaselineViewVO>> baselineJobGraph(@Param("baselineTaskId") Long baselineTaskId);

    @RequestLine("POST /node/sdk/baseline/job/baselineBlockJob")
    ApiResponse<List<BaselineBlockJobRecordVO>> baselineBlockJob(@Param("baselineJobId") Long baselineJobId);

}
