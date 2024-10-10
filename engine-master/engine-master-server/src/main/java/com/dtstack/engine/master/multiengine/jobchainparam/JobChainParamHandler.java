package com.dtstack.engine.master.multiengine.jobchainparam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.loader.dto.JobExecInfo;
import com.dtstack.engine.master.dto.JobChainParamOutputResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleEngineProjectDao;
import com.dtstack.engine.dao.ScheduleJobChainOutputParamDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleTaskChainParamDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.distributor.OperatorDistributor;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.dto.JobChainParamStatusResult;
import com.dtstack.engine.master.dto.ScheduleTaskChainParamDTO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleEngineProject;
import com.dtstack.engine.po.ScheduleJobChainOutputParam;
import com.dtstack.engine.po.ScheduleTaskChainParam;
import com.dtstack.schedule.common.enums.EOutputParamType;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.enums.EProcessedLevelType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 任务上下游参数处理
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-14 16:48
 */
@Component
public class JobChainParamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobChainParamHandler.class);

    private final static String VAR_FORMAT = "${%s}";

    private final static String SP_EL_FORMAT = "#%s";

    private static final Pattern SP_EL_ARRAY_REGEX = Pattern.compile("#array\\(\\s*#(?<param>\\w+)\\s*(,\\s*(?<first>\\d+)\\s*)?(,\\s*(?<second>\\d+)\\s*)?\\)");

    private final static String INDEX_FORMAT = "[%s]";

    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(?<=\\$\\{).*?(?=\\})");
    private static final Pattern VARIABLE_INDEX_PATTERN = Pattern.compile("(?<=\\[)\\d+(?=\\])");

    /**
     * HDFS 文件存储根路径
     */
    private static String ROOT_OUTPUT_PATH;

    @Value("${job.chain.output.rootPath:/data/job}")
    private void setRootOutputPath(String outputPath) {
        ROOT_OUTPUT_PATH = outputPath;
    }

    private final static String OUTPUT_FILE_PATH_PREFIX = "output_";

    public static final List<EParamType> IN_AND_OUT_CHAIN_PARAM_TYPE = ImmutableList.of(EParamType.INPUT, EParamType.OUTPUT);

    /**
     * 支持上下游参数传递的 worker 任务类型
     */
    private static final Map<Integer, List<EParamType>> SUPPORT_WORKER_TASK_TYPE = new ImmutableMap.Builder<Integer, List<EParamType>>()
            .put(EScheduleJobType.SPARK_SQL.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            .put(EScheduleJobType.HIVE_SQL.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            .put(EScheduleJobType.SHELL.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            .put(EScheduleJobType.PYTHON.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            // 数据同步任务仅支持输入参数
            .put(EScheduleJobType.SYNC.getType(), ImmutableList.of(EParamType.INPUT))
            .build();

    /**
     * 支持上下游参数传递的 rdb 任务类型
     */
    private static Map<Integer, List<EParamType>> SUPPORT_RDB_TASK_TYPE;

    /**
     * 支持上下游参数传递的其他任务类型
     */
    private static final Map<Integer, List<EParamType>> SUPPORT_OTHER_TASK_TYPE = new ImmutableMap.Builder<Integer, List<EParamType>>()
            .put(EScheduleJobType.SHELL_ON_AGENT.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            .put(EScheduleJobType.CONDITION_BRANCH.getType(), IN_AND_OUT_CHAIN_PARAM_TYPE)
            .build();

    /**
     * 支持上下游参数传递的任务类型。由于需要对接底层插件，不宜做成配置化
     */
    private static Map<Integer, List<EParamType>> ALL_SUPPORT_CHAIN_PARAM_TASK_TYPE;

    /**
     * @see EScheduleJobType
     * @param rdbTaskType
     */
    @Value("${job.chain.support.rdb.taskType:20,32,33,19,30,43,15,21,36,18,28,44,45,46}")
    private void setRdbTaskTypes(List<Integer> rdbTaskType) {
        // 根据配置项，初始化 RDB 任务类型，支持：Oracle/MySQL/SQLServer/TiDB/ADB/HANA/GaussDB
        // GreenPlum/Trino/Impala/Inceptor/StarRocks/HashData
        SUPPORT_RDB_TASK_TYPE = new ImmutableMap.Builder<Integer, List<EParamType>>()
                .putAll(rdbTaskType.stream()
                        .filter(Objects::nonNull)
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        k -> ImmutableList.of(EParamType.INPUT, EParamType.OUTPUT),
                                        (old, newer) -> newer))
                ).build();
        ALL_SUPPORT_CHAIN_PARAM_TASK_TYPE = new ImmutableMap.Builder<Integer, List<EParamType>>()
                .putAll(SUPPORT_WORKER_TASK_TYPE)
                .putAll(SUPPORT_RDB_TASK_TYPE)
                .putAll(SUPPORT_OTHER_TASK_TYPE)
                .build();
    }

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskChainParamDao scheduleTaskChainParamDao;

    @Autowired
    private ScheduleJobChainOutputParamDao scheduleJobChainOutputParamDao;

    @Autowired
    private JobChainParamQuerier jobChainParamQuerier;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private JobChainParamCleaner jobChainParamCleaner;

    @Autowired
    private ScheduleEngineProjectDao scheduleEngineProjectDao;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private OperatorDistributor operatorDistributor;

    @Autowired
    private JobChainParamProcessorDelegate jobChainParamProcessorDelegate;

    public static final int HEADER_ROW_INDEX = 0;

    /**
     * 主方法，替换任务上下游输入输出参数
     * @param sql
     * @param taskShade
     * @param taskParamsToReplace
     * @param scheduleJob
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JobChainParamHandleResult handle(String sql, ScheduleTaskShade taskShade, List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult handleResult = new JobChainParamHandleResult(sql, taskShade.getTaskParams());
        if (StringUtils.isEmpty(sql)) {
            return handleResult;
        }
        boolean supportChainParam = supportChainParam(taskShade.getAppType(), taskShade.getTaskType());
        if (!supportChainParam) {
            return handleResult;
        }

        // 0正常调度 1补数据 2临时运行
        Integer type = scheduleJob.getType();
        if (EScheduleType.TEMP_JOB.getType().equals(type)) {
            if (CollectionUtils.isEmpty(taskParamsToReplace)) {
                return handleResult;
            }
            boolean containsInOutParam = JobChainParamHandler.containsInOutParam(taskParamsToReplace);
            if (!containsInOutParam) {
                return handleResult;
            }
            validateTaskParamShade(taskParamsToReplace, taskShade);
            handleResult = handleTempJobInputParam(handleResult.getSql(), handleResult.getTaskParams(), taskShade, taskParamsToReplace, scheduleJob);
            handleResult = handleTempJobOutputParam(handleResult.getSql(), handleResult.getTaskParams(), taskShade, taskParamsToReplace, scheduleJob);
        } else {
            handleResult = handleInputParam(handleResult.getSql(), handleResult.getTaskParams(), taskShade, taskParamsToReplace, scheduleJob);
            handleResult = handleOutputParam(handleResult.getSql(), handleResult.getTaskParams(), taskShade, taskParamsToReplace, scheduleJob);
        }
        return handleResult;
    }

    /**
     * 拼接任务输出参数
     *
     * @param sql
     * @param taskParams
     * @param taskShade
     * @param taskParamsToReplace
     * @param scheduleJob
     * @return
     */
    private JobChainParamHandleResult handleOutputParam(String sql, String taskParams, ScheduleTaskShade taskShade,
                                                        List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult handleResult = new JobChainParamHandleResult(sql, taskParams);
        List<ScheduleTaskChainParam> outputParams = scheduleTaskChainParamDao.listParamsByTask(taskShade.getTaskId(), taskShade.getAppType(), EParamType.OUTPUT.getType());
        if (CollectionUtils.isEmpty(outputParams)) {
            // 不存在任务输出参数配置，则直接返回原始 sql
            return handleResult;
        }
        if (!isSupport(taskShade.getTaskType(), EParamType.OUTPUT)) {
            // 不支持输出参数，则直接返回原始 sql
            return handleResult;
        }
        //outputType 类型存在scheduleTaskChainParamDao 自定义参数存在taskParamsToReplace 需要聚合数据
        taskParamsToReplace.addAll(outputParams.stream().map(a -> {
            ScheduleTaskParamShade t = new ScheduleTaskParamShade();
            t.setParamName(a.getParamName());
            t.setParamCommand(a.getParamCommand());
            t.setOutputParamType(a.getOutputParamType());
            t.setType(a.getType());
            return t;
        }).collect(Collectors.toList()));
        // 先解析自定义参数配置，比如 key1:$(yyyy-MM-dd)
        Map<String, String> paramName2Value = jobParamReplace.parseTaskParam(taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        StringBuilder scriptAppender = new StringBuilder();

        // 输出文件名称集合
        List<String> taskParamAppender = new ArrayList<>();
        ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(scheduleJob.getProjectId(), scheduleJob.getAppType());

        List<ScheduleJobChainOutputParam> jobChainOutputParams = new ArrayList<>(outputParams.size());
        for (ScheduleTaskChainParam outputParam : outputParams) {
            JobChainParamOutputResult outputResult = jobChainParamProcessorDelegate.dealOutputParam(outputParam.getTaskType(), outputParam.getParamName(), outputParam.getOutputParamType(), outputParam.getParamCommand(),
                    taskShade, scheduleJob, paramName2Value, project, taskParamsToReplace);

            jobChainOutputParams.add(outputResult.getScheduleJobChainOutputParam());
            Optional.ofNullable(outputResult.getTaskParamAppender()).ifPresent(taskParamAppender::add);
            Optional.ofNullable(outputResult.getScriptFragment()).ifPresent(scriptAppender::append);
        }

        // 输出参数预生成，先删后插 -- 周期实例运行时，jobId 不同，这里删除操作是为了以防万一，所以此处不考虑 hdfs 文件的清理
        scheduleJobChainOutputParamDao.deleteByJobId(scheduleJob.getJobId());
        scheduleJobChainOutputParamDao.batchSave(jobChainOutputParams);

        // 组装最终结果
        sql = addScriptAppender(sql, scriptAppender, taskShade);
        // 整体替换一下，防止输出参数中嵌套了自定义参数的情况
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        handleResult.setSql(sql);
        if (CollectionUtils.isNotEmpty(taskParamAppender)) {
            String files = StringUtils.join(taskParamAppender, ",");
            String outputParentDir = generateFileRootPath(ROOT_OUTPUT_PATH, scheduleJob, project);
            taskParams += JobChainParamScripter.generateOutputTaskParams(files, outputParentDir);
        }
        handleResult.setTaskParams(taskParams);
        return handleResult;
    }

    /**
     * 拼接临时运行任务输出参数
     *
     * @param sql
     * @param taskParams
     * @param taskShade 临时运行，taskShade 必须从入参取，不能由 job 中的 taskId 反推，因为任务可能没有提交， job 中的 taskId 可能是「-1」
     * @param taskParamsToReplace 临时运行，taskParamsToReplace 必须从入参取，由离线传入，
     *                            因为参数在动态变化，要将本次的入参及结果落库
     * @param scheduleJob
     * @return
     */
    private JobChainParamHandleResult handleTempJobOutputParam(String sql, String taskParams, ScheduleTaskShade taskShade,
                                                               List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult handleResult = new JobChainParamHandleResult(sql, taskParams);
        List<ScheduleTaskParamShade> outputTaskParams = filterToGetOutputTaskParams(taskParamsToReplace);
        if (CollectionUtils.isEmpty(outputTaskParams)) {
            // 不存在任务输出参数配置，则直接返回原始 sql
            return handleResult;
        }
        if (!isSupport(taskShade.getTaskType(), EParamType.OUTPUT)) {
            // 不支持输出参数，则直接返回原始 sql
            return handleResult;
        }
        // 先解析自定义参数配置，比如 key1:$(yyyy-MM-dd) --> key1:2022-01-01
        Map<String, String> paramName2Value = jobParamReplace.parseTaskParam(taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), taskShade.getProjectId());
        StringBuilder scriptAppender = new StringBuilder();

        // 输出文件名称集合
        List<String> taskParamAppender = new ArrayList<>();
        List<ScheduleJobChainOutputParam> jobChainOutputParams = new ArrayList<>(outputTaskParams.size());
        ScheduleEngineProject project = scheduleEngineProjectDao.getProjectByProjectIdAndApptype(scheduleJob.getProjectId(), scheduleJob.getAppType());

        for (ScheduleTaskParamShade outputParam : outputTaskParams) {
            JobChainParamOutputResult outputResult = jobChainParamProcessorDelegate.dealOutputParam(outputParam.getTaskType(), outputParam.getParamName(), outputParam.getOutputParamType(), outputParam.getParamCommand(),
                    taskShade, scheduleJob, paramName2Value, project, taskParamsToReplace);

            jobChainOutputParams.add(outputResult.getScheduleJobChainOutputParam());
            Optional.ofNullable(outputResult.getScriptFragment()).ifPresent(scriptAppender::append);
            Optional.ofNullable(outputResult.getTaskParamAppender()).ifPresent(taskParamAppender::add);
        }

        // 输出参数预生成，listOutputParamsByTask先删后插
        List<ScheduleJobChainOutputParam> oldJobChainOutputParams = scheduleJobChainOutputParamDao.listOutputParamsByTask(taskShade.getTaskId(), taskShade.getAppType(), EScheduleType.TEMP_JOB.getType());
        if (CollectionUtils.isNotEmpty(oldJobChainOutputParams)) {
            jobChainParamCleaner.asyncCleanJobOutputProcessedParamsIfNeed(oldJobChainOutputParams);
        }
        scheduleJobChainOutputParamDao.deleteOutputParamsByTask(taskShade.getTaskId(), taskShade.getAppType(), EScheduleType.TEMP_JOB.getType());
        scheduleJobChainOutputParamDao.batchSave(jobChainOutputParams);

        // 组装最终结果
        sql = addScriptAppender(sql, scriptAppender, taskShade);
        // 再整体替换一下，防止输出参数中嵌套了自定义参数的情况
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());

        handleResult.setSql(sql);
        if (CollectionUtils.isNotEmpty(taskParamAppender)) {
            String files = StringUtils.join(taskParamAppender, ",");
            String outputParentDir = generateFileRootPath(ROOT_OUTPUT_PATH, scheduleJob, project);
            taskParams += JobChainParamScripter.generateOutputTaskParams(files, outputParentDir);
        }
        handleResult.setTaskParams(taskParams);
        return handleResult;
    }

    private String addScriptAppender(String sql, StringBuilder scriptAppender, ScheduleTaskShade taskShade) {
        if (scriptAppender.length() <= 0) {
            return sql;
        }
        Integer taskType = taskShade.getTaskType();
        if (EScheduleJobType.getEJobType(taskType) == EScheduleJobType.PYTHON) {
            return sql + scriptAppender;
        }

        sql = StringUtils.trim(sql);
        // 针对 hiveSql/sparkSql/shell 进行特殊处理，只保留末尾的一个分号
        if (StringUtils.endsWith(sql, GlobalConst.SEMICOLON)) {
            return sql + scriptAppender.substring(1);
        } else {
            return sql + scriptAppender;
        }
    }

    private List<ScheduleTaskChainParam> filterToGetTempJobInputParams(List<ScheduleTaskParamShade> taskParamsToReplace) {
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            return Collections.emptyList();
        }
        return taskParamsToReplace.stream()
                .filter(s -> EParamType.INPUT.getType().equals(s.getType()))
                .map(s -> {
                    ScheduleTaskChainParam t = new ScheduleTaskChainParam();
                    BeanUtils.copyProperties(s, t);
                    return t;
                }).collect(Collectors.toList());
    }

    private List<ScheduleTaskParamShade> filterToGetOutputTaskParams(List<ScheduleTaskParamShade> taskParamsToReplace) {
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            return Collections.emptyList();
        }
        return taskParamsToReplace.stream()
                .filter(s -> EParamType.OUTPUT.getType().equals(s.getType()))
                .collect(Collectors.toList());
    }

    /**
     * 替换任务输入参数
     *
     * @param sql
     * @param taskShade
     * @param scheduleJob
     * @return
     */
    private JobChainParamHandleResult handleInputParam(String sql, String taskParams, ScheduleTaskShade taskShade,
                                                       List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult handleResult = new JobChainParamHandleResult(sql, taskParams);
        // 输入参数配置项对应上游输出参数配置项 ed,ad,mk
        List<ScheduleTaskChainParam> sourceParams = scheduleTaskChainParamDao.listParamsByTask(taskShade.getTaskId(), taskShade.getAppType(), EParamType.INPUT.getType());
        if (CollectionUtils.isEmpty(sourceParams)) {
            return handleResult;
        }
        if (!isSupport(taskShade.getTaskType(), EParamType.INPUT)) {
            // 不支持输入参数，则直接返回原始 sql
            return handleResult;
        }
        List<ScheduleTaskChainParamDTO> sourceDtoParams = sourceParams.stream().map(p -> {
            ScheduleTaskChainParamDTO t = new ScheduleTaskChainParamDTO();
            BeanUtils.copyProperties(p, t);
            return t;
        }).collect(Collectors.toList());

        // 先将自定义参数替换掉，防止后续引入不必要的处理步骤
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        removeInputParamWhenNotInSql(sourceDtoParams, sql);
        if (CollectionUtils.isEmpty(sourceDtoParams)) {
            return handleResult;
        }
        // 定位 sourceParams 对应的 fatherTaskShade 和 fatherJob，获取到参数对应的值，组装为Map, {paramName, ScheduleTaskChainParamDTO}
        Map<String, ScheduleTaskChainParamDTO> param2Result = acquireTarget(sourceDtoParams, scheduleJob);
        // 解析 sql 中的详细变量，{2, {${abc}[0][1], "tony"}}, {1, {${abc}[0], "(1,2,3)"}},{1, {${cd}[1], "(1,2,3)"}}, {0, {${cx}, "cx"}}
        // 解析顺序：${}[][] --> ${}[] --> ${}
        // 条件分支任务特殊处理, 需要特殊处理
        if (EScheduleJobType.CONDITION_BRANCH.getType().equals(taskShade.getTaskType())) {
            sql = replaceConditionParamValue(sql, param2Result);
        }
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.SECOND);
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.FIRST);
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.ZERO);
        handleResult.setSql(sql);
        return handleResult;
    }

    private String replaceConditionParamValue(String sql, Map<String, ScheduleTaskChainParamDTO> param2Result) {
        // 按照参数长度从大到小进行排序, 避免类似 #abcd、#abc、#ab 替换错误
        List<ScheduleTaskChainParamDTO> sortValues = param2Result
                .values()
                .stream()
                .sorted((param1, param2) -> param2.getParamName().length() - param1.getParamName().length())
                .collect(Collectors.toList());
        for (ScheduleTaskChainParamDTO chainParam : sortValues) {
            if (EOutputParamType.PROCESSED.getType().equals(chainParam.getOutputParamType())) {
                // 走原有逻辑, 解析、替换
                // el 二维表达式格式 array(param,0,1)
                try {
                    Matcher matcher = SP_EL_ARRAY_REGEX.matcher(sql);
                    while (matcher.find()) {
                        String replace = matcher.group();
                        StringBuilder target = new StringBuilder();
                        String param = matcher.group("param");
                        String first = matcher.group("first");
                        String second = matcher.group("second");
                        target.append(String.format(VAR_FORMAT, param));
                        if (StringUtils.isNotBlank(first)) {
                            target.append(String.format(INDEX_FORMAT, first));
                        }
                        if (StringUtils.isNotBlank(second)) {
                            target.append(String.format(INDEX_FORMAT, second));
                        }
                        sql = sql.replace(replace, target.toString());
                    }
                } catch (Exception e) {
                    LOGGER.error("replace condition param value error, sql: {}", sql, e);
                    return sql;
                }
                // 兼容非二维表达式写法
                sql = replaceConditionZeroParam(sql, chainParam.getParamName(), String.format(VAR_FORMAT, chainParam.getParamName()));
            } else {
                sql = replaceConditionZeroParam(sql, chainParam.getParamName(), chainParam.getFatherParamValue());
            }
        }
        return sql;
    }

    /**
     * 替换条件分支 #param 写法的参数为
     *
     * @param sql       条件分支表达式
     * @param paramName 参数名称
     * @param target    替换后的值
     * @return 替换后的条件分支
     */
    private String replaceConditionZeroParam(String sql, String paramName, String target) {
        String replace = String.format(SP_EL_FORMAT, paramName);
        if (sql.contains(replace)) {
            // 替换为来源任务的参数值
            sql = sql.replace(replace, target);
        }
        return sql;
    }

    /**
     * 替换临时运行任务输入参数
     *
     * @param sql
     * @param taskParams
     * @param taskShade
     * @param taskParamsToReplace 临时运行，考虑实时性，taskParamsToReplace 必须从入参取，由离线传入
     * @param scheduleJob
     * @return
     */
    private JobChainParamHandleResult handleTempJobInputParam(String sql, String taskParams, ScheduleTaskShade taskShade,
                                                              List<ScheduleTaskParamShade> taskParamsToReplace, ScheduleJob scheduleJob) {
        JobChainParamHandleResult handleResult = new JobChainParamHandleResult(sql, taskParams);
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            return handleResult;
        }
        if (!isSupport(taskShade.getTaskType(), EParamType.INPUT)) {
            // 不支持输入参数，则直接返回原始 sql
            return handleResult;
        }

        // 输入参数配置项对应上游输出参数配置项(形如ed,ad,mk)，临时运行必须从离线入参中获取，因为要满足实时性
        List<ScheduleTaskChainParam> sourceParams = filterToGetTempJobInputParams(taskParamsToReplace);
        if (CollectionUtils.isEmpty(sourceParams)) {
            return handleResult;
        }
        List<ScheduleTaskChainParamDTO> sourceDtoParams = sourceParams.stream().map(s -> {
            ScheduleTaskChainParamDTO t = new ScheduleTaskChainParamDTO();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());

        // 先将自定义参数替换掉，防止后续引入不必要的处理步骤
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        removeInputParamWhenNotInSql(sourceDtoParams, sql);
        if (CollectionUtils.isEmpty(sourceDtoParams)) {
            LOGGER.info("temp jobId:{} has no input params", scheduleJob.getJobId());
            return handleResult;
        }
        // 定位 sourceParams 对应的 fatherTaskShade 和 fatherJob，获取到参数对应的值，组装为Map, {paramName, ScheduleTaskChainParamDTO}
        Map<String, ScheduleTaskChainParamDTO> param2Result = acquireTempJobTarget(sourceDtoParams, scheduleJob);
        // 解析 sql 中的详细变量，{2, {${abc}[0][1], "tony"}}, {1, {${abc}[0], "(1,2,3)"}},{1, {${cd}[1], "(1,2,3)"}}, {0, {${cx}, "cx"}}
        // 解析顺序：${}[][] --> ${}[] --> ${}
        // 条件分支任务特殊处理, 需要特殊处理
        if (EScheduleJobType.CONDITION_BRANCH.getType().equals(taskShade.getTaskType())) {
            sql = replaceConditionParamValue(sql, param2Result);
        }
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.SECOND);
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.FIRST);
        sql = replaceSpecificParamValue(sql, param2Result, EProcessedLevelType.ZERO);
        handleResult.setSql(sql);
        return handleResult;
    }

    /**
     * 解析 sql 中的详细变量值, {2, {${abc}[0][1], "tony"}}, {1, {${abc}[0], "(1,2,3)"}},{1, {${cd}[1], "(1,2,3)"}}, {0, {${cx}, "cx"}}
     *
     * @param script
     * @param param2Result
     * @param levelType
     * @return
     */
    private String replaceSpecificParamValue(String script, Map<String, ScheduleTaskChainParamDTO> param2Result, EProcessedLevelType levelType) {
        Matcher matcher = levelType.getPattern().matcher(script);
        while (matcher.find()) {
            // ${abc}[0][1]
            String specificParam = matcher.group();
            // abc
            String paramName = findParamName(specificParam);
            // [0, 1]
            List<Integer> paramIndexes = findParamIndexes(specificParam);
            // 参数配置项存在该参数
            if (param2Result.containsKey(paramName)) {
                // 计算 ${abc}[0][1] 的值
                String specificParamValue;
                try {
                    specificParamValue = findSpecificParamValue(levelType, param2Result, paramName, paramIndexes);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new RdosDefineException(e.getMessage(), e);
                }
                // chop with value
                script = script.replace(specificParam, specificParamValue);
            } else {
                // 脚本中存在，但是配置项不存在，则抛出异常
                throw new RdosDefineException("cant find result, paramName:" + paramName);
            }
        }
        return script;
    }

    /**
     * 根据不同任务类型解析出 ${abc}[0][1]、${ab}[0]、${a}
     *
     * @param levelType
     * @param param2Result
     * @param paramName 参数名称
     * @param paramIndexes 参数索引
     * @return
     */
    private String findSpecificParamValue(EProcessedLevelType levelType, Map<String, ScheduleTaskChainParamDTO> param2Result, String paramName, List<Integer> paramIndexes) throws Exception {
        ScheduleTaskChainParamDTO sourceParamDTO = param2Result.get(paramName);
        if (sourceParamDTO == null) {
            return StringUtils.EMPTY;
        }
        // 上游任务可能不同
        return jobChainParamProcessorDelegate.parseUpstreamParam(sourceParamDTO, levelType, paramName, paramIndexes);
    }

    /**
     * 过滤掉 sql 中不存在的参数，避免做多余的查询
     *
     * @param sourceParams
     * @param sql
     */
    private void removeInputParamWhenNotInSql(List<ScheduleTaskChainParamDTO> sourceParams, String sql) {
        Iterator<ScheduleTaskChainParamDTO> iterator = sourceParams.iterator();
        while (iterator.hasNext()) {
            ScheduleTaskChainParamDTO sourceParam = iterator.next();
            String paramName = sourceParam.getParamName();
            // abc --> ${abc}, abc --> #abc
            String paramNameFormat = String.format(VAR_FORMAT, paramName);
            // 如果 ${abc}, #abc  不在 sql 中，则过滤掉该变量
            if (!sql.contains(paramNameFormat) && !sql.contains(String.format(SP_EL_FORMAT, paramName))) {
                iterator.remove();
            }
        }
    }

    /**
     * 填充任务输入参数配置项的目标配置项
     *
     * @param sourceParams
     * @param sourceJob
     * @return
     */
    private Map<String, ScheduleTaskChainParamDTO> acquireTarget(List<ScheduleTaskChainParamDTO> sourceParams, ScheduleJob sourceJob) {
        Map<String, ScheduleTaskChainParamDTO> param2Result = new HashMap<>(sourceParams.size());
        for (ScheduleTaskChainParamDTO sourceParam : sourceParams) {
            Long fatherTaskId = sourceParam.getFatherTaskId();
            Integer fatherAppType = sourceParam.getFatherAppType();
            ScheduleTaskShade fatherTaskShade = scheduleTaskShadeDao.getOneByTaskIdAndAppType(fatherTaskId, fatherAppType);
            if (fatherTaskShade == null) {
                throw new RdosDefineException("can't find fatherTaskShade, fatherTaskId:" + fatherTaskId + ",fatherAppType:" + fatherAppType);
            }
            sourceParam.setFatherTaskShade(fatherTaskShade);
            sourceParam.setFatherTaskType(fatherTaskShade.getTaskType());

            // 获取上游任务实例
            ScheduleJob fatherJob = scheduleJobDao.getFatherJob(sourceJob.getJobId(), fatherTaskId, fatherAppType);
            if (fatherJob == null) {
                throw new RdosDefineException("cant find fatherJob, sourceJobId:" + sourceJob.getJobId() + ",fatherTaskId:" + fatherTaskId + ",fatherAppType:" + fatherAppType);
            }
            sourceParam.setFatherJob(fatherJob);

            String fatherParamName = sourceParam.getFatherParamName();

            ScheduleJobChainOutputParam outputParam = scheduleJobChainOutputParamDao.getJobOutputParam(fatherJob.getJobId(), fatherParamName);
            if (outputParam == null) {
                throw new RdosDefineException("can't find outputParam, sourceParam:" + sourceParam.getParamName() + ",fatherJobId:" + fatherJob.getJobId() + ",fatherAppType:" + fatherAppType + ",fatherParamName:" + fatherParamName);
            }
            sourceParam.setFatherTaskType(outputParam.getTaskType());

            // 父任务的类型，比如计算结果
            sourceParam.setOutputParamType(outputParam.getOutputParamType());
            sourceParam.setParamCommand(outputParam.getParamCommand());
            sourceParam.setFatherParamValue(outputParam.getParamValue());

            sourceParam.setSourceJob(sourceJob);
            param2Result.put(sourceParam.getParamName(), sourceParam);
        }
        return param2Result;
    }

    /**
     * 填充任务输入参数配置项的目标配置项
     *
     * @param sourceParams
     * @param sourceJob
     * @return
     */
    private Map<String, ScheduleTaskChainParamDTO> acquireTempJobTarget(List<ScheduleTaskChainParamDTO> sourceParams, ScheduleJob sourceJob) {
        Map<String, ScheduleTaskChainParamDTO> param2Result = new HashMap<>(sourceParams.size());
        for (ScheduleTaskChainParamDTO sourceParam : sourceParams) {
            Long fatherTaskId = sourceParam.getFatherTaskId();
            Integer fatherAppType = sourceParam.getFatherAppType();

            String fatherParamName = sourceParam.getFatherParamName();
            ScheduleJobChainOutputParam outputParam = scheduleJobChainOutputParamDao.getTempJobOutputParam(fatherTaskId, fatherAppType, fatherParamName);
            if (outputParam == null) {
                throw new RdosDefineException("can't find outputParam, sourceParam:" + sourceParam.getParamName() + ",fatherTaskId:" + fatherTaskId + ",fatherAppType:" + fatherAppType + ",fatherParamName:" + fatherParamName);
            }
            sourceParam.setFatherTaskType(outputParam.getTaskType());
            String jobId = outputParam.getJobId();
            ScheduleJob job = scheduleJobDao.getByJobId(jobId, IsDeletedEnum.NOT_DELETE.getType());
            if (job == null) {
                throw new RdosDefineException("can't find fatherJob which jobId is:" + jobId);
            }
            sourceParam.setFatherJob(job);

            // 父任务的类型，比如「计算结果」
            sourceParam.setOutputParamType(outputParam.getOutputParamType());
            sourceParam.setParamCommand(outputParam.getParamCommand());
            sourceParam.setFatherParamValue(outputParam.getParamValue());

            sourceParam.setSourceJob(sourceJob);
            param2Result.put(sourceParam.getParamName(), sourceParam);
        }
        return param2Result;
    }

    /**
     * 区分两种任务类型：
     * 1) enginePlugins 任务：执行不成功，清理本次预生成数据；任务执行成功，但输出参数大小超过阈值，则置为失败，清理掉此次生成的数据以及 HDFS 文件；
     * 再次判断任务执行成功，清理上一个周期的任务实例产生的临时数据(如果存在的话)。
     * 2) rdb 任务：填充运行结果。
     * @param rdosTaskStatus
     * @param scheduleJob
     * @param jobIdentifier
     * @param clientType
     * @return
     */
    public JobChainParamStatusResult checkOutputParamIfNeed(RdosTaskStatus rdosTaskStatus, ScheduleJob scheduleJob, JobIdentifier jobIdentifier, Integer clientType) {
        JobChainParamStatusResult result = new JobChainParamStatusResult(rdosTaskStatus, null);
        if (ComputeType.STREAM.getType().equals(scheduleJob.getComputeType())) {
            return result;
        }
        if (!RdosTaskStatus.getStoppedStatus().contains(rdosTaskStatus.getStatus())) {
            return result;
        }
        if (RdosTaskStatus.FINISHED != rdosTaskStatus) {
            // 任务跑完了但是不成功，则清理本次预生成数据(HDFS 文件可能存在，尝试清理)
            List<ScheduleJobChainOutputParam> outputProcessedParams = scheduleJobChainOutputParamDao.listByOutputParamType(scheduleJob.getJobId(), EOutputParamType.PROCESSED.getType());
            if (CollectionUtils.isNotEmpty(outputProcessedParams)) {
                jobChainParamCleaner.asyncCleanJobOutputProcessedParamsIfNeed(outputProcessedParams);
            }
            int affectRows = scheduleJobChainOutputParamDao.deleteByJobId(scheduleJob.getJobId());
            LOGGER.info("jobId:{}, status:{}, clear schedule_job_chain_output_param, affectRows:{}", scheduleJob.getJobId(), rdosTaskStatus, affectRows);
            return result;
        }
        // 任务跑完且成功的情况下，进入该分支
        List<ScheduleJobChainOutputParam> jobOutputParams = scheduleJobChainOutputParamDao.listByJobId(scheduleJob.getJobId());
        if (CollectionUtils.isEmpty(jobOutputParams)) {
            // 本次没有输出参数，清理上一个周期的任务实例产生的临时数据(如果存在的话)
            jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
            return result;
        }
        if (JobChainParamHandler.supportWorkerTaskTypes().contains(scheduleJob.getTaskType())
                && !EScheduleJobType.SYNC.getType().equals(scheduleJob.getTaskType())) {
            // enginePlugins 类型的任务，要判断计算型参数是否超过容量阈值
            return judgeSpaceThreshold(jobOutputParams, scheduleJob, result);
        } else if (JobChainParamHandler.supportRdbTaskTypes().contains(scheduleJob.getTaskType())) {
            // rdb 类型的任务
            return populateSelectValue(jobOutputParams, scheduleJob, jobIdentifier, clientType, result);
        } else {
            // 需求未考虑到的分支，直接返回
            return result;
        }
    }

    /**
     * rdb 类型的任务，填充 select 执行结果
     * @param jobOutputParams
     * @param scheduleJob
     * @param jobIdentifier
     * @param clientType
     * @param result
     * @return
     */
    private JobChainParamStatusResult populateSelectValue(List<ScheduleJobChainOutputParam> jobOutputParams, ScheduleJob scheduleJob,
                                                          JobIdentifier jobIdentifier, Integer clientType, JobChainParamStatusResult result) {
        List<ScheduleJobChainOutputParam> rdbProcessParams = jobOutputParams.stream()
                .filter(s -> s.getOutputParamType().equals(EOutputParamType.PROCESSED.getType()))
                .filter(s -> JobChainParamHandler.supportRdbTaskTypes().contains(s.getTaskType()))
                .filter(p -> StringUtils.isNotEmpty(p.getParsedParamCommand()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rdbProcessParams)) {
            // 本次有输出参数，但没有产生 select 计算型参数，则直接清理上一周期数据，返回成功
            jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
            return result;
        }
        try {
            JobExecInfo jobExecInfo = operatorDistributor.getOperator(ClientTypeEnum.getClientTypeEnum(clientType), jobIdentifier.getEngineType())
                    .getJobExecInfo(jobIdentifier, false, true);
            if (jobExecInfo == null || MapUtils.isEmpty(jobExecInfo.getOutputResult())) {
                // select 计算异常，则清理预生成的数据，将状态置为失败
                int affectRows = scheduleJobChainOutputParamDao.deleteByJobId(scheduleJob.getJobId());
                result.setRdosTaskStatus(RdosTaskStatus.FAILED);
                LOGGER.warn("output param select null, jobId:{} fail, remove output params, affectRows:{}", scheduleJob.getJobId(), affectRows);
                String log = String.format("jobId:%s，输出参数计算异常，请检查输出参数语句是否正确", scheduleJob.getJobId());
                result.setLog(log);
                return result;
            }
            // 填充计算结果
            Map<String, List<List<String>>> outputResult = jobExecInfo.getOutputResult();
            for (ScheduleJobChainOutputParam rdbProcessParam : rdbProcessParams) {
                String paramName = rdbProcessParam.getParamName();
                List<List<String>> preview = outputResult.getOrDefault(paramName, Collections.emptyList());
                // 由于 preview 包含了表头，为了与 sparkSql 等写入逻辑保持一致，需要过滤掉表头
                if (preview.size() <= HEADER_ROW_INDEX + 1) {
                    preview = Collections.emptyList();
                } else {
                    preview = preview.subList(HEADER_ROW_INDEX + 1, preview.size());
                }
                scheduleJobChainOutputParamDao.modifyParamValue(JSON.toJSONString(preview), scheduleJob.getJobId(), paramName);
            }
            // select 计算正常，则直接清理上一周期数据，返回成功
            jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
            return result;
        } catch (Exception e) {
            // 处理异常，则清理预生成的数据，将状态置为失败
            int affectRows = scheduleJobChainOutputParamDao.deleteByJobId(scheduleJob.getJobId());
            result.setRdosTaskStatus(RdosTaskStatus.FAILED);
            LOGGER.warn("output param select null, jobId:{} fail, remove output params, affectRows:{}", scheduleJob.getJobId(), affectRows);
            String log = String.format("jobId:%s，输出参数填充异常:%s", scheduleJob.getJobId(), ExceptionUtil.getErrorMessage(e));
            result.setLog(log);
            return result;
        }
    }

    /**
     * enginePlugins 类型的任务，判断计算型参数是否超过容量阈值
     *
     * @param jobOutputParams
     * @param scheduleJob
     * @param result
     * @return
     */
    private JobChainParamStatusResult judgeSpaceThreshold(List<ScheduleJobChainOutputParam> jobOutputParams, ScheduleJob scheduleJob, JobChainParamStatusResult result) {
        // worker 任务会产生 hdfs 文件
        List<String> hdfsPaths = jobOutputParams.stream()
                .filter(s -> s.getOutputParamType().equals(EOutputParamType.PROCESSED.getType()))
                .filter(s ->
                        JobChainParamHandler.supportWorkerTaskTypes().contains(s.getTaskType())
                        && !EScheduleJobType.SYNC.getType().equals(s.getTaskType())
                )
                .map(ScheduleJobChainOutputParam::getParamValue)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(hdfsPaths)) {
            // 本次有输出参数，但没有产生 hdfs 文件，则直接清理上一周期数据，返回成功
            jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
            return result;
        }

        // 判断计算型参数是否超过容量阈值
        Long dtuicTenantId = scheduleJob.getDtuicTenantId();
        JSONObject pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(scheduleJob.getProjectId(), scheduleJob.getAppType(), scheduleJob.getTaskType(), dtuicTenantId, ScheduleEngineType.Hadoop.getEngineName(), scheduleJob.getCreateUserId(), null,null, null);
        String typeName = componentService.buildHdfsTypeName(dtuicTenantId,null);
        Integer dataSourceCode = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
        pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
        pluginInfoWithComponentType.put("dataSourceType", dataSourceCode);
        String pluginInfo = pluginInfoWithComponentType.toJSONString();
        // 调用查询容量的接口，判断是否超过阈值，默认不超过阈值
        boolean anyOneTooLarge = false;
        try {
            anyOneTooLarge = jobChainParamQuerier.isAnyOneTooLarge(environmentContext.getJobChainOutputParamLimitSize().longValue(), dataSourceCode, dtuicTenantId, pluginInfo, hdfsPaths);
        } catch (Exception e) {
            // 查询容量接口异常
            LOGGER.error("isAnyOneTooLarge error, jobId:{}, hdfsPaths:{}", scheduleJob.getJobId(), hdfsPaths, e);
            result.setRdosTaskStatus(RdosTaskStatus.NOTFOUND);
            return result;
        }
        if (!anyOneTooLarge) {
            // 不存在超过阈值的文件，则直接清理上一周期数据，返回成功
            jobChainParamCleaner.asyncCleanLastCycJobOutputParamsIfNeed(scheduleJob);
            return result;
        }

        // 输出文件内容过大，需要将状态置为失败，清理掉此次生成的数据以及 HDFS 文件
        int affectRows = scheduleJobChainOutputParamDao.deleteByJobId(scheduleJob.getJobId());
        LOGGER.info("output param too large, jobId:{}, remove output params, affectRows:{}", scheduleJob.getJobId(), affectRows);
        jobChainParamQuerier.deleteHdfsFiles(scheduleJob.getDtuicTenantId(), dataSourceCode, pluginInfo, new HashSet<>(hdfsPaths));
        result.setRdosTaskStatus(RdosTaskStatus.FAILED);
        LOGGER.info("output param too large, jobId:{}, status:{}, hdfsPaths:{}", scheduleJob.getJobId(), RdosTaskStatus.FAILED, hdfsPaths);
        String log = String.format("存在输出参数:%s 占用空间大于%s", StringUtils.join(hdfsPaths, ","), environmentContext.getJobChainOutputParamLimitSize().longValue());
        result.setLog(log);
        return result;
    }

    /**
     * 最终文件路径
     *
     * @param rootPath
     * @param paramName
     * @param scheduleJob
     * @param project
     * @return
     */
    public static String generateFilePath(String rootPath, String paramName, ScheduleJob scheduleJob, ScheduleEngineProject project) {
        String fileRootPath = generateFileRootPath(rootPath, scheduleJob, project);
        String fileName = generateFileName(paramName, scheduleJob);
        return fileRootPath + "/" + fileName;
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

    /**
     * 顶层路径
     *
     * @param rootPath
     * @param scheduleJob
     * @param project
     * @return
     */
    public static String generateFileRootPath(String rootPath, ScheduleJob scheduleJob, ScheduleEngineProject project) {
        project = (project != null ? project : new ScheduleEngineProject());
        String projectUniqueKey = StringUtils.isNotEmpty(project.getProjectIdentifier()) ? project.getProjectIdentifier() : String.valueOf(scheduleJob.getProjectId());
        // ${租户id}/${项目标识}
        return rootPath + "/" + scheduleJob.getDtuicTenantId() + "/" + projectUniqueKey;
    }

    /**
     * 获取 ${name} 中的 name 字段
     *
     * @param sql
     * @return
     */
    private static String findParamName(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        Matcher matcher = VARIABLE_NAME_PATTERN.matcher(sql);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return sql;
        }
    }

    /**
     * 解析[]中的数字
     *
     * @param
     * @return
     */
    public static List<Integer> findParamIndexes(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }
        Matcher matcher = VARIABLE_INDEX_PATTERN.matcher(sql);
        List<Integer> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(Integer.valueOf(matcher.group()));
        }
        return result;
    }

    /**
     * 是否包含输入/出参配置
     *
     * @param taskChainParams
     * @return
     */
    public static boolean containsInOutParam(List<ScheduleTaskParamShade> taskChainParams) {
        if (CollectionUtils.isEmpty(taskChainParams)) {
            return false;
        }
        for (ScheduleTaskParamShade param : taskChainParams) {
            if (EParamType.INPUT.getType().equals(param.getType())
                    || EParamType.OUTPUT.getType().equals(param.getType())) {
                return true;
            }
        }
        return false;
    }

    public static List<ScheduleTaskChainParam> trans2TaskChainParams(List<ScheduleTaskParamShade> taskParamShades) {
        if (CollectionUtils.isEmpty(taskParamShades)) {
            return Collections.emptyList();
        }
        return taskParamShades.stream().map(s -> {
            ScheduleTaskChainParam t = new ScheduleTaskChainParam();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
    }

    public static List<ScheduleTaskParamShade> trans2TaskParamShades(List<ScheduleTaskChainParam> taskChainParams) {
        if (CollectionUtils.isEmpty(taskChainParams)) {
            return Collections.emptyList();
        }
        return taskChainParams.stream().map(s -> {
            ScheduleTaskParamShade t = new ScheduleTaskParamShade();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
    }

    /**
     * 是否支持上下游参数传递
     *
     * @param appType
     * @param taskType
     * @return
     */
    public static boolean supportChainParam(Integer appType, Integer taskType) {
        if (appType == null || taskType == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        if (!AppType.RDOS.getType().equals(appType)) {
            return false;
        }
        return isSupport(taskType);
    }

    /**
     * 校验配置项参数
     *
     * @param taskParamShades
     */
    public static void validateTaskParamShade(List<ScheduleTaskParamShade> taskParamShades, ScheduleTaskShade taskShade) {
        if (CollectionUtils.isEmpty(taskParamShades)) {
            return;
        }
        for (ScheduleTaskParamShade taskParamShade : taskParamShades) {
            // 入参多余参数需要设置为 null
            taskParamShade.setId(null);
            taskParamShade.setGmtCreate(null);
            taskParamShade.setGmtModified(null);
            taskParamShade.setAppType(taskShade.getAppType());
            taskParamShade.setTaskId(taskShade.getTaskId());
            taskParamShade.setTaskType(taskShade.getTaskType());
            Integer taskType = taskParamShade.getTaskType();
            String paramName = taskParamShade.getParamName();
            // 校验常规参数非空
            if (StringUtils.isEmpty(paramName)) {
                throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
            }
            Integer type = taskParamShade.getType();
            if (EParamType.getByType(type) == null) {
                throw new RdosDefineException("not support paramType:" + type);
            }
            // 校验输入参数非空
            if (EParamType.INPUT.getType().equals(type)) {
                Long fatherTaskId = taskParamShade.getFatherTaskId();
                taskParamShade.setFatherAppType(taskShade.getAppType());
                String fatherParamName = taskParamShade.getFatherParamName();
                if (fatherTaskId == null || StringUtils.isEmpty(fatherParamName)) {
                    throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
                }
                // 筛掉离线传来的无用数据
                taskParamShade.setParamCommand(null);
            } else if (EParamType.OUTPUT.getType().equals(type)) {
                // 校验输出参数非空
                Integer outputParamType = taskParamShade.getOutputParamType();
                String paramCommand = taskParamShade.getParamCommand();
                if (EOutputParamType.getByType(outputParamType) == null
                        || StringUtils.isEmpty(paramCommand)) {
                    throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
                }
                if (!isSupport(taskType, EParamType.OUTPUT)) {
                    throw new RdosDefineException(ErrorCode.NOT_SUPPORT);
                }
            }
        }
    }

    public static boolean isSupport(Integer taskType) {
        if (Objects.isNull(taskType)) {
            return false;
        }
        return ALL_SUPPORT_CHAIN_PARAM_TASK_TYPE.containsKey(taskType);
    }

    public static boolean isSupport(Integer taskType, EParamType eParamType) {
        if (Objects.isNull(taskType)) {
            return false;
        }
        List<EParamType> supportParamTypes = ALL_SUPPORT_CHAIN_PARAM_TASK_TYPE.get(taskType);
        if (CollectionUtils.isEmpty(supportParamTypes)) {
            return false;
        }
        return supportParamTypes.contains(eParamType);
    }

    public static Set<Integer> supportRdbTaskTypes() {
        return SUPPORT_RDB_TASK_TYPE.keySet();
    }

    public static Set<Integer> supportWorkerTaskTypes() {
        return SUPPORT_WORKER_TASK_TYPE.keySet();
    }

    public static Set<Integer> supportOtherTaskTypes() {
        return SUPPORT_OTHER_TASK_TYPE.keySet();
    }

    public static String getRootOutputPath() {
        return ROOT_OUTPUT_PATH;
    }
}