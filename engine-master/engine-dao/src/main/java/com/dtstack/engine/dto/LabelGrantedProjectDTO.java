package com.dtstack.engine.dto;

import com.dtstack.engine.master.dto.LabelResourceGroupUserDTO;

import java.util.List;

public class LabelGrantedProjectDTO {

    private Integer appType;

    /**
     * 产品名称
     */
    private String productName;

    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 租户 id
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 资源组 id -- 逗号拼接
     */
    private String concatResourceIds;

    /**
     * 资源组详情
     */
    private List<LabelResourceGroupUserDTO> labelResourceGroupUsers;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getConcatResourceIds() {
        return concatResourceIds;
    }

    public void setConcatResourceIds(String concatResourceIds) {
        this.concatResourceIds = concatResourceIds;
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

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<LabelResourceGroupUserDTO> getLabelResourceGroupUsers() {
        return labelResourceGroupUsers;
    }

    public void setLabelResourceGroupUsers(List<LabelResourceGroupUserDTO> labelResourceGroupUsers) {
        this.labelResourceGroupUsers = labelResourceGroupUsers;
    }
}
