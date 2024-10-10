package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author leon
 * @date 2022-08-23 10:59
 **/
@ApiModel
public class AddOrUpdateTenantComponentVO {

    @ApiModelProperty(value = "租户id",required = true)
    private Long tenantId;

    @ApiModelProperty(value = "组件code",required = true)
    private Integer componentTypeCode;

    @ApiModelProperty(value = "组件配置",required = true)
    private String componentConfig;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }
}
