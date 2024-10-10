package com.dtstack.engine.api.vo.template;

public class EnvironmentParamTemplateVO {

    /**
     * 任务参数
     */
    private String params;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务版本
     */
    private String taskVersion;

    /**
     * 任务名称
     */
    private String taskName;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskVersion() {
        return taskVersion;
    }

    public void setTaskVersion(String taskVersion) {
        this.taskVersion = taskVersion;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
