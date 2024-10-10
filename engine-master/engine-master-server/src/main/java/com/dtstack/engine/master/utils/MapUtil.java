package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: dazhi
 * @Date: 2021/10/20 3:15 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class MapUtil {

    public static JSONObject paramToMap(String header) {
        JSONObject jsonObject = new JSONObject();

        List<String> strings = Splitter.on(";").trimResults().splitToList(header);

        for (String param : strings) {
            String[] split1 = param.split("=");
            if (ArrayUtils.isNotEmpty(split1) && split1.length == 2) {
                jsonObject.put(split1[0],split1[1]);
                jsonObject.put(lineToHump(split1[0]),split1[1]);
            }
        }

        return jsonObject;
    }

    public static String lineToHump(String str) {
        Pattern linePattern = Pattern.compile("_(\\w)");
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void removeBlankValue(JSONObject conf) {
        if (MapUtils.isEmpty(conf)) {
            return;
        }
        Iterator<String> keysIterator = conf.keySet().iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            Object value = conf.get(key);
            if (Objects.isNull(value) || (value instanceof String && StringUtils.isBlank((String) value))) {
                keysIterator.remove();
                continue;
            }
            if (value instanceof JSONObject) {
                JSONObject valueJson = (JSONObject) value;
                removeBlankValue(valueJson);
            }
        }
    }
}
