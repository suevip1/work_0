package com.dtstack.engine.api.enums;

/**
 * 实例生成方式枚举
 *
 * @author ：wangchuan
 * date：Created in 10:00 2022/8/23
 * company: www.dtstack.com
 */
public enum JobBuildType {

    /**
     * 默认为 T + 1 生成实例
     */
    THE_NEXT_DAY(1),

    /**
     * 立即生成当天的实例
     */
    IMMEDIATELY(2);

    /**
     * 实例生成方式
     */
    private final Integer type;

    JobBuildType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
