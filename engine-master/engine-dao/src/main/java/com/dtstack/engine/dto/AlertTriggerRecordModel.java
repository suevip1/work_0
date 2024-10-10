package com.dtstack.engine.dto;

import com.dtstack.engine.api.param.PageParam;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 3:32 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecordModel extends PageParam {

    private String alertAlarmName;

    private Integer appType;

    private Long projectId;

    private Long tenantId;

    private List<Integer> alarmTypes;

    private List<Long> ruleIds;

    private String baselineName;

    private String taskName;

    /**
     * 开始触发时间
     */
    private Timestamp startTimestamp;

    /**
     * 结束触发时间
     */
    private Timestamp endTimestamp;

    private Long ownerUserId;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getAlertAlarmName() {
        return alertAlarmName;
    }

    public void setAlertAlarmName(String alertAlarmName) {
        this.alertAlarmName = alertAlarmName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<Integer> getAlarmTypes() {
        return alarmTypes;
    }

    public void setAlarmTypes(List<Integer> alarmTypes) {
        this.alarmTypes = alarmTypes;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }


}
