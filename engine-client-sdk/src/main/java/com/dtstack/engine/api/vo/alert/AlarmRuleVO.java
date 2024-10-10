package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/19 3:37 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlarmRuleVO {

    private Long alarmRuleId;

    private Long businessId;

    public Long getAlarmRuleId() {
        return alarmRuleId;
    }

    public void setAlarmRuleId(Long alarmRuleId) {
        this.alarmRuleId = alarmRuleId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
