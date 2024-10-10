package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/3/17 10:15 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum OpenKerberosEnum {
    OPEN(1,"启动"),CLOES(0,"关闭");
    private Integer code;

    private String msg;

    OpenKerberosEnum(Integer code, String msg) {
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
