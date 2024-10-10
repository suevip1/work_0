package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class PrestoPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest {

    private PrestoPluginInfoAdapter adapter = new PrestoPluginInfoAdapter();

    @Test
    public void getSelfConfigWithTask() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        adapter.getSelfConfigWithTask(new JSONObject());
        adapter.getEComponentType();
    }
}