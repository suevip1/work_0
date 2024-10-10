package com.dtstack.engine.master.plugininfo.proxy;

import com.dtstack.engine.master.plugininfo.IPluginInfoAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * adapter proxy
 *
 * @author ：wangchuan
 * date：Created in 14:24 2022/10/31
 * company: www.dtstack.com
 */
public class PluginInfoAdapterProxyHandle implements InvocationHandler {

    private final IPluginInfoAdapter pluginAdapter;

    public PluginInfoAdapterProxyHandle(IPluginInfoAdapter pluginInfoAdapter) {
        this.pluginAdapter = pluginInfoAdapter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method.invoke(pluginAdapter, args);
    }
}
