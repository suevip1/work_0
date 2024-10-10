package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.console.KerberosProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * @Auther: dazhi
 * @Date: 2022/3/14 5:25 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ConsoleKerberosProjectService extends DtInsightServer {

    @RequestLine("POST /node/sdk/kerberos/project/addOrUpdateKer")
    ApiResponse<Boolean> addOrUpdateKerberosProject(KerberosProjectVO kerberosProjectVO);

    @RequestLine("POST /node/sdk/kerberos/project/findKerberosProject")
    ApiResponse<KerberosProjectVO> findKerberosProject(@Param("projectId") Long projectId, @Param("appType") Integer appType);
}
