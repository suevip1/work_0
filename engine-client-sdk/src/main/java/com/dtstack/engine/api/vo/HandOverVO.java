package com.dtstack.engine.api.vo;

public class HandOverVO {
    private Long sourceResourceId;
    private boolean needHandOver;
    private Long targetResourceId;

    public Long getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(Long sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    public boolean isNeedHandOver() {
        return needHandOver;
    }

    public void setNeedHandOver(boolean needHandOver) {
        this.needHandOver = needHandOver;
    }

    public Long getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(Long targetResourceId) {
        this.targetResourceId = targetResourceId;
    }
}
