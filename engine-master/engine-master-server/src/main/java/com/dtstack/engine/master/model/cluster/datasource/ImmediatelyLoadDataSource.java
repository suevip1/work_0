package com.dtstack.engine.master.model.cluster.datasource;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.model.cluster.ComponentFacade;
import com.dtstack.engine.master.model.cluster.DataSource;

import java.util.Collections;
import java.util.List;

public class ImmediatelyLoadDataSource implements DataSource {

    private final Long clusterId;
    private final ComponentFacade facade;

    public ImmediatelyLoadDataSource(Long clusterId, ComponentFacade facade) {
        this.clusterId = clusterId;
        this.facade = facade;
    }

    public List<Component> listAllByClusterId() {
        return this.facade.listAllByClusterId(clusterId);
    }

    @Override
    public List<ComponentConfig> listComponentConfig(List<Long> componentIds, boolean excludeCustom) {
        if (componentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.facade.listByComponentIds(componentIds, excludeCustom);
    }

    @Override
    public List<Component> getComponents(EComponentType type, String versionName) {
        return this.facade.listAllByClusterIdAndComponentTypeAndVersionName(this.clusterId, type, versionName);
    }

    @Override
    public List<Component> getComponents(EComponentType type, int max) {
        return this.facade.listAllByClusterIdAndComponentType(this.clusterId, type, max);
    }

    @Override
    public List<Component> getComponents(EComponentType type) {
        return this.facade.listAllByClusterIdAndComponentType(this.clusterId, type);
    }

    @Override
    public List<Component> getComponents(List<EComponentType> types) {
        return this.facade.listAllByClusterIdAndComponentTypes(this.clusterId, types);
    }

}
