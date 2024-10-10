package com.dtstack.engine.common.enums;

/**
 * @author leon
 * @date 2022-06-10 10:32
 **/
public enum ConsoleModeEnum {

    MAINTENANCE(1,"升级维护模式")

    ;

    private Integer type;

    private String desc;

    ConsoleModeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }
}
