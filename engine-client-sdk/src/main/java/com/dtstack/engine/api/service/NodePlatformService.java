package com.dtstack.engine.api.service;

import com.dtstack.engine.api.dto.ScheduleJobStatusMonitorDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-02-14 20:53
 */
public interface NodePlatformService extends DtInsightServer {
    /**
     * 注册接口
     */
    @RequestLine("POST /node/sdk/platform/job/status/register")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Boolean> register(ScheduleJobStatusMonitorDTO jobStatusMonitorDTO);

    /**
     * 注销接口
     */
    // @RequestLine("POST /node/platform/job/status/logout")
    // @Headers(value={"Content-Type: application/json"})
    // ApiResponse<Boolean> logout(ScheduleJobStatusMonitorLogoutDTO jobStatusMonitorLogoutDTO);
}
