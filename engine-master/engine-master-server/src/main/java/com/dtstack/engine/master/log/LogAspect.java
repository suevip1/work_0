package com.dtstack.engine.master.log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author yuebai
 * @date 2021-01-28
 */

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private static final ArrayList<String> filterMethod = Lists.newArrayList("getEngineMessageByHttp", "getEngineLog",
            "getRollingLogBaseInfo", "clusterResource", "getDefaultPluginConfig", "containerInfos", "judgeSlots","clearCheckpoint", "getCheckpoints","getThriftServerUrl");
    private static final ArrayList<String> logPluginInfoMethod = Lists.newArrayList("submitJob");
    private static final ArrayList<String> skipChangeMethod = Lists.newArrayList("getJobStatus");

    private static final PropertyFilter propertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("pluginInfo") || name.equalsIgnoreCase("paramAction"));


    private static final PropertyFilter submitPropertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("taskParams") || name.equalsIgnoreCase("paramAction"));

    @AfterReturning(pointcut = "execution(public * com.dtstack.engine.master.worker.EnginePluginsOperator.*(..))", returning = "ret")
    public void afterReturningAdvice(JoinPoint joinPoint, Object ret) {
        try {
            String methodName = joinPoint.getSignature().getName();
            if (filterMethod.contains(methodName)) {
                return;
            }
            String argsString = null;
            Object[] args = joinPoint.getArgs();
            Optional<Object> jobClientOpt = Arrays.stream(args)
                    .filter(a -> a instanceof JobClient)
                    .findFirst();
            if (jobClientOpt.isPresent()) {
                if (logPluginInfoMethod.contains(methodName)) {
                    argsString = JSONObject.toJSONString(jobClientOpt.get(),submitPropertyFilter);
                } else {
                    //忽略pluginInfo打印
                    argsString = JSONObject.toJSONString(jobClientOpt.get(), propertyFilter);
                }
            } else {
                if (skipChangeMethod.contains(methodName)) {
                    if (ret instanceof RdosTaskStatus && (RdosTaskStatus.RUNNING.equals(ret)|| RdosTaskStatus.SCHEDULED.equals(ret))) {
                        //状态获取 多以running 为主 过滤频繁打印
                        return;
                    }
                } else {
                    argsString = JSONObject.toJSONString(args);
                }
            }

            if (logger.isInfoEnabled()) {
                JSONObject logInfo = new JSONObject(3);
                logInfo.put("method", joinPoint.getSignature().getDeclaringTypeName() + "." + methodName);
                logInfo.put("args", argsString);
                logInfo.put("return", JSONObject.toJSONString(ret));
                logger.info(logInfo.toJSONString());
            }
        } catch (Exception e) {
            logger.error("logAspect error ", e);
        }
    }

}