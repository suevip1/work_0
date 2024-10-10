package com.dtstack.engine.master.faced.sdk;

import com.dtstack.engine.master.faced.base.DtStackSdk;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;

import java.util.List;

/**
 * @author leon
 * @date 2022-10-18 12:01
 **/
@DtStackSdk(sdkName = "public service", clientName = "alertApiClient")
public class PublicServiceAlertApiClientSdkFaced extends AbstractPublicServiceSdkFaced {

    public List<ClusterAlertResultDTO> listShow() {
        return getData(alertApiClient.listShow());
    }

}
