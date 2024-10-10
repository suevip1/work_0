package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yuebai
 * @date 2022/8/16
 */
public class ProjectDetailParam {

    private List<Long> projectIds;
    @NotNull(message = "appType is not null")
    private Integer appType;
    private Long dtuicTenantId;
    private Long userId;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
