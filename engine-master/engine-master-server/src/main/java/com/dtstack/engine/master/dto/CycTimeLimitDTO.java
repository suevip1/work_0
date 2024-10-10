package com.dtstack.engine.master.dto;

/**
 * 执行时间限制
 *
 * @author ：wangchuan
 * date：Created in 09:53 2022/9/1
 * company: www.dtstack.com
 */
public class CycTimeLimitDTO {

    /**
     * 延迟后是否是当天
     */
    private boolean today;

    /**
     * 触发日期
     */
    private String triggerDay;

    /**
     * cycTime 开始时间
     */
    private String startTime;

    /**
     * cycTime 结束时间
     */
    private String endTime;

    /**
     * 时分开始时间
     */
    private String startTimeHourMinute;

    /**
     * 时分结束时间
     */
    private String endTimeHourMinute;

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public String getTriggerDay() {
        return triggerDay;
    }

    public void setTriggerDay(String triggerDay) {
        this.triggerDay = triggerDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeHourMinute() {
        return startTimeHourMinute;
    }

    public void setStartTimeHourMinute(String startTimeHourMinute) {
        this.startTimeHourMinute = startTimeHourMinute;
    }

    public String getEndTimeHourMinute() {
        return endTimeHourMinute;
    }

    public void setEndTimeHourMinute(String endTimeHourMinute) {
        this.endTimeHourMinute = endTimeHourMinute;
    }
}
