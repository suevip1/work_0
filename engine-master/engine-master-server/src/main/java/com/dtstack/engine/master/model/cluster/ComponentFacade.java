package com.dtstack.engine.master.model.cluster;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class ComponentFacade {

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigDao configDao;

    private static final List<EComponentType> ALL_RESOURCE_COMPONENTS = Lists.newArrayList(EComponentType.YARN, EComponentType.KUBERNETES);

    private static final List<EComponentType> ALL_STORAGE_COMPONENTS = Lists.newArrayList(EComponentType.HDFS, EComponentType.S3, EComponentType.NFS);

    public List<Component> listAllByClusterId(Long clusterId) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        return this.componentDao.listAllByClusterId(clusterId);
    }

    public List<Component> listAllByClusterIdAndComponentType(Long clusterId, EComponentType type) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        Objects.requireNonNull(type, "Type is null.");
        return this.componentDao.listAllByClusterIdAndComponentType(clusterId, type.getTypeCode());
    }

    public List<Component> listAllByClusterIdAndComponentTypes(Long clusterId, List<EComponentType> types) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Empty types.");
        }
        List<Integer> codes = types.stream()
                .map(EComponentType::getTypeCode)
                .collect(Collectors.toList());
        return this.componentDao.listAllByClusterIdAndComponentTypes(clusterId, codes);
    }

    public List<Component> listAllByClusterIdAndComponentType(Long clusterId, EComponentType type, int max) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        Objects.requireNonNull(type, "Type is null.");
        if (max <= 0) {
            throw new IllegalArgumentException("Max is not positive.");
        }
        return this.componentDao.listAllByClusterIdAndComponentTypeWithLimit(
                clusterId, type.getTypeCode(), max);
    }

    public List<Component> listAllByClusterIdAndComponentTypeAndVersionName(
            Long clusterId, EComponentType type, String version) {
        Objects.requireNonNull(clusterId, "ClusterId is null.");
        Objects.requireNonNull(type, "Type is null.");
        Objects.requireNonNull(version, "Version is null.");
        return this.componentDao.listAllByClusterIdAndComponentTypeAndVersionName(
                clusterId, type.getTypeCode(), version);
    }

    public List<ComponentConfig> listByComponentIds(List<Long> componentIds, boolean excludeCustom) {
        if (componentIds == null || componentIds.isEmpty()) {
            throw new IllegalArgumentException("Empty componentIds.");
        }
        return this.configDao.listByComponentIds(componentIds, excludeCustom);
    }

    public Component tryGetResourceComponent(Long clusterId) {
        List<Component> resourceComponents = listAllByClusterIdAndComponentTypes(clusterId, ALL_RESOURCE_COMPONENTS);
        if (CollectionUtils.isEmpty(resourceComponents)) {
            return null;
        }
        if (resourceComponents.size() != 1) {
            throw new RdosDefineException(ErrorCode.COMPONENT_COUNT_ERROR);
        }
        Component resourceComponent = resourceComponents.get(0);
        boolean correctResource = EComponentType.KUBERNETES.getTypeCode().equals(resourceComponent.getComponentTypeCode())
                || EComponentType.YARN.getTypeCode().equals(resourceComponent.getComponentTypeCode());
        if (!correctResource) {
            throw new RdosDefineException(ErrorCode.COMPONENT_INVALID);
        }
        return resourceComponent;
    }

    public List<Component> tryGetStorageComponent(Long clusterId) {
        List<Component> storageComponents = listAllByClusterIdAndComponentTypes(clusterId, ALL_STORAGE_COMPONENTS);
        return storageComponents;
    }

    public List<Component> tryGetFlinkComponent(Long clusterId) {
        List<Component> components = listAllByClusterIdAndComponentTypes(clusterId, Lists.newArrayList(EComponentType.FLINK));
        return components;
    }
}
