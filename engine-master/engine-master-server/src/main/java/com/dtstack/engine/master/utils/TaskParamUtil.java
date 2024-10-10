package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.util.DtStringUtil;
import com.google.common.base.Joiner;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2023-09-18 21:39
 */
public class TaskParamUtil {

    /**
     * 切分 '\n' 分隔的 k/v 对
     * @param taskParam
     * @return
     */
    public static Map<String, String> splitTaskParam(String taskParam) {
        if (StringUtils.isBlank(taskParam)) {
            return Collections.emptyMap();
        }
        List<String> paramList = DtStringUtil.splitIgnoreQuota(taskParam, '\n');
        Map<String, String> result = new HashMap<>(paramList.size());
        for (String param : paramList) {
            if (!param.contains("=")) {
                continue;
            }
            String[] properties = param.split("=");
            result.put(StringUtils.trim(properties[0]), StringUtils.trim(properties[1]));
        }
        return result;
    }

    public static String concatTaskParam(Map<String, String> taskParamMap) {
        if (MapUtils.isEmpty(taskParamMap)) {
            return StringUtils.EMPTY;
        }
        return Joiner.on("\r\n").withKeyValueSeparator("=").join(taskParamMap);
    }
}
