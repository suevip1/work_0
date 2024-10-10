package com.dtstack.engine.api.vo.alert;

import com.dtstack.engine.api.enums.AlarmTypeEnum;

import java.util.List;

/**
 * @author leon
 * @date 2022-10-21 15:09
 **/
public class AlertAlarmScopeVO {

    private Long id;

    private Long tenantId;

    private Long projectId;

    private Integer appType;

    private String name;

    /**
     * {@link AlarmTypeEnum}
     */
    private Integer alarmType;

    private Integer openStatus;

    /**
     * {@link com.dtstack.engine.api.enums.AlarmScopeEnum}
     */
    private Integer scope;

    /**
     * 选择的告警目录
     * 当 scope = 3，才会有值。
     */
    private List<Long> nodePIds;

    /**
     * 任务黑名单
     * 当 scope = 2、3，才会有值。
     */
    private List<Long> blacklist;

    private List<Long> tagIds;

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

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public List<Long> getNodePIds() {
        return nodePIds;
    }

    public void setNodePIds(List<Long> nodePIds) {
        this.nodePIds = nodePIds;
    }

    public List<Long> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Long> blacklist) {
        this.blacklist = blacklist;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
