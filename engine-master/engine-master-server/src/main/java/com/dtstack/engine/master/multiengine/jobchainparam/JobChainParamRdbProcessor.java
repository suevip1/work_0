package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.dtstack.schedule.common.enums.EProcessedLevelType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-04 16:56
 */
@Component
public class JobChainParamRdbProcessor implements IJobChainParamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamRdbProcessor.class);

    /**
     * 是否为查询语句
     */
    private static final Pattern pattern = Pattern.compile("^select");

    @Autowired
    private JobParamReplace jobParamReplace;

    /**
     * 解析上游参数
     * @param sourceParamDTO 输入参数源
     * @param levelType 输入参数类型
     * @param inputParamName 输入参数名称
     * @param paramIndexes 输入参数索引
     * @return
     */
    @Override
    public String parseUpstreamParam(ScheduleTaskChainParamDTO sourceParamDTO, EProcessedLevelType levelType, String inputParamName, List<Integer> paramIndexes) {
        Integer fatherTaskType = sourceParamDTO.getFatherTaskType();
        Integer outputParamType = sourceParamDTO.getOutputParamType();
        String rdbValue;
        Integer rowIndex;
        Integer colIndex;

        if (EOutputParamType.CONS.getType().equals(outputParamType) || EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
            if (levelType == EProcessedLevelType.ZERO) {
                // 只解析${abc}，如果出现 ${abc}[0][1]、${abc}[0]，视为异常(后续可以直接替换常量，形如 6[0][1])
                return sourceParamDTO.getFatherParamValue();
            } else {
                throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
            }
        } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
            if (levelType == EProcessedLevelType.SECOND) {
                // 调用接口获取 ${}[][] 的值
                rdbValue = sourceParamDTO.getFatherParamValue();
                rowIndex = paramIndexes.get(0);
                colIndex = paramIndexes.get(1);
                return parseRdbValue(rdbValue, rowIndex, colIndex);
            } else if (levelType == EProcessedLevelType.FIRST) {
                rdbValue = sourceParamDTO.getFatherParamValue();
                // 第几列，解析 ${}[]
                colIndex = paramIndexes.get(0);
                return parseRdbValue(rdbValue, null, colIndex);
            } else if (levelType == EProcessedLevelType.ZERO) {
                rdbValue = sourceParamDTO.getFatherParamValue();
                return parseRdbValue(rdbValue, 0, 0);
            } else {
                throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
            }
        } else {
            throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",fatherTaskType" + fatherTaskType);
        }
    }

    @Override
    public JobChainParamOutputResult dealOutputParam(Integer outputParamTaskType, String outputParamName, Integer outputParamType, String paramCommand, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, Map<String, String> paramName2Value, ScheduleEngineProject project, List<ScheduleTaskParamShade> taskParamsToReplace) {
        // 任务输出参数值，这个值要落库
        String paramValue = null;
        // 被解析后的任务输出参数，目前用于计算型 rdb 参数，这个值要落库
        String parsedParamCommand = null;

        if (EOutputParamType.CONS.getType().equals(outputParamType)) {
            paramValue = paramCommand;
        } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
            // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
            paramValue = paramName2Value.get(paramCommand);
        } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
            if (!isSelectSql(paramCommand)) {
                throw new RdosDefineException("handleChainOutputParam error, sql invalid, please check again:" + paramCommand);
            }
            parsedParamCommand = jobParamReplace.paramReplace(paramCommand, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        } else {
            throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
        }
        ScheduleJobChainOutputParam jobOutputParam = generateJobChainOutputParam(scheduleJob, taskShade.getTaskId(), outputParamName, outputParamType, paramCommand, paramValue, parsedParamCommand);

        JobChainParamOutputResult outputResult = new JobChainParamOutputResult();
        outputResult.setScheduleJobChainOutputParam(jobOutputParam);
        return outputResult;
    }

    private static boolean isSelectSql(String sql) {
        Matcher matcher = pattern.matcher(sql.toLowerCase().trim());
        return matcher.find();
    }

    /**
     * 解析 rowIndex 行 colIndex 列的值，若 rowIndex 为空，则只解析 colIndex 列
     * @param rdbValue
     * @param rowIndex
     * @param colIndex
     * @return
     */
    private String parseRdbValue(String rdbValue, Integer rowIndex, Integer colIndex) {
        if (StringUtils.isEmpty(rdbValue)) {
            return rdbValue;
        }
        if (colIndex == null) {
            throw new RdosDefineException("parseRdbValue, colIndex should not be null");
        }
        List<List> parsedRdbValue = null;
        try {
            parsedRdbValue = JSONObject.parseArray(rdbValue, List.class);
        } catch (Exception e) {
            throw new RdosDefineException("parseRdbValue error", e);
        }
        // 不指定 rowIndex，则只取 colIndex 列
        if (rowIndex == null) {
            List<String> result = new ArrayList<>(parsedRdbValue.size());
            for (int i = 0; i < parsedRdbValue.size(); i++) {
                List<Object> rowValues = parsedRdbValue.get(i);
                if (CollectionUtils.isEmpty(rowValues) || colIndex >= rowValues.size()) {
                    // 列表为空，或者索引越界，则不予处理
                    continue;
                }
                result.add(String.valueOf(rowValues.get(colIndex)));
            }
            return "(" + StringUtils.join(result, ",") + ")";
        }
        if (rowIndex < 0 || rowIndex >= parsedRdbValue.size()) {
            throw new RdosDefineException("parseRdbValue, rowIndex size invalid, limit:" + parsedRdbValue.size());
        }
        // 取 rowIndex 行 colIndex 列
        return String.valueOf(parsedRdbValue.get(rowIndex).get(colIndex));
    }
}
