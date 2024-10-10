package com.dtstack.engine.api.vo.components;

/**
 * @author yuebai
 * @date 2021-11-01
 */
public class ComponentVersionVO {

    /**
     * 组件版本 如1.8 1.10
     */
    private String version;

    /**
     * 当前组件是否为默认版本
     */
    private Boolean isDefault;

    /**
     * 部署类型 on yarn  或者 standalone
     */
    private Integer deployType;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }
}
