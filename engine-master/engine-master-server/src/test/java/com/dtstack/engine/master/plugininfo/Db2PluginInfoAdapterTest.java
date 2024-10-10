package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class Db2PluginInfoAdapterTest extends DefaultPluginInfoAdapterTest{

    private AdbForPgPluginInfoAdapter adapter = new AdbForPgPluginInfoAdapter();

    @Test
    public void getSelfConfigWithTask() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        adapter.getSelfConfigWithTask(new JSONObject());
        adapter.getEComponentType();
    }
}