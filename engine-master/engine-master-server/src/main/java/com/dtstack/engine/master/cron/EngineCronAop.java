package com.dtstack.engine.master.cron;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

/**
 * 定时任务切面，接管加了注解 {@link EngineCron} 的定时任务，
 * 以抢占式方式运行
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-09 20:06
 */
@Aspect
@Order(1)
@Configuration
public class EngineCronAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngineCronAop.class);

    /**
     * 分布式锁级别，默认分钟级。一方面定时任务的粒度最小是分钟级，
     * 另一方面当 A、B 机器存在秒级时间差时，能够保证只有一台获取到锁。
     */
    @Value("${cron.lock.level:yyyyMMddHHmm}")
    private String cronLockLevel;

    @Value("${cron.lock.duration.in.seconds:60}")
    private Integer cronLockDurationInSeconds;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    private void engineCronMethod() {
    }

    /**
     * 符合定时器+自定义注解，则进行拦截
     */
    @Async("cronExecutor")
    @Around("engineCronMethod() && @annotation(engineCron)")
    public void doAround(ProceedingJoinPoint point, EngineCron engineCron) throws Throwable {
        // 1. 构造锁键
        String now = DateTime.now().toString(cronLockLevel);
        MethodSignature signature = (MethodSignature) point.getSignature();
        String namespace = String.format("%s/%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        String lockName = String.format("%s/%s", namespace, now);

        // 2. 获取锁
        boolean tryLock = true;
        try {
            // 利用 try...catch 容错，防止加锁出现异常，阻断后续流程
            String ok = redisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                SetParams setParams = SetParams.setParams();
                setParams.nx().ex(cronLockDurationInSeconds);
                // 如果写入成功返回 "OK"，失败则返回空
                return commands.set(lockName, StringUtils.EMPTY, setParams);
            });
            tryLock = StringUtils.isNotBlank(ok);
        } catch (Exception e) {
            LOGGER.error("{} get lock error", lockName, e);
        }
        if (!tryLock) {
            LOGGER.info("{} not get lock, do skip", lockName);
            return;
        }
        LOGGER.info("{} get lock, do start", lockName);

        // 3. 执行
        try {
            point.proceed();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("{} do over", lockName);
    }
}