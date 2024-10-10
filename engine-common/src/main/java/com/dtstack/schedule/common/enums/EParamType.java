package com.dtstack.schedule.common.enums;

import com.dtstack.engine.common.enums.EDateBenchmark;

/**
 * 参数类型
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public enum EParamType {

    SYS_TYPE(0),

    CUSTOMIZE_TYPE(1),
    COMPONENT(2),
    INPUT(5),
    OUTPUT(6),

    //全局参数 为 常量、时间
    GLOBAL(7),

    /**
     * 时间
     * 包括自然日和自定义日期 {@link EDateBenchmark}
     */
    GLOBAL_PARAM_BASE_TIME(8),
    // 常量
    GLOBAL_PARAM_CONST(9),
    // 按计划时间查找参数值
    GLOBAL_PARAM_BASE_CYCTIME(10),

    // 工作流级参数
    WORK_FLOW(11),
    // 项目级参数
    PROJECT(12)
    ;

    private Integer type;

    EParamType(Integer type){
        this.type = type;
    }

    public static boolean isGlobalParamConst(Integer paramType) {
        return GLOBAL_PARAM_CONST.getType().equals(paramType);
    }

    public static boolean isGlobalParamBaseTime(Integer paramType) {
        return GLOBAL_PARAM_BASE_TIME.getType().equals(paramType);
    }

    public static boolean isGlobalParamBaseCycTime(Integer paramType) {
        return GLOBAL_PARAM_BASE_CYCTIME.getType().equals(paramType);
    }

    public static boolean isGlobalParam(Integer paramType) {
        return GLOBAL.getType().equals(paramType) ||
                isGlobalParamConst(paramType) ||
                isGlobalParamBaseTime(paramType) ||
                isGlobalParamBaseCycTime(paramType);
    }

    public static boolean isSelfParam(Integer paramType) {
        return CUSTOMIZE_TYPE.getType().equals(paramType)
                || COMPONENT.getType().equals(paramType);
    }

    /**
     * 上下游参数
     * @param paramType
     * @return
     */
    public static boolean isChainParam(Integer paramType) {
        return INPUT.getType().equals(paramType)
                || OUTPUT.getType().equals(paramType);
    }

    public Integer getType(){
        return type;
    }

    public static EParamType getByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (EParamType oneEnum : EParamType.values()) {
            if (oneEnum.getType().equals(type)) {
                return oneEnum;
            }
        }
        return null;
    }

}
