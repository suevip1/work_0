package com.dtstack.engine.master.scheduler.parser;


import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 按月周期调度
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronMonthParser extends ScheduleCron {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleCronMonthParser.class);

    private static final String CRON_FORMAT = "0 ${min} ${hour} ${days} * ?";

    private static final String MINUTE_KEY = "min";

    private static final String HOUR_KEY = "hour";

    private static final String DAY_KEY = "day";

    private static final String IS_DAY_OF_EACH_MONTH = "isDayOfEachMonth";

    private int hour = 0;

    private int minute = 0;

    private Boolean isDayOfEachMonth = false;

    @Override
    public String parse(Map<String, Object> param) {
        Preconditions.checkState(param.containsKey(MINUTE_KEY), MINUTE_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(HOUR_KEY), HOUR_KEY + " not be null!");

        isDayOfEachMonth = MathUtil.getBoolean(param.get(IS_DAY_OF_EACH_MONTH));
        minute = MathUtil.getIntegerVal(param.get(MINUTE_KEY));
        hour = MathUtil.getIntegerVal(param.get(HOUR_KEY));

        if (isDayOfEachMonth == null || !isDayOfEachMonth) {
            Preconditions.checkState(param.containsKey(DAY_KEY), DAY_KEY + " not be null!");
        }

        String days = param.get(DAY_KEY).toString();
        if (StringUtils.isBlank(days)) {
            days = "L";
        }

        String cronStr = CRON_FORMAT.replace("${min}", minute + "").replace("${hour}", hour + "")
                .replace("${days}", days+"");

        setCronStr(cronStr);
        return cronStr;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {

        if(!checkSpecifyDayCanExe(specifyDate)){
            return Lists.newArrayList();
        }

        List<String> resultList = Lists.newArrayList();
        String triggerTime = specifyDate + " " + getTimeStr(hour) + ":" + getTimeStr(minute) + ":00";
        resultList.add(triggerTime);

        return resultList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        int day = MathUtil.getIntegerVal(specifyDate.substring(8,10));
        if (isDayOfEachMonth != null && isDayOfEachMonth) {
            // 说明是每个月的最后一天
            LocalDate lastDay = LocalDate.parse(specifyDate).with(TemporalAdjusters.lastDayOfMonth());
            int dayOfMonth = lastDay.getDayOfMonth();

            if (dayOfMonth == day) {
                return true;
            }
        }

        String canExeDay = CronStrUtil.getDayStr(getCronStr());
        if(canExeDay == null){
            LOGGER.error("error cronStr:{}", getCronStr());
            return false;
        }

        boolean canExe = false;
        for(String tmpDay : canExeDay.split(",")){
            tmpDay = tmpDay.trim();
            if(tmpDay.startsWith("0")){
                tmpDay = tmpDay.substring(1);
            }

            if(tmpDay.equals(day + "")){
                return true;
            }
        }

        return canExe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScheduleCronMonthParser that = (ScheduleCronMonthParser) o;
        return hour == that.hour && minute == that.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hour, minute);
    }
}
