package com.dtstack.engine.api.vo.alert;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 2:46 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmVO {

    private Long id;

    private Long tenantId;

    private Integer appType;

    private Long projectId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 告警类型 0：周期任务  1：基线 2:手动任务
     */
    private Integer alarmType;

    /**
     * 任务ids
     */
    private List<Long> taskIds;

    /**
     * 基线ids
     */
    private List<Long> baselineIds;

    /**
     * 规则ids
     */
    private List<Long> ruleIds;

    /**
     * 通道ids
     */
    private List<Long> channelIds;

    /**
     * 其他用户ids
     */
    private List<Long> otherUserIds;

    /**
     * 接收人用户ids
     */
    private List<Long> receiverIds;

    /**
     * 用户组 ids
     */
    private List<Long> groupIds;

    /**
     * 创建人用户id
     */
    private Long createUserId;

    /**
     * 规则状态 0 开始 1 关闭
     */
    private Integer openStatus;

    /**
     * 额外参数 用于传入规则所需要的字段
     */
    private Map<String,Object> extraParamMap;

    /**
     * 任务选择范围：1：选择，2：所有任务，3：类目。默认 1
     * 目前只有告警对象是任务（alarmType=1）才会有任务选择范围
     */
    private Integer scope;


    /**
     * 任务黑名单
     */
    private List<Long> blacklist;

    /**
     * 选择的告警类目pid
     */
    private List<Long> rootNodePIds;

    /**
     * rootNodePId 下（包含rootNodePId）所有 nodePId
     *
     */
    private List<Long> nodePIds;

    /**
     * 选择的标签
     */
    private List<Long> tagIds;


    /**
     * 是否勾选了任务责任人
     */
    private Boolean chooseTaskOwner;

    /**
     * 是否支持补数据告警
     */
    private boolean supportFillData;

    public boolean isSupportFillData() {
        return supportFillData;
    }

    public void setSupportFillData(boolean supportFillData) {
        this.supportFillData = supportFillData;
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

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getBaselineIds() {
        return baselineIds;
    }

    public void setBaselineIds(List<Long> baselineIds) {
        this.baselineIds = baselineIds;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
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

    public Map<String, Object> getExtraParamMap() {
        return extraParamMap;
    }

    public void setExtraParamMap(Map<String, Object> extraParamMap) {
        this.extraParamMap = extraParamMap;
    }

    public List<Long> getOtherUserIds() {
        return otherUserIds;
    }

    public void setOtherUserIds(List<Long> otherUserIds) {
        this.otherUserIds = otherUserIds;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public List<Long> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Long> blacklist) {
        this.blacklist = blacklist;
    }

    public List<Long> getRootNodePIds() {
        return rootNodePIds;
    }

    public void setRootNodePIds(List<Long> rootNodePIds) {
        this.rootNodePIds = rootNodePIds;
    }

    public List<Long> getNodePIds() {
        return nodePIds;
    }

    public void setNodePIds(List<Long> nodePIds) {
        this.nodePIds = nodePIds;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Boolean getChooseTaskOwner() {
        return chooseTaskOwner;
    }

    public void setChooseTaskOwner(Boolean chooseTaskOwner) {
        this.chooseTaskOwner = chooseTaskOwner;
    }
}
