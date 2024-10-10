package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/5/6 4:27 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlarmRuleBusinessTypeEnum {

    USER(0, "用户"),

    TASK(1, "任务"),

    RULE(2, "规则"),

    CHANNEL(3, "通道"),

    BASELINE(4, "基线"),

    OTHER_USER(5, "其他用户"),

    GROUP(6, "用户组"),

    CATALOGUE(7, "类目"),

    BLACK_LIST(8, "黑名单"),

    TASK_OWNER_USER(9, "任务"),

    FILL_DATA_ALERT(10, "补数据告警"),

    TAG_LIST(11,"标签"),
    ;

    private final String name;

    private final Integer code;

    AlarmRuleBusinessTypeEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public static AlarmRuleBusinessTypeEnum getByCode(Integer code) {
        for (AlarmRuleBusinessTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
