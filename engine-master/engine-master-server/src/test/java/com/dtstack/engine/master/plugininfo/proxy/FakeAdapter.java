package com.dtstack.engine.master.plugininfo.proxy;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.plugininfo.IPluginInfoAdapter;
import com.dtstack.engine.master.plugininfo.PluginInfoContext;

public class FakeAdapter implements IPluginInfoAdapter {


    @Override
    public PluginInfoContext getPluginInfoContext() {
        return null;
    }

    @Override
    public void setPluginInfoContext(PluginInfoContext pluginInfoContext) {

    }

    @Override
    public JSONObject buildPluginInfo(boolean isConsole) {
        return new JSONObject();
    }

    @Override
    public EComponentType getEComponentType() {
        return null;
    }
}