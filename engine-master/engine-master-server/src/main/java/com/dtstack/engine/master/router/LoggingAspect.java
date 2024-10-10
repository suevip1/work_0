package com.dtstack.engine.master.router;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * 拦截所有controller包下的方法
     */
    @Pointcut("execution(* com.dtstack.engine.master.controller.*Controller.*(..))")
    private void controllerMethod() {
    }

    @Around("controllerMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
            result = joinPoint.proceed();
        } catch (Exception e) {
            //如果有异常继续抛
            throw e;
        } finally {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            long handleTime = System.currentTimeMillis() - startTime;
            String url = req.getRequestURI();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("url:{} handleTime:{}", url, handleTime);
            }
        }
        return result;
    }
}
