package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.dao.ClusterTenantDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 9:08 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FlinkResourceMocK extends BaseMock {


    @MockInvoke(targetClass = ClusterTenantDao.class)
    Long getClusterIdByDtUicTenantId(Long dtUicTenantId){
        return dtUicTenantId;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public Component getComponentByClusterId(Long clusterId, Integer componentType, String componentVersion) {
        return new Component();
    }
}
