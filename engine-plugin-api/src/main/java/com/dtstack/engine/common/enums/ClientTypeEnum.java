package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/3/2 11:18 AM
 * @Email: dazhi@dtstack.com
 * @Description: 任务
 */
public enum ClientTypeEnum {


    /**
     * dagshedulex状态任务
     */
    WORKER_STATUS(0),

    /**
     * sourceX 无状态任务
     */
    DATASOURCEX_NO_STATUS(1),

    /**
     * sourceX 状态任务
     */
    DATASOURCEX_STATUS(2),

    /**
     * dagshedulex内部任务
     */
    DAGSCHEDULEX_LOCAL(4),
    ;

    private Integer code;

    ClientTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static ClientTypeEnum getClientTypeEnum(Integer code) {
        ClientTypeEnum[] values = values();

        for (ClientTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }

        return null;
    }


}
