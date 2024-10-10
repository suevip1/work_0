package com.dtstack.engine.master.mockcontainer.scheduler.parser;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.impl.TimeService;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-28 16:39
 */
public class ScheduleCronCalenderParserMock {

    @MockInvoke(targetClass = TimeService.class)
    String getNearestTime(Long taskId, Integer appType, String currentDateMillis,boolean eq) {
        return "202207280000";
    }

    @MockInvoke(targetClass = TimeService.class)
    List<String> listBetweenTime(Long taskId, Integer appType, String startTime, String endTime) {
        return Lists.newArrayList("20220728000000");
    }

}
