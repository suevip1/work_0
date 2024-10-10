package com.dtstack.engine.master.plugininfo.proxy;

import com.dtstack.engine.master.plugininfo.IPluginInfoAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class PluginInfoAdapterProxyHandleTest {

    private static PluginInfoAdapterProxyHandle handle;

    private static IPluginInfoAdapter adapter = new FakeAdapter();

    @Test
    public void invoke() throws Throwable {
        handle = new PluginInfoAdapterProxyHandle(adapter);
        Method buildPluginInfo = adapter.getClass().getDeclaredMethod("buildPluginInfo",boolean.class);
        Object invoke = handle.invoke(adapter, buildPluginInfo,new Object[]{true});
        Assert.assertNotNull(invoke);
    }
}