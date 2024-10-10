package com.dtstack.engine.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class TenantNameVO {

    @ApiModelProperty(value = "租户id")
    private Long tenantId;
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

}
