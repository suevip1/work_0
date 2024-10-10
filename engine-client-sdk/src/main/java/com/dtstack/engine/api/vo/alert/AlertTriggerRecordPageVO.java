package com.dtstack.engine.api.vo.alert;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 3:17 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecordPageVO {

    private Long id;

    /**
     * 触发时间
     */
    private Timestamp gmtCreate;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 基线名称
     */
    private String baselineName;

    /**
     * 触发对象
     */
    private Integer alarmType;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 触发方式id
     */
    private Integer ruleId;

    /**
     * 触发方式
     */
    private String ruleName;

    /**
     * 责任人
     */
    private String ownerUserName;

    private Long alertChannelId;

    private Long alertAlarmId;

    private String alertAlarmName;
    /**
     * 告警方式
     */
    private String alertChannelName;

    /**
     * 通道是否被删除 true 被删除 false 未被删除
     */
    private Boolean deleteChannel;

    /**
     * 接收人信息
     */
    private List<String> receiveList;

    /**
     * 内容
     */
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getBaselineName() {
        return baselineName;
    }

    public void setBaselineName(String baselineName) {
        this.baselineName = baselineName;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getAlertChannelName() {
        return alertChannelName;
    }

    public void setAlertChannelName(String alertChannelName) {
        this.alertChannelName = alertChannelName;
    }

    public Long getAlertChannelId() {
        return alertChannelId;
    }

    public void setAlertChannelId(Long alertChannelId) {
        this.alertChannelId = alertChannelId;
    }

    public Boolean getDeleteChannel() {
        return deleteChannel;
    }

    public void setDeleteChannel(Boolean deleteChannel) {
        this.deleteChannel = deleteChannel;
    }

    public List<String> getReceiveList() {
        return receiveList;
    }

    public void setReceiveList(List<String> receiveList) {
        this.receiveList = receiveList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAlertAlarmId() {
        return alertAlarmId;
    }

    public void setAlertAlarmId(Long alertAlarmId) {
        this.alertAlarmId = alertAlarmId;
    }

    public String getAlertAlarmName() {
        return alertAlarmName;
    }

    public void setAlertAlarmName(String alertAlarmName) {
        this.alertAlarmName = alertAlarmName;
    }
}
