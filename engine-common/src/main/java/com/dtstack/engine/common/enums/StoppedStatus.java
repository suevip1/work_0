package com.dtstack.engine.common.enums;

public enum StoppedStatus {
    //停止中
    STOPPING(0),
    //已停止
    STOPPED(1),
    MISSED(2),
    RETRY(3),
    NO_OPERATE(4);

    Integer type;
    StoppedStatus(int type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }
}
