package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.master.strategy.AbstractTemplateReplaceStrategy;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthProjectVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICTenantVO;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import org.mapstruct.Mapper;

/**
 * @Auther: dazhi
 * @Date: 2022/6/6 10:35 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface AlterTempStruct {

    AbstractTemplateReplaceStrategy.TemProject toTemProject(AuthProjectVO data);

    AbstractTemplateReplaceStrategy.TemTenant toTemTenant(UICTenantVO data);

    AbstractTemplateReplaceStrategy.TemUser toTemUser(UICUserVO uicUserVO);
}
