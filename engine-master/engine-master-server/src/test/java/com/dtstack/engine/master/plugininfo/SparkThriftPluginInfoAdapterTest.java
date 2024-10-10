package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class SparkThriftPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest{

    private SparkThriftPluginInfoAdapter adapter = new SparkThriftPluginInfoAdapter();

    @Test
    public void afterProcessSelfConfig() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        adapter.afterProcessSelfConfig(new JSONObject());
    }

    @Test
    public void replaceDefaultSchema() {
        adapter.replaceDefaultSchema(new JSONObject());
    }

    @Test
    public void getEComponentType() {
        adapter.getEComponentType();
    }
}