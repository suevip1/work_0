package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ComponentConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-02-08
 */
public interface ComponentConfigDao {

    List<ComponentConfig> listByComponentId(@Param("componentId")Long componentId,@Param("isFilter") boolean isFilter);

    List<ComponentConfig> listByComponentIds(@Param("componentIds") List<Long> componentIds, @Param("isFilter") boolean isFilter);

    List<ComponentConfig> listByClusterId(@Param("clusterId")Long clusterId,@Param("isFilter") boolean isFilter);

    List<ComponentConfig> listByType(@Param("componentId")Long componentId,@Param("type")String type);

    List<ComponentConfig> listByComponentTypeAndKey(@Param("clusterId")Long clusterId,@Param("key")String key,@Param("componentTypeCode")Integer componentTypeCode);

    ComponentConfig listByKey(@Param("componentId") Long componentId,@Param("key")String key);

    Integer insertBatch(@Param("componentConfigs") List<ComponentConfig> componentConfigs);

    Integer deleteByComponentId(@Param("componentId")Long componentId);

    Integer deleteByComponentIdAndKeyAndComponentTypeCode(@Param("componentId") Long componentId,
                                                         @Param("key") String key,
                                                         @Param("typeCode") Integer typeCode);

    ComponentConfig listFirst();

    void update(ComponentConfig componentConfig);

    List<ComponentConfig> listByComponentIdAndKey(@Param("componentId") Long componentId, @Param("key") String key);

    List<ComponentConfig> listRequiredKeyWithNullValue(@Param("componentId") Long componentId, @Param("excludeDeployModes") List<String> excludeDeployModes);
}
