package com.dtstack.engine.api.domain;

import com.dtstack.engine.api.param.PageParam;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 4:49 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlertAlarmModel extends PageParam {

    private List<Long> alarmIds;

    private Long createUserId;

    private Integer appType;

    private Long projectId;

    private Long tenantId;

    private Integer openStatus;

    private List<Integer> alarmTypes;

    private String name;

    public AlertAlarmModel() {
    }

    public AlertAlarmModel(List<Integer> alarmTypes,
                           List<Long> alarmIds,
                           Long createUserId,
                           Integer appType,
                           Long projectId,
                           Long tenantId,
                           Integer openStatus,
                           String name,
                           Integer currentPage,
                           Integer pageSize) {
        this.alarmTypes = alarmTypes;
        this.alarmIds = alarmIds;
        this.createUserId = createUserId;
        this.appType = appType;
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.openStatus = openStatus;
        this.name = name;
        setCurrentPage(currentPage);
        setPageSize(pageSize);
    }

    public List<Long> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(List<Long> alarmIds) {
        this.alarmIds = alarmIds;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
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

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public List<Integer> getAlarmTypes() {
        return alarmTypes;
    }

    public void setAlarmTypes(List<Integer> alarmTypes) {
        this.alarmTypes = alarmTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
