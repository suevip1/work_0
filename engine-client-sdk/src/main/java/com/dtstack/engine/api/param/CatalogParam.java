package com.dtstack.engine.api.param;

import com.dtstack.engine.api.pojo.catalog.Catalog;

import javax.validation.constraints.NotNull;

public class CatalogParam {
    @NotNull(message = "jobId can not be null")
    private String jobId;

    @NotNull(message = "catalog can not be null")
    private Catalog catalog;

    @NotNull(message = "engineType can not be null")
    private String engineType;

    @NotNull(message = "uic tenant can not be null")
    private Long tenantId;

    @NotNull(message = "ddl can not be null")
    private String ddl;

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
