package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ResourceQueueUsed extends BaseEntity {
    private Long id;
    private Long clusterId;
    private String queueName;
    private String used;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }
}
