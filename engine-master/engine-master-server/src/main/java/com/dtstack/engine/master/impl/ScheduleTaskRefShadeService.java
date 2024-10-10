package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.dtcenter.loader.utils.AssertUtils;
import com.dtstack.engine.api.domain.ConsoleParam;
import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskRefShade;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ConsoleParamBO;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.JobResourceFileTypeEnum;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.api.vo.task.SaveTaskRefResultVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobResourceFileDao;
import com.dtstack.engine.dao.ScheduleTaskCommitMapper;
import com.dtstack.engine.dao.ScheduleTaskRefShadeDao;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.graph.DirectedGraphCycleJudge;
import com.dtstack.engine.master.graph.GenericDirectedGraphCycleJudge;
import com.dtstack.engine.master.graph.adapter.ScheduleTaskRefShadeGraphSideAdapter;
import com.dtstack.engine.master.mapstruct.ParamStruct;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.TaskUtils;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.po.ScheduleJobResourceFile;
import com.dtstack.engine.po.ScheduleTaskCommit;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.TaskConstant.UPLOADPATH;


@Service
@Slf4j
public class ScheduleTaskRefShadeService {

    public static final String SHIP_FILES_KEY = "dtscript.ship-files";

    public static final String TEMP_JOB_REF_TASK_IDS_KEY = "refTaskIds";

    public static final String TASK_NOT_EXIST_ERROR_MESSAGE = "被引用的任务不存在：%s";

    private static final String UPLOAD_FILE_PATH_TEMPLATE = "--files %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskRefShadeService.class);


    @Autowired
    private ScheduleTaskRefShadeDao scheduleTaskRefShadeDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleTaskCommitMapper scheduleTaskCommitMapper;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private ParamStruct paramStruct;

    private static final Set<Integer> SUPPORT_TASK_REF_TYPE = new HashSet<>();

    private static final Map<String, Set<Integer>> SUPPORT_TASK_REF_TYPE_GROUP = new HashMap<>();

    private static final ThreadLocal<List<ScheduleTaskRefShade>> TASK_REF_JUDGE_HISTORY = new ThreadLocal<>();

    private static final String DT_SCRIPT_GROUP_KEY = "dtScript";

    private static final String AGENT_SCRIPT_GROUP_KEY = "agent";

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobResourceFileDao scheduleJobResourceFileDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    static {
        SUPPORT_TASK_REF_TYPE.add(EScheduleJobType.PYTHON.getType());
        SUPPORT_TASK_REF_TYPE.add(EScheduleJobType.SHELL.getType());
        SUPPORT_TASK_REF_TYPE.add(EScheduleJobType.PYTHON_ON_AGENT.getType());
        SUPPORT_TASK_REF_TYPE.add(EScheduleJobType.SHELL_ON_AGENT.getType());

        SUPPORT_TASK_REF_TYPE_GROUP.put(DT_SCRIPT_GROUP_KEY, ImmutableSet.of(EScheduleJobType.PYTHON.getType(), EScheduleJobType.SHELL.getType()));
        SUPPORT_TASK_REF_TYPE_GROUP.put(AGENT_SCRIPT_GROUP_KEY, ImmutableSet.of(EScheduleJobType.PYTHON_ON_AGENT.getType(), EScheduleJobType.SHELL_ON_AGENT.getType()));
    }

    @Transactional(rollbackFor = Exception.class)
    public SaveTaskRefResultVO saveTaskRefList(String taskRefListJsonStr, String commitId) {
        List<ScheduleTaskRefShade> taskRefs = JSONObject.parseArray(taskRefListJsonStr, ScheduleTaskRefShade.class);
        SaveTaskRefResultVO checkResult = perCheck(taskRefs, commitId);
        if (checkResult.getSuccess()) {
            insertRecord(taskRefs);
        }
        return checkResult;
    }


    public List<JobResourceFile> getJobRefResourceFiles(Map<String, Object> actionParam, ScheduleJob scheduleJob) throws Exception {
        Integer taskType = scheduleJob.getTaskType();
        if (!isSupportRefJob(taskType)) {
            return new ArrayList<>();
        }
        Integer scheduleJobType = scheduleJob.getType();
        if (EScheduleType.TEMP_JOB.getType().equals(scheduleJobType)) {
            return processTempJobRefResource(actionParam, scheduleJob.getAppType(), scheduleJob.getCycTime());
        }
        // 非临时运行，从 job 上游遍历出所有引用的资源信息
        CheckJobRefResult checkJobRefResult = travelJobRef(scheduleJob, false);
        return checkJobRefResult.getJobResourceFiles();
    }

