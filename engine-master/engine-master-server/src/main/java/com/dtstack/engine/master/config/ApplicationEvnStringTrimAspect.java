package com.dtstack.engine.master.config;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.AopTargetUtils;
import com.dtstack.engine.master.faced.base.DtStackSdk;
import com.dtstack.engine.master.strategy.AbstractStrategy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2023/6/2 4:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
@Aspect
public class ApplicationEvnStringTrimAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategy.class);

    @Pointcut(value = "execution(String com.dtstack.engine.common.env.EnvironmentContext.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object trim(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed();

        if (proceed instanceof String) {
            try {
                return ((String)proceed).trim();
            } catch (Exception e) {
                LOGGER.error("",e);
            }
        }

        return proceed;
    }
}
