package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 3:35 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public enum TaskRunOrderEnum {
    // 0 无 默认
    // 1. 按业务日期升序
    // 2. 按业务日期降序

    DEFAULT(0,"默认"),
    AES(1,"按业务日期升序"),
    DESC(2,"按业务日期降序")
    ;

    private final Integer type;

    private final String name;

    TaskRunOrderEnum(Integer type, String name) {
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
