package com.dtstack.engine.master.utils;

import java.util.Date;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.cronutils.model.CronType.QUARTZ;
/**
 * @Auther: dazhi
 * @Date: 2023-10-16 17:27
 * @Email: dazhi@dtstack.com
 * @Description: CronUtils
 */
public class CronUtils {

    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
    /**
     * 获得距离date的上一次执行最近的时间
     * @param date 时间
     * @return 上一次执行最近的时间
     */
    public static Date last(String cronExpression,Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cronExpression);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        ZonedDateTime zonedDateTime = executionTime.lastExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);

        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获得距离date的下一次执行最近的时间
     *
     * @param date 时间
     * @return 下一次执行最近的时间
     */
    public static Date next(String cronExpression,Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cronExpression);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        ZonedDateTime zonedDateTime = executionTime.nextExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);

        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 判断当前时间是否是执行时间
     * @param date 当前时间
     */
    public static Boolean isMatch(String cronExpression,Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cronExpression);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        return executionTime.isMatch(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

}
