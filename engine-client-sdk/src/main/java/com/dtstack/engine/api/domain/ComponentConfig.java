package com.dtstack.engine.api.domain;

/**
 * @author yuebai
 * @date 2021-02-08
 */
public class ComponentConfig extends DataObject{

    private Long componentId;
    private Long clusterId;
    private Integer componentTypeCode;
    private String type;
    private String key;
    private String keyDescribe;
    private String dependName;
    private String value;
    private String values;
    private String dependencyKey;
    private String dependencyValue;
    private Integer required;
    private String desc;
    private Integer sort;
    private String cascadeKey;
    private String cascadeValue;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getComponentId() {
        return componentId;
    }

    public String getKeyDescribe() {
        return keyDescribe;
    }

    public void setKeyDescribe(String keyDescribe) {
        this.keyDescribe = keyDescribe;
    }

    public String getDependName() {
        return dependName;
    }

    public void setDependName(String dependName) {
        this.dependName = dependName;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public void setDependencyKey(String dependencyKey) {
        this.dependencyKey = dependencyKey;
    }

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getCascadeKey() {
        return cascadeKey;
    }

    public void setCascadeKey(String cascadeKey) {
        this.cascadeKey = cascadeKey;
    }

    public String getCascadeValue() {
        return cascadeValue;
    }

    public void setCascadeValue(String cascadeValue) {
        this.cascadeValue = cascadeValue;
    }

}
