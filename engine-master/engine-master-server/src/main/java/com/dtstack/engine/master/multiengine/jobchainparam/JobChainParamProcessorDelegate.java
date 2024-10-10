package com.dtstack.engine.master.multiengine.jobchainparam;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.schedule.common.enums.EProcessedLevelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-04 19:47
 */
@Component
public class JobChainParamProcessorDelegate implements IJobChainParamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamProcessorDelegate.class);

    @Resource
    private JobChainParamWorkerProcessor jobChainParamWorkerProcessor;

    @Resource
    private JobChainParamRdbProcessor jobChainParamRdbProcessor;

    @Resource
    private JobChainParamOtherProcessor jobChainParamOtherProcessor;

    /**
     * 解析上游参数 inputParamName
     *
     * @param sourceParamDTO 输入参数源
     * @param levelType      输入参数类型
     * @param inputParamName 输入参数名称
     * @param paramIndexes   输入参数索引
     * @return 上游参数被解析后的值
     */
    @Override
    public String parseUpstreamParam(ScheduleTaskChainParamDTO sourceParamDTO, EProcessedLevelType levelType, String inputParamName, List<Integer> paramIndexes) {
        Integer fatherTaskType = sourceParamDTO.getFatherTaskType();
        if (JobChainParamHandler.supportWorkerTaskTypes().contains(fatherTaskType)) {
            return jobChainParamWorkerProcessor.parseUpstreamParam(sourceParamDTO, levelType, inputParamName, paramIndexes);
        } else if (JobChainParamHandler.supportRdbTaskTypes().contains(fatherTaskType)) {
            return jobChainParamRdbProcessor.parseUpstreamParam(sourceParamDTO, levelType, inputParamName, paramIndexes);
        } else if (JobChainParamHandler.supportOtherTaskTypes().contains(fatherTaskType)) {
            return jobChainParamOtherProcessor.parseUpstreamParam(sourceParamDTO, levelType, inputParamName, paramIndexes);
        } else {
            throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + sourceParamDTO.getOutputParamType() + ",fatherTaskType" + fatherTaskType);
        }
    }

    /**
     * 处理输出参数
     *
     * @param outputParamTaskType
     * @param outputParamName
     * @param outputParamType
     * @param outputParamCommand
     * @param taskShade           当前任务
     * @param scheduleJob         当前任务实例
     * @param paramName2Value     自定义参数，用于替换
     * @param project 当前项目
     * @param taskParamsToReplace 当前替换参数
     */
    @Override
    public JobChainParamOutputResult dealOutputParam(Integer outputParamTaskType, String outputParamName, Integer outputParamType, String outputParamCommand, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, Map<String, String> paramName2Value, ScheduleEngineProject project, List<ScheduleTaskParamShade> taskParamsToReplace) {
        if (JobChainParamHandler.supportWorkerTaskTypes().contains(outputParamTaskType)) {
            return jobChainParamWorkerProcessor.dealOutputParam(outputParamTaskType, outputParamName, outputParamType, outputParamCommand, taskShade, scheduleJob, paramName2Value, project, taskParamsToReplace);
        } else if (JobChainParamHandler.supportRdbTaskTypes().contains(outputParamTaskType)) {
            return jobChainParamRdbProcessor.dealOutputParam(outputParamTaskType, outputParamName, outputParamType, outputParamCommand, taskShade, scheduleJob, paramName2Value, project, taskParamsToReplace);
        } else if (JobChainParamHandler.supportOtherTaskTypes().contains(outputParamTaskType)) {
            return jobChainParamOtherProcessor.dealOutputParam(outputParamTaskType, outputParamName, outputParamType, outputParamCommand, taskShade, scheduleJob, paramName2Value, project, taskParamsToReplace);
        } else {
            throw new RdosDefineException("handleChainOutputParam error, not support outputTaskType!");
        }
    }
}