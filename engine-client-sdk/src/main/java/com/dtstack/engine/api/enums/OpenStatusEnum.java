package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 1:46 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum OpenStatusEnum {

    OPEN(0,"启动"),CLOES(1,"关闭");

    private final Integer code;

    private final String msg;

    OpenStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
