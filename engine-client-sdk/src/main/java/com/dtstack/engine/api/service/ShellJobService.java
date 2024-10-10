package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * sdk shell job service
 *
 * @author ：wangchuan
 * date：Created in 16:03 2022/8/4
 * company: www.dtstack.com
 */
public interface ShellJobService extends DtInsightServer {

    @RequestLine("POST /node/sdk/shellJob/getShellContentByFileName")
    ApiResponse<String> getShellContentByFileName(@Param("fileName") String fileName);
}
