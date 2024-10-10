package com.dtstack.engine.dto;

public class CalenderTaskDTO {

    private String concatTaskNames;
    private Long calenderId;
    private Long tenantId;
    private Long projectId;
    private String taskName;
    private Long ownerUserId;
    private Long taskId;
    private String calenderName;
    private Integer appType;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getConcatTaskNames() {
        return concatTaskNames;
    }

    public void setConcatTaskNames(String concatTaskNames) {
        this.concatTaskNames = concatTaskNames;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }
}
