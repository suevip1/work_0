package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Objects;

/**
 * json util
 *
 * @author ：wangchuan
 * date：Created in 09:54 2022/10/21
 * company: www.dtstack.com
 */
public class JsonUtil {

    public static void putIgnoreEmpty(JSONObject origin, Map<String, JSONObject> subJsonMap) {
        if (Objects.isNull(origin)) {
            return;
        }
        if (MapUtils.isEmpty(subJsonMap)) {
            return;
        }
        subJsonMap.forEach((key, value) -> putIgnoreEmpty(origin, key, value));
    }

    public static void putIgnoreEmpty(JSONObject origin, String key, String value) {
        if (Objects.isNull(origin)) {
            return;
        }
        if (StringUtils.isNotBlank(value)) {
            origin.put(key, value);
        }
    }

    public static void putIgnoreNull(JSONObject origin, String key, Object value) {
        if (Objects.isNull(origin)) {
            return;
        }
        origin.put(key, value);
    }

    public static void putIgnoreEmpty(JSONObject origin, String key, JSONObject subJson) {
        if (Objects.isNull(origin)) {
            return;
        }
        if (MapUtils.isEmpty(subJson)) {
            return;
        }
        origin.put(key, subJson);
    }
}
