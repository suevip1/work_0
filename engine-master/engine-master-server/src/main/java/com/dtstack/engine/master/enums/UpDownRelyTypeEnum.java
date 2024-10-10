package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2023-10-13 13:58
 * @Email: dazhi@dtstack.com
 * @Description: UpDownRelyTypeEnum
 */
public enum UpDownRelyTypeEnum {
    DEFAULT(0,"default"),
    CUSTOM(1,"custom"),
    ;

    private final Integer type;

    private final String name;

    UpDownRelyTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
