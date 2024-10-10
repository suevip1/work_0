package com.dtstack.engine.api.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ClusterTenantResourceBindingParam {
    @NotNull
    private Long clusterId;
    @NotNull
    @Min(1)
    private Long tenantId;
    private Long resourceId;
    private String namespace;
    /**
     * 节点标签资源组 id
     */
    private Long labelResourceId;

    public Long getLabelResourceId() {
        return labelResourceId;
    }

    public void setLabelResourceId(Long labelResourceId) {
        this.labelResourceId = labelResourceId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClusterTenantResourceBindingParam{");
        sb.append("clusterId=").append(clusterId);
        sb.append(", tenantId=").append(tenantId);
        sb.append(", resourceId=").append(resourceId);
        sb.append(", labelResourceId=").append(labelResourceId);
        sb.append(", namespace='").append(namespace).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
