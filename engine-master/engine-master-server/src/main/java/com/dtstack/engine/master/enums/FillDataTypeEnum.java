package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 1:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillDataTypeEnum {
    BATCH(0, "批量生成"),
    CONDITION_PROJECT_TASK(1, "按照条件补数据： 工程，任务类型"),
    MANUAL(2, "手动任务"),
    RELY(3,"按照依赖关系补数据"),
    IMMEDIATELY(4,"补数据立即运行");
    private final Integer type;

    private final String name;

    FillDataTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 支持自定义参数替换
     * @param fillDataType
     * @return
     */
    public static boolean supportTaskCustomParam(Integer fillDataType) {
        return BATCH.getType().equals(fillDataType) || RELY.getType().equals(fillDataType);
    }
}
