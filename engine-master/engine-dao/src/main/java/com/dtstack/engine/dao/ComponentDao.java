package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Component;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComponentDao {

    Component getOne(@Param("id") Long id);

    Integer insert(Component component);

    Integer update(Component component);

    Integer updateMetadata(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("isMetadata") Integer isMetadata);

    List<Component> listByClusterId(@Param("clusterId") Long clusterId,@Param("type") Integer typeCode, @Param("isDefault") boolean isDefault);

    Component getByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("componentVersion")String componentVersion,@Param("deployType") Integer deployType);

    Component getByVersionName(@Param("clusterId") Long clusterId, @Param("type") Integer type,@Param("versionName")String versionName,@Param("deployType") Integer deployType);

    Long getClusterIdByComponentId(@Param("componentId") Long componentId);

    void deleteById(@Param("componentId") Long componentId);

    Component getNextDefaultComponent(@Param("clusterId") Long clusterId,@Param("componentTypeCode") Integer componentTypeCode,@Param("currentDeleteId") Long currentDeleteId);

    String getDefaultComponentVersionByClusterAndComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer type);

    /**
     * 此接口返回的component_version为schedule_dict的dict_name
     * e.g 1.10 - 110
     */
    List<Component> getComponentVersionByEngineType(@Param("uicTenantId") Long uicTenantId, @Param("componentTypeCode") Integer componentTypeCode);

    Component getMetadataComponent(@Param("clusterId") Long clusterId);

    int updateDefault(@Param("clusterId")Long clusterId, @Param("componentType")Integer componentType, @Param("isDefault") boolean isDefault,@Param("id") Long id);

    List<Component> listComponentByClusterId(@Param("clusterIds") List<Long> clusterIds, @Param("types") List<Integer> typeCodes);

    List<Component> listAllByClusterId(@Param("clusterId") Long clusterId);

    List<Component> listAllByClusterIdAndComponentType(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    List<Component> listAllByClusterIdAndComponentTypes(@Param("clusterId") Long clusterId, @Param("componentTypes") List<Integer> componentTypes);

    List<Component> listAllByClusterIdAndComponentTypeWithLimit(@Param("clusterId") Long clusterId,
                                                                @Param("componentType") Integer componentType,
                                                                @Param("maximum") int max);

    long countByClusterIdAndComponentType(
            @Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    List<Component> listDefaultByClusterIdAndComponentType(
            @Param("clusterId") Long clusterId, @Param("componentType") Integer componentType);

    List<Component> listAllByClusterIdAndComponentTypeAndVersionName(
            @Param("clusterId") Long clusterId, @Param("componentType") Integer componentType,
            @Param("versionName") String versionName);
}

