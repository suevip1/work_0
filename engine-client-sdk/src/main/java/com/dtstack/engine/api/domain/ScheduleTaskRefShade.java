package com.dtstack.engine.api.domain;


import com.dtstack.engine.api.annotation.Unique;
import com.dtstack.engine.api.domain.AppTenantEntity;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;


@ApiModel
public class ScheduleTaskRefShade extends AppTenantEntity {

    public ScheduleTaskRefShade() {
    }

    public ScheduleTaskRefShade(String taskKey, String refTaskKey) {
        this.taskKey = taskKey;
        this.refTaskKey = refTaskKey;
    }

    @Unique
    private Long taskId;

    private Long refTaskId;

    private Integer refAppType;

    private String taskKey;

    private String refTaskKey;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getRefTaskId() {
        return refTaskId;
    }

    public void setRefTaskId(Long refTaskId) {
        this.refTaskId = refTaskId;
    }

    public Integer getRefAppType() {
        return refAppType;
    }

    public void setRefAppType(Integer refAppType) {
        this.refAppType = refAppType;
    }

    public String getTaskKey() {
        if (StringUtils.isBlank(taskKey)) {
            taskKey = taskId + "-" + getAppType();
        }
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getRefTaskKey() {
        if (StringUtils.isBlank(refTaskKey) && refTaskId != null) {
            Integer parentAppType = getRefAppType();
            if (parentAppType == null) {
                refTaskKey = refTaskId + "-" + getAppType();
            } else {
                refTaskKey = refTaskId + "-" + parentAppType;
            }
        }
        return refTaskKey;
    }

    public void setRefTaskKey(String refTaskKey) {
        this.refTaskKey = refTaskKey;
    }

}
