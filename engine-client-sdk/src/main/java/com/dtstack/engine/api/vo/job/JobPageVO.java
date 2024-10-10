package com.dtstack.engine.api.vo.job;

import com.dtstack.engine.api.param.PageParam;
import com.dtstack.engine.api.vo.task.TaskSearchNameVO;

import java.util.List;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName JobPageVO
 * @date 2022/7/19 2:10 PM
 */
public class JobPageVO extends PageParam {

    private Long tenantId;

    private Long projectId;

    private Integer appType;

    private Long ownerId;

    private TaskSearchNameVO searchName;

    private JobTimeVO jobTime;

    private List<Integer> jobStatuses;

    private List<Integer> taskTypes;

    private List<Integer> periodTypes;

    private List<Long> resourceIds;

    private List<Long> calenderIds;

    private List<Long> tagIds;
    
    private List<Long> rangeTaskIds;

    private Long fillId;

    private SortVO sortVO;

    private Integer type;

    private List<Integer> types;

    private String jobId;

    private String businessType;

    private List<Integer> priorityList;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public JobTimeVO getJobTime() {
        return jobTime;
    }

    public void setJobTime(JobTimeVO jobTime) {
        this.jobTime = jobTime;
    }

    public List<Integer> getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(List<Integer> jobStatuses) {
        this.jobStatuses = jobStatuses;
    }

    public List<Integer> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<Integer> taskTypes) {
        this.taskTypes = taskTypes;
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

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<Long> getCalenderIds() {
        return calenderIds;
    }

    public void setCalenderIds(List<Long> calenderIds) {
        this.calenderIds = calenderIds;
    }

    public List<Long> getRangeTaskIds() {
        return rangeTaskIds;
    }

    public void setRangeTaskIds(List<Long> rangeTaskIds) {
        this.rangeTaskIds = rangeTaskIds;
    }

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public SortVO getSortVO() {
        return sortVO;
    }

    public void setSortVO(SortVO sortVO) {
        this.sortVO = sortVO;
    }

    public Integer getType() {
        return type;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<Integer> priorityList) {
        this.priorityList = priorityList;
    }
}
