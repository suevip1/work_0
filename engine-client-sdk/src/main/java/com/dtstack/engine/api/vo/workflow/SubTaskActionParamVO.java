package com.dtstack.engine.api.vo.workflow;

public class SubTaskActionParamVO {
    /**
     * 工作流 taskId
     */
    private Long flowId;

    /**
     * 子任务 taskId
     */
    private Long taskId;

    /**
     * {@link com.dtstack.engine.api.pojo.ParamTaskAction} json string
     */
    private String paramTaskAction;

    /**
     * 应用类型
     */
    private Integer appType;


    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getParamTaskAction() {
        return paramTaskAction;
    }

    public void setParamTaskAction(String paramTaskAction) {
        this.paramTaskAction = paramTaskAction;
    }
}