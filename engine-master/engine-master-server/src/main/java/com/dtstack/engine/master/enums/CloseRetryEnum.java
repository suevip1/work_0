package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/10/28 3:36 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum CloseRetryEnum {
    // 0 关闭 1开启

    CLOSE(0,"关闭"),
    OPEN(1,"开启")
    ;

    private final Integer type;

    private final String name;

    CloseRetryEnum(Integer type, String name) {
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
