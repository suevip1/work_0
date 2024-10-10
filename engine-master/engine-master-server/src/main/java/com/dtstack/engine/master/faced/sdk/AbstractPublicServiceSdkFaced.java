package com.dtstack.engine.master.faced.sdk;

import com.dtstack.engine.master.faced.base.RemoteClientFaced;
import com.dtstack.pubsvc.sdk.alert.channel.service.AlertApiClient;
import com.dtstack.pubsvc.sdk.alert.channel.service.AlertNotifyApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicLicenseApiClient;
import com.dtstack.pubsvc.sdk.usercenter.client.UicSubProductModuleApiClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author leon
 * @date 2022-10-09 19:18
 **/
public abstract class AbstractPublicServiceSdkFaced implements RemoteClientFaced {

    @Autowired
    protected AlertNotifyApiClient alertNotifyApiClient;

    @Autowired
    protected AlertApiClient alertApiClient;

    @Autowired
    protected UicSubProductModuleApiClient uicSubProductModuleApiClient;


    @Autowired
    protected UicLicenseApiClient uicLicenseApiClient;
}
