package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 2:43 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecord {

    private Long id;

    private Long tenantId;

    private Long projectId;

    private Integer appType;

    private String alertAlarmName;

    private Timestamp triggerTime;

    /**
     * 触发方式id 例如 任务失败，任务到时间未执行等
     */
    private Long ruleId;

    /**
     * 触发方式名称
     */
    private String ruleName;

    /**
     * 规则类型，例如任务，基线
     */
    private Integer alarmType;

    private String taskName;

    private String baselineName;

    private Integer taskType;

    private Long alertAlarmId;

    private String alertChannelName;

    private String ownerUserName;

    private String receiverUserName;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    private String content;

    private Long ownerUserId;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAlertAlarmName() {
        return alertAlarmName;
    }

    public void setAlertAlarmName(String alertAlarmName) {
        this.alertAlarmName = alertAlarmName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Timestamp getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Timestamp triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
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

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getAlertAlarmId() {
        return alertAlarmId;
    }

    public void setAlertAlarmId(Long alertAlarmId) {
        this.alertAlarmId = alertAlarmId;
    }

    public String getAlertChannelName() {
        return alertChannelName;
    }

    public void setAlertChannelName(String alertChannelName) {
        this.alertChannelName = alertChannelName;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
