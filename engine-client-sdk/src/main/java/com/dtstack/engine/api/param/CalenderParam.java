package com.dtstack.engine.api.param;

import java.util.List;

public class CalenderParam {

    private int currentPage;
    private int pageSize;

    private List<Long> taskIds;
    private int appType;
    private long calenderId;
    /**
     * 返回限制
     */
    private int limit;
    /**
     * 从特定时间往后
     * 格式yyyyMMddHHmm
     * 如202204101500
     */
    private Long afterTime;

    /**
     * 日历名称
     */
    private String calenderName;

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Long getAfterTime() {
        return afterTime;
    }

    public void setAfterTime(Long afterTime) {
        this.afterTime = afterTime;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public long getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(long calenderId) {
        this.calenderId = calenderId;
    }
}
