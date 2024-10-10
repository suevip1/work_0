package com.dtstack.engine.api.vo.task;

import com.dtstack.engine.api.param.PageParam;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskPageVO
 * @date 2022/7/18 4:53 PM
 */
public class TaskPageVO extends PageParam {

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
     * 搜索类型
     */
    private TaskSearchNameVO searchName;

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
     * 日历id
     */
    private List<Long> calenderIds;

    /**
     * 时间类型
     */
    private TaskTimeVO taskTime;

    /**
     * 任务分支
     */
    private Integer taskGroup;

    private String businessType;

    private List<Integer> priorityList;

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

    public TaskSearchNameVO getSearchName() {
        return searchName;
    }

    public void setSearchName(TaskSearchNameVO searchName) {
        this.searchName = searchName;
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

    public List<Long> getCalenderIds() {
        return calenderIds;
    }

    public void setCalenderIds(List<Long> calenderIds) {
        this.calenderIds = calenderIds;
    }

    public TaskTimeVO getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(TaskTimeVO taskTime) {
        this.taskTime = taskTime;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<Integer> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<Integer> priorityList) {
        this.priorityList = priorityList;
    }
}
