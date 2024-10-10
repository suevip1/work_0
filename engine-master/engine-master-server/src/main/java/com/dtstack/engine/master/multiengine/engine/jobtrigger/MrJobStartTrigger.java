package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-04 10:40
 * MR 任务相关
 */
@Component
public class MrJobStartTrigger extends HadoopJobStartTrigger {
    @Autowired
    private JobParamReplace jobParamReplace;

    @Override
    protected void doProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        List<ScheduleTaskParamShade> taskParamsToReplace = (List<ScheduleTaskParamShade>)actionParam.getOrDefault(GlobalConst.taskParamToReplace, Collections.emptyList());
        //hadoop spark mr提交 不用上传文件
        String exeArgs = (String) actionParam.get(GlobalConst.EXE_ARGS);
        if (StringUtils.isNotBlank(exeArgs)) {
            //替换系统参数
            String taskExeArgs = jobParamReplace.paramReplace(exeArgs, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
            super.replaceTaskExeArgs(actionParam, scheduleJob, taskParamsToReplace, taskExeArgs, null);
        }
    }
}
