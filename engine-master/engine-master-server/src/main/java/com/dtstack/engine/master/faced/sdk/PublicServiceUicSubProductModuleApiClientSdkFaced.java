package com.dtstack.engine.master.faced.sdk;

import com.dtstack.engine.master.faced.base.DtStackSdk;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.SubProductModuleVO;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-17 20:48
 **/
@DtStackSdk(sdkName = "public service", clientName = "uicSubProductModuleApiClient")
public class PublicServiceUicSubProductModuleApiClientSdkFaced extends AbstractPublicServiceSdkFaced {

    public List<SubProductModuleVO> getModuleList() {
        return getData(uicSubProductModuleApiClient.getModuleList());
    }

}