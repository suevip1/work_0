package com.dtstack.engine.master.controller.sdk;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlarmRuleAddOrDeleteVO;
import com.dtstack.engine.api.vo.alert.AlarmRuleVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmMapBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageConditionVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmScopeVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.AlertAlarmService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 5:49 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping({"/node/sdk/alarm"})
public class AlertAlarmSdkController {

    @Autowired
    private AlertAlarmService alertAlarmService;

    @PostMapping(value = "/page")
    public PageResult<List<AlertAlarmPageVO>> page(@RequestBody AlertAlarmPageConditionVO vo) {
        return alertAlarmService.page(vo);
    }

    @PostMapping(value = "/addOrUpdateAlertAlarm")
    public void addOrUpdateAlertAlarm(@RequestBody AlertAlarmVO alertAlarmVO) {
        alertAlarmService.addOrUpdateAlertAlarm(alertAlarmVO);
    }

    @PostMapping(value = "/getAlertAlarmDetails")
    public AlertAlarmVO getAlertAlarmDetails (@RequestParam("id") Long id) {
        return alertAlarmService.getAlertAlarmDetails(id);
    }

    @PostMapping(value = "/getAlertAlarmByProjectIdAndName")
    public List<AlertAlarmVO> getAlertAlarmByProjectIdAndName(@RequestParam("projectId") Long projectId, @RequestParam("name") String name) {
        return alertAlarmService.getAlertAlarmByProjectIdAndName(projectId, Lists.newArrayList(name));
    }

    @PostMapping(value = "/listAlarmsByProjectIdAndNames")
    public List<AlertAlarmVO> listAlarmsByProjectIdAndNames(@RequestParam("projectId") Long projectId, @RequestParam("names")List<String> names) {
        return alertAlarmService.getAlertAlarmByProjectIdAndName(projectId, names);
    }

    @PostMapping(value = "/getAlertAlarmByTaskId")
    public List<AlertAlarmMapBusinessVO> getAlertAlarmByTaskId(@RequestParam("taskIds") List<Long> taskIds,
                                                               @RequestParam("appType") Integer appType,
                                                               @RequestParam("businessType") Integer businessType,
                                                               @RequestParam("projectId") Long projectId) {
        return alertAlarmService.getAlertAlarmByTaskId(taskIds, appType, businessType,projectId);
    }

    @PostMapping(value = "/updateOpenStatus")
    public Integer updateOpenStatus(@RequestParam("id") Long id,@RequestParam("openStatus") Integer openStatus) {
        return alertAlarmService.updateOpenStatus(id,openStatus);
    }

    @PostMapping(value = "/updateAlarmRules")
    public Integer updateAlarmRules(@RequestBody List<AlarmRuleVO> vos) {
        return alertAlarmService.updateAlarmRules(vos);
    }

    @PostMapping(value = "/delete")
    public Integer delete(@RequestParam("id") Long id) {
        return alertAlarmService.delete(id);
    }

    @PostMapping(value = "/deleteByProjectId")
    public Integer deleteByProjectId(@RequestParam("projectId") Long projectId) {
        return alertAlarmService.deleteByProjectId(projectId);
    }

    @PostMapping(value = "/addOrDeleteAlarmRules")
    public Boolean addOrDeleteAlarmRules(@RequestBody AlarmRuleAddOrDeleteVO alarmRuleAddOrDeleteVO) {
        return alertAlarmService.addOrDeleteAlarmRules(alarmRuleAddOrDeleteVO);
    }

    @PostMapping(value = "/getAlarmByProjectIdAndScope")
    public List<AlertAlarmScopeVO> getAlarmByProjectIdAndScope(@RequestParam("projectId") Long projectId,
                                                               @RequestParam("appType") Integer appType,
                                                               @RequestParam("scopes") List<Integer> scopes) {
        if (Objects.isNull(projectId)) {
            throw new RdosDefineException(ErrorCode.PROJECT_ID_CAN_NOT_NULL);
        }
        if (Objects.isNull(appType)) {
            throw new RdosDefineException(ErrorCode.APP_TYPE_NOT_NULL);
        }
        return alertAlarmService.getAlarmByProjectIdAndScope(projectId, appType, scopes);
    }
}
