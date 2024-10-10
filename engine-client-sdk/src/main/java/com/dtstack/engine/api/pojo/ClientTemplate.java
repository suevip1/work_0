package com.dtstack.engine.api.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-14
 */
@ApiModel
public class ClientTemplate implements Serializable {
    /**
     * 前端界面展示 名称
     */
    @ApiModelProperty(notes = "前端界面展示 名称")
    private String key;
    /**
     * 前端界面tips提示 key的中文提示
     */
    @ApiModelProperty(notes = "前端界面tips提示 名称")
    private String keyDescribe;
    /**
     *
     */
    @ApiModelProperty(notes = "前端分组依赖名")
    private String dependName;
    /**
     * 前端界面展示 多选值
     */
    @ApiModelProperty(notes = "前端界面展示 多选值")
    private List<ClientTemplate> values;

    /**
     * 分组的值
     */
    @ApiModelProperty(notes = "分组的值")
    private List<ClientTemplate> groupValues;

    /**
     * 前端界面展示类型  0: 输入框 1:单选:
     */
    @ApiModelProperty(notes = "前端界面展示类型  0: 输入框 1:单选:")
    private String type;
    /**
     * 默认值
     */
    @ApiModelProperty(notes = "默认值")
    private Object value;
    /**
     * 是否必填 默认必须
     */
    @ApiModelProperty(notes = "是否必填 默认非必须")
    private Boolean required = false;

    private String dependencyKey;

    private String dependencyValue;

    @JSONField(serialize = false)
    private Long id = 0L;

    private List<Integer> deployTypes;

    private Integer sort;

    @ApiModelProperty(notes = "级联的 key")
    private String cascadeKey;

    @ApiModelProperty(notes = "级联的 value")
    private String cascadeValue;

    /**
     * 是否隐藏 true 隐藏 false 显示
     */
    private Boolean hidden = false;

    /**
     * 是否为默认获取方式 true是 false否 如果未null的话第一次选择的为默认版本
     */
    private Boolean defaulted;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getRequired() {
        return required;
    }

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public void setDependencyKey(String dependencyKey) {
        this.dependencyKey = dependencyKey;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public List<ClientTemplate> getGroupValues() {
        return groupValues;
    }

    public void setGroupValues(List<ClientTemplate> groupValues) {
        this.groupValues = groupValues;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ClientTemplate> getValues() {
        return values;
    }

    public void setValues(List<ClientTemplate> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ClientTemplate(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public List<Integer> getDeployTypes() {
        return deployTypes;
    }

    public void setDeployTypes(List<Integer> deployTypes) {
        this.deployTypes = deployTypes;
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

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getDefaulted() {
        return defaulted;
    }

    public void setDefaulted(Boolean defaulted) {
        this.defaulted = defaulted;
    }

    public ClientTemplate() {
    }
}
