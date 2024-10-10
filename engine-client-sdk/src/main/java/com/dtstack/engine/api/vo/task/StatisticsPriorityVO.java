package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2023-07-05 11:11
 * @Email: dazhi@dtstack.com
 * @Description: StatisticsPriorityVO
 */
public class StatisticsPriorityVO {

    private Long projectId;

    private Integer appType;

    private Long tenantId;

    private Long resourceId;

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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
