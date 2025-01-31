package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;


import java.util.List;

public interface EngineService extends DtInsightServer {

    // @Deprecated
    // @RequestLine("POST /node/engine/getQueue")
    // ApiResponse<List<QueueVO>> getQueue(@Param("engineId") Long engineId);

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }F
     * ]
     */
    @RequestLine("POST /node/engine/listSupportEngine")
    ApiResponse<List<EngineSupportVO>> listSupportEngine(@Param("tenantId") Long dtUicTenantId);

    @RequestLine("POST /node/engine/listSupportEngineWithCommon")
    ApiResponse<List<EngineSupportVO>> listSupportEngineWithCommon(@Param("tenantId") Long dtUicTenantId,@Param("needCommon") Boolean needCommon);
}