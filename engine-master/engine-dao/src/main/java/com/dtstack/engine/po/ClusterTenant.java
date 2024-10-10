package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ClusterTenant extends BaseEntity {

    private Long queueId;

    private Long defaultResourceId;

    private Long defaultLabelResourceId;

    private Long clusterId;

    private Long dtUicTenantId;

    private String commonConfig;

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public Long getDefaultResourceId() {
        return defaultResourceId;
    }

    public void setDefaultResourceId(Long defaultResourceId) {
        this.defaultResourceId = defaultResourceId;
    }

    public String getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(String commonConfig) {
        this.commonConfig = commonConfig;
    }

    public Long getDefaultLabelResourceId() {
        return defaultLabelResourceId;
    }

    public void setDefaultLabelResourceId(Long defaultLabelResourceId) {
        this.defaultLabelResourceId = defaultLabelResourceId;
    }
}

