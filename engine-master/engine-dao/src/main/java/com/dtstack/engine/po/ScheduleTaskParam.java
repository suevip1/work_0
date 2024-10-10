package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

public class ScheduleTaskParam extends BaseEntity {

    private Long taskId;

    private Integer appType;

    private Long paramId;

    private String offset;

    private String replaceTarget;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }


    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getReplaceTarget() {
        return replaceTarget;
    }

    public void setReplaceTarget(String replaceTarget) {
        this.replaceTarget = replaceTarget;
    }
}
