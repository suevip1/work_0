package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.role.RoleVO;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthRoleVO;
import org.mapstruct.Mapper;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface RoleStruct {
    RoleVO toRoleVO(AuthRoleVO roleVO);
}
