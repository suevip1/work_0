package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;
import java.util.List;

@ApiModel
public class ScheduleTaskShadeDTO extends ScheduleTaskShade {

    private Timestamp startGmtModified;
    private Timestamp endGmtModified;

    private List<Integer> periodTypeList;
    private List<Integer> taskTypeList;
    /**
     * 责任人列表
     */
    private List<Long> ownerUserIds;
    private String fuzzName;
    private Integer searchType;

    private Timestamp startTime;

    private Timestamp endTime;

    private String taskName;

    private Integer pageSize = 10;

    private Integer pageIndex = 1;

    private String sort = "desc";

    private Integer version;


    private List<Long> resourceIds;

    /**
     * 调度日历
     */
    private Long calenderId;

    /**
     * 扩展时间, 配合调度日历一起使用
     */
    private String expendTime;


    /**
     * 日历调度，单批次标识 0单批次1多批次
     */
    private Integer candlerBatchType;

    private List<Long> queryTaskIds;

    private List<Long> tagIds;

    private Integer pythonVersion;

    /**
     * 是否忽略发布条件
     */
    private Integer ignorePublish;

    public Integer getIgnorePublish() {
        return ignorePublish;
    }

    public void setIgnorePublish(Integer ignorePublish) {
        this.ignorePublish = ignorePublish;
    }

    public List<Long> getQueryTaskIds() {
        return queryTaskIds;
    }

    public void setQueryTaskIds(List<Long> queryTaskIds) {
        this.queryTaskIds = queryTaskIds;
    }

    public Long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(Long calenderId) {
        this.calenderId = calenderId;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public List<Long> getOwnerUserIds() {
        return ownerUserIds;
    }

    public void setOwnerUserIds(List<Long> ownerUserIds) {
        this.ownerUserIds = ownerUserIds;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        super.setVersionId(version);
        this.version = version;
    }

    public Timestamp getStartGmtModified() {
        return startGmtModified;
    }

    public void setStartGmtModified(Timestamp startGmtModified) {
        this.startGmtModified = startGmtModified;
    }

    public Timestamp getEndGmtModified() {
        return endGmtModified;
    }

    public void setEndGmtModified(Timestamp endGmtModified) {
        this.endGmtModified = endGmtModified;
    }

    public List<Integer> getPeriodTypeList() {
        return periodTypeList;
    }

    public void setPeriodTypeList(List<Integer> periodTypeList) {
        this.periodTypeList = periodTypeList;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public String getFuzzName() {
        return fuzzName;
    }

    public void setFuzzName(String fuzzName) {
        this.fuzzName = fuzzName;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(String expendTime) {
        this.expendTime = expendTime;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Integer getPythonVersion() {
        return pythonVersion;
    }

    public void setPythonVersion(Integer pythonVersion) {
        this.pythonVersion = pythonVersion;
    }


    public Integer getCandlerBatchType() {
        return candlerBatchType;
    }

    public void setCandlerBatchType(Integer candlerBatchType) {
        this.candlerBatchType = candlerBatchType;
    }
}
