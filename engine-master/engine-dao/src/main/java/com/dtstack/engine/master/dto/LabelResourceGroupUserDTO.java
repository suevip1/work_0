package com.dtstack.engine.master.dto;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-22 14:39
 */
public class LabelResourceGroupUserDTO {
    /**
     * 资源组 id
     */
    private Long resourceId;
    /**
     * 节点标签
     */
    private String label;
    /**
     * 服务器用户
     */
    private String userName;
    /**
     * 组件类型
     */
    private Integer componentTypeCode;

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
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
}
