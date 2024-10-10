package com.dtstack.engine.master.cron;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 定时任务自定义注解，如果存在该注解，定时任务则以抢占式方式运行
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-09 20:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EngineCron {
}
