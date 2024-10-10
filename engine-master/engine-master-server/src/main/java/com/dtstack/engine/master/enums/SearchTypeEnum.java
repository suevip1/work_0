package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/17 2:22 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum SearchTypeEnum {
    FUZZY(1,"fuzzy"),
    PRECISE(2,"precise"),
    FRONT(3,"front"),
    TAIL(4,"tail"),
    ;
    private final Integer type;

    private final String name;

    SearchTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static Integer getTypeByName(String name) {
        SearchTypeEnum[] values = SearchTypeEnum.values();
        for (SearchTypeEnum value : values) {
            if (value.getName().equals(name)) {
                return value.getType();
            }
        }
        return SearchTypeEnum.FUZZY.getType();
    }
}
