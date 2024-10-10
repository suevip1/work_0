package com.dtstack.engine.api.param;

public class ComponentConfigQueryParam {
    private Long tenantId;
    private Integer componentCode;
    private String componentVersion;

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(Integer componentCode) {
        this.componentCode = componentCode;
    }
}