    public List<JobResourceFile> processTempJobRefResource(Map<String, Object> actionParam, Integer appType, String cycTime) throws Exception {
        List<JobResourceFile> result = new ArrayList<>();
        // 临时运行,从 actionParams 中解析出引用的任务(包括引用的引用)，将引用的任务参数替换，并上传 hdfs T_T
        List<ScheduleTaskShade> refTaskShades = getRefTaskShadesAndCheck(actionParam, appType);
        if (CollectionUtils.isEmpty(refTaskShades)) {
            return result;
        }
        Long uicUserId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        return doProcessTempJobRefResource(refTaskShades, cycTime, uicUserId);
    }

    private List<JobResourceFile> doProcessTempJobRefResource(List<ScheduleTaskShade> refTaskShades, String cycTime, Long uicUserId) throws Exception {
        if (CollectionUtils.isEmpty(refTaskShades)) {
            return new ArrayList<>();
        }
        // 预处理，参数替换并上传 hdfs
        return preprocessingTaskShade(refTaskShades, cycTime, uicUserId);
    }

    private List<JobResourceFile> preprocessingTaskShade(List<ScheduleTaskShade> refTaskShades, String cycTime, Long uicUserId) throws IOException {
        List<JobResourceFile> result = new ArrayList<>();

        Set<Long> alreadyProcess = new HashSet<>();

        for (ScheduleTaskShade taskShade : refTaskShades) {
            recursionPreprocessing(taskShade, cycTime, alreadyProcess, result, uicUserId);
        }

        return result;
    }

    private void recursionPreprocessing(ScheduleTaskShade taskShade,
                                        String cycTime,
                                        Set<Long> alreadyProcess, List<JobResourceFile> result,
                                        Long uicUserId) throws IOException {
        if (alreadyProcess.contains(taskShade.getTaskId())) {
            return;
        }
        Long taskId = taskShade.getTaskId();
        List<ScheduleTaskRefShade> refShades = scheduleTaskRefShadeDao.listRefTaskByTaskIds(Lists.newArrayList(taskId), taskShade.getAppType());

        Set<Long> sets = new HashSet<>();
        refShades.forEach(e -> sets.add(e.getRefTaskId()));

        // 没有引用任务，或者引用任务已经被处理了
        if (CollectionUtils.isEmpty(refShades) || alreadyProcess.containsAll(sets)) {
            // 被引用的任务都已经被处理了，已经在 result 中了
            if (CollectionUtils.isNotEmpty(refShades)) {
                doPreprocessingTaskShade(taskShade, cycTime, refShades, result, uicUserId);
            } else {
                String content = paramReplace(taskShade, cycTime);
                // 直接处理当前任务
                doPreprocessingTaskShade(taskShade, result, content, uicUserId);
            }
            alreadyProcess.add(taskShade.getTaskId());
            return;
        }
        for (ScheduleTaskRefShade refShade : refShades) {

            ScheduleTaskShade ref = scheduleTaskShadeService.getBatchTaskById(refShade.getRefTaskId(), refShade.getAppType());
            AssertUtils.notNull(ref, String.format(TASK_NOT_EXIST_ERROR_MESSAGE, taskId));

            recursionPreprocessing(ref, cycTime, alreadyProcess, result, uicUserId);
        }

        doPreprocessingTaskShade(taskShade, cycTime, refShades, result, uicUserId);
        alreadyProcess.add(taskShade.getTaskId());
    }

    /**
     * 临时运行，py被引用任务的预处理。主要将 python 和 shell 任务的脚本进行参数替换，然后上传 hdfs, 然后将 exeArgs 改成
     * --files {hdfsPath} 主要是为了
     * {@link com.dtstack.engine.master.impl.ScheduleTaskRefShadeService#getResourceFilesFromTaskShade(java.lang.String, com.dtstack.engine.api.domain.ScheduleTaskShade)}
     *
     * @param refTaskShade
     * @throws IOException
     */
    private void doPreprocessingTaskShade(ScheduleTaskShade refTaskShade,
                                          List<JobResourceFile> result,
                                          String content,
                                          Long uicUserId) throws IOException {
        Integer taskType = refTaskShade.getTaskType();
        if (SUPPORT_TASK_REF_TYPE_GROUP.get(DT_SCRIPT_GROUP_KEY).contains(taskType)) {
            doPreprocessingTaskShaeForDTScript(refTaskShade, result, content, uicUserId);
            return;
        }
        if (SUPPORT_TASK_REF_TYPE_GROUP.get(AGENT_SCRIPT_GROUP_KEY).contains(taskType)) {
            doPreprocessingTaskShaeForAgent(refTaskShade, result, content);
        }
    }

