package com.dtstack.engine.master.model.cluster;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.mockcontainer.impl.ComponentServiceMock;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-24 09:29
 */
@MockWith(ComponentServiceMock.class)
public class ComponentFacadeTest {
    ComponentFacade componentFacade = new ComponentFacade();

    @Test
    public void listAllByClusterId() {
        componentFacade.listAllByClusterId(-1L);
    }

    @Test
    public void listAllByClusterIdAndComponentType() {
        componentFacade.listAllByClusterIdAndComponentType(-1L, EComponentType.SPARK);
    }

    @Test
    public void listAllByClusterIdAndComponentTypes() {
        componentFacade.listAllByClusterIdAndComponentTypes(-1L, Lists.newArrayList(EComponentType.SPARK));
    }

    @Test
    public void testListAllByClusterIdAndComponentType() {
        componentFacade.listAllByClusterIdAndComponentType(-1L, EComponentType.SPARK, 1);
    }

    @Test
    public void listAllByClusterIdAndComponentTypeAndVersionName() {
        componentFacade.listAllByClusterIdAndComponentTypeAndVersionName(-1L, EComponentType.SPARK, "2.4");
    }

    @Test
    public void listByComponentIds() {
        componentFacade.listByComponentIds(Lists.newArrayList(1L), true);
    }

    @Test
    public void tryGetResourceComponent() {
        componentFacade.tryGetResourceComponent(-1L);
    }

    @Test
    public void tryGetStorageComponent() {
        componentFacade.tryGetStorageComponent(-1L);
    }
}