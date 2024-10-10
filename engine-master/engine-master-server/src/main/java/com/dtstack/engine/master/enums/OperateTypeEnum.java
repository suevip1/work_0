package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 2:08 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum OperateTypeEnum {

    STOP(0,"杀死实例"),RESTART(1,"重跑实例"),RELY(2,"紧急去依赖(去除上游依赖：%s)"),RESTART_CANCELED(3,"实例在运行状态，重跑取消,任务：%s")
    ;

    OperateTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
