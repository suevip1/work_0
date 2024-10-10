package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

/**
 * @author leon
 * @date 2022-08-22 10:49
 **/
@ApiModel
public class ConsoleTenantComponent extends BaseEntity {

    private Long tenantId;

    private Long clusterId;

    private String componentName;

    private Integer componentTypeCode;

    private String componentConfig;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
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
