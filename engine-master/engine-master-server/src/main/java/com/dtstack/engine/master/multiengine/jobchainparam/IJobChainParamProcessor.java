package com.dtstack.engine.master.multiengine.jobchainparam;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EProcessedLevelType;

import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-04 16:47
 */
public interface IJobChainParamProcessor {
    /**
     * 解析上游参数 inputParamName
     * @param sourceParamDTO 输入参数源
     * @param levelType 输入参数类型
     * @param inputParamName 输入参数名称
     * @param paramIndexes 输入参数索引
     * @return 上游参数被解析后的值
     */
    String parseUpstreamParam(ScheduleTaskChainParamDTO sourceParamDTO, EProcessedLevelType levelType,
                              String inputParamName, List<Integer> paramIndexes);

    /**
     * 处理输出参数
     * @param outputParamTaskType
     * @param outputParamName
     * @param outputParamType
     * @param outputParamCommand
     * @param taskShade 当前任务
     * @param scheduleJob 当前任务实例
     * @param paramName2Value 自定义参数，用于替换
     * @param project 当前项目
     * @param taskParamsToReplace 当前替换参数
     * @return
     */
    JobChainParamOutputResult dealOutputParam(Integer outputParamTaskType,
                                              String outputParamName,
                                              Integer outputParamType,
                                              String outputParamCommand,
                                              ScheduleTaskShade taskShade,
                                              ScheduleJob scheduleJob,
                                              Map<String, String> paramName2Value,
                                              ScheduleEngineProject project,
                                              List<ScheduleTaskParamShade> taskParamsToReplace);

    default ScheduleJobChainOutputParam generateJobChainOutputParam(ScheduleJob scheduleJob,
                                                                   Long taskId,
                                                                   String paramName,
                                                                   Integer outputParamType,
                                                                   String paramCommand,
                                                                   String paramValue,
                                                                   String parsedParamCommand) {
        ScheduleJobChainOutputParam jobOutputParam = new ScheduleJobChainOutputParam();
        jobOutputParam.setJobId(scheduleJob.getJobId());
        jobOutputParam.setJobType(scheduleJob.getType());
        jobOutputParam.setTaskId(taskId);
        jobOutputParam.setAppType(scheduleJob.getAppType());
        jobOutputParam.setTaskType(scheduleJob.getTaskType());
        jobOutputParam.setParamName(paramName);
        jobOutputParam.setOutputParamType(outputParamType);
        jobOutputParam.setParamCommand(paramCommand);
        jobOutputParam.setParamValue(paramValue);
        jobOutputParam.setParsedParamCommand(parsedParamCommand);
        return jobOutputParam;
    }
}