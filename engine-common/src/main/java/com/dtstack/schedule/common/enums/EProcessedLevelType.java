package com.dtstack.schedule.common.enums;

import java.util.regex.Pattern;

/**
 * 计算结果类型
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-18 18:03
 */
public enum EProcessedLevelType {
    SECOND(2, Pattern.compile("\\$\\{\\w+\\}\\[\\d+\\]\\[\\d+\\]"), "${}[][]"),
    FIRST(1, Pattern.compile("\\$\\{\\w+\\}\\[\\d+\\]"),"${}[]"),
    ZERO(0, Pattern.compile("\\$\\{\\w+\\}"), "${}"),
            ;
    private Integer type;
    private Pattern pattern;
    private String desc;

    EProcessedLevelType(Integer type, Pattern pattern, String desc) {
        this.type = type;
        this.pattern = pattern;
        this.desc = desc;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getType(){
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
