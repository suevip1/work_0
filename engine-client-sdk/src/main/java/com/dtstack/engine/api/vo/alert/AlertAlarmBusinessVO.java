package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/19 2:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmBusinessVO {

    private Long id;

    /**
     * 规则id
     */
    private Long alertAlarmId;

    /**
     * 规则对应的业务类型： USER(0,"用户"),TASK(1,"任务"), RULE(2,"规则"),CHANNEL(3,"通道"),BASELINE(4,"基线")  SCOPE(7,"范围"),
     */
    private Integer businessType;

    /**
     * 业务id
     */
    private Long businessId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlertAlarmId() {
        return alertAlarmId;
    }

    public void setAlertAlarmId(Long alertAlarmId) {
        this.alertAlarmId = alertAlarmId;
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
