package com.dtstack.engine.master.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.enums.EComponentType;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

@MockWith(EngineServiceMock.class)
public class EngineServiceTest {
    EngineService engineService = new EngineService();

    @Test
    public void testGetQueue() throws Exception {
        engineService.getQueue(1L);
    }

    @Test
    public void testListSupportEngine() throws Exception {
        engineService.listSupportEngine(1L, Boolean.TRUE);
    }
}

class EngineServiceMock {
    @MockInvoke(targetClass = ComponentService.class)
    public List<Component> listComponents(Long dtUicTenantId, Integer engineType) {
        Component component = new Component();
        component.setComponentTypeCode(EComponentType.YARN.getTypeCode());
        return Lists.newArrayList(component);
    }
}