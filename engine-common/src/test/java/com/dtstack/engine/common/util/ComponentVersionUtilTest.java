package com.dtstack.engine.common.util;

import com.dtstack.engine.common.enums.EComponentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ComponentVersionUtilTest {

    @Test
    public void getComponentVersion() {
        Map<Integer,String > componentVersionMap = new HashMap<>();
        componentVersionMap.put(1,"310");
        String componentVersion = ComponentVersionUtil.getComponentVersion(componentVersionMap, EComponentType.SPARK);
        Assert.assertNotNull(componentVersion);
    }

    @Test
    public void testGetComponentVersion() {
        Map<Integer,String > componentVersionMap = new HashMap<>();
        componentVersionMap.put(1,"310");
        String componentVersion = ComponentVersionUtil.getComponentVersion(componentVersionMap, 1);
        Assert.assertNotNull(componentVersion);
    }

    @Test
    public void isMultiVersionComponent() {
        Assert.assertTrue(ComponentVersionUtil.isMultiVersionComponent(0));
        Assert.assertFalse(ComponentVersionUtil.isMultiVersionComponent(3));
    }

    @Test
    public void testGetComponentVersion1() {
        Assert.assertNotNull(ComponentVersionUtil.getComponentVersion("310"));
        Assert.assertNull(ComponentVersionUtil.getComponentVersion(null));
    }

    @Test
    public void formatMultiVersion() {
        Assert.assertNotNull(ComponentVersionUtil.formatMultiVersion(1,"310"));
    }
}