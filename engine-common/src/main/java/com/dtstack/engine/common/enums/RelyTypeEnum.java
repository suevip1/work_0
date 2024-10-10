package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/10/26 3:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RelyTypeEnum {

    // 0正常 1逻辑删除
    NORMAL(0, "正常依赖"),TO_RELY_ON(1,"去依赖");

    private final Integer type;

    private final String desc;

    RelyTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
