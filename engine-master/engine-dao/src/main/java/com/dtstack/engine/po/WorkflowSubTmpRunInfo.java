package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * uk: flow_job_id
 * key: `flow_id`, `task_id`, `app_type`
 * @author leon
 * @date 2023-03-07 16:54
 **/
public class WorkflowSubTmpRunInfo extends BaseEntity {

    @NotNull
    private String flowJobId;

    @NotNull
    private String jobId;

    @NotNull
    private Long flowId;

    @NotNull
    private Long taskId;

    @NotNull
    private Integer appType;

    private Integer isParentFail;

    private Integer isSkip;

    private String paramTaskAction;

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

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

    public String getParamTaskAction() {
        return paramTaskAction;
    }

    public void setParamTaskAction(String paramTaskAction) {
        this.paramTaskAction = paramTaskAction;
    }

    public Integer getIsParentFail() {
        return isParentFail;
    }

    public void setIsParentFail(Integer isParentFail) {
        this.isParentFail = isParentFail;
    }

    public Integer getIsSkip() {
        return isSkip;
    }

    public void setIsSkip(Integer isSkip) {
        this.isSkip = isSkip;
    }
}
