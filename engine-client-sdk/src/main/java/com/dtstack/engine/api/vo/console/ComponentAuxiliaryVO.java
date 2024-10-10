package com.dtstack.engine.api.vo.console;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-04-18 17:45
 */
public class ComponentAuxiliaryVO {
    private String key;
    private String label;
    private Integer required;
    private String value;
    private String formType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ComponentAuxiliaryVO{");
        sb.append("key='").append(key).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", required=").append(required);
        sb.append(", value='").append(value).append('\'');
        sb.append(", formType='").append(formType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
