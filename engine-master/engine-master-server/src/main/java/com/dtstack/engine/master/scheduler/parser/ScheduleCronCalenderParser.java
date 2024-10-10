package com.dtstack.engine.master.scheduler.parser;

import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.TimeService;
import com.dtstack.engine.po.ScheduleTaskCalender;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScheduleCronCalenderParser extends ScheduleCron {

    private final TimeService timeService;

    private Long calenderId;

    private String expendTime;
    private Integer candlerBatchType;

    public ScheduleCronCalenderParser(TimeService timeService, Long taskId, Integer appType) {
        this.timeService = timeService;
        ScheduleTaskCalender taskCalender = timeService.getScheduleTaskCalender(taskId, appType);
        if (Objects.nonNull(taskCalender)) {
            this.calenderId = taskCalender.getCalenderId();
            this.expendTime = taskCalender.getExpandTime();
            this.candlerBatchType = taskCalender.getCandlerBatchType();
        }
    }

    public ScheduleCronCalenderParser(TimeService timeService, Long calenderId, String expendTime) {
        this.timeService = timeService;
        this.calenderId = calenderId;
        this.expendTime = expendTime;
    }
    public ScheduleCronCalenderParser(TimeService timeService, Long calenderId, String expendTime,Integer candlerBatchType) {
        this.timeService = timeService;
        this.calenderId = calenderId;
        this.expendTime = expendTime;
        this.candlerBatchType = candlerBatchType;
    }

    @Override
    public String parse(Map<String, Object> param) {
        return "";
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        LocalDate startLocalDate = DateUtil.parseDate(specifyDate, DateUtil.DATE_FORMAT).toInstant().atZone(DateUtil.DEFAULT_ZONE).toLocalDate();
        Date startDate = Date.from(startLocalDate.atStartOfDay(DateUtil.DEFAULT_ZONE).toInstant());
        String startFormattedDate = DateUtil.getFormattedDate(startDate.getTime(), DateUtil.UN_STANDARD_DATETIME_FORMAT);

        LocalDate endLocalDate = startLocalDate.plusDays(1);
        Date endDate = Date.from(endLocalDate.atStartOfDay(DateUtil.DEFAULT_ZONE).toInstant());
        String endFormattedDate = DateUtil.getFormattedDate(endDate.getTime(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
        return timeService.listBetweenTime(calenderId, expendTime, startFormattedDate, endFormattedDate, candlerBatchType);
    }

    public String getNearestTime(DateTime currentDate, boolean eq) {
        String currentDateFormat = DateUtil.getFormattedDate(currentDate.getMillis(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
        return timeService.getNearestTime(calenderId, expendTime, currentDateFormat,candlerBatchType, eq);
    }

    public List<String> ListBetweenTime(String startTime, String endTime) {
        return timeService.listBetweenTime(calenderId, expendTime, startTime, endTime,candlerBatchType);
    }

    public String getNearestOffset(DateTime currentDate, Integer customOffset, boolean eq) {
        String currentDateFormat = DateUtil.getFormattedDate(currentDate.getMillis(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
        return timeService.getNearestOffset(calenderId, expendTime,customOffset, currentDateFormat,candlerBatchType, eq);
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) throws ParseException {
        return false;
    }

    @Override
    public String toString() {
        return "ScheduleCronCalenderParser{" +
                "calenderId=" + calenderId +
                ", expendTime=" + expendTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScheduleCronCalenderParser that = (ScheduleCronCalenderParser) o;
        return Objects.equals(calenderId, that.calenderId) && Objects.equals(expendTime, that.expendTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), calenderId, expendTime);
    }


}
