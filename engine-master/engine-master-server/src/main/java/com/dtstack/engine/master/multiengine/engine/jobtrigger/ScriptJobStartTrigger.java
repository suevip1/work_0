package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobResourceFile;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleJobResourceFileDao;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.RdosWrapper;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.engine.master.utils.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-04 10:40
 * Script 任务相关
 */
@Component
public class ScriptJobStartTrigger extends HadoopJobStartTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptJobStartTrigger.class);

    @Autowired
    private RdosWrapper rdosWrapper;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private JobChainParamHandler jobChainParamHandler;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleTaskRefShadeService scheduleTaskRefShadeService;

    @Autowired
    private ScheduleJobResourceFileDao scheduleJobResourceFileDao;

    @Autowired
    private PluginInfoManager pluginInfoManager;

    @Override
    protected void doProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        //提交
        String exeArgs = Objects.toString(actionParam.get(GlobalConst.EXE_ARGS), StringUtils.EMPTY);
        List<ScheduleTaskParamShade> taskParamsToReplace = (List<ScheduleTaskParamShade>)actionParam.getOrDefault(GlobalConst.taskParamToReplace, Collections.emptyList());
        //替换系统参数
        String content = jobParamReplace.paramReplace(exeArgs, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        //替换 jobId
        String taskExeArgs = content.replace(TaskConstant.JOB_ID, scheduleJob.getJobId());
        //提交上传路径
        String sqlTextFromTaskShade = taskShade.getSqlText();

        // 处理任务上下游参数 -- 由于历史原因这里针对 taskShade 中的 sqlText 进行处理(离线的 pyspark 任务的 sqlText 在 extraInfo 和 taskShade 并不一样)
        JobChainParamHandleResult jobChainParamHandleResult = jobChainParamHandler.handle(sqlTextFromTaskShade, taskShade, taskParamsToReplace, scheduleJob);
        sqlTextFromTaskShade = jobChainParamHandleResult.getSql();
        String taskParams = jobChainParamHandleResult.getTaskParams();
        LOGGER.info("{}, sqlTextFromTaskShade, jobId:{}, after jobChainParamHandler, result:{}", this.getClass().getName(), scheduleJob.getJobId(), jobChainParamHandleResult);

        // python、shell 任务处理任务引用
        if (scheduleTaskRefShadeService.isSupportRefJob(taskShade.getTaskType())) {
            // 获取引用的任务资源文件
            List<JobResourceFile> jobRefResourceFiles = scheduleTaskRefShadeService.getJobRefResourceFiles(actionParam, scheduleJob);
            String sql = Objects.toString(actionParam.get(GlobalConst.sqlText), StringUtils.EMPTY);
            // 用的任务名进行替换与实际引用文件名称进行替换
            sql = scheduleTaskRefShadeService.replaceRefFileContent(sql, jobRefResourceFiles);
            // taskParam 处理下 dtscript.ship-files
            taskParams = scheduleTaskRefShadeService.processTaskParamForShipFiles(taskParams, jobRefResourceFiles);
            sqlTextFromTaskShade = sql;
            actionParam.put(GlobalConst.sqlText, sql);
        }

        // 处理 taskParams
        actionParam.put(GlobalConst.TASK_PARAMS, taskParams);
        Long userId = MapUtils.getLong(actionParam, ConfigConstant.USER_ID);
        if (userId == null) {
            userId = taskShade.getOwnerUserId();
        }

        String uploadPath = this.uploadSqlTextToHdfs(
                taskShade.getProjectId(),
                taskShade.getTaskType(),
                scheduleJob.getDtuicTenantId(),
                sqlTextFromTaskShade,
                taskShade.getTaskType(),
                taskParamsToReplace,
                scheduleJob.getCycTime(),
                scheduleJob.getJobId(),
                scheduleJob.getType(),
                userId);
        taskExeArgs = taskExeArgs.replace(TaskConstant.UPLOADPATH, uploadPath);
        super.replaceTaskExeArgs(actionParam, scheduleJob, taskParamsToReplace, taskExeArgs, uploadPath);

        if (scheduleTaskRefShadeService.isSupportRefJob(taskShade.getTaskType())) {
            recordJobResourceFile(taskExeArgs,taskShade,scheduleJob.getJobId(),scheduleJob.getType());
        }

        String sql = Objects.toString(actionParam.get(GlobalConst.sqlText), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.UPLOADPATH)) {
            // 替换 ${uploadPath}
            sql = sql.replace(TaskConstant.UPLOADPATH, uploadPath);
        }
        actionParam.put(GlobalConst.sqlText, sql);
    }

    public String uploadSqlTextToHdfs(Long projectId, Integer appType,Long dtuicTenantId, String content, Integer taskType,
                                       List<ScheduleTaskParamShade> taskParamShades, String cycTime,String jobId,Integer scheduleType, Long uicUserId) {
        String hdfsPath = null;
        try {
            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = jobParamReplace.paramReplace(content, taskParamShades, cycTime, scheduleType, projectId);
            }
            // shell任务，创建脚本文件
            String fileName = FileUtil.getUploadFileName(taskType,jobId);

            hdfsPath = environmentContext.getHdfsTaskPath() + fileName;
            if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
                content = content.replaceAll("\r\n", System.getProperty("line.separator"));
            }

            JSONObject pluginInfoWithComponentType = pluginInfoManager.buildTaskPluginInfo(projectId,appType,taskType,dtuicTenantId, ScheduleEngineType.Hadoop.getEngineName(), uicUserId, null,null, null);
            String typeName = componentService.buildHdfsTypeName(dtuicTenantId,null);
            Integer dataSourceCodeByDiceName = rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
            pluginInfoWithComponentType.put(ConfigConstant.TYPE_NAME_KEY, typeName);
            pluginInfoWithComponentType.put("dataSourceType", dataSourceCodeByDiceName);
            IHdfsFile hdfs = ClientCache.getHdfs(dataSourceCodeByDiceName);
            ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfoWithComponentType.toJSONString(), dtuicTenantId);
            String hdfsUploadPath = hdfs.uploadStringToHdfs(sourceDTO,content,hdfsPath);
            if(StringUtils.isBlank(hdfsUploadPath)){
                throw new RdosDefineException("Update task to HDFS failure hdfsUploadPath is blank");
            }
            return hdfsUploadPath;
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    /**
     * 记录job本身涉及到资源文件，主要来自于 exeArgs 中的 --files taskParams 中的 dtscript.ship-files;
     * @param taskExeArgs
     * @param taskShade
     */
    private void recordJobResourceFile(String taskExeArgs, ScheduleTaskShade taskShade,String jobId, Integer type) throws IOException {
        if (EScheduleType.TEMP_JOB.getType().equals(type)) {
            return;
        }
        List<JobResourceFile> jobResourceFiles = scheduleTaskRefShadeService.getResourceFilesFromTaskShade(taskExeArgs,taskShade);
        if (CollectionUtils.isNotEmpty(jobResourceFiles)) {
            ScheduleJobResourceFile scheduleJobResourceFile = new ScheduleJobResourceFile();
            scheduleJobResourceFile.setJobId(jobId);
            scheduleJobResourceFile.setType(type);
            scheduleJobResourceFile.setJobResourceFiles(JSONObject.toJSONString(jobResourceFiles));
            scheduleJobResourceFileDao.deleteByJobIdAndType(jobId, type);
            scheduleJobResourceFileDao.insert(scheduleJobResourceFile);
        }
    }
}