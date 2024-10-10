package com.dtstack.engine.api.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ResourceGroupPageParam {
    @NotNull
    private Long clusterId;

    private Long dtuicTenantId;

    @NotNull
    @Min(1)
    private Integer currentPage;
    @NotNull
    @Min(1)
    private Integer pageSize;

    private Integer componentTypeCode;

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
