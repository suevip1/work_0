package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.HdfsQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.dtstack.schedule.common.enums.EProcessedLevelType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class JobChainParamWorkerProcessor implements IJobChainParamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamWorkerProcessor.class);

    private final static String OUTPUT_FILE_PATH_PREFIX = "output_";

    private static Integer HDFS_ROW_LIMIT;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Value("${job.chain.hdfs.fetch.row.limit:500}")
    private void setHdfsRowLimit(Integer hdfsRowLimit) {
        HDFS_ROW_LIMIT = hdfsRowLimit;
    }

    /**
     * 解析上游参数
     *
     * @param sourceParamDTO 输入参数源
     * @param levelType      输入参数类型
     * @param inputParamName 输入参数名称
     * @param paramIndexes   输入参数索引
     * @return
     */
    @Override
    public String parseUpstreamParam(ScheduleTaskChainParamDTO sourceParamDTO, EProcessedLevelType levelType, String inputParamName, List<Integer> paramIndexes) {
        Integer fatherTaskType = sourceParamDTO.getFatherTaskType();
        EScheduleJobType eFatherTaskType = EScheduleJobType.getEJobType(fatherTaskType);
        Integer outputParamType = sourceParamDTO.getOutputParamType();

        String hdfsPath;
        Integer rowIndex;
        Integer colIndex;

        ScheduleJob fatherJob = sourceParamDTO.getFatherJob();
        Long dtuicTenantId = fatherJob.getDtuicTenantId();

        String pluginInfo = null;
        String typeName = null;
        Integer dataSourceCode = null;
        JSONObject pluginInfoWithComponentType = null;

        if (eFatherTaskType == EScheduleJobType.SPARK_SQL
                || eFatherTaskType == EScheduleJobType.HIVE_SQL
                || eFatherTaskType == EScheduleJobType.SHELL
                || eFatherTaskType == EScheduleJobType.PYTHON) {
            pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(fatherJob.getProjectId(), fatherJob.getAppType(), fatherJob.getTaskType(), dtuicTenantId, ScheduleEngineType.Hadoop.getEngineName(), fatherJob.getCreateUserId(), null, null, null);
            typeName = componentService.buildHdfsTypeName(dtuicTenantId, null);
            dataSourceCode = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
            pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
            pluginInfoWithComponentType.put("dataSourceType", dataSourceCode);
            pluginInfo = pluginInfoWithComponentType.toJSONString();
        } else {
            throw new RdosDefineException("not support eFatherTaskType:" + eFatherTaskType + ", fatherJob:" + fatherJob.getJobId());
        }

        switch (eFatherTaskType) {
            case SPARK_SQL:
            case HIVE_SQL:
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
                        hdfsPath = sourceParamDTO.getFatherParamValue();
                        rowIndex = paramIndexes.get(0);
                        colIndex = paramIndexes.get(1);
                        return getForSql(dtuicTenantId, dataSourceCode, pluginInfo, hdfsPath, rowIndex, colIndex);
                    } else if (levelType == EProcessedLevelType.FIRST) {
                        hdfsPath = sourceParamDTO.getFatherParamValue();
                        // 第几列，解析 ${}[]
                        colIndex = paramIndexes.get(0);
                        return getForSql(dtuicTenantId, dataSourceCode, pluginInfo, hdfsPath, colIndex);
                    } else if (levelType == EProcessedLevelType.ZERO) {
                        hdfsPath = sourceParamDTO.getFatherParamValue();
                        return getForSql(dtuicTenantId, dataSourceCode, pluginInfo, hdfsPath, 0, 0);
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else {
                    throw new RdosDefineException("not support outputParamType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",fatherTaskType" + fatherTaskType);
                }
            case SHELL:
            case PYTHON:
                if (EOutputParamType.CONS.getType().equals(outputParamType) || EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    if (levelType == EProcessedLevelType.ZERO) {
                        // 只解析${abc}，如果出现 ${abc}[0][1]、${abc}[0]，视为异常(后续可以直接替换常量，形如 6[0][1])
                        return sourceParamDTO.getFatherParamValue();
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    if (levelType == EProcessedLevelType.ZERO) {
                        hdfsPath = sourceParamDTO.getFatherParamValue();
                        // 解析 ${a}
                        return getForScript(dtuicTenantId, dataSourceCode, pluginInfo, hdfsPath);
                    } else {
                        throw new RdosDefineException("not support levelType, inputParamName:" + inputParamName + ",outputParamType:" + outputParamType + ",levelType:" + levelType + ",fatherTaskType" + fatherTaskType);
                    }
                } else {
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
        String taskParamAppender = null;

        EScheduleJobType eJobType = EScheduleJobType.getEJobType(outputParamTaskType);
        if (eJobType == null) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        switch (eJobType) {
            case SPARK_SQL:
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    // 最终路径
                    paramValue = JobChainParamHandler.generateFilePath(JobChainParamHandler.getRootOutputPath(), outputParamName, scheduleJob, project);
                    // 拼接输出到文件语句
                    scriptFragment = JobChainParamScripter.generateSparkSql(paramCommand, paramValue);
                } else {
                    throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
                }
                break;
            case HIVE_SQL:
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    String tmpTableName = generateFileName(outputParamName, scheduleJob);
                    // 最终路径
                    paramValue = JobChainParamHandler.generateFilePath(JobChainParamHandler.getRootOutputPath(), outputParamName, scheduleJob, project);
                    // 拼接创建临时表语句、输出到文件语句
                    scriptFragment = JobChainParamScripter.generateHiveSql(paramCommand, tmpTableName, paramValue);
                } else {
                    throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
                }
                break;
            case SHELL:
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    // 文件名拼接规则:output_${参数名称}_${任务id}_${计划时间}
                    String fileName = generateFileName(outputParamName, scheduleJob);
                    taskParamAppender = fileName;
                    paramValue = JobChainParamHandler.generateFilePath(JobChainParamHandler.getRootOutputPath(), outputParamName, scheduleJob, project);
                    // 将结果追加到文件
                    scriptFragment = JobChainParamScripter.generateShellScript(paramCommand, fileName);
                } else {
                    throw new RdosDefineException("handleChainOutputParam error, not support outputParamType!");
                }
                break;
            case PYTHON:
                if (EOutputParamType.CONS.getType().equals(outputParamType)) {
                    paramValue = paramCommand;
                } else if (EOutputParamType.CUSTOMIZE.getType().equals(outputParamType)) {
                    // 根据参数内容解析出真正的自定义运行参数，比如 key1 则得到运行日期
                    paramValue = paramName2Value.get(paramCommand);
                } else if (EOutputParamType.PROCESSED.getType().equals(outputParamType)) {
                    // 文件名拼接规则:output_${参数名称}_${任务id}_${计划时间}
                    String fileName = generateFileName(outputParamName, scheduleJob);
                    taskParamAppender = fileName;
                    paramValue = JobChainParamHandler.generateFilePath(JobChainParamHandler.getRootOutputPath(), outputParamName, scheduleJob, project);
                    // 将结果写入文件
                    scriptFragment = JobChainParamScripter.generatePythonScript(paramCommand, fileName);
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
        Optional.ofNullable(taskParamAppender).ifPresent(outputResult::setTaskParamAppender);
        return outputResult;
    }

    private String getForSql(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String hdfsPath, Integer rowIndex, Integer colIndex) {
        HdfsQueryDTO queryDTO = buildQueryDtoForSql(hdfsPath, rowIndex, colIndex);
        List<String> hdfsWithJob = null;

        try {
            IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
            hdfsWithJob = hdfs.getHdfsWithJob(sourceDTO, queryDTO);
        } catch (Exception e) {
            LOGGER.error("getHdfsWithJob error, queryDTO:{}", queryDTO, e);
            throw new RdosDefineException(e.getMessage(), e);
        }
        if (hdfsWithJob == null || hdfsWithJob.isEmpty()) {
            throw new RdosDefineException("can't find firstResult from hdfs");
        }
        if (hdfsWithJob.size() != 1) {
            throw new RdosDefineException("firstResult more than one");
        }
        return hdfsWithJob.get(0);
    }

    private String getForSql(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String hdfsPath, Integer colIndex) {
        HdfsQueryDTO queryDTO = buildQueryDtoForSql(hdfsPath, null, colIndex);
        List<String> hdfsWithJob = null;
        try {
            IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
            hdfsWithJob = hdfs.getHdfsWithJob(sourceDTO, queryDTO);
        } catch (Exception e) {
            LOGGER.error("getHdfsWithJob error, queryDTO:{}", queryDTO, e);
            throw new RdosDefineException(e.getMessage(), e);
        }
        if (hdfsWithJob == null || hdfsWithJob.isEmpty()) {
            throw new RdosDefineException("can't find firstResult from hdfs");
        }

        return "(" + StringUtils.join(hdfsWithJob, ",") + ")";
    }

    private String getForScript(Long dtuicTenantId, Integer dataSourceCode, String pluginInfo, String hdfsPath) {
        IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCode);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo, dtuicTenantId);
        return hdfs.getHdfsWithScript(sourceDTO, hdfsPath);
    }

    private static HdfsQueryDTO buildQueryDtoForSql(String hdfsPath, Integer rowIndex, Integer colIndex) {
        if (colIndex == null || colIndex < 0) {
            throw new RdosDefineException("buildQueryDtoForSql error, colIndex invalid:" + colIndex);
        }
        if (rowIndex != null && rowIndex < 0) {
            throw new RdosDefineException("buildQueryDtoForSql error, rowIndex invalid:" + rowIndex);
        }
        HdfsQueryDTO queryDTO = buildQueryDtoForSql();
        queryDTO.setHdfsPath(hdfsPath);
        queryDTO.setRowIndex(rowIndex);
        queryDTO.setColIndex(colIndex);
        return queryDTO;
    }

    private static HdfsQueryDTO buildQueryDtoForSql() {
        HdfsQueryDTO queryDTO = new HdfsQueryDTO();
        queryDTO.setFileType("orc");
        queryDTO.setLimit(HDFS_ROW_LIMIT);
        queryDTO.setIsRecursion(false);
        return queryDTO;
    }

    /**
     * 文件名称
     *
     * @param paramName
     * @param scheduleJob
     * @return
     */
    public static String generateFileName(String paramName, ScheduleJob scheduleJob) {
        // output_${参数名称}_${任务id}_${计划时间}
        return OUTPUT_FILE_PATH_PREFIX + paramName + "_" + scheduleJob.getJobId() + "_" + scheduleJob.getCycTime();
    }
}
