package com.dtstack.engine.master.config;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.DTInsightApiMock;
import org.junit.Test;

@MockWith(DTInsightApiMock.class)
public class SdkConfigTest {

    private static final SdkConfig sdkConfig = new SdkConfig();


    @Test
    public void getDataSourceApi() {
        sdkConfig.getDataSourceApi();
    }

    @Test
    public void getDataSourceClient() {
        sdkConfig.getDataSourceClient(null);
    }

    @Test
    public void getAuthCenterAPIClient() {
        sdkConfig.getAuthCenterAPIClient(null);
    }

    @Test
    public void getUicTenantApi() {
        sdkConfig.getUicTenantApi(null);
    }

    @Test
    public void getPubRangerClient() {
        sdkConfig.getPubRangerClient(null);
    }

    @Test
    public void getUicUserApi() {
        sdkConfig.getUicUserApi(null);
    }

    @Test
    public void getUIcUserTenantRelApiClient() {
        sdkConfig.getUIcUserTenantRelApiClient(null);
    }

    @Test
    public void getUicKerberosApiClient() {
        sdkConfig.getUicKerberosApiClient(null);
    }
}