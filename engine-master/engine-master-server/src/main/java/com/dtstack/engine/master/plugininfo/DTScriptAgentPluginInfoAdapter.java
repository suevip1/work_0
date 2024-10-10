package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.utils.MapUtil;
import org.springframework.stereotype.Component;

/**
 * DTScriptAgent pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 19:08 2022/10/31
 * company: www.dtstack.com
 */
@Component
public class DTScriptAgentPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected void afterProcessPluginInfo(JSONObject pluginInfo) {
        // ignore
        MapUtil.removeBlankValue(pluginInfo);
    }

    @Override
    protected String getSelfConfName() {
        return EComponentType.DTSCRIPT_AGENT.getConfName();
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.DTSCRIPT_AGENT;
    }
}
