package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.resource.ResourceGroupQueryParam;
import com.dtstack.engine.api.vo.resource.ResourceGroupVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ResourceGroupService extends DtInsightServer {
    @RequestLine("POST /node/sdk/resource/isAccessedByProject")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Boolean> isAccessedByProject(@Param("resourceId") Long resourceId, @Param("appType") Integer appType, @Param("projectId") Long projectId);

    @RequestLine("POST /node/sdk/resource/listAccessedResources")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<AccessedResourceGroupVO<ResourceGroupDetailVO>> listAccessedResources(@Param("appType") Integer appType, @Param("projectId") Long projectId);

    @RequestLine("POST /node/sdk/resource/getResourceUsedMonitor")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ResourceGroupUsedVO> getResourceUsedMonitor(@Param("resourceId") Long resourceId);

    @RequestLine("POST /node/sdk/resource/getHandOver")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<List<HandOverVO>> getHandOver(@Param("oldResourceIds") List<Long> oldResourceId, @Param("appType") Integer appType, @Param("projectId") Long projectId);

    @RequestLine("POST /node/sdk/resource/default")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ResourceGroupListVO> getDefault(@Param("dtUicTenantId") Long dtUicTenantId);


    @RequestLine("POST /node/sdk/resource/getResourceGroup")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ResourceGroupVO> getResourceGroup(@Param("resourceId") Long resourceId);

    // @Deprecated
    // @RequestLine("POST /node/sdk/resource/batchChangeResource")
    // @Headers(value = {"Content-Type: application/json"})
    // ApiResponse<Void> getResourceGroup(@Param("dtUicTenantId")Long dtUicTenantId, @Param("taskIds")List<Long> taskIds,
    //                                    @Param("appType")Integer appType, @Param("resourceId")Long resourceId);

    @RequestLine("POST /node/sdk/resource/batchChangeResource")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Void> batchChangeResource(@Param("dtUicTenantId")Long dtUicTenantId, @Param("taskIds")List<Long> taskIds,
                                       @Param("appType")Integer appType, @Param("resourceId")Long resourceId);

    /******************************************** dtscript agent 资源组接口 **************************************/

    @RequestLine("POST /node/sdk/resource/listAccessedLabelResources")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<AccessedResourceGroupVO<ResourceGroupLabelDetailVO>> listAccessedLabelResources(ResourceGroupQueryParam resourceGroupQueryParam);

    @RequestLine("POST /node/sdk/resource/changeProjectDefaultResource")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<Boolean> changeProjectDefaultResource(@Param("resourceGrantId")Long resourceGrantId, @Param("isProjectDefault")Integer isProjectDefault);

    @RequestLine("POST /node/sdk/resource/listLabelResourceByIds")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<AccessedResourceGroupVO<ResourceGroupLabelDetailVO>> listLabelResourceByIds(@Param("resourceIds") List<Long> resourceIds);

    @RequestLine("POST /node/sdk/resource/findAccessedByLabelAndUser")
    @Headers(value = {"Content-Type: application/json"})
    ApiResponse<ResourceGroupLabelDetailVO> findAccessedByLabelAndUser(ResourceGroupQueryParam resourceGroupQueryParam);
}
