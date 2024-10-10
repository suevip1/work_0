package com.dtstack.engine.dto;

import java.util.List;

public class ResourceGroupGrantSearchDTO {
    private Long resourceId;
    private Integer appType;
    private List<Long> projectIds;
    private List<Long> dtuicTenantIds;
    private List<Long> resourceIds;

    /**用户组id,查询trino资源组授权关系时，用用户组id作为条件查询engine_project_id这个冗余字段**/
    private List<Long> userGroupIds;

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public List<Long> getDtuicTenantIds() {
        return dtuicTenantIds;
    }

    public void setDtuicTenantIds(List<Long> dtuicTenantIds) {
        this.dtuicTenantIds = dtuicTenantIds;
    }

    public List<Long> getUserGroupIds() {
        return userGroupIds;
    }

    public void setUserGroupIds(List<Long> userGroupIds) {
        this.userGroupIds = userGroupIds;
    }
}
