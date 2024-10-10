package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TaskInfoChangeParam {

    private List<Long> taskIdList;

    @NotNull(message =  "owner userId can not null")
    private Long ownerUserId;
    
    @NotNull(message =  "app type can not null")
    private Integer appType;

    public TaskInfoChangeParam() {
    }

    public TaskInfoChangeParam(List<Long> taskIdList, Long ownerUserId, Integer appType) {
        this.taskIdList = taskIdList;
        this.ownerUserId = ownerUserId;
        this.appType = appType;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public Integer getAppType() {
        return appType;
    }
}
