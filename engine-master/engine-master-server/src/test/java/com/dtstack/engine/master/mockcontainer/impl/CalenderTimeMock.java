package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.dao.ConsoleCalenderDao;
import com.dtstack.engine.dao.ConsoleCalenderTimeDao;
import com.dtstack.engine.dao.ScheduleTaskCalenderDao;
import com.dtstack.engine.po.ConsoleCalender;
import com.dtstack.engine.po.ConsoleCalenderTime;
import com.dtstack.engine.po.ScheduleTaskCalender;
import org.assertj.core.util.Lists;

import java.util.List;

public class CalenderTimeMock {
    @MockInvoke(targetClass = ScheduleTaskCalenderDao.class)
    ScheduleTaskCalender findByTaskId(Long taskId, int appType) {
        ScheduleTaskCalender scheduleTaskCalender = new ScheduleTaskCalender();
        scheduleTaskCalender.setCalenderId(1L);
        scheduleTaskCalender.setAppType(1);
        return scheduleTaskCalender;
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    List<ConsoleCalenderTime> listAfterTime(Long calenderId, Long startTime, Long endTime) {
        ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
        consoleCalenderTime.setCalenderTime("2022070212000");
        consoleCalenderTime.setCalenderId(1l);
        return Lists.newArrayList(consoleCalenderTime);
    }

    @MockInvoke(targetClass = ConsoleCalenderTimeDao.class)
    ConsoleCalenderTime getNearestTime(Long calenderId, Long currentTime, boolean canEq) {
        ConsoleCalenderTime consoleCalenderTime = new ConsoleCalenderTime();
        consoleCalenderTime.setCalenderTime("2022070212000");
        consoleCalenderTime.setCalenderId(1l);
        return consoleCalenderTime;
    }

    @MockInvoke(targetClass = ConsoleCalenderDao.class)
    public ConsoleCalender selectById(Long calenderId) {
        ConsoleCalender consoleCalender = new ConsoleCalender();
        consoleCalender.setCalenderName("121");
        consoleCalender.setCalenderTimeFormat("yyyyMMddHHmm");
        consoleCalender.setLatestCalenderTime("202203020807");
        consoleCalender.setCalenderFileName("calendar.csv");
        consoleCalender.setUseType(2);
        return consoleCalender;
    }


}
