package com.dtstack.engine.api.vo.schedule.job;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023-09-13 15:18
 * @Email: dazhi@dtstack.com
 * @Description: ScheduleAlterVO
 */
public class ScheduleAlterPageVO {

    /**
     * 计划时间 格式 yyyymmddhhmmss
     */
    private String startCycTime;

    /**
     * 计划时间 格式 yyyymmddhhmmss
     */
    private String engCycTime;

    private Integer appType;

    private List<Long> taskIds;

    private Long projectId;

    private Integer page = 1;

    private Integer pageSize = 10;

    public String getStartCycTime() {
        return startCycTime;
    }

    public void setStartCycTime(String startCycTime) {
        this.startCycTime = startCycTime;
    }

    public String getEngCycTime() {
        return engCycTime;
    }

    public void setEngCycTime(String engCycTime) {
        this.engCycTime = engCycTime;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
