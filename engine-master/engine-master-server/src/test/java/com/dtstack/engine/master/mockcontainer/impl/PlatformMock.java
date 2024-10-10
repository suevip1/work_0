package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.impl.AccountService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.router.login.DtUicUserConnect;

public class PlatformMock extends BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getDatasourceNode() {
        return "127.0.0.1:8090";
    }

    @MockInvoke(targetClass = DtUicUserConnect.class)
    public void registerEvent(String uicUrl, PlatformEventType eventType, String callbackUrl, boolean active) {
        return;
    }

    @MockInvoke(targetClass = DtUicUserConnect.class)
    public String getDatasourceRandomNode(String url) {
        return url;
    }

    @MockInvoke(targetClass = TenantService.class)
    public void deleteTenantId(Long dtUicTenantId) {
        return;
    }

    @MockInvoke(targetClass = AccountService.class)
    public void deleteUser(Long userId) {
        return;
    }

}
