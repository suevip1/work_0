package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ResourceGroupGrant extends BaseEntity {
    private Long id;
    private Long resourceId;
    private Long projectId;
    private Integer appType;
    private Long dtuicTenantId;

    /**
     * 是否项目级默认，0否1是
     */
    private Integer isProjectDefault;

    public Integer getIsProjectDefault() {
        return isProjectDefault;
    }

    public void setIsProjectDefault(Integer isProjectDefault) {
        this.isProjectDefault = isProjectDefault;
    }

    private Long engineProjectId;

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public Long getEngineProjectId() {
        return engineProjectId;
    }

    public void setEngineProjectId(Long engineProjectId) {
        this.engineProjectId = engineProjectId;
    }
}
