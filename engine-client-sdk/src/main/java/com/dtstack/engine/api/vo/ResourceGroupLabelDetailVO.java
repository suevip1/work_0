package com.dtstack.engine.api.vo;

/**
 * dtscript agent 资源组节点标签
 * @author qiuyun
 * @version 1.0
 * @date 2023-09-18 13:39
 */
public class ResourceGroupLabelDetailVO {
    /**
     * 资源组 id
     */
    private Long resourceId;

    /**
     * 资源组授权 id
     */
    private Long resourceGrantId;
    private String label;
    private String userName;
    private Long clusterId;
    private Integer componentTypeCode;

    /**
     * 是否项目级默认，0否1是
     */
    private Integer isProjectDefault;

    /**
     * 是否租户级默认，0否1是
     */
    private Integer isTenantDefault;

    /**
     * 项目 id
     */
    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResourceGrantId() {
        return resourceGrantId;
    }

    public void setResourceGrantId(Long resourceGrantId) {
        this.resourceGrantId = resourceGrantId;
    }

    public Integer getIsProjectDefault() {
        return isProjectDefault;
    }

    public void setIsProjectDefault(Integer isProjectDefault) {
        this.isProjectDefault = isProjectDefault;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Integer getIsTenantDefault() {
        return isTenantDefault;
    }

    public void setIsTenantDefault(Integer isTenantDefault) {
        this.isTenantDefault = isTenantDefault;
    }
}
