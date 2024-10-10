package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.PluginInfoConst;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Reason:
 * Date: 2018/12/8
 * Company: www.dtstack.com
 * @author xuchao
 */

public class PluginInfoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInfoUtil.class);

    /**
     * 获取 md5sum, md5sum = md5plugin + md5zip
     *
     * @param pluginInfo pluginInfo 信息
     * @return md5sum
     */
    public static String getMd5SumFromJson(String pluginInfo) {
        if (StringUtils.isBlank(pluginInfo)) {
            throw new RuntimeException("pluginInfo can't be empty.");
        }
        JSONObject pluginInfoJson = JSONObject.parseObject(pluginInfo);
        JSONObject otherConf = pluginInfoJson.getJSONObject(PluginInfoConst.OTHER_CONF);
        if (MapUtils.isEmpty(otherConf)) {
            LOGGER.warn("otherConf is empty...");
            return MD5Util.getMd5String(pluginInfo);
        }
        String md5sum = otherConf.getString(ConfigConstant.MD5_SUM_KEY);
        if (StringUtils.isBlank(md5sum)) {
            return MD5Util.getMd5String(pluginInfo);
        }
        return md5sum;
    }

    /**
     * 获取 md5sum, md5sum = md5plugin + md5zip
     *
     * @param pluginInfo pluginInfo 信息
     * @return md5sum
     */
    public static String getMd5Sum(JSONObject pluginInfo) {
        if (MapUtils.isEmpty(pluginInfo)) {
            throw new RuntimeException("pluginInfo can't be empty.");
        }
        String md5plugin = MD5Util.getMd5String(pluginInfo.toJSONString());
        JSONObject otherConf = pluginInfo.getJSONObject(PluginInfoConst.OTHER_CONF);
        if (MapUtils.isEmpty(otherConf)) {
            throw new RuntimeException("pluginInfo other can't be empty.");
        }
        String md5zip = MapUtils.getString(otherConf, ConfigConstant.MD5_ZIP_KEY, "");
        return md5zip + "_" + md5plugin;
    }

    public static Integer getDeployMode(String pluginInfo) {
        if (StringUtils.isBlank(pluginInfo)) {
            return null;
        }
        return getDeployMode(JSONObject.parseObject(pluginInfo));
    }

    public static Integer getDeployMode(JSONObject pluginInfo) {
        if (MapUtils.isEmpty(pluginInfo)) {
            return null;
        }
        return (Integer) JSONPath.eval(pluginInfo, "$.otherConf.deployMode");
    }
}
