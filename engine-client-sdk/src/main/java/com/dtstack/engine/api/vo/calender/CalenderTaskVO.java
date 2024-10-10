package com.dtstack.engine.api.vo.calender;

import io.swagger.annotations.ApiModelProperty;

public class CalenderTaskVO {
    @ApiModelProperty(notes = "租户名称")
    private String tenantName;
    @ApiModelProperty(notes = "项目名称")
    private String projectName;
    @ApiModelProperty(notes = "任务名称")
    private String taskName;
    @ApiModelProperty(notes = "责任人")
    private String ownerUserName;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }
}
