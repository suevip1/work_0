package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

@ApiModel
public class ResourceHandOver extends BaseEntity {

    private Long projectId;
    private Long oldResourceId;
    private Long targetResourceId;
    private Integer appType;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getOldResourceId() {
        return oldResourceId;
    }

    public void setOldResourceId(Long oldResourceId) {
        this.oldResourceId = oldResourceId;
    }

    public Long getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(Long targetResourceId) {
        this.targetResourceId = targetResourceId;
    }
}
