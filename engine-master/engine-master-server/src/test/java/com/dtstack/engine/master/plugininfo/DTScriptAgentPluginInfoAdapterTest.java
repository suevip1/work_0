package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class DTScriptAgentPluginInfoAdapterTest {

    private DTScriptAgentPluginInfoAdapter adapter = new DTScriptAgentPluginInfoAdapter();


    @Test
    public void afterProcessPluginInfo() {
        adapter.afterProcessPluginInfo(new JSONObject());
    }

    @Test
    public void getSelfConfName() {
        adapter.getSelfConfName();
    }

    @Test
    public void getEComponentType() {
        adapter.getEComponentType();
    }
}