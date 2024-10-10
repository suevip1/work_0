package com.dtstack.engine.master.plugininfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.utils.MapUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.dtstack.engine.common.constrant.ConfigConstant.DEPLOY_MODEL;
import static com.dtstack.engine.common.constrant.ConfigConstant.FLINK_ON_STANDALONE_CONF;

/**
 * flink engine plugin adapter
 *
 * @author ：wangchuan
 * date：Created in 15:33 2022/10/10
 * company: www.dtstack.com
 */
@Component
public class FlinkPluginInfoAdapter extends AbstractPluginAdapter {

    @Override
    protected JSONObject getSelfConfig(JSONObject clusterConfigJson, boolean isConsole) {
        Integer deployMode = getPluginInfoContext().getDeployMode();
        com.dtstack.engine.api.domain.Component component = getPluginInfoContext().getComponent();
        // Flink 默认为session
        EDeployMode deploy = Objects.nonNull(deployMode) ? EDeployMode.getByType(deployMode) : EDeployMode.SESSION;
        JSONObject confConfig;
        if (EComponentType.FLINK.equals(EComponentType.getByCode(component.getComponentTypeCode())) && EDeployMode.STANDALONE.getType().equals(deployMode)) {
            confConfig = clusterConfigJson.getJSONObject(FLINK_ON_STANDALONE_CONF);
            return confConfig;
        } else {
            confConfig = clusterConfigJson.getJSONObject(EComponentType.getByCode(component.getComponentTypeCode()).getConfName());
        }
        if (MapUtils.isEmpty(confConfig)) {
            throw new RdosDefineException("Flink configuration information is empty");
        }
        JSONObject selfConfig = confConfig.getJSONObject(deploy.getMode());
        if (MapUtils.isEmpty(selfConfig)) {
            throw new RdosDefineException(String.format("Corresponding mode [%s] no information is configured", deploy.name()));
        }
        selfConfig.put(DEPLOY_MODEL, deployMode);
        return selfConfig;
    }

    @Override
    protected void afterProcessPluginInfo(JSONObject pluginInfo) {
        // ignore
        MapUtil.removeBlankValue(pluginInfo);
    }

    @Override
    protected String getSelfConfName() {
        return EComponentType.FLINK.getConfName();
    }

    @Override
    public EComponentType getEComponentType() {
        return EComponentType.FLINK;
    }
}
