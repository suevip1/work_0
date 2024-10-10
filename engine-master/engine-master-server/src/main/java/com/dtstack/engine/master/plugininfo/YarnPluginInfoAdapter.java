package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.master.dto.ComponentProxyConfigDTO;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.po.ConsoleComponentAuxiliary;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * yarn pluginInfo adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
@Component
public class YarnPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfig(JSONObject clusterConfigJson, boolean isConsole) {
        PluginInfoContext pluginInfoContext = getPluginInfoContext();
        com.dtstack.engine.api.domain.Component component = pluginInfoContext.getComponent();
        JSONObject selfConfig = componentService.getComponentByClusterId(component.getClusterId(), EComponentType.YARN.getTypeCode(), false, JSONObject.class, null);
        appendKnoxConfig(selfConfig);
        return selfConfig;
    }

    public void appendKnoxConfig(JSONObject yarnConfJson) {
        if (MapUtils.isEmpty(yarnConfJson)) {
            return;
        }
        com.dtstack.engine.api.domain.Component component = getPluginInfoContext().getComponent();
        Cluster cluster = clusterService.getClusterByIdOrDefault(component.getClusterId());
        if (Objects.isNull(cluster)) {
            return;
        }
        ConsoleComponentAuxiliary auxiliary = new ConsoleComponentAuxiliary(cluster.getId(), EComponentType.YARN.getTypeCode());
        ComponentProxyConfigDTO proxyConfigDTO = componentAuxiliaryService.queryOpenAuxiliaryConfig(auxiliary);
        if (Objects.isNull(proxyConfigDTO)) {
            return;
        }
        yarnConfJson.putIfAbsent(GlobalConst.PROXY, JSONObject.toJSONString(proxyConfigDTO));
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.YARN;
    }
}
