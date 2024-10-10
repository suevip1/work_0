package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class DTScriptPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest {

    private DTScriptPluginInfoAdapter adapter = new DTScriptPluginInfoAdapter();


    @Test
    public void afterProcessPluginInfo() {
        adapter.afterProcessPluginInfo(new JSONObject());
    }

    @Test
    public void fillCommonConf() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dtScriptConf",new JSONObject());
        adapter.fillCommonConf(jsonObject,new JSONObject());
    }
}