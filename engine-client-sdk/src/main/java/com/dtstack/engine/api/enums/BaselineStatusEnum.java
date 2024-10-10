package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/11 2:42 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum BaselineStatusEnum {

    SAFETY(0,"安全"),
    WARNING(1,"预警"),
    BREAKING_THE_LINE(2,"破线"),
    TIMING_NOT_COMPLETED(3,"定时未完成"),
    OTHERS(4,"其他");

    private final Integer code;

    private final String msg;

    BaselineStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(Integer code) {
        BaselineStatusEnum[] values = values();

        for (BaselineStatusEnum baselineStatusEnum : values) {
            if (baselineStatusEnum.getCode().equals(code)) {
                return baselineStatusEnum.getMsg();
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
