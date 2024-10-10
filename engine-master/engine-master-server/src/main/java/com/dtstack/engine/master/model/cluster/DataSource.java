package com.dtstack.engine.master.model.cluster;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;

import java.util.List;

/**
 *
 * All input versionName parameters should normalize.
 */
public interface DataSource {
    List<ComponentConfig> listComponentConfig(List<Long> componentId, boolean excludeCustom);
    List<Component> getComponents(EComponentType type, String versionName);
    List<Component> getComponents(EComponentType type, int max);
    List<Component> getComponents(EComponentType type);
    List<Component> listAllByClusterId();
    List<Component> getComponents(List<EComponentType> types);
}
