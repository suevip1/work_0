package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.OperatorParam;
import com.dtstack.engine.api.vo.ScheduleJobOperateVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 3:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobOperateService extends DtInsightServer {

    @RequestLine("POST /node/sdk/scheduleJobOperate/page")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<PageResult<List<ScheduleJobOperateVO>>> page(OperatorParam pageParam);

}
