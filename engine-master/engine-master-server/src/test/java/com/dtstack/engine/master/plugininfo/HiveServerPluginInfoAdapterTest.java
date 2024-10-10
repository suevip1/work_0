package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.testable.core.annotation.MockWith;
import org.junit.Test;

import static org.junit.Assert.*;

@MockWith(DefaultPluginInfoAdapterTest.PluginInfoAdapterMock.class)
public class HiveServerPluginInfoAdapterTest extends DefaultPluginInfoAdapterTest{

    private HiveServerPluginInfoAdapter adapter = new HiveServerPluginInfoAdapter();


    @Test
    public void afterProcessSelfConfig() {
        pluginInfoContext = JSONObject.parseObject(contextJson, PluginInfoContext.class);
        adapter.setPluginInfoContext(pluginInfoContext);
        JSONObject selfConf = new JSONObject();
        selfConf.put("jdbcUrl","jdbc:hive2://cdp02:10000/%s;principal=hive/cdp02@DTSTACK.COM");
        adapter.afterProcessSelfConfig(selfConf);
    }

}