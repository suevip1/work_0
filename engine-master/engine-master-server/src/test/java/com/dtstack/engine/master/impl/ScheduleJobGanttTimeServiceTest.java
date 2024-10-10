package com.dtstack.engine.master.impl;

import com.dtstack.engine.master.diagnosis.enums.JobGanttChartEnum;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-03-08 17:07
 */
public class ScheduleJobGanttTimeServiceTest {
    @Test
    public void test() {
        List<String> caches = Arrays.stream(JobGanttChartEnum.values())
                .map(g -> ScheduleJobGanttTimeService.generateGanttCacheKey("hello", g))
                .collect(Collectors.toList());
        System.out.println(caches);
    }
}
