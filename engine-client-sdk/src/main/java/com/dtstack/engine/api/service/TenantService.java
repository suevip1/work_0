package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface TenantService extends DtInsightServer {

    /**
     * @param clusterId
     * @param engineType
     * @param tenantName
     * @param pageSize
     * @param currentPage
     * @return
     * @see TenantService#clusterTenantPageQuery(Long, String, int, int)
     */
    // @RequestLine("POST /node/tenant/pageQuery")
    // @Deprecated
    // ApiResponse<PageResult<List<EngineTenantVO>>> pageQuery(@Param("clusterId") Long clusterId,
    //                                                         @Param("engineType") Integer engineType,
    //                                                         @Param("tenantName") String tenantName,
    //                                                         @Param("pageSize") int pageSize,
    //                                                         @Param("currentPage") int currentPage);


    /**
     * 获取处于统一集群的全部tenant
     * @param dtuicTenantId
     * @param engineType
     * @return
     * @see TenantService#listClusterTenant(Long)
     */
    // @RequestLine("POST /node/tenant/listEngineTenant")
    // @Deprecated
    // ApiResponse<List<EngineTenantVO>> listEngineTenant(@Param("dtuicTenantId") Long dtuicTenantId,
    //                                                    @Param("engineType") Integer engineType);

    @RequestLine("POST /node/tenant/dtToken")
    ApiResponse<List<UserTenantVO>> listTenant(@Param("dtToken") String dtToken);

    @RequestLine("POST /node/tenant/bindingTenant")
    ApiResponse<Void> bindingTenant(@Param("tenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId,
                              @Param("queueId") Long queueId, @Param("dtToken") String dtToken) ;


    @RequestLine("POST /node/tenant/bindingQueue")
    ApiResponse<Void> bindingQueue(@Param("queueId") Long queueId,
                             @Param("tenantId") Long dtUicTenantId,
                              @Param("taskTypeResourceJson") String taskTypeResourceJson);


    @RequestLine("POST /node/tenant/queryResourceLimitByTenantIdAndTaskType")
    ApiResponse<String> queryResourceLimitByTenantIdAndTaskType(@Param("dtUicTenantId") Long dtUicTenantId,
                                   @Param("taskType") Integer taskType);


    /**
     * 获取集群下的绑定租户信息
     * @param clusterId
     * @param tenantName
     * @param pageSize
     * @param currentPage
     * @return
     */
    @RequestLine("POST /node/sdk/tenant/pageQuery")
    ApiResponse<PageResult<List<ClusterTenantVO>>> clusterTenantPageQuery(@Param("clusterId") Long clusterId,
                                                             @Param("tenantName") String tenantName,
                                                             @Param("pageSize") int pageSize,
                                                             @Param("currentPage") int currentPage);

    /**
     * 获取该dtuicTenantId 对应集群下已绑定的所有租户id
     *
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/sdk/tenant/listClusterTenant")
    ApiResponse<List<ClusterTenantVO>> listClusterTenant(@Param("dtuicTenantId") Long dtuicTenantId);

    /**
     * 判断 dtuicTenantId 是否开启数据安全
     */
    @RequestLine("POST /node/sdk/tenant/openDataSecurity")
    ApiResponse<Boolean> openDataSecurity(@Param("dtuicTenantId") Long dtuicTenantId);
}