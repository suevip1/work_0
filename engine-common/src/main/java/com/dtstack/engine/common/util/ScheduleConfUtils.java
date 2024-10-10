package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.Objects;

/**
 * @author yuebai
 * @date 2022/9/23
 */
public class ScheduleConfUtils {

    public static String DEFAULT_SCHEDULE = "{\"selfReliance\":0, \"min\":0,\"hour\":0,\"periodType\":\"2\",\"beginDate\":\"2001-01-01\",\"endDate\":\"2121-01-01\",\"isFailRetry\":true,\"maxRetryNum\":\"3\"}";

    /**
     * 是否超时中断
     */
    public static final String IS_TIMEOUT = "isTimeout";

    /**
     * 超时中断小时数
     */
    public static final String TIMEOUT_HOUR = "timeoutHour";

    /**
     * 超时中断分钟数
     */
    public static final String TIMEOUT_MIN = "timeoutMin";

    public static final String EXPEND_TIME = "expendTime";
    /**
     *
     */
    public static final String CANDLER_BATCH_TYPE= "candlerBatchType";


    public static String buildManualTaskScheduleConf(String originScheduleConf) {
        JSONObject defaultScheduleConf = JSONObject.parseObject(DEFAULT_SCHEDULE);
        if (StringUtils.isNotBlank(originScheduleConf)) {
            return DEFAULT_SCHEDULE;
        }
        defaultScheduleConf.putAll(JSONObject.parseObject(originScheduleConf));
        return defaultScheduleConf.toJSONString();
    }

    /**
     * 获取运行超时时间, 单位 s
     *
     * @param scheduleConf scheduleConf 信息
     * @return 超时时间
     */
    public static long getRunTimeoutSecond(String scheduleConf) {
        long timeout = 0;
        if (StringUtils.isBlank(scheduleConf)) {
            return timeout;
        }
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        Boolean isTimeout = scheduleConfJson.getBoolean(IS_TIMEOUT);
        if (BooleanUtils.isFalse(isTimeout)) {
            return timeout;
        }
        Integer timeoutHour = scheduleConfJson.getInteger(TIMEOUT_HOUR);
        Integer timeoutMin = scheduleConfJson.getInteger(TIMEOUT_MIN);
        if (Objects.nonNull(timeoutHour)) {
            timeout += timeoutHour * 60 * 60;
        }
        if (Objects.nonNull(timeoutMin)) {
            timeout += timeoutMin * 60;
        }
        return timeout;
    }

    /**
     * 获取运行超时时间, 格式如：12小时1分钟
     *
     * @param scheduleConf scheduleConf 信息
     * @return 超时时间
     */
    public static String getRunTimeoutStr(String scheduleConf) {
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        Boolean isTimeout = scheduleConfJson.getBoolean(IS_TIMEOUT);
        if (BooleanUtils.isFalse(isTimeout)) {
            return null;
        }
        Integer timeoutHour = MapUtils.getInteger(scheduleConfJson, TIMEOUT_HOUR, 0);
        Integer timeoutMin = MapUtils.getInteger(scheduleConfJson, TIMEOUT_MIN, 0);
        return String.format("%s小时%s分钟", timeoutHour, timeoutMin);
    }

    public static String getExpendTime(String scheduleConf) {
        if (StringUtils.isBlank(scheduleConf)) {
            return "";
        }
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        return scheduleConfJson.getString(EXPEND_TIME);
    }

    public static String removeKey(String scheduleConf,String key){
        if (StringUtils.isBlank(scheduleConf)) {
            return scheduleConf;
        }
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        scheduleConfJson.remove(key);
        return scheduleConfJson.toJSONString();
    }

    public static String pubKeyIfAbsent(String scheduleConf,String key,Object value){
        if (StringUtils.isBlank(scheduleConf)) {
            return scheduleConf;
        }
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        scheduleConfJson.putIfAbsent(key,value);
        return scheduleConfJson.toJSONString();
    }
    public static Integer getCandlerBatchType(String scheduleConf) {
        if (StringUtils.isBlank(scheduleConf)) {
           return null;
        }
        JSONObject scheduleConfJson = JSONObject.parseObject(scheduleConf);
        return scheduleConfJson.getInteger(CANDLER_BATCH_TYPE);
    }
}
