package com.dtstack.engine.master.mockcontainer.strategy;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.AlertTriggerRecordDao;
import com.dtstack.engine.dao.AlertTriggerRecordReceiveDao;
import com.dtstack.engine.dao.AlertTriggerRecordRepeatSendDao;
import com.dtstack.engine.master.faced.sdk.PublicServiceAlertApiClientSdkFaced;
import com.dtstack.engine.master.faced.sdk.PublicServiceNotifyApiClientSdkFaced;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.po.AlertTriggerRecord;
import com.dtstack.engine.po.AlertTriggerRecordReceive;
import com.dtstack.engine.po.AlertTriggerRecordRepeatSend;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.AlarmSendParam;
import com.dtstack.pubsvc.sdk.alert.channel.domain.param.NotifyRecordParam;
import com.dtstack.pubsvc.sdk.alert.channel.dto.ClusterAlertResultDTO;
import com.dtstack.pubsvc.sdk.usercenter.client.UicUserApiClient;
import com.dtstack.pubsvc.sdk.usercenter.domain.dto.UICUserVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.feign.Param;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/12/14 10:55 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AbstractStrategyMock extends BaseMock {

    @MockInvoke(targetClass = AlertTriggerRecordRepeatSendDao.class)
    public List<AlertTriggerRecordRepeatSend> selectByJobIdAndRuleId(String jobId, Long ruleId) {
        return Lists.newArrayList();
    }

    @MockInvoke(targetClass = PublicServiceAlertApiClientSdkFaced.class)
    public List<ClusterAlertResultDTO> listShow() {
        ClusterAlertResultDTO clusterAlertResultDTO = new ClusterAlertResultDTO();
        clusterAlertResultDTO.setAlertId(-1);

        ClusterAlertResultDTO clusterAlertResultDTO1 = new ClusterAlertResultDTO();
        clusterAlertResultDTO1.setAlertId(1);
        return Lists.newArrayList(clusterAlertResultDTO,clusterAlertResultDTO1);
    }

    @MockInvoke(targetClass = PublicServiceNotifyApiClientSdkFaced.class)
    public Boolean sendAlarmNew(AlarmSendParam alarmSendParam) {
        return  Boolean.TRUE;
    }

    @MockInvoke(targetClass = PublicServiceNotifyApiClientSdkFaced.class)
    public Long generateContent(NotifyRecordParam notifyRecordParam) {
        return 1L;
    }

    @MockInvoke(targetClass = UicUserApiClient.class)
    public ApiResponse<List<UICUserVO>> getByUserIds(@Param("userIds") List<Long> var1) {
        List<UICUserVO> uicUserVOS = Lists.newArrayList();
        UICUserVO uicUserVO = new UICUserVO();
        uicUserVO.setUserId(1L);
        uicUserVO.setTenantId(1L);
        uicUserVO.setUserName("1L");
        uicUserVOS.add(uicUserVO);
        ApiResponse<List<UICUserVO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(uicUserVOS);
        apiResponse.setSuccess(true);
        return apiResponse;
    }
    @MockInvoke(targetClass = AlertTriggerRecordReceiveDao.class)
    public Integer batchInsert(List<AlertTriggerRecordReceive> alertTriggerRecordReceives){
        return 1;
    }
    @MockInvoke(targetClass = AlertTriggerRecordDao.class)
    public Integer insert(AlertTriggerRecord alertTriggerRecord){
        return 1;
    }

    @MockInvoke(targetClass = AlertTriggerRecordRepeatSendDao.class)
    public Integer insert(AlertTriggerRecordRepeatSend alertTriggerRecordRepeatSend){
        return 1;
    }



}
