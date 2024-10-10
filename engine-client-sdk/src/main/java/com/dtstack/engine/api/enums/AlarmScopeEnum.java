package com.dtstack.engine.api.enums;

/**
 *
 *
 * @author leon
 * @date 2022-10-19 10:47
 **/
public enum AlarmScopeEnum {

    SELECT(1),
    ALL(2),
    CATALOGUE(3),
    TAG(4);

    private final Integer code;

    AlarmScopeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static boolean isSelectScope(Integer code) {
        return SELECT.getCode().equals(code);
    }

    public static boolean isAllScope(Integer code) {
        return ALL.getCode().equals(code);
    }

    public static boolean isTagScope(Integer code) {
        return TAG.getCode().equals(code);
    }

    public static boolean isCatalogueScope(Integer code) {
        return CATALOGUE.getCode().equals(code);
    }

    public static boolean isALLOrCatalogueScope(Integer code) {
        return ALL.getCode().equals(code) || CATALOGUE.getCode().equals(code) || TAG.getCode().equals(code);
    }
}

