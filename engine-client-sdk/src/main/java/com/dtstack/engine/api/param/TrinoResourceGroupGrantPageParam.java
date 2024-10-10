package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;

public class TrinoResourceGroupGrantPageParam {

    /**资源组名称，多级的用-分隔拼接**/
    @NotNull
    private String resourceName;

    @NotNull
    private Long clusterId;

    /**租户名称**/
    private String tenantName;

    /**用户组名称**/
    private String groupName;

    private Integer currentPage;
    private Integer pageSize;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
