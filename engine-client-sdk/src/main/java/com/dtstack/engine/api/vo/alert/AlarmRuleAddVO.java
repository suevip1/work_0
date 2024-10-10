package com.dtstack.engine.api.vo.alert;

/**
 * alarm rule add vo
 *
 * @author ：wangchuan
 * date：Created in 13:56 2022/8/4
 * company: www.dtstack.com
 */
public class AlarmRuleAddVO {

    /**
     * 告警 id
     */
    private Long alarmId;

    /**
     * 告警规则类型
     */
    private Integer businessType;

    /**
     * 告警 business id
     */
    private Long businessId;

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
