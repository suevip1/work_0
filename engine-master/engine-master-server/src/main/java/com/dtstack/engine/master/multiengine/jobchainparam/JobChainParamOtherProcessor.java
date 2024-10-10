package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleJobExpandDao;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.master.worker.EnginePluginsOperator;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.dtstack.schedule.common.enums.EProcessedLevelType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-04 17:11
 */
@Component
public class JobChainParamOtherProcessor implements IJobChainParamProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamOtherProcessor.class);

    private static final String SHELL_AGENT_KEY = "shell-agent";

    @Autowired
    private ScheduleJobExpandDao scheduleJobExpandDao;

    @Autowired
    private EnginePluginsOperator enginePluginsOperator;

    @Override
    public String parseUpstreamParam(ScheduleTaskChainParamDTO sourceParamDTO, EProcessedLevelType levelType, String inputParamName, List<Integer> paramIndexes) {
        Integer fatherTaskType = sourceParamDTO.getFatherTaskType();
        EScheduleJobType eFatherTaskType = EScheduleJobType.getEJobType(fatherTaskType);
        Integer outputParamType = sourceParamDTO.getOutputParamType();
        ScheduleJob fatherJob = sourceParamDTO.getFatherJob();

        switch (eFatherTaskType) {
            case SHELL_ON_AGENT:
                if (EOutputParamType.CONS.getType().equals(outputParamType) || EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    if (levelType == EProcessedLevelType.ZERO) {
                        // 只解析${abc}，如果出现 ${abc}[0][1]、${abc}[0]，视为异常(后续可以直接替换常量，形如 6[0][1])
                        return sourceParamDTO.getFatherParamValue();
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    if (levelType == EProcessedLevelType.ZERO) {
                        // 解析 ${a}
                        return getForShellOnAgentScript(fatherJob, sourceParamDTO.getFatherParamValue());
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else {
                    throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",fatherTaskType" + fatherTaskType);
                }
            case CONDITION_BRANCH:
                if (EOutputParamType.CONS.getType().equals(outputParamType) || EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    if (levelType == EProcessedLevelType.ZERO) {
                        // 只解析${abc}，如果出现 ${abc}[0][1]、${abc}[0]，视为异常(后续可以直接替换常量，形如 6[0][1])
                        return sourceParamDTO.getFatherParamValue();
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else {
                    // 条件分支作为上游，只支持常量和自定义参数
                    throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",fatherTaskType" + fatherTaskType);
                }
            default:
                throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",fatherTaskType" + fatherTaskType);
        }
    }

    @Override
    public JobChainParamOutputResult dealOutputParam(Integer outputParamTaskType, String outputParamName, Integer outputParamType, String paramCommand, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, Map<String, String> paramName2Value, ScheduleEngineProject project, List<ScheduleTaskParamShade> taskParamsToReplace) {
        // 任务输出参数值，这个值要落库
        String paramValue = null;
        // 被解析后的任务输出参数，目前用于计算型 rdb 参数，这个值要落库
        String parsedParamCommand = null;
        String scriptFragment = null;

        EScheduleJobType eJobType = EScheduleJobType.getEJobType(outputParamTaskType);
        if (eJobType == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        switch (eJobType) {
            case SHELL_ON_AGENT:
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    // 最终路径
                    // 文件名拼接规则:output_${参数名称}_${任务id}_${计划时间}
                    paramValue = JobChainParamHandler.generateFilePath(JobChainParamHandler.getRootOutputPath(), outputParamName, scheduleJob, project);
                    // 将结果追加到文件
                    scriptFragment = JobChainParamScripter.generateShellOnAgentScript(paramCommand, paramValue);
                } else {
                    throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
                }
                break;
            case CONDITION_BRANCH:
                // 条件分支任务输出参数不支持计算结果类型
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else {
                    throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
                }
                break;
            default:
                throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
        }
        ScheduleJobChainOutputParam jobOutputParam = generateJobChainOutputParam(scheduleJob, taskShade.getTaskId(), outputParamName, outputParamType, paramCommand, paramValue, parsedParamCommand);

        JobChainParamOutputResult outputResult = new JobChainParamOutputResult();
        outputResult.setScheduleJobChainOutputParam(jobOutputParam);
        Optional.ofNullable(scriptFragment).ifPresent(outputResult::setScriptFragment);
        return outputResult;
    }

    private String getForShellOnAgentScript(ScheduleJob job, String paramName) {
        if (job == null || StringUtils.isEmpty(paramName)) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }
        if (!job.getStatus().equals(RdosTaskStatus.FINISHED.getStatus())) {
            throw new RdosDefineException("任务状态非完成,任务名称:" + job.getJobName());
        }

        String engineLog;
        // 先查询 DB，查不到再利用插件查询
        ScheduleJobExpand scheduleJobExpand = scheduleJobExpandDao.getLogByJobId(job.getJobId());
        if (scheduleJobExpand != null && StringUtils.isNotEmpty(scheduleJobExpand.getEngineLog())) {
            engineLog = scheduleJobExpand.getEngineLog();
        } else {
            JobIdentifier jobIdentifier = new JobIdentifier(job.getEngineJobId(), job.getApplicationId(), job.getJobId()
                    , job.getDtuicTenantId(), ScheduleEngineType.DTSCRIPT_AGENT.getEngineName(), EDeployMode.PERJOB.getType(), null, job.getResourceId(), null, null);
            jobIdentifier.setProjectId(job.getProjectId());
            jobIdentifier.setAppType(job.getAppType());
            jobIdentifier.setTaskType(job.getTaskType());

            engineLog = enginePluginsOperator.getEngineLog(jobIdentifier);
        }

        String notFoundMsg = "can't find paramValue from agent, jobId:" + job.getJobId() + ", paramName:" + paramName;
        if (StringUtils.isEmpty(engineLog)) {
            throw new RdosDefineException(notFoundMsg);
        }

        String content;
        try {
            JSONObject engineLogObj = JSONObject.parseObject(engineLog);
            content = engineLogObj.getString(SHELL_AGENT_KEY);
        } catch (Exception e) {
            throw new RdosDefineException("parse shell agent resp error, jobId:" + job.getJobId() + ", paramName:" + paramName);
        }
        if (StringUtils.isEmpty(content)) {
            throw new RdosDefineException(notFoundMsg);
        }

        String[] lines = content.split(GlobalConst.LINE_SEPARATOR);
        for (String line : lines) {
            if (StringUtils.isEmpty(line) || !StringUtils.contains(line, GlobalConst.STAR)) {
                continue;
            }
            String[] pair = line.split(GlobalConst.STAR);
            // v-k
            if (pair.length == 2 && paramName.equals(pair[1])) {
                // Gotcha
                return pair[0];
            }
        }
        // if not found, should throw error
        throw new RdosDefineException(notFoundMsg);
    }
}
