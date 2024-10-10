package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class InceptorPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest{

    private InceptorPluginInfoAdapter adapter = new InceptorPluginInfoAdapter();

    @Test
    public void getSelfConfigWithTask() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        adapter.getSelfConfigWithTask(new JSONObject());
        adapter.getEComponentType();
    }
}