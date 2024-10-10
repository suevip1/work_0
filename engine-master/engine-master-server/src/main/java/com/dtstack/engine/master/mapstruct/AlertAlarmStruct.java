package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.alert.AlertAlarmBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmMapBusinessVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmPageVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmScopeVO;
import com.dtstack.engine.api.vo.alert.AlertAlarmVO;
import com.dtstack.engine.po.AlertAlarm;
import com.dtstack.engine.po.AlertAlarmRule;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 5:24 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface AlertAlarmStruct {

    List<AlertAlarmPageVO> alertAlarmListToAlertAlarmPageVOs(List<AlertAlarm> alertAlarmList);

    AlertAlarm alertAlarmVOToAlertAlarm(AlertAlarmVO alertAlarmVO);

    AlertAlarmVO toAlertAlarmVO(AlertAlarm alertAlarm);

    AlertAlarmMapBusinessVO toAlertAlarmMapBusinessVO(AlertAlarm alertAlarm);

    List<AlertAlarmBusinessVO> toAlertAlarmBusinessVO(List<AlertAlarmRule> rules);

    List<AlertAlarmVO> toAlertAlarmVOs(List<AlertAlarm> alertAlarms);

    AlertAlarmScopeVO toAlertAlarmScopeVO(AlertAlarm alertAlarm);

    List<AlertAlarmScopeVO> toAlertAlarmScopeVOs(List<AlertAlarm> alertAlarms);
}
