package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlarmRuleVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmMapBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageConditionVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.master.impl.AlertAlarmService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MockWith(AlertAlarmMock.class)
public class AlertAlarmSdkControllerTest {
    AlertAlarmSdkController alertAlarmSdkController = new AlertAlarmSdkController();

    @Test
    public void testPage() throws Exception {
        alertAlarmSdkController.page(new AlertAlarmPageConditionVO());
    }


    @Test
    public void testGetAlertAlarmDetails() throws Exception {
        alertAlarmSdkController.getAlertAlarmDetails(1L);
    }

    @Test
    public void testGetAlertAlarmByProjectIdAndName() throws Exception {
        alertAlarmSdkController.getAlertAlarmByProjectIdAndName(1L, "name");
    }

    @Test
    public void testListAlarmsByProjectIdAndNames() throws Exception {
        alertAlarmSdkController.listAlarmsByProjectIdAndNames(1L, Collections.<String>singletonList("String"));
    }

    @Test
    public void testGetAlertAlarmByTaskId() throws Exception {
        alertAlarmSdkController.getAlertAlarmByTaskId(Collections.<Long>singletonList(1L), 0, 0, 1L);
    }

    @Test
    public void testUpdateOpenStatus() throws Exception {
        alertAlarmSdkController.updateOpenStatus(1L, 0);
    }

    @Test
    public void testUpdateAlarmRules() throws Exception {
        alertAlarmSdkController.updateAlarmRules(Collections.singletonList(new AlarmRuleVO()));
    }

    @Test
    public void testDelete() throws Exception {
        alertAlarmSdkController.delete(1L);
    }

    @Test
    public void testDeleteByProjectId() throws Exception {
        alertAlarmSdkController.deleteByProjectId(1L);
    }
}

class AlertAlarmMock {
    @MockInvoke(targetClass = AlertAlarmService.class)
    public List<AlertAlarmMapBusinessVO> getAlertAlarmByTaskId(List<Long> taskIds,
                                                               Integer appType,
                                                               Integer businessType,
                                                               Long projectId) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public Integer updateOpenStatus(Long id, Integer openStatus) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public List<AlertAlarmVO> getAlertAlarmByProjectIdAndName(Long projectId, List<String> names) {
        return new ArrayList<>();

    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public Integer updateAlarmRules(List<AlarmRuleVO> vos) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public AlertAlarmVO getAlertAlarmDetails(Long id) {
        return null;
    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public Integer deleteByProjectId(Long projectId) {
        return 1;

    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public Integer delete(Long id) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public Integer addOrUpdateAlertAlarm(AlertAlarmVO alertAlarmVO) {
        return 1;
    }

    @MockInvoke(targetClass = AlertAlarmService.class)
    public PageResult<List<AlertAlarmPageVO>> page(AlertAlarmPageConditionVO vo) {
        return PageResult.EMPTY_PAGE_RESULT;
    }


}