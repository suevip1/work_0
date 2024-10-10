package com.dtstack.engine.api.vo.alert;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 2:46 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmPageVO {

    private Long id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 告警规则类型 0 任务 1 基线
     */
    private Integer alarmType;

    /**
     * 任务名称 多个会用逗号隔开
     */
    private List<String> taskNames;

    /**
     * 基线名称集合
     */
    private List<String> baselineNames;

    /**
     * 规则名称 多个会用逗号隔开
     */
    private List<Long> ruleIds;

    /**
     * 通道名称 多个会用逗号隔开
     */
    private List<String> channelNames;

    /**
     * 接收人用户id
     */
    private List<Long> receiverIds;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 规则状态 0 开始 1 关闭
     */
    private Integer openStatus;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 规则扩展信息
     */
    private String extraParams;

    /**
     * 任务ID list
     */
    private List<Long> taskIdList;

    /**
     * 基线ID list
     */
    private List<Long> baselineIdList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public List<String> getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(List<String> taskNames) {
        this.taskNames = taskNames;
    }

    public List<String> getBaselineNames() {
        return baselineNames;
    }

    public void setBaselineNames(List<String> baselineNames) {
        this.baselineNames = baselineNames;
    }

    public List<String> getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public List<Long> getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(List<Long> receiverIds) {
        this.receiverIds = receiverIds;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public List<Long> getBaselineIdList() {
        return baselineIdList;
    }

    public void setBaselineIdList(List<Long> baselineIdList) {
        this.baselineIdList = baselineIdList;
    }
}
