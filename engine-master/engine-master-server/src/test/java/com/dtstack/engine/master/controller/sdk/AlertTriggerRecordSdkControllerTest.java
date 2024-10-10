package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordConditionVO;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordPageVO;
import com.dtstack.engine.api.vo.alert.CountAlarmVO;
import com.dtstack.engine.master.impl.AlertTriggerRecordService;
import org.junit.Test;

import java.util.List;

@MockWith(AlertTriggerRecordSdkControllerMock.class)
public class AlertTriggerRecordSdkControllerTest {
    AlertTriggerRecordSdkController alertTriggerRecordSdkController = new AlertTriggerRecordSdkController();

    @Test
    public void testPage() throws Exception {
        alertTriggerRecordSdkController.page(new AlertTriggerRecordConditionVO());
    }

//    @Test
//    public void testCountAlarm() throws Exception {
//        alertTriggerRecordSdkController.countAlarm(1L);
//    }
}

class AlertTriggerRecordSdkControllerMock {

    @MockInvoke(targetClass = AlertTriggerRecordService.class)
    public CountAlarmVO countAlarm(Long projectId) {
        return null;
    }

    @MockInvoke(targetClass = AlertTriggerRecordService.class)
    public PageResult<List<AlertTriggerRecordPageVO>> page(AlertTriggerRecordConditionVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;
    }

}