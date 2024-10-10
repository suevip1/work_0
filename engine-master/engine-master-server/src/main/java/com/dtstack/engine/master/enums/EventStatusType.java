package com.dtstack.engine.master.enums;

/**
 * @author yuebai
 * @date 2020-05-09
 */
public enum EventStatusType {
    SUCCESS(1),
    FAILED(2),
    TIME_OUT(3);
    private int status;

    EventStatusType(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
