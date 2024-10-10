package com.dtstack.engine.master.router.login;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

@MockWith(BaseMock.class)
public class DtUicUserConnectTest {

    private static final DtUicUserConnect dtUicUserConnect = new DtUicUserConnect();

    @Test
    public void getDatasourceRandomNode() {
        dtUicUserConnect.getDatasourceRandomNode("http://127.0.0.1");
    }

    @Test
    public void getUserByUserIds() {
        dtUicUserConnect.getUserByUserIds("","http://127.0.0.1", Lists.newArrayList(1L,2L));
    }

    @Test
    public void getInfo() {
        dtUicUserConnect.getInfo("", "http://127.0.0.1", new Consumer<DtUicUser>() {
            @Override
            public void accept(DtUicUser dtUicUser) {

            }
        });
    }

    @Test
    public void removeUicInfo() {
        dtUicUserConnect.removeUicInfo("sds","http://127.0.0.1");
    }

    @Test
    public void getUserTenants() {
        dtUicUserConnect.getUserTenants("http://127.0.0.1","sds","ds");
    }

    @Test(expected = Exception.class)
    public void getAllUicUsers() {
        dtUicUserConnect.getAllUicUsers("http://127.0.0.1","sds",1L,"ds");
    }

    @Test
    public void getTenantByTenantId() {
        dtUicUserConnect.getTenantByTenantId("http://127.0.0.1",1L,"sd");
    }

    @Test
    public void registerEvent() {
        dtUicUserConnect.registerEvent("http://127.0.0.1", PlatformEventType.LOG_OUT,"http://127.0.0.1",true);
    }

    @Test
    public void getLdapUserName() {
        dtUicUserConnect.getLdapUserName(1L,"","http://127.0.0.1");
    }
}