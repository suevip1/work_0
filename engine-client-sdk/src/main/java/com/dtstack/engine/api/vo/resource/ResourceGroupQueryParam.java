package com.dtstack.engine.api.vo.resource;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-18 13:52
 */
public class ResourceGroupQueryParam {
    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 项目 id
     */
    private Long projectId;

    private Integer componentTypeCode;

    /**
     * 租户 id
     */
    private Long dtuicTenantId;

    /**
     * 节点标签
     */
    private String label;

    /**
     * 服务器用户
     */
    private String userName;

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
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

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }
}
