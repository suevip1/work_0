package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.ComponentConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.master.mockcontainer.impl.ComponentConfigServiceMock;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-06-11 10:57
 */
@MockWith(value = ComponentConfigServiceMock.class)
public class ComponentConfigServiceTest {
    ComponentConfigService componentConfigService = new ComponentConfigService();

    @Test
    public void addOrUpdateComponentConfig() {
        componentConfigService.addOrUpdateComponentConfig(ComponentConfigServiceMock.mockSparkComponnetTemplate(), ComponentConfigServiceMock.SPARK_COMPONENT_ID, -1L, EComponentType.SPARK.getTypeCode(),(e) -> {});
    }

    @Test
    public void deleteComponentConfig() {
        componentConfigService.deleteComponentConfig(ComponentConfigServiceMock.SPARK_COMPONENT_ID);
    }

    @Test
    public void deleteByComponentIdAndKeyAndComponentTypeCode() {
        componentConfigService.deleteByComponentIdAndKeyAndComponentTypeCode(ComponentConfigServiceMock.SPARK_COMPONENT_ID, "perjob", EComponentType.SPARK.getTypeCode());
    }

    @Test
    public void batchSaveComponentConfig() {
        componentConfigService.batchSaveComponentConfig(ComponentConfigServiceMock.mockSparkComponentConfigs(-1L));
    }

    @Test
    public void deepOldClientTemplate() {
        componentConfigService.deleteComponentConfig(1L);
    }

    @Test
    public void getComponentConfigByKey() {
        ComponentConfig config = componentConfigService.getComponentConfigByKey(ComponentConfigServiceMock.SPARK_COMPONENT_ID, "perjob");
        assertNotNull(config);
    }

    @Test
    public void getComponentConfigByType() {
        Map<String, Object> map = componentConfigService.getComponentConfigByType(ComponentConfigServiceMock.SPARK_COMPONENT_ID, EFrontType.CHECKBOX);
        assertTrue(!map.isEmpty());
    }

    @Test
    public void getComponentConfigListByTypeCodeAndKey() {
        List<ComponentConfig> list = componentConfigService.getComponentConfigListByTypeCodeAndKey(-1L, EComponentType.SPARK.getTypeCode(), "perjob");
        assertTrue(!list.isEmpty());
    }

    @Test
    public void convertComponentConfigToMap() {
        Map<String, Object> map = componentConfigService.convertComponentConfigToMap(3825L, true);
        assertTrue(!map.isEmpty());
    }

    @Test
    public void getComponentVoByComponent() {
        assertTrue(CollectionUtils.isEmpty(componentConfigService.getComponentVoByComponent(null, false, -1L, false,false)));

        List<Component> components = new ArrayList<>();
        Component spark = new Component();
        spark.setComponentTypeCode(EComponentType.SPARK.getTypeCode());
        spark.setId(3825L);
        components.add(spark);
        assertTrue(CollectionUtils.isNotEmpty(componentConfigService.getComponentVoByComponent(components, true, -1L, true,false)));
    }

    @Test
    public void updateValueComponentConfig() {
        ComponentConfig componentConfig = new ComponentConfig();
        componentConfigService.updateValueComponentConfig(componentConfig);
    }

    @Test
    public void getCacheComponentConfigMap() {
        Map<String, Object> cacheComponentConfigMap = componentConfigService.getCacheComponentConfigMap(-1L, EComponentType.SPARK.getTypeCode(), true, Maps.newHashMap(), null);
        assertTrue(!cacheComponentConfigMap.isEmpty());
        Map<String, Object> map = componentConfigService.getCacheComponentConfigMap(-1L, EComponentType.SPARK.getTypeCode(), true, Maps.newHashMap(), 3825L);
        assertTrue(!map.isEmpty());
    }

    @Test
    public void clearComponentCache() {
        componentConfigService.clearComponentCache();
    }
}