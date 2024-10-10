package com.dtstack.engine.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 任务重试类型
 *
 * @author ：wangchuan
 * date：Created in 下午5:21 2022/5/18
 * company: www.dtstack.com
 */
public enum ERetryType {

    /**
     * 重跑
     */
    RERUN,

    /**
     * 续跑, 从最近的一次 checkPoint 续跑
     */
    CONTINUE;

    /**
     * 获取重试类型
     *
     * @param retryType 重试诶行
     * @return 重试类型
     */
    public static ERetryType getRetryType(String retryType, ERetryType defaultRetryType) {
        for (ERetryType value : values()) {
            if (StringUtils.equalsIgnoreCase(value.name(), retryType)) {
                return value;
            }
        }
        return defaultRetryType;
    }
}
