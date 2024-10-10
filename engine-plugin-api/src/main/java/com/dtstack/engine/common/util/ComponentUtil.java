package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EDeployMode;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * component util
 *
 * @author ：wangchuan
 * date：Created in 17:32 2023/1/18
 * company: www.dtstack.com
 */
public class ComponentUtil {

    public static String getComponentKeyNotNull(String pluginInfo) {
        String componentKey = getComponentKey(pluginInfo);
        if (StringUtils.isNotBlank(componentKey)) {
            return componentKey;
        }
        //数据源ID
        JSONObject pluginInfoJson = JSON.parseObject(pluginInfo);
        if (pluginInfoJson.containsKey(ConfigConstant.DATA_SOURCE_ID)) {
            return pluginInfoJson.getString(ConfigConstant.DATA_SOURCE_ID);
        }
        return MD5Util.getMd5String(pluginInfo);
    }

    /**
     * 获取组件的 key
     *
     * @param pluginInfo pluginInfo 信息
     * @return cache key
     */
    public static String getComponentKey(String pluginInfo) {
        if (StringUtils.isBlank(pluginInfo)) {
            return null;
        }
        JSONObject pluginInfoJson = JSONObject.parseObject(pluginInfo);
        String cluster = (String) JSONPath.eval(pluginInfoJson, "$.otherConf.cluster");
        String componentType = (String) JSONPath.eval(pluginInfoJson, "$.otherConf.componentType");
        String componentVersion = (String) JSONPath.eval(pluginInfoJson, "$.otherConf.componentVersion");
        if (StringUtils.isBlank(cluster) || StringUtils.isBlank(componentType)) {
            return null;
        }
        JSONObject flinkConf = pluginInfoJson.getJSONObject("flinkConf");
        Integer appType = (Integer) Optional.ofNullable(pluginInfoJson.get("appType")).
                orElse(1);
        String componentKey;
        if (MapUtils.isNotEmpty(flinkConf) && Objects.nonNull(flinkConf.getInteger("deployMode"))) {
            // 处理 flink deploy mode
            componentKey = String.format("%s::%s::%s::%s", cluster, componentType, StringUtils.isBlank(componentVersion) ? "default" : componentVersion, flinkConf.getInteger("deployMode"));
            if(flinkConf.getInteger("deployMode").equals(EDeployMode.SESSION.getType())){
                //flink session模式需要区分离线和实时的client
                componentKey = AppType.STREAM.getType().equals(appType) ? String.format("%s::%s",componentKey,ComputeType.STREAM.getType()) :
                        String.format("%s::%s",componentKey,ComputeType.BATCH.getType());
            }
        } else {
            componentKey = String.format("%s::%s::%s", cluster, componentType, StringUtils.isBlank(componentVersion) ? "default" : componentVersion);
        }
        // 队列隔离
        String queue = (String) JSONPath.eval(pluginInfoJson, "$.otherConf.queue");
        if (StringUtils.isNotEmpty(queue)) {
            componentKey = String.format("%s::%s", componentKey, queue);
        }
        // namespace 隔离
        String namespace = (String) JSONPath.eval(pluginInfoJson, "$.otherConf.namespace");
        if (StringUtils.isNotEmpty(namespace)) {
            componentKey = String.format("%s::%s", componentKey, namespace);
        }

        return componentKey;
    }
}
