package com.dtstack.engine.api.param;

import javax.validation.constraints.*;

public class ResourceGroupParam {
    @Min(1)
    private Long id;

    @NotNull
    @Size(min=1, max=64, message = "支持1-64字符")
    @Pattern(regexp="^[_a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "支持中文、英文、数字、下划线")
    private String name;

    @NotBlank
    private String queueName;

    @NotNull
    private Long clusterId;

    @Size(max=255, message = "不能超过255字符")
    private String description;

    private Integer componentTypeCode;

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
