package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:45 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FinishStatus {
    // 0 未完成 1 已完成
    NO_FINISH(0,"未完成"),FINISH(1,"完成");

    private final Integer code;

    private final String msg;

    FinishStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(Integer code) {
        FinishStatus[] values = values();

        for (FinishStatus finishStatus : values) {
            if (finishStatus.getCode().equals(code)) {
                return finishStatus.getMsg();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
