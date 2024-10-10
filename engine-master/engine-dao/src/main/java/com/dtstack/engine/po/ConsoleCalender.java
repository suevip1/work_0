package com.dtstack.engine.po;

import com.dtstack.engine.api.domain.BaseEntity;

public class ConsoleCalender extends BaseEntity {

    private String calenderName;
    private String latestCalenderTime;
    private String calenderFileName;
    /**
     * 自定义调度日历用处, 1 全局参数, 2 自定义调度日期
     */
    private Integer useType;

    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    private String calenderTimeFormat;
    public String getCalenderFileName() {
        return calenderFileName;
    }

    public void setCalenderFileName(String calenderFileName) {
        this.calenderFileName = calenderFileName;
    }

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    public String getLatestCalenderTime() {
        return latestCalenderTime;
    }

    public void setLatestCalenderTime(String latestCalenderTime) {
        this.latestCalenderTime = latestCalenderTime;
    }

    public String getCalenderTimeFormat() {
        return calenderTimeFormat;
    }

    public void setCalenderTimeFormat(String calenderTimeFormat) {
        this.calenderTimeFormat = calenderTimeFormat;
    }
}
