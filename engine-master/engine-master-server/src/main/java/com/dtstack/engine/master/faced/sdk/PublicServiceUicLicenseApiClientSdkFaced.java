package com.dtstack.engine.master.faced.sdk;

import com.dtstack.engine.master.faced.base.DtStackSdk;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.LicenseProductComponentVO;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-04-14 20:04
 */
@DtStackSdk(sdkName = "public service", clientName = "uicLicenseApiClient")
public class PublicServiceUicLicenseApiClientSdkFaced extends AbstractPublicServiceSdkFaced {

    public LicenseProductComponentVO readComponent(String code) {
        return  getData(uicLicenseApiClient.readComponent(code));
    }
}
