package com.dtstack.engine.common.enums;


public enum EJobLogType {
    //
    FINISH_LOG(0),
    //
    RETRY_LOG(1),
    //
    EXPIRE_LOG(2);

    Integer type;

    public Integer getType() {
        return type;
    }



    EJobLogType(Integer type) {
        this.type = type;
    }
}
