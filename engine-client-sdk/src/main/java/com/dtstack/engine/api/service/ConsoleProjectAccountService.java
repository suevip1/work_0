package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.console.ConsoleProjectAccountVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * 项目级账号绑定
 *
 * @author qiuyun
 * @version 1.0
 * @since 2023-08-14 17:48
 */
public interface ConsoleProjectAccountService extends DtInsightServer {

    @RequestLine("POST /node/sdk/project/account/add")
    ApiResponse<Long> add(ConsoleProjectAccountVO projectAccountVO);

    @RequestLine("POST /node/sdk/project/account/modify")
    ApiResponse<Boolean> modify(ConsoleProjectAccountVO projectAccountVO);

    @RequestLine("POST /node/sdk/project/account/findByProjectAndComponent")
    ApiResponse<ConsoleProjectAccountVO> findByProjectAndComponent(ConsoleProjectAccountVO projectAccountVO);
}
