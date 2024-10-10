package com.dtstack.engine.dao;

import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.vo.ClusterTenantResourceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterTenantDao {

    Integer insert(ClusterTenant engineTenant);

    Integer updateQueueId(@Param("dtUicTenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId, @Param("queueId") Long queueId);

    Integer updateResourceId(@Param("dtUicTenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId, @Param("resourceId") Long resourceId, @Param("queueId") Long queueId);

    Integer updateLabelResourceId(@Param("dtUicTenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId, @Param("labelResourceId") Long labelResourceId);

    Integer generalCount(@Param("clusterId") Long clusterId,@Param("dtUicTenantIds") List<Long> dtUicTenantId);

    List<ClusterTenant> generalQuery(@Param("query") PageQuery<Object> query, @Param("clusterId") Long clusterId, @Param("dtUicTenantIds") List<Long> dtUicTenantId);

    List<Long> listTenantIdByQueueIds(@Param("queueIds") List<Long> queueIds);

    Long getQueueIdByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<ClusterTenant> listEngineTenant(@Param("clusterId") Long clusterId);

    Long getClusterIdByDtUicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    ClusterTenant getByDtuicTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<ClusterTenant> listByDtuicTenantId(@Param("dtUicTenantIds") List<Long> dtUicTenantIds);

    Integer deleteTenantId(@Param("dtUicTenantId") Long dtUicTenantId);

    List<Long> listAllDtUicTenantId();

    List<Long> listBoundedTenants(@Param("clusterId") Long clusterId);

    List<ClusterTenantResourceVO> generalQueryWithResource(@Param("query") PageQuery<Object> query,
                                                           @Param("clusterId") Long clusterId,
                                                           @Param("dtUicTenantIds") List<Long> dtUicTenantId);

    Long countByDefaultResourceId(@Param("resourceId") Long resourceId);

    Integer existsQueue(@Param("queueId")Long queueId);

    List<ClusterTenant> listByClusterId(@Param("clusterId") Long clusterId);

    Integer hasBindTenants(@Param("clusterId") Long clusterId);

    Integer unbindTenant(@Param("clusterId") Long clusterId, @Param("dtUicTenantId") Long dtUicTenantId);

    List<Long> getTenantIdsByClusterId(@Param("clusterId") Long clusterId, @Param("tenantIds") List<Long> tenantIds);

    void updateCommonConfig(@Param("tenantId") Long tenantId, @Param("commonConfig") String commonConfig);

    List<Long> getTenantIdsByClusterName(@Param("clusterName") String clusterName);
}

