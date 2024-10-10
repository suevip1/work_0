package com.dtstack.engine.api.vo.task;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName WorkflowSubViewList
 * @date 2022/7/19 2:07 PM
 */
public class WorkflowSubViewList {

    private Long taskId;

    private Integer appType;

    private TaskViewRetrunVO taskViewRetrunVO;

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

    public TaskViewRetrunVO getTaskViewRetrunVO() {
        return taskViewRetrunVO;
    }

    public void setTaskViewRetrunVO(TaskViewRetrunVO taskViewRetrunVO) {
        this.taskViewRetrunVO = taskViewRetrunVO;
    }
}
