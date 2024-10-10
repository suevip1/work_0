package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.util.Base64Util;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class HadoopJobStartTrigger extends JobStartTriggerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopJobStartTrigger.class);

    @Autowired
    private JobParamReplace jobParamReplace;

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        // 前置处理
        beforeProcess(actionParam, taskShade, scheduleJob);

        // 具体处理过程，交由子类覆写
        doProcess(actionParam, taskShade, scheduleJob);

        // 后置处理
        afterProcess(actionParam, taskShade, scheduleJob);
    }

    protected void beforeProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) {
        //info信息中数据
        String sql = Objects.toString(actionParam.get(GlobalConst.sqlText), StringUtils.EMPTY) ;
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);
        // 处理任务上下游参数
        JobChainParamHandleResult jobChainParamHandleResult = jobChainParamHandler.handle(sql, taskShade, taskParamsToReplace, scheduleJob);
        sql = jobChainParamHandleResult.getSql();
        String taskParams = jobChainParamHandleResult.getTaskParams();

        //统一替换下sql
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), taskShade.getProjectId());
        LOGGER.info("{}, sqlTextFromActionParam, jobId:{}, after replace, sql:「{}」, taskParams:「{}」", this.getClass().getName(), scheduleJob.getJobId(), sql, jobChainParamHandleResult.getTaskParams());

        actionParam.put(GlobalConst.sqlText, sql);
        actionParam.put(GlobalConst.TASK_PARAMS, taskParams);
        // 将 JSON 解析后的结果放入
        actionParam.put(GlobalConst.taskParamToReplace, taskParamsToReplace);
        // todo 其他冗余的参数，可以用特殊的 key +  hashMap 进行暂存
    }

    protected void afterProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) {
        //engine 不需要用到的参数 去除
        actionParam.remove(GlobalConst.taskParamToReplace);
        actionParam.remove(ScheduleTaskRefShadeService.TEMP_JOB_REF_TASK_IDS_KEY);
    }

    protected void doProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        // do nothing, only print log
        LOGGER.info("doProcess, jobId:{}, taskType:{}, taskEngineType:{}, clazz:{}", scheduleJob.getJobId(),
                taskShade.getTaskType(), taskShade.getEngineType(),
                this.getClass().getName());
    }

    /**
     * 替换任务运行参数
     *
     * @param actionParam
     * @param scheduleJob
     * @param taskParamsToReplace
     * @param taskExeArgs
     * @param uploadPath
     * @throws UnsupportedEncodingException
     */
    protected void replaceTaskExeArgs(Map<String, Object> actionParam, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace,
                                    String taskExeArgs,String uploadPath) throws UnsupportedEncodingException {
        taskExeArgs = Objects.toString(taskExeArgs, StringUtils.EMPTY);
        //替换jobId
        taskExeArgs = taskExeArgs.replace(TaskConstant.JOB_ID, scheduleJob.getJobId());
        if(StringUtils.isNotBlank(uploadPath)){
            taskExeArgs = taskExeArgs.replace(TaskConstant.UPLOADPATH, uploadPath);
        }

        //替换组件的exeArgs中的cmd参数
        if (taskExeArgs.contains(TaskConstant.LAUNCH)) {
            String modelParam = (String) actionParam.get("modelParam");
            String launchCmd = (String) actionParam.get(TaskConstant.LAUNCH_CMD);
            if (StringUtils.isNotBlank(modelParam)) {
                if (StringUtils.isNotBlank(uploadPath)) {
                    //替换文件名
                    String fileName = uploadPath.substring(StringUtils.lastIndexOf(uploadPath, "/") + 1);
                    launchCmd = launchCmd.replace(TaskConstant.FILE_NAME, fileName);
                }
                //如果存在modelParam参数 需要进行cycTime替换url加密
                modelParam = URLEncoder.encode(jobParamReplace.paramReplace(modelParam,taskParamsToReplace,scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId()), Charsets.UTF_8.name());
                launchCmd = launchCmd.replace(TaskConstant.MODEL_PARAM, modelParam);
                launchCmd = jobParamReplace.paramReplace(launchCmd, taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId());
                taskExeArgs = taskExeArgs.replace(TaskConstant.LAUNCH, Base64Util.baseEncode(launchCmd));
            } else {
                launchCmd = jobParamReplace.paramReplace(launchCmd, taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId());
                //替换参数 base64 生成launchCmd
                taskExeArgs = taskExeArgs.replace(TaskConstant.LAUNCH, Base64Util.baseEncode(URLEncoder.encode(launchCmd, Charsets.UTF_8.name())));
            }
            LOGGER.info("replaceTaskExeArgs job {} exeArgs {} ", scheduleJob.getJobId(), taskExeArgs);
        }
        if (taskExeArgs.contains(TaskConstant.CMD_OPTS)){
            List<String> argList = DtStringUtil.splitIngoreBlank(taskExeArgs);
            for (int i = 0; i < argList.size(); i++) {
                if(TaskConstant.CMD_OPTS.equals(argList.get(i))){
                    String base64 = argList.get(i + 1);
                    try {
                        base64 = Base64Util.baseEncode(jobParamReplace.paramReplace(Base64Util.baseDecode(base64),taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId()));
                        argList.set(i+1,base64);
                    }catch (Exception e){
                        argList.set(i+1,jobParamReplace.paramReplace(base64,taskParamsToReplace, scheduleJob.getCycTime(),scheduleJob.getType(), scheduleJob.getProjectId()));
                    }
                    break;
                }
            }
            taskExeArgs = String.join(" ", argList);
        }
        actionParam.put(GlobalConst.EXE_ARGS, taskExeArgs);
    }
}