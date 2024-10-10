package com.dtstack.engine.master.model.cluster.exception;

import com.dtstack.engine.common.enums.EComponentType;

public class InvalidComponentException extends BaseComponentException {

    public InvalidComponentException(EComponentType type, String msg) {
        super(type, msg);
    }

    @Override
    public String getErrorMsg() {
        return super.getMessage();
    }
}
