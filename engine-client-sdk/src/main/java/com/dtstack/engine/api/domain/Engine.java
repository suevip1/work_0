package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel
public class Engine extends BaseEntity{

    private Long clusterId;

    private String engineName;

    private int engineType;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public int getEngineType() {
        return engineType;
    }

    public void setEngineType(int engineType) {
        this.engineType = engineType;
    }

}
