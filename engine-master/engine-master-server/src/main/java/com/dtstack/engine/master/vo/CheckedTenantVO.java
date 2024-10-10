package com.dtstack.engine.master.vo;

public class CheckedTenantVO {

    private Long tenantId;
    private String tenantName;
    private boolean bounded;

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

    public boolean isBounded() {
        return bounded;
    }

    public void setBounded(boolean bounded) {
        this.bounded = bounded;
    }

}
