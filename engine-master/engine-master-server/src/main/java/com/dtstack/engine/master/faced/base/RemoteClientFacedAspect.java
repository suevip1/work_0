package com.dtstack.engine.master.faced.base;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.AopTargetUtils;
import com.dtstack.engine.master.strategy.AbstractStrategy;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author leon
 * @date 2022-10-09 19:21
 **/
@Component
@Aspect
public class RemoteClientFacedAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategy.class);

    @Pointcut(value = "execution(* com.dtstack.engine.master.faced.sdk..*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?> targetClass = AopTargetUtils.getTarget(joinPoint.getTarget()).getClass();
        if (targetClass.isAnnotationPresent(DtStackSdk.class)) {
            DtStackSdk annotation = joinPoint.getTarget().getClass().getAnnotation(DtStackSdk.class);
            String sdkName = annotation.sdkName();
            String clientName = annotation.clientName();
            LOGGER.info("invoke sdk name: [{}], client name: [{}], method: [{}], params: {}", sdkName, clientName, methodName, JSONObject.toJSONString(args));
            Object result = joinPoint.proceed();
            LOGGER.info("invoke sdk name: [{}], client name: [{}], method: [{}], result: {}", sdkName, clientName, methodName, JSONObject.toJSONString(result));
            return result;
        }
        return joinPoint.proceed();
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void handleException(JoinPoint joinPoint, Throwable exception) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?> targetClass = AopTargetUtils.getTarget(joinPoint.getTarget()).getClass();

        if (targetClass.isAnnotationPresent(DtStackSdk.class)) {
            DtStackSdk annotation = joinPoint.getTarget().getClass().getAnnotation(DtStackSdk.class);
            String sdkName = annotation.sdkName();
            String clientName = annotation.clientName();

            LOGGER.error("invoke sdk name: [{}], client name: [{}], method: [{}],  params: {}, exception: {}",
                    sdkName, clientName, methodName,
                    JSONObject.toJSONString(args),
                    exception.getMessage(), exception);

            throw exception;
        }

        throw exception;
    }
}
