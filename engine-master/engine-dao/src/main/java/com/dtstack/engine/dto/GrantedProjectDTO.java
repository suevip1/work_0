package com.dtstack.engine.dto;

public class GrantedProjectDTO {
    private Long projectId;
    private Long tenantId;
    private Integer appType;
    private Long engineProjectId;

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

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getEngineProjectId() {
        return engineProjectId;
    }

    public void setEngineProjectId(Long engineProjectId) {
        this.engineProjectId = engineProjectId;
    }
}
