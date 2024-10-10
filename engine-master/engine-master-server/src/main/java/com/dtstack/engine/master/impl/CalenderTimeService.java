package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.enums.CandlerBatchTypeEnum;
import com.dtstack.engine.master.utils.TimeUtils;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskCalender;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ScheduleTaskCalenderDao;
import com.dtstack.schedule.common.enums.CalenderTimeFormat;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CalenderTimeService implements TimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalenderTimeService.class);

    @Autowired
    private ConsoleCalenderDao consoleCalenderDao;

    @Autowired
    private ScheduleTaskCalenderDao scheduleTaskCalenderDao;

    @Autowired
    private ConsoleCalenderTimeDao consoleCalenderTimeDao;


    @Override
    public List<String> listBetweenTime(Long calenderId, String expendTime, String startTime, String endTime,Integer candlerBatchType) {
        if (null == calenderId) {
            return new ArrayList<>();
        }
        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (null == consoleCalender){
            LOGGER.error("calenderId {} calender is empty", calenderId);
            return new ArrayList<>();
        }
        List<ConsoleCalenderTime> calenderTimes = getConsoleCalenderTimes(calenderId, startTime, endTime);
        if (CandlerBatchTypeEnum.MANY.getType().equals(candlerBatchType)){
            List<String> times = dealAndConvertExpandTimes(expendTime);
            List<String> betweenTimes = Lists.newArrayListWithExpectedSize(times.size());
            times.forEach(expandTime->{
                List<String> ts = calenderTimes.stream()
                        .map(consoleCalenderTime ->
                                CalenderTimeFormat.toTriggerTime(consoleCalenderTime.getCalenderTime(),
                                        consoleCalender.getCalenderTimeFormat(),
                                        expandTime))
                        .collect(Collectors.toList());
                betweenTimes.addAll(ts);
            });
            return betweenTimes;
        }else {
            return calenderTimes.stream()
                    .map(consoleCalenderTime ->
                            CalenderTimeFormat.toTriggerTime(consoleCalenderTime.getCalenderTime(),
                                    consoleCalender.getCalenderTimeFormat(),
                                    expendTime))
                    .collect(Collectors.toList());
        }

    }

    private List<ConsoleCalenderTime> getConsoleCalenderTimes(Long calenderId, String startTime, String endTime) {
        //截取末尾2位
        long startLong = Long.parseLong(startTime.substring(0, startTime.length() - 2));
        long endLong = Long.parseLong(endTime.substring(0, endTime.length() - 2));
        return consoleCalenderTimeDao.listAfterTime(calenderId, startLong, endLong);
    }


    private List<String> dealAndConvertExpandTimes(String expandTimes) {
        List<String> times = TimeUtils.parseTimes(expandTimes, "\n");
        if (CollectionUtils.isEmpty(times)){
            return Lists.newArrayList();
        }
        return times.stream().map((t)-> t.replace(":", "")).collect(Collectors.toList());
    }


    @Override
    public String getNearestTime(Long calenderId, String expendTime, String currentDateMillis,Integer candlerBatchType, boolean eq) {
        if (null == calenderId) {
            return "";
        }
        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (Objects.isNull(consoleCalender)) {
            LOGGER.error("calendar id {} calendar is empty", calenderId);
            return "";
        }
        ConsoleCalenderTime nearestTime = getConsoleCalenderTime(calenderId, currentDateMillis, eq, consoleCalender);
        if (Objects.isNull(nearestTime)) {
            return null;
        }
        if (CandlerBatchTypeEnum.MANY.getType().equals(candlerBatchType)){
            // 取最接近的过去第一个时间点(包含相等)
            List<String> times = dealAndConvertExpandTimes(expendTime);
            List<String> fullTimes = times.stream().map(time -> CalenderTimeFormat.toTriggerTime(nearestTime.getCalenderTime(),
                    consoleCalender.getCalenderTimeFormat(),
                    time)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for(String ft:fullTimes){
                if (ft.compareTo(currentDateMillis) <= 0){
                    return ft;
                }
            }
            // 这里不应该走到这
            LOGGER.warn("calendarId {} expendTime {}  currentDateMillis {} candlerBatchType {} eq {}",calenderId,expendTime,currentDateMillis,candlerBatchType,eq);
            return null;
        }else {
            return CalenderTimeFormat.toTriggerTime(nearestTime.getCalenderTime(),
                    consoleCalender.getCalenderTimeFormat(),
                    expendTime);
        }

    }

    private ConsoleCalenderTime getConsoleCalenderTime(Long calenderId, String currentDateMillis, boolean eq, ConsoleCalender consoleCalender) {
        long currentLong = getCurrentTimeFormatted(currentDateMillis, consoleCalender);
        return consoleCalenderTimeDao.getNearestTime(calenderId, currentLong, eq);
    }


    @Override
    public String getNearestOffset(Long calenderId, String expendTime, Integer customOffset, String currentDateMillis,Integer candlerBatchType, boolean eq) {
        if (null == calenderId) {
            return "";
        }

        ConsoleCalender consoleCalender = consoleCalenderDao.selectById(calenderId);
        if (Objects.isNull(consoleCalender)) {
            LOGGER.error("calendar id {} calendar is empty", calenderId);
            return "";
        }
        long currentLong = getCurrentTimeFormatted(currentDateMillis, consoleCalender);


        // 处理偏移量
        boolean symbol = true;
        if (customOffset < 0) {
            symbol = false;
            customOffset = Math.abs(customOffset);
        }

        if (customOffset == 0) {
            customOffset ++;
        }

        if (CandlerBatchTypeEnum.MANY.getType().equals(candlerBatchType)){
            // 一天有多批次的时候，偏移量不再以天为单位,这里为了避免边界问题，直接取出过去一段时间的所有时间点
            List<String> times = dealAndConvertExpandTimes(expendTime);
            // 偏移量是批次的
            Integer offset = customOffset / times.size();
            if (customOffset % times.size() > 0){
                offset++;
            }
            List<ConsoleCalenderTime> nearestTime = consoleCalenderTimeDao.getNearestOffset(calenderId, currentLong,eq,symbol,offset);
            if (CollectionUtils.isEmpty(nearestTime)) {
                return null;
            }

            List<String> fullTimes = Lists.newArrayList();
            List<String> finalFullTimes;
            times.forEach(t -> {
                nearestTime.forEach(fnt->{
                    String ft = CalenderTimeFormat.toTriggerTime(fnt.getCalenderTime(),
                            consoleCalender.getCalenderTimeFormat(),
                            t);
                    fullTimes.add(ft);
                });
            });
            String currentStr = String.valueOf(currentDateMillis);
            if (symbol) {
                finalFullTimes = fullTimes.stream().filter(ft->ft.compareTo(currentStr) >= 0).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            } else {
                finalFullTimes =  fullTimes.stream().filter(ft->ft.compareTo(currentStr) <= 0).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            }
            //  偏移量过大，则取最端的时间
            if (customOffset > finalFullTimes.size()){
                customOffset = finalFullTimes.size();
            }
            return finalFullTimes.get(customOffset-1);

        }else {
            List<ConsoleCalenderTime> nearestTime = consoleCalenderTimeDao.getNearestOffset(calenderId, currentLong,eq,symbol,customOffset);
            if (CollectionUtils.isEmpty(nearestTime)) {
                return null;
            }
            ConsoleCalenderTime  consoleCalenderTime = nearestTime.get(nearestTime.size() - 1);
            return CalenderTimeFormat.toTriggerTime(consoleCalenderTime.getCalenderTime(),
                    consoleCalender.getCalenderTimeFormat(),
                    expendTime);
        }
    }

    private long getCurrentTimeFormatted(String currentDateMillis, ConsoleCalender consoleCalender) {
        long currentLong;
        // 处理年月日调度
        if (CalenderTimeFormat.YEAR_MONTH_DAY.getFormat().equals(consoleCalender.getCalenderTimeFormat())) {
            currentLong = Long.parseLong(CalenderTimeFormat.formatCalenderTime(currentDateMillis.substring(0,
                    CalenderTimeFormat.YEAR_MONTH_DAY.getFormat().length()), consoleCalender.getCalenderTimeFormat()));
        } else {
            currentLong = Long.parseLong(currentDateMillis.substring(0, currentDateMillis.length() - 2));
        }
        return currentLong;
    }

    @Override
    public ScheduleTaskCalender getScheduleTaskCalender(Long taskId, Integer appType) {
        ScheduleTaskCalender calender = scheduleTaskCalenderDao.findByTaskId(taskId, appType);
        if (null == calender) {
            LOGGER.error("taskId {} appType {} calender is empty", taskId, appType);
            return null;
        }
        return calender;
    }


}
