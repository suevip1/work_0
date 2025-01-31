package com.dtstack.engine.master.scheduler.parser;

import com.dtstack.engine.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 按天调度解析
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronDayParser extends ScheduleCron {

    private static final String CRON_FORMAT = "0 ${minute} ${hour} * * ?";

    private static final String MINUTE_KEY = "min";

    private static final String HOUR_KEY = "hour";

    private int hour = 0;

    private int minute = 0;

    @Override
    public String parse(Map<String, Object> param) {

        Preconditions.checkState(param.containsKey(MINUTE_KEY), MINUTE_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(HOUR_KEY), HOUR_KEY + " not be null!");

        minute = MathUtil.getIntegerVal(param.get(MINUTE_KEY));
        hour = MathUtil.getIntegerVal(param.get(HOUR_KEY));

        String cronStr = CRON_FORMAT.replace("${minute}", minute + "").replace("${hour}", hour + "");
        setCronStr(cronStr);
        return cronStr;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        if(!checkSpecifyDayCanExe(specifyDate)){
            return Lists.newArrayList();
        }

        String exeTime = specifyDate +  " " + getTimeStr(hour) + ":" + getTimeStr(minute) + ":00";
        List<String> result = Lists.newArrayList();
        result.add(exeTime);
        return result;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        return true;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScheduleCronDayParser that = (ScheduleCronDayParser) o;
        return hour == that.hour && minute == that.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hour, minute);
    }
}
