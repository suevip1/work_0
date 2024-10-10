package com.dtstack.engine.api.vo.calender;

import com.dtstack.engine.api.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ConsoleCalenderVO extends BaseEntity {

    @ApiModelProperty(notes = "调度日历名称")
    private String calenderName;
    @ApiModelProperty(notes = "最后一次调度时间")
    private Long latestCalenderTime;
    @ApiModelProperty(notes = "任务列表")
    private String taskNames;
    @ApiModelProperty(notes = "时间列表")
    private List<String> times;
    @ApiModelProperty(notes = "是否快过期")
    private boolean expiringSoon;
    @ApiModelProperty(notes = "上传文件名")
    private String calenderFileName;
    @ApiModelProperty(notes = "调度日历时间格式")
    private String calenderTimeFormat;

    public String getCalenderFileName() {
        return calenderFileName;
    }

    public void setCalenderFileName(String calenderFileName) {
        this.calenderFileName = calenderFileName;
    }

    public boolean isExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(boolean expiringSoon) {
        this.expiringSoon = expiringSoon;
    }

    public String getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(String taskNames) {
        this.taskNames = taskNames;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public String getCalenderName() {
        return calenderName;
    }

    public void setCalenderName(String calenderName) {
        this.calenderName = calenderName;
    }

    public Long getLatestCalenderTime() {
        return latestCalenderTime;
    }

    public void setLatestCalenderTime(Long latestCalenderTime) {
        this.latestCalenderTime = latestCalenderTime;
    }

    public String getCalenderTimeFormat() {
        return calenderTimeFormat;
    }

    public void setCalenderTimeFormat(String calenderTimeFormat) {
        this.calenderTimeFormat = calenderTimeFormat;
    }
}
