package com.dtstack.engine.master.controller.sdk;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.param.ComponentConfigQueryParam;
import com.dtstack.engine.api.vo.components.ComponentVersionVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.mapstruct.ComponentStruct;
import org.assertj.core.util.Lists;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MockWith(ComponentSdkControllerMock.class)
public class ComponentSdkControllerTest {

    ComponentSdkController componentSdkController = new ComponentSdkController();


    @Test
    public void testGetComponentVersionByEngineType() throws Exception {
        componentSdkController.getComponentVersionByEngineType(1L, 0);
    }

    @Test
    public void testExistsComponentByTenantIdAndComponentType() throws Exception {
        componentSdkController.existsComponentByTenantIdAndComponentType(1L, 0);
    }

    @Test
    public void testGetComponentSimpleConfig() throws Exception {
        ComponentConfigQueryParam componentConfigQueryParam = new ComponentConfigQueryParam();
        componentConfigQueryParam.setComponentCode(EComponentType.YARN.getTypeCode());
        componentConfigQueryParam.setTenantId(1L);
        componentSdkController.getComponentSimpleConfig(componentConfigQueryParam);
    }
}

class ComponentSdkControllerMock {

    @MockInvoke(targetClass = TenantService.class)
    public Long getClusterIdByDtUicTenantId(Long dtuicTenantId) {
        return -1L;
    }

    @MockInvoke(targetClass = ComponentService.class)
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz, Map<Integer, String> componentVersionMap) {
        return null;

    }

    @MockInvoke(targetClass = ComponentService.class)
    public List<Component> getComponents(Long uicTenantId, EComponentType componentType) {
        Component component = new Component();
        component.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        component.setHadoopVersion("12");
        return Lists.newArrayList(component);
    }


    @MockInvoke(targetClass = ComponentStruct.class)
    List<ComponentVersionVO> toComponentVersions(List<Component> component) {
        return new ArrayList<>();
    }
}