package com.dtstack.engine.api.vo.task;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskReturnPageVO
 * @date 2022/7/18 5:08 PM
 */
public class TaskReturnPageVO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 提交时间
     */
    private String submitTime;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 调度周期
     */
    private Integer periodType;

    /**
     * 资源组id
     */
    private Long resourceId;

    /**
     * 资源组名称
     */
    private String resourceName;

    /**
     * 责任人名称
     */
    private Long ownerId;

    /**
     * 责任人名称
     */
    private String ownerName;

    /**
     * 工作流id
     */
    private Long flowId;

    /**
     * 标签
     */
    private List<Long> tagIds;

    /**
     * 任务状态 1 正常调度 2 冻结
     */
    private Integer scheduleStatus;

    /**
     * 是否被删除下线 0正常 1删除
     */
    private Integer isDeleted;

    /**
     * 优先级
     */
    private Integer priority;


    private List<TaskReturnPageVO> flowVOs;

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<TaskReturnPageVO> getFlowVOs() {
        return flowVOs;
    }

    public void setFlowVOs(List<TaskReturnPageVO> flowVOs) {
        this.flowVOs = flowVOs;
    }
}
