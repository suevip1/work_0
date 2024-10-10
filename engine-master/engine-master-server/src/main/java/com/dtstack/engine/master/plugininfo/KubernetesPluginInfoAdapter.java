package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.util.JsonUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

/**
 * k8s pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
@Component
public class KubernetesPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfig(JSONObject clusterConfigJson, boolean isConsole) {
        JSONObject selfConfig = super.getSelfConfig(clusterConfigJson, isConsole);
        if (!isConsole) {
            return selfConfig;
        }
        JSONObject confJson = new JSONObject();
        if (MapUtils.isNotEmpty(selfConfig) && selfConfig.containsKey(GlobalConst.KUBERNETES_CONTEXT)) {
            JsonUtil.putIgnoreEmpty(confJson, EComponentType.KUBERNETES.getConfName(), selfConfig.getString(GlobalConst.KUBERNETES_CONTEXT));
        }
        JsonUtil.putIgnoreEmpty(selfConfig, EComponentType.KUBERNETES.getConfName(), confJson);
        return selfConfig;
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.KUBERNETES;
    }
}
