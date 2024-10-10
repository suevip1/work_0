package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/9 5:07 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CountAlarmVO {

    private Integer today;

    private Integer week;

    private Integer month;

    public Integer getToday() {
        return today;
    }

    public void setToday(Integer today) {
        this.today = today;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
