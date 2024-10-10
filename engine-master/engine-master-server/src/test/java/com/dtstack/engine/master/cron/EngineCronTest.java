package com.dtstack.engine.master.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-09 21:03
 */
@Component
public class EngineCronTest {
    /**
     * 若存在 @EngineCron，则以抢占式方式运行
     */
    @EngineCron
    @Scheduled(cron = "0/15 * * * * ? ")
    public void testMethodA() {
        System.out.println(Thread.currentThread().getName() + ":testMethodA");
    }

    /**
     * 普通的定时任务，由 Spring 以正常方式运行
     */
    @Scheduled(cron = "0/30 * * * * ? ")
    public void testMethodB() {
        System.out.println(Thread.currentThread().getName() + ":testMethodB");
    }
}
