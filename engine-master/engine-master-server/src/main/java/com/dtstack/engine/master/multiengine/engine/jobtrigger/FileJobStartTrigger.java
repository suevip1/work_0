package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.master.multiengine.job.FileCopyJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-18 21:00
 * 文件任务相关
 */
@Component
public class FileJobStartTrigger extends HadoopJobStartTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileJobStartTrigger.class);

    @Autowired
    private FileCopyJobService fileCopyJobService;

    @Override
    protected void beforeProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) {
        // do nothing, only print log
        LOGGER.info("beforeProcess, jobId:{}, taskType:{}, taskEngineType:{}, clazz:{}", scheduleJob.getJobId(),
                taskShade.getTaskType(), taskShade.getEngineType(),
                this.getClass().getName());
    }

    @Override
    protected void doProcess(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        // 处理文件拷贝任务
        String taskExeArgs = fileCopyJobService.buildFileCopyTask(actionParam, taskShade, scheduleJob);
        if (taskExeArgs != null) {
            actionParam.put(GlobalConst.EXE_ARGS, taskExeArgs);
        }
        actionParam.put(GlobalConst.TASK_PARAMS, taskShade.getTaskParams());
        actionParam.put(GlobalConst.sqlText, "");
        return;
    }
}
