package com.dtstack.engine.master.utils;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 7:44 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobExecuteOrderUtil {

    public static Long buildJobExecuteOrder(String triggerTime, AtomicInteger count) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new RuntimeException("cycTime is not null");
        }

        // 时间格式 yyyyMMddHHmmss  截取 jobExecuteOrder = yyMMddHHmm +  9位的自增
        String substring = triggerTime.substring(2, triggerTime.length() - 2);
        String increasing = String.format("%09d", count.getAndIncrement());
        return Long.parseLong(substring+increasing);
    }
}
