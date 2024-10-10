package com.dtstack.engine.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName JobPageDTO
 * @date 2022/7/21 3:28 PM
 */
public class JobPageDTO {

    private Long tenantId;

    private Long projectId;

    private Integer appType;

    private Set<Long> taskIds;

    private Set<String> jobIds;

    private JobTimeDTO jobTimeDTO;

    private List<Integer> jobStatuses;

    private List<Integer> taskTypes;

    private List<Integer> periodTypes;

    private List<Long> resourceIds;

    private Long fillId;

    private SortDTO sort;

    private Integer type;

    private List<Integer> types;

    private List<Integer> fillTypes;

    private Map<Long, List<Long>> flowMap;

    private Map<String, List<String>> flowJobMap;

    private Integer start;

    private Integer pageSize;

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

    public Set<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(Set<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Set<String> getJobIds() {
        return jobIds;
    }

    public void setJobIds(Set<String> jobIds) {
        this.jobIds = jobIds;
    }

    public JobTimeDTO getJobTimeDTO() {
        return jobTimeDTO;
    }

    public void setJobTimeDTO(JobTimeDTO jobTimeDTO) {
        this.jobTimeDTO = jobTimeDTO;
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

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public SortDTO getSort() {
        return sort;
    }

    public void setSort(SortDTO sort) {
        this.sort = sort;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public List<Integer> getFillTypes() {
        return fillTypes;
    }

    public void setFillTypes(List<Integer> fillTypes) {
        this.fillTypes = fillTypes;
    }

    public Map<Long, List<Long>> getFlowMap() {
        return flowMap;
    }

    public void setFlowMap(Map<Long, List<Long>> flowMap) {
        this.flowMap = flowMap;
    }

    public Map<String, List<String>> getFlowJobMap() {
        return flowJobMap;
    }

    public void setFlowJobMap(Map<String, List<String>> flowJobMap) {
        this.flowJobMap = flowJobMap;
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
