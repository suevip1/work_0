package com.dtstack.engine.api.vo.alert;

import java.util.List;

/**
 * alarm rule delete vo
 *
 * @author ：wangchuan
 * date：Created in 13:56 2022/8/4
 * company: www.dtstack.com
 */
public class AlarmRuleDeleteVO {

    /**
     * 告警 id, 必需
     */
    private List<Long> alarmIds;

    /**
     * 类型, 如不传则删除所有类型
     */
    private List<Integer> businessTypes;

    private List<Long> businessIds;

    public List<Long> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(List<Long> alarmIds) {
        this.alarmIds = alarmIds;
    }

    public List<Integer> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<Integer> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public List<Long> getBusinessIds() {
        return businessIds;
    }

    public void setBusinessIds(List<Long> businessIds) {
        this.businessIds = businessIds;
    }
}
