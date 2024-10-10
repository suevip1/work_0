package com.dtstack.engine.api.vo.alert;

import com.dtstack.engine.api.param.PageParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 3:17 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecordConditionVO extends PageParam {

    private String alertAlarmName;

    private Integer appType;

    private Long projectId;

    private Long tenantId;

    private List<Integer> alarmTypes;

    private List<Long> ruleIds;

    private String baselineName;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 开始触发时间 yyyy-mm-dd
     */
    private Long startTime;

    /**
     * 结束触发时间 yyyy-mm-dd
     */
    private Long endTime;

    /**
     * 接收人id （历史原因 receiverId 实际接受为任务责任人 = taskOwnerId）
     */
    @Deprecated
    private Long receiverId;

    private Long taskOwnerId;

    public Long getTaskOwnerId() {
        return taskOwnerId;
    }

    public void setTaskOwnerId(Long taskOwnerId) {
        this.taskOwnerId = taskOwnerId;
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

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
