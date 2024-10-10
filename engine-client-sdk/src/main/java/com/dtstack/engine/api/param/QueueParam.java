package com.dtstack.engine.api.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class QueueParam {
    @NotNull
    private Long clusterId;
    @NotBlank
    private String queueName;

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
}
