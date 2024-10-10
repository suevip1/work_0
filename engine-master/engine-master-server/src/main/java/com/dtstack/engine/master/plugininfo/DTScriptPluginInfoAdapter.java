package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.JsonUtil;
import com.dtstack.engine.master.utils.MapUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

/**
 * DTScript pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:08 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class DTScriptPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected void fillCommonConf(JSONObject pluginInfo, JSONObject clusterConfigJson) {
        super.fillCommonConf(pluginInfo, clusterConfigJson);
        JSONObject commonConfJson = MapUtils.isNotEmpty(pluginInfo.getJSONObject(PluginInfoConst.COMMON_CONF)) ?
                pluginInfo.getJSONObject(PluginInfoConst.COMMON_CONF) : new JSONObject();
        JSONObject dtScriptConfJson = pluginInfo.getJSONObject(EComponentType.DT_SCRIPT.getConfName());
        JSONObject selfCommonConfJson = dtScriptConfJson.getJSONObject(PluginInfoConst.COMMON_CONF);
        if (MapUtils.isNotEmpty(selfCommonConfJson)) {
            commonConfJson.putAll(selfCommonConfJson);
            dtScriptConfJson.remove(PluginInfoConst.COMMON_CONF);
        }
        JsonUtil.putIgnoreEmpty(pluginInfo, PluginInfoConst.COMMON_CONF, commonConfJson);
    }

    @Override
    protected void afterProcessPluginInfo(JSONObject pluginInfo) {
        // ignore
        MapUtil.removeBlankValue(pluginInfo);
    }

    @Override
    protected String getSelfConfName() {
        return EComponentType.DT_SCRIPT.getConfName();
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.DT_SCRIPT;
    }
}
