package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.po.ClusterTenant;
import com.dtstack.engine.api.vo.ClusterTenantVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.TenantNameVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantApiVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.TenantDeletedVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UserFullTenantVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-09-09
 */
@Mapper(componentModel = "spring")
public interface TenantStruct {

    @Deprecated
    List<EngineTenantVO> toClusterTenantVO(List<ClusterTenantVO> engineTenantVO);

    TenantNameVO toNameVO(TenantDeletedVO tenantDetailVOS);

    List<TenantNameVO> toNameVOS(List<TenantDeletedVO> tenantDeletedVOS);

    ClusterTenantVO toClusterTenantVO(ClusterTenant clusterTenant);

    List<TenantNameVO> clusterTenantVOToNameVO(List<ClusterTenantVO> clusterTenantVO);

    TenantNameVO toNameVO(UserFullTenantVO userFullTenantVO);

    TenantNameVO toNameVO(TenantApiVO data);

}
