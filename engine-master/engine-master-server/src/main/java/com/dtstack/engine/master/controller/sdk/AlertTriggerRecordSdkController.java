package com.dtstack.engine.master.controller.sdk;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordConditionVO;
import com.dtstack.engine.api.vo.alert.AlertTriggerRecordPageVO;
import com.dtstack.engine.api.vo.alert.CountAlarmVO;
import com.dtstack.engine.api.vo.project.ProjectVO;
import com.dtstack.engine.master.impl.AlertTriggerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 5:45 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping({"/node/sdk/alert/trigger/record"})
public class AlertTriggerRecordSdkController {

    @Autowired
    private AlertTriggerRecordService alertTriggerRecordService;

    @PostMapping(value = "/page")
    public PageResult<List<AlertTriggerRecordPageVO>> page(@RequestBody AlertTriggerRecordConditionVO vo) {
        return alertTriggerRecordService.page(vo);
    }

    @PostMapping(value = "/countAlarm")
    public CountAlarmVO countAlarm(@RequestBody ProjectVO projectVO) {
        Integer appType = projectVO.getAppType() == null ? AppType.RDOS.getType() : projectVO.getAppType();
        return alertTriggerRecordService.countAlarm(projectVO.getProjectId(), appType);
    }
}
