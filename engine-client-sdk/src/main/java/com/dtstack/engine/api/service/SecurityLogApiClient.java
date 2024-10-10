package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.SecurityLogParam;
import com.dtstack.engine.api.vo.security.SecurityLogVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * Date: 2020/7/29
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface SecurityLogApiClient extends DtInsightServer {

    /**
     * 添加安全日志接口免登陆，需将参数加密传输,用于替换console: /api/console/service/securityAudit/addSecurityLog
     * @param securityLogParam 加密后的字符串
     */
    // @Deprecated
    // @RequestLine("POST /node/securityAudit/addSecurityLog")
    // @Headers(value={"Content-Type: application/json"})
    // ApiResponse<Void> addSecurityLog(SecurityLogParam securityLogParam);


    /**
     * 添加安全日志接口免登陆 需将参数com.dtstack.engine.api.vo.security.SecurityLogVO 通过AESUtil加密传输
     * @param sign
     * @return
     */
    @RequestLine("POST /node/securityAudit/sdk/addSecurityLog")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> addSecurityLogSign(@Param("sign") String sign);

    /**
     * 查询审计日志
     * @param param
     * @return
     */
    @RequestLine("POST /node/securityAudit/sdk/pageQuery")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<List<SecurityLogVO>>> pageQuery(SecurityLogParam param);
}
