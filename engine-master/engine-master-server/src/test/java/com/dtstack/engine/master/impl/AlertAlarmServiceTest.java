package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.enums.AlarmRuleBusinessTypeEnum;
import com.dtstack.engine.api.enums.AlarmTypeEnum;
import com.dtstack.engine.api.vo.alert.AlarmRuleVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageConditionVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.master.mockcontainer.impl.AlertAlarmServiceMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;

@MockWith(AlertAlarmServiceMock.class)
public class AlertAlarmServiceTest {
    AlertAlarmService alertAlarmService = new AlertAlarmService();

    @Test
    public void testPage() throws Exception {
        AlertAlarmPageConditionVO alertAlarmPageConditionVO = new AlertAlarmPageConditionVO();
        alertAlarmPageConditionVO.setTaskName("test");
        alertAlarmService.page(alertAlarmPageConditionVO);
    }

    @Test
    public void testAddOrUpdateAlertAlarm() throws Exception {
        AlertAlarmVO alertAlarmVO = new AlertAlarmVO();
        alertAlarmVO.setReceiverIds(Lists.newArrayList(1L));
        alertAlarmVO.setChannelIds(Lists.newArrayList(1L));
        alertAlarmVO.setRuleIds(Lists.newArrayList(1L));
        alertAlarmVO.setAlarmType(AlarmTypeEnum.TASK.getCode());
        alertAlarmVO.setTaskIds(Lists.newArrayList(1L));
        alertAlarmVO.setName("test");
        alertAlarmService.addOrUpdateAlertAlarm(alertAlarmVO);
    }

    @Test
    public void testDelete() throws Exception {
        alertAlarmService.delete(1L);
    }

    @Test
    public void testDeleteAlarmTask() throws Exception {
        alertAlarmService.deleteAlarmTask(Collections.singletonList(1L), 0, AlarmRuleBusinessTypeEnum.USER);
    }

    @Test
    public void testGetAlertAlarmDetails() throws Exception {
        alertAlarmService.getAlertAlarmDetails(1L);
    }


    @Test
    public void testGetAlertAlarmByTaskId() throws Exception {
        alertAlarmService.getAlertAlarmByTaskId(Collections.singletonList(1L), 0, 0, 1L);
    }

    @Test
    public void testUpdateAlarmRules() throws Exception {
        AlarmRuleVO alarmRuleVO = new AlarmRuleVO();
        alarmRuleVO.setAlarmRuleId(1L);
        alertAlarmService.updateAlarmRules(Collections.singletonList(alarmRuleVO));
    }

}