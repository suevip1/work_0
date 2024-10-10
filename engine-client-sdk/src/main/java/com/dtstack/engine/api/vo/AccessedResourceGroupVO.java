package com.dtstack.engine.api.vo;

import java.util.List;

public class AccessedResourceGroupVO<T> {

    //集群租户绑定默认资源组
    private Long defaultResourceId;
    private List<T> accessedResources;

    public Long getDefaultResourceId() {
        return defaultResourceId;
    }

    public void setDefaultResourceId(Long defaultResourceId) {
        this.defaultResourceId = defaultResourceId;
    }

    public List<T> getAccessedResources() {
        return accessedResources;
    }

    public void setAccessedResources(List<T> accessedResources) {
        this.accessedResources = accessedResources;
    }

}