    private void doPreprocessingTaskShaeForDTScript(ScheduleTaskShade refTaskShade,
                                                    List<JobResourceFile> result,
                                                    String content,
                                                    Long uicUserId) {
        try {
            String uploadHdfsPath = getUploadHdfsPath(refTaskShade);
            uploadHdfsPath = uploadHdfs(refTaskShade, content, uploadHdfsPath, uicUserId);

            result.addAll(getResourceFilesFromTaskShade(String.format(UPLOAD_FILE_PATH_TEMPLATE, uploadHdfsPath), refTaskShade));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doPreprocessingTaskShaeForAgent(ScheduleTaskShade refTaskShade,
                                                 List<JobResourceFile> result,
                                                 String content) throws IOException {
        JSONObject info = JSONObject.parseObject(refTaskShade.getExtraInfo());
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        String exeArgs = Objects.toString(actionParam.get(GlobalConst.EXE_ARGS), org.apache.commons.lang.StringUtils.EMPTY);
        result.addAll(getResourceFilesForAgentJob(exeArgs,refTaskShade, content));
    }


    private void doPreprocessingTaskShade(ScheduleTaskShade taskShade,
                                          String cycTime,
                                          List<ScheduleTaskRefShade> refShades, List<JobResourceFile> result,
                                          Long uicUserId) throws IOException {

        Set<String> refTaskNames = new HashSet<>();
        for (ScheduleTaskRefShade refShade : refShades) {
            ScheduleTaskShade batchTaskById = scheduleTaskShadeService.getBatchTaskById(refShade.getRefTaskId(), refShade.getAppType());
            refTaskNames.add(batchTaskById.getName());
        }
        List<JobResourceFile> collect = result.stream().filter(e -> refTaskNames.contains(e.getTaskName())).collect(Collectors.toList());

        String content = paramReplace(taskShade, cycTime);
        content = replaceRefFileContent(content, collect);
        doPreprocessingTaskShade(taskShade, result, content, uicUserId);
    }

    private String uploadHdfs(ScheduleTaskShade refTaskShade, String content, String uploadHdfsPath, Long uicUserId) {
        Integer taskType = refTaskShade.getTaskType();
        Long projectId = refTaskShade.getProjectId();
        Long dtUicTenantId = refTaskShade.getDtuicTenantId();
        Integer appType = refTaskShade.getAppType();
        if (uicUserId == null) {
            uicUserId = refTaskShade.getOwnerUserId();
        }
        JSONObject pluginInfo = pluginInfoManager.buildTaskPluginInfo(projectId, appType, taskType, dtUicTenantId, ScheduleEngineType.Hadoop.getEngineName(), uicUserId, null, null, null);
        String typeName = componentService.buildHdfsTypeName(dtUicTenantId, null);
        Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, typeName);
        pluginInfo.put("dataSourceType", dataSourceCodeByDiceName);
        IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCodeByDiceName);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtUicTenantId);
        return hdfs.uploadStringToHdfs(sourceDTO, content, uploadHdfsPath);
    }

    private String getUploadHdfsPath(ScheduleTaskShade refTaskShade) {
        String fileName = FileUtil.getTempJobRefUploadFileName(refTaskShade.getTaskType(), refTaskShade.getTaskId());
        return environmentContext.getHdfsTaskPath() + fileName;
    }

    private String paramReplace(ScheduleTaskShade refTaskShade, String cycTime) {
        Integer taskType = refTaskShade.getTaskType();
        String wrapExtraInfo = scheduleTaskShadeDao.getExtInfoByTaskId(refTaskShade.getTaskId(), refTaskShade.getAppType());
        JSONObject actualExtraInfo = Optional.ofNullable(wrapExtraInfo)
                .map(JSONObject::parseObject)
                .orElse(new JSONObject());

        List<ScheduleTaskParamShade> taskParamsToReplace = fillParam(actualExtraInfo, refTaskShade.getTaskId(), refTaskShade.getAppType());

        String content = refTaskShade.getSqlText();
        if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamsToReplace)) {
            content = jobParamReplace.paramReplace(content, taskParamsToReplace, cycTime, EScheduleType.TEMP_JOB.getType(), refTaskShade.getProjectId());
        }

        if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
            content = content.replaceAll("\r\n", System.getProperty("line.separator"));
        }
        return content;
    }

    private List<ScheduleTaskParamShade> fillParam(Map<String, Object> actionParam, Long taskId, Integer appType) {
        // 用户自定义参数
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);
        if (CollectionUtils.isEmpty(taskParamsToReplace)) {
            taskParamsToReplace = new ArrayList<>();
        }
        // 系统参数
        List<ConsoleParam> consoleSysParams = paramService.selectSysParam();
        List<ConsoleParamBO> consoleParamBOS = paramStruct.toBOs(consoleSysParams);

        // 全局参数
        List<ConsoleParamBO> taskBindParams = paramService.selectByTaskId(taskId, appType);
        taskBindParams.addAll(consoleParamBOS);
        List<ScheduleTaskParamShade> allScheduleJobParamShades = Lists.newArrayList();
        allScheduleJobParamShades.addAll(paramStruct.BOtoTaskParams(taskBindParams));
        allScheduleJobParamShades.addAll(taskParamsToReplace);
        return allScheduleJobParamShades;
    }


    public List<ScheduleTaskShade> getRefTaskShadesAndCheck(Map<String, Object> actionParam, Integer appType) {
        try {
            List<ScheduleTaskShade> result = new ArrayList<>();
            String refTaskIdsStr = (String) actionParam.get(TEMP_JOB_REF_TASK_IDS_KEY);
            if (StringUtils.isBlank(refTaskIdsStr)) {
                return result;
            }
            // 直接依赖的任务id
            List<Long> directRefTaskIds = JSONObject.parseObject(refTaskIdsStr, new TypeReference<List<Long>>() {
            });

            // 直接依赖的任务存在的引用关系
            List<ScheduleTaskRefShade> refShades = getExistTaskRef(directRefTaskIds, appType);
            if (checkCycle(refShades)) {
                throw new RdosDefineException(SaveTaskRefResultVO.REF_FAIL_CYCLE);
            }

            List<ScheduleTaskRefShade> allRefShade = TASK_REF_JUDGE_HISTORY.get();

            Set<Long> allTaskIds = new HashSet<>();

            for (ScheduleTaskRefShade refShade : allRefShade) {
                allTaskIds.add(refShade.getTaskId());
            }

            allTaskIds.addAll(directRefTaskIds);

            for (Long taskId : allTaskIds) {
                ScheduleTaskShade taskShade = scheduleTaskShadeDao.getOneByTaskIdAndAppType(taskId, appType);
                // check
                AssertUtils.notNull(taskShade, String.format(TASK_NOT_EXIST_ERROR_MESSAGE, taskId));
                result.add(taskShade);
            }
            return result;
        } finally {
            TASK_REF_JUDGE_HISTORY.remove();
        }
    }

    private List<ScheduleTaskRefShade> getExistTaskRef(List<Long> refTaskIds, Integer appType) {
        return scheduleTaskRefShadeDao.listRefTaskByTaskIds(refTaskIds, appType);
    }

    public String replaceRefFileContent(String sqlText, List<JobResourceFile> jobResourceFiles, Long subTaskId, Integer scheduleType) {
        if (EScheduleType.TEMP_JOB.getType().equals(scheduleType)) {
            return replaceRefFileContent(sqlText, jobResourceFiles);
        }
        // 非临时运行，只取直接引用的资源文件
        // 因为周期运行时可能存在两个相同的 taskName，但其实是不同周期的，当前替换只替换当前任务直接依赖的
        // com.dtstack.engine.api.domain.JobResourceFile.subTaskId 来源于 com.dtstack.engine.master.impl.ScheduleTaskRefShadeService.getJobRefResource
        List<JobResourceFile> directJobResourceFiles = jobResourceFiles.stream().filter(e -> Objects.nonNull(e.getSubTaskId()) && e.getSubTaskId().equals(subTaskId)).collect(Collectors.toList());
        return replaceRefFileContent(sqlText, directJobResourceFiles);
    }


    public String replaceRefFileContent(String sqlText, List<JobResourceFile> jobResourceFiles) {
        // 将 sqlText 里的内容替换一下，任务名称替换成任务实例对应的sqlText上传后 file 名称 T_T
        if (CollectionUtils.isEmpty(jobResourceFiles)) {
            return sqlText;
        }
        for (JobResourceFile jobResourceFile : jobResourceFiles) {
            // 只替换任务本身的资源文件
            if (JobResourceFileTypeEnum.SHIP_FILE.equals(jobResourceFile.getType())) {
                continue;
            }
            String fileName = jobResourceFile.getFileName();
            String taskName = jobResourceFile.getTaskName();
            if (StringUtils.isBlank(fileName) || StringUtils.isBlank(taskName)) {
                continue;
            }
            sqlText = sqlText.replaceAll(taskName, fileName);
        }
        return sqlText;
    }


    public String processTaskParamForShipFiles(String taskParams, List<JobResourceFile> jobRefResourceFiles) throws IOException {
        Properties properties = PublicUtil.stringToProperties(taskParams);
        if (CollectionUtils.isNotEmpty(jobRefResourceFiles)) {
            String shipFiles = Optional.ofNullable(properties.getProperty(SHIP_FILES_KEY)).orElse(StringUtils.EMPTY);
            String mergeResult = mergeRefShipFiles(jobRefResourceFiles);
            if (StringUtils.isNotBlank(shipFiles) && !shipFiles.endsWith(GlobalConst.COMMA)) {
                shipFiles = shipFiles + GlobalConst.COMMA + mergeResult;
            } else {
                shipFiles += mergeResult;
            }
            properties.setProperty(SHIP_FILES_KEY, shipFiles);
            return PublicUtil.propertiesToString(properties);
        }

        return taskParams;
    }

    private String mergeRefShipFiles(List<JobResourceFile> jobRefResourceFiles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (JobResourceFile jobRefResourceFile : jobRefResourceFiles) {
            String path = jobRefResourceFile.getPath();
            if (StringUtils.isBlank(path)) {
                continue;
            }

            stringBuilder.append(path);
            stringBuilder.append(GlobalConst.COMMA);
        }
        String result = stringBuilder.toString();
        if (result.endsWith(GlobalConst.COMMA)) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }


    // 遍历 Job 的任务引用关系
    public CheckJobRefResult travelJobRef(ScheduleJob scheduleJob, boolean judgeBreak) {
        CheckJobRefResult result = new CheckJobRefResult();
        List<JobResourceFile> allJobResourceFiles = new ArrayList<>();
        Deque<ScheduleJob> queue = new LinkedList<>();
        queue.push(scheduleJob);
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; ++i) {
                ScheduleJob job = queue.poll();
                if (Objects.isNull(job)) {
                    continue;
                }
                // 引用任务
                List<Long> refTaskIds = getRefTaskIdsByJob(job);
                for (Long refTaskId : refTaskIds) {
                    // 引用任务的 job, 这里目前只取相同 appType 的 job，且状态为 FINISHED => 5
                    List<ScheduleJob> refJobs = getBeforeRefJobsByJobAndRefTaskId(refTaskId, job);
                    // 判断引用关系
                    if (judgeBreak) {
                        // 引用任务的实例不存在，或者状态不是成功
                        if (CollectionUtils.isEmpty(refJobs)) {
                            ScheduleTaskShade batchTaskById = scheduleTaskShadeService.getBatchTaskById(refTaskId, scheduleJob.getAppType());
                            return CheckJobRefResult.refJobNotFinishedFail(refTaskId, batchTaskById.getName(), job.getCycTime());
                        }
                        ScheduleJob refJob = refJobs.get(0);
                        if (!isSupportRefJob(refJob.getTaskType())) {
                            ScheduleTaskShade batchTaskById = scheduleTaskShadeService.getBatchTaskById(refTaskId, scheduleJob.getAppType());
                            return CheckJobRefResult.refJobNotSupportFail(refTaskId, batchTaskById.getName());
                        }
                    }
                    // 下一层引用任务入队
                    if (CollectionUtils.isNotEmpty(refJobs)) {
                        ScheduleJob refJob = refJobs.get(0);
                        // 记录下 job 涉及的资源
                        List<JobResourceFile> jobResourceFiles = getJobRefResource(job.getTaskId(), refJob);
                        allJobResourceFiles.addAll(jobResourceFiles);
                        queue.offer(refJob);
                    }
                }
            }
        }

        result.setJobRefBreak(false);
        result.setJobResourceFiles(allJobResourceFiles);
        return result;
    }

    private List<ScheduleJob> getBeforeRefJobsByJobAndRefTaskId(Long refTaskId, ScheduleJob job) {
        String cycTime = job.getCycTime();
        Integer appType = job.getAppType();
        Integer scheduleType = job.getType();
        return scheduleJobDao.listIdByTaskIdAndStatusBeforeCyctime(refTaskId, Lists.newArrayList(RdosTaskStatus.FINISHED.getStatus()), appType, cycTime, scheduleType);
    }

    public List<Long> getRefTaskIdsByJob(ScheduleJob job) {
        Integer appType = job.getAppType();
        Long taskId = job.getTaskId();
        // 任务引用
        return scheduleTaskRefShadeDao.listRefTaskIdByTaskId(taskId, appType);
    }

    private List<JobResourceFile> getJobRefResource(Long subTaskId, ScheduleJob refJob) {
        List<JobResourceFile> resourceFiles = new ArrayList<>();
        ScheduleJobResourceFile scheduleJobResourceFile = scheduleJobResourceFileDao.getByJobId(refJob.getJobId());
        String jobRefResourcesJson = Optional.ofNullable(scheduleJobResourceFile).map(ScheduleJobResourceFile::getJobResourceFiles).orElse(StringUtils.EMPTY);
        if (StringUtils.isNotBlank(jobRefResourcesJson)) {
            resourceFiles = JSONObject.parseObject(jobRefResourcesJson, new com.alibaba.fastjson.TypeReference<List<JobResourceFile>>() {
            });
            resourceFiles.forEach(e -> e.setSubTaskId(subTaskId));
        }
        return resourceFiles;
    }

    private SaveTaskRefResultVO perCheck(List<ScheduleTaskRefShade> taskRefs, String commitId) {
        if (CollectionUtils.isEmpty(taskRefs)) {
            return SaveTaskRefResultVO.success();
        }

        // 检查引用任务是否存在
        if (!checkRefExist(taskRefs, commitId)) {
            return SaveTaskRefResultVO.notExistFail();
        }

        // 检查任务类型，任务引用仅支持 python 和 shell 任务
        if (!checkTaskType(taskRefs, commitId)) {
            return SaveTaskRefResultVO.taskTypeNotSupportFail();
        }

        // 检查引用关系是否成环
        if (checkCycle(taskRefs)) {
            return SaveTaskRefResultVO.cycleFail();
        }
        return SaveTaskRefResultVO.success();
    }

    private boolean checkTaskType(List<ScheduleTaskRefShade> taskRefs, String commitId) {
        for (ScheduleTaskRefShade refShade : taskRefs) {
            Long taskId = refShade.getTaskId();
            Integer appType = refShade.getAppType();
            Long refTaskId = refShade.getRefTaskId();
            Integer refAppType = refShade.getRefAppType();
            return doCheckTaskType(taskId, appType, commitId) && doCheckTaskType(refTaskId, refAppType, commitId);
        }
        return true;
    }

    private boolean doCheckTaskType(Long taskId, Integer appType, String commitId) {
        Integer taskType;
        ScheduleTaskShade taskShade = scheduleTaskShadeService.getBatchTaskById(taskId, appType);
        if (Objects.nonNull(taskShade)) {
            taskType = taskShade.getTaskType();
        } else {
            ScheduleTaskCommit commitTask = scheduleTaskCommitMapper.getTaskCommitByTaskId(taskId, appType, commitId);
            taskType = getTaskTypeFromCommitTaskJson(commitTask.getTaskJson());
        }

        return EScheduleJobType.SHELL.getType().equals(taskType)
                || EScheduleJobType.PYTHON.getType().equals(taskType)
                || EScheduleJobType.SHELL_ON_AGENT.getType().equals(taskType)
                || EScheduleJobType.PYTHON_ON_AGENT.getType().equals(taskType);
    }

    private Integer getTaskTypeFromCommitTaskJson(String taskJson) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(taskJson);
            return jsonObject.getInteger("taskType");
        } catch (Throwable e) {
            LOGGER.error("getTaskTypeFromCommitTaskJson error: {}", e.getMessage(), e);
            return null;
        }
    }


    private boolean checkRefExist(List<ScheduleTaskRefShade> taskRefs, String commitId) {
        for (ScheduleTaskRefShade refShade : taskRefs) {
            Long refTaskId = refShade.getRefTaskId();
            Integer appType = refShade.getAppType();
            Integer refAppType = Optional.ofNullable(refShade.getRefAppType()).orElse(appType);

            if (!appType.equals(refAppType)) {
                return Objects.nonNull(scheduleTaskShadeService.getBatchTaskById(refTaskId, refAppType));
            }

            if (StringUtils.isNotBlank(commitId)) {
                ScheduleTaskShade refTaskShade = scheduleTaskShadeService.getBatchTaskById(refTaskId, refAppType);
                if (refTaskShade == null) {
                    return Objects.nonNull(scheduleTaskCommitMapper.getTaskCommitByTaskId(refTaskId, refAppType, commitId));
                }
                return true;
            }
            return false;
        }
        return true;
    }


    private boolean checkCycle(List<ScheduleTaskRefShade> taskRefs) {
        List<ScheduleTaskRefShadeGraphSideAdapter> adapters = ScheduleTaskRefShadeGraphSideAdapter.build(taskRefs);
        DirectedGraphCycleJudge<String, Long, ScheduleTaskRefShadeGraphSideAdapter> cycleJudge = new GenericDirectedGraphCycleJudge<>(adapters);

        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> parentProvider =
                taskKeys -> ScheduleTaskRefShadeGraphSideAdapter.build(scheduleTaskRefShadeDao.listByTaskKeys(taskKeys));

        Function<List<String>, List<ScheduleTaskRefShadeGraphSideAdapter>> childProvider =
                taskKeys -> ScheduleTaskRefShadeGraphSideAdapter.build(scheduleTaskRefShadeDao.listByRefTaskKeys(taskKeys));


        boolean cycle = cycleJudge.isCycle(parentProvider, childProvider);

        Set<ScheduleTaskRefShadeGraphSideAdapter> accessHistory = cycleJudge.accessHistory();

        TASK_REF_JUDGE_HISTORY.remove();
        TASK_REF_JUDGE_HISTORY.set(ScheduleTaskRefShadeGraphSideAdapter.extract(accessHistory));

        return cycle;
    }


    private void insertRecord(List<ScheduleTaskRefShade> taskRefs) {
        List<ScheduleTaskRefShade> deduplicationTaskRefs = deduplication(taskRefs);
        for (ScheduleTaskRefShade deduplicationTaskRef : deduplicationTaskRefs) {
            // 清除原有关系，再插入
            scheduleTaskRefShadeDao.deleteByTaskIdAndRefTaskIdAndAppType(deduplicationTaskRef.getTaskId(), deduplicationTaskRef.getRefTaskId(), deduplicationTaskRef.getAppType());
            scheduleTaskRefShadeDao.insert(deduplicationTaskRef);
        }
    }


    // 去重
    private List<ScheduleTaskRefShade> deduplication(List<ScheduleTaskRefShade> taskRefs) {
        List<ScheduleTaskRefShadeGraphSideAdapter> build = ScheduleTaskRefShadeGraphSideAdapter.build(taskRefs);
        return ScheduleTaskRefShadeGraphSideAdapter.extract(new HashSet<>(build));
    }


    public boolean isSupportRefJob(Integer taskType) {
        return SUPPORT_TASK_REF_TYPE.contains(taskType);
    }

    public List<JobResourceFile> getResourceFilesFromTaskShade(String taskExeArgs, ScheduleTaskShade taskShade) throws IOException {
        List<JobResourceFile> result = new ArrayList<>(2);
        String taskFiles = TaskUtils.parseTaskArgs(taskExeArgs, "--files");
        if (org.apache.commons.lang.StringUtils.isNotBlank(taskFiles)) {
            JobResourceFile taskFile = new JobResourceFile();
            taskFile.setType(JobResourceFileTypeEnum.TASK_FILE);
            taskFile.setPath(taskFiles);
            taskFile.setFileName(FileUtil.getOriginalFileName(taskFiles));
            taskFile.setTaskName(taskShade.getName());
            result.add(taskFile);
        }

        String taskParams = taskShade.getTaskParams();
        if (org.apache.commons.lang.StringUtils.isNotBlank(taskParams)) {
            Properties properties = PublicUtil.stringToProperties(taskParams);
            String shipFiles = properties.getProperty(ScheduleTaskRefShadeService.SHIP_FILES_KEY, org.apache.commons.lang.StringUtils.EMPTY);
            if (org.apache.commons.lang.StringUtils.isNotBlank(shipFiles)) {
                JobResourceFile shipFile = new JobResourceFile();
                shipFile.setType(JobResourceFileTypeEnum.SHIP_FILE);
                shipFile.setPath(shipFiles);
                shipFile.setFileName(FileUtil.getOriginalFileName(shipFiles));
                shipFile.setTaskName(taskShade.getName());
                result.add(shipFile);
            }
        }
        return result;
    }

    public List<JobResourceFile> getResourceFilesForAgentJob(String taskExeArgs,
                                                             ScheduleTaskShade taskShade,
                                                             String realUserScript) throws IOException {
        List<JobResourceFile> result = new ArrayList<>(2);

        // 引用资源
        String taskParams = taskShade.getTaskParams();
        if (org.apache.commons.lang.StringUtils.isNotBlank(taskParams)) {
            Properties properties = PublicUtil.stringToProperties(taskParams);
            String shipFiles = properties.getProperty(ScheduleTaskRefShadeService.SHIP_FILES_KEY, org.apache.commons.lang.StringUtils.EMPTY);
            if (org.apache.commons.lang.StringUtils.isNotBlank(shipFiles)) {
                JobResourceFile shipFile = new JobResourceFile();
                shipFile.setType(JobResourceFileTypeEnum.SHIP_FILE);
                shipFile.setPath(shipFiles);
                shipFile.setFileName(FileUtil.getOriginalFileName(shipFiles));
                shipFile.setTaskName(taskShade.getName());
                result.add(shipFile);
            }
        }

        // 资源上传
        String taskFiles = TaskUtils.parseTaskArgs(taskExeArgs, "--files");
        if (org.apache.commons.lang.StringUtils.isNotBlank(taskFiles)
                && !UPLOADPATH.equals(taskFiles)
                && taskFiles.trim().startsWith("hdfs://")) {
            JobResourceFile taskFile = new JobResourceFile();
            taskFile.setType(JobResourceFileTypeEnum.TASK_FILE);
            taskFile.setPath(taskFiles);
            taskFile.setFileName(FileUtil.getOriginalFileName(taskFiles));
            taskFile.setTaskName(taskShade.getName());
            taskFile.setTaskType(taskShade.getTaskType());
            result.add(taskFile);
            return result;
        }

        // web 编辑
        if (org.apache.commons.lang.StringUtils.isNotBlank(realUserScript)) {
            JobResourceFile taskFile = new JobResourceFile();
            taskFile.setType(JobResourceFileTypeEnum.TASK_FILE);
            taskFile.setFileName(taskShade.getName());
            taskFile.setTaskName(taskShade.getName());
            taskFile.setTaskType(taskShade.getTaskType());
            taskFile.setContent(realUserScript);
            result.add(taskFile);
        }

        return result;
    }


    public static class CheckJobRefResult {

        private static final String NOT_FINISHED_STATUS_REASON = "引用任务 [%s,taskId: %s] 在执行时间 [%s] 之前不存在实例或者实例状态为非成功状态";

        private static final String NOT_SUPPORT_REF_TASK_TYPE = "引用任务 [%s,taskId: %s] 任务类型不支持, 仅支持 python/shell 任务引用";

        private List<JobResourceFile> jobResourceFiles;

        private Boolean isJobRefBreak;

        private Long breakTaskId;

        private String breakReason;

        private String breakCycTime;

        public List<JobResourceFile> getJobResourceFiles() {
            return jobResourceFiles;
        }

        public void setJobResourceFiles(List<JobResourceFile> jobResourceFiles) {
            this.jobResourceFiles = jobResourceFiles;
        }

        public Boolean getJobRefBreak() {
            return isJobRefBreak;
        }

        public void setJobRefBreak(Boolean jobRefBreak) {
            isJobRefBreak = jobRefBreak;
        }

        public Long getBreakTaskId() {
            return breakTaskId;
        }

        public void setBreakTaskId(Long breakTaskId) {
            this.breakTaskId = breakTaskId;
        }

        public String getBreakReason() {
            return breakReason;
        }

        public void setBreakReason(String breakReason) {
            this.breakReason = breakReason;
        }

        public String getBreakCycTime() {
            return breakCycTime;
        }

        public void setBreakCycTime(String breakCycTime) {
            this.breakCycTime = breakCycTime;
        }

        public static CheckJobRefResult refJobNotFinishedFail(Long breakTaskId, String taskName, String breakCycTime) {
            CheckJobRefResult failResult = new CheckJobRefResult();
            failResult.setJobRefBreak(true);
            failResult.setBreakTaskId(breakTaskId);
            failResult.setBreakCycTime(breakCycTime);
            failResult.setBreakReason(String.format(NOT_FINISHED_STATUS_REASON, taskName, breakTaskId, breakCycTime));
            return failResult;
        }

        public static CheckJobRefResult refJobNotSupportFail(Long breakTaskId, String taskName) {
            CheckJobRefResult failResult = new CheckJobRefResult();
            failResult.setJobRefBreak(true);
            failResult.setBreakTaskId(breakTaskId);
            failResult.setBreakReason(String.format(NOT_SUPPORT_REF_TASK_TYPE, taskName, breakTaskId));
            return failResult;
        }
    }

}
