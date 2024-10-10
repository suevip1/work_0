package com.dtstack.engine.common.enums;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-13 21:51
 */
public enum EDateBenchmark {
    NATURAL_DATE(1, "自然日"),
    CUSTOM_DATE(2, "自定义日期")
    ;

    private Integer type;

    private String desc;

    EDateBenchmark(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static EDateBenchmark valueOf(Integer type){
        if (type == null) {
            return null;
        }
        for(EDateBenchmark anEnum : EDateBenchmark.values()){
            if(anEnum.getType().equals(type)){
                return anEnum;
            }
        }
        return null;
    }
}
