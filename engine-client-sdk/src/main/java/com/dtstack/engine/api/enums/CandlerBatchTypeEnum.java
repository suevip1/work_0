package com.dtstack.engine.api.enums;

/**
 * @author jiuwei@dtstack.com
 **/
public enum CandlerBatchTypeEnum {
    /**
     * 自定义调度日历
     */
    SINGLE(0,"单批次"),
    MANY(1,"多批次")
    ;
    private Integer type;
    private String msg;

    CandlerBatchTypeEnum(Integer type,String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsg(Integer val){
        for (CandlerBatchTypeEnum typeEnum:CandlerBatchTypeEnum.values()){
            if (typeEnum.getType().equals(val)){
                return typeEnum.getMsg();
            }
        }
        return null;
    }

}
