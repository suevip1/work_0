package com.dtstack.engine.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskPageDTO
 * @date 2022/7/20 10:36 AM
 */
public class TaskPageDTO {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 应用类型
     */
    private Integer appType;

    /**
     * 责任人id
     */
    private Long ownerId;

    /**
     * 任务集合
     */
    private Set<Long> taskIds;

    /**
     * 调度状态： 1正常调度,2 冻结
     */
    private List<Integer> scheduleStatus;

    /**
     * 任务类型：
     */
    private List<Integer> taskTypes;

    /**
     * 标签类型
     */
    private List<Long> tagIds;

    /**
     * 调度类型
     */
    private List<Integer> periodTypes;

    /**
     * 资源组id
     */
    private List<Long> resourceIds;

    /**
     * 时间类型
     */
    private TaskTimeDTO taskTime;

    private Map<Long, List<Long>> flowMap;

    /**
     * 分页
     */
    private Integer start;

    /**
     * 页码
     */
    private Integer pageSize;

    /**
     * 任务分支
     */
    private Integer taskGroup;

    public Integer getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(Integer taskGroup) {
        this.taskGroup = taskGroup;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Set<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(Set<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Integer> getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(List<Integer> scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public List<Integer> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<Integer> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Integer> getPeriodTypes() {
        return periodTypes;
    }

    public void setPeriodTypes(List<Integer> periodTypes) {
        this.periodTypes = periodTypes;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public TaskTimeDTO getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(TaskTimeDTO taskTime) {
        this.taskTime = taskTime;
    }

    public Map<Long, List<Long>> getFlowMap() {
        return flowMap;
    }

    public void setFlowMap(Map<Long, List<Long>> flowMap) {
        this.flowMap = flowMap;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
