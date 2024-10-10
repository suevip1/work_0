package com.dtstack.engine.master.faced.sdk;

import com.dtstack.engine.master.faced.base.DtStackSdk;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.AlarmSendParam;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.NotifyRecordParam;

/**
 * @author leon
 * @date 2022-10-09 17:41
 **/
@DtStackSdk(sdkName = "public service", clientName = "alertNotifyApiClient")
public class PublicServiceNotifyApiClientSdkFaced extends AbstractPublicServiceSdkFaced {


    public Boolean sendAlarmNew(AlarmSendParam alarmSendParam) {
        return  getData(alertNotifyApiClient.sendAlarmNew(alarmSendParam));
    }

    public Long generateContent(NotifyRecordParam notifyRecordParam) {
        return getData(alertNotifyApiClient.generateContent(notifyRecordParam));
    }
}
