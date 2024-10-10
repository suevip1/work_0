package com.dtstack.engine.api.vo.alert;

import java.util.List;

/**
 * alarm rule add or delete vo
 *
 * @author ：wangchuan
 * date：Created in 13:56 2022/8/4
 * company: www.dtstack.com
 */
public class AlarmRuleAddOrDeleteVO {

    /**
     * 需要删除的 alarmRule
     */
    private List<AlarmRuleDeleteVO> alarmRuleDeleteVOS;

    /**
     * 需要新增的 alarmRule
     */
    private List<AlarmRuleAddVO> alarmRuleAddVOS;

    public List<AlarmRuleDeleteVO> getAlarmRuleDeleteVOS() {
        return alarmRuleDeleteVOS;
    }

    public void setAlarmRuleDeleteVOS(List<AlarmRuleDeleteVO> alarmRuleDeleteVOS) {
        this.alarmRuleDeleteVOS = alarmRuleDeleteVOS;
    }

    public List<AlarmRuleAddVO> getAlarmRuleAddVOS() {
        return alarmRuleAddVOS;
    }

    public void setAlarmRuleAddVOS(List<AlarmRuleAddVO> alarmRuleAddVOS) {
        this.alarmRuleAddVOS = alarmRuleAddVOS;
    }
}
