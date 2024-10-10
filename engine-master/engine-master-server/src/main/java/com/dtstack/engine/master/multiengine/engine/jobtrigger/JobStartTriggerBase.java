package com.dtstack.engine.master.multiengine.engine.jobtrigger;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.master.dto.JobChainParamHandleResult;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamHandler;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Component
public class JobStartTriggerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStartTriggerBase.class);

    @Resource
    private JobParamReplace jobParamReplace;

    @Autowired
    protected JobChainParamHandler jobChainParamHandler;

    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        String sql = (String) actionParam.getOrDefault(GlobalConst.sqlText, StringUtils.EMPTY);
        //对于DQ的任务采用不同的替换方式
        if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.DQ_JOB_ID)) {
            sql = sql.replace(TaskConstant.DQ_JOB_ID, scheduleJob.getJobId());
        }

        if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.DQ_FLOW_JOB_ID)) {
            sql = sql.replace(TaskConstant.DQ_FLOW_JOB_ID, scheduleJob.getFlowJobId());
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get(GlobalConst.taskParamToReplace), ScheduleTaskParamShade.class);
        // 处理任务上下游参数
        JobChainParamHandleResult jobChainParamHandleResult = jobChainParamHandler.handle(sql, taskShade, taskParamsToReplace, scheduleJob);
        sql = jobChainParamHandleResult.getSql();
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime(), scheduleJob.getType(), taskShade.getProjectId());
        LOGGER.info("{}, sqlTextFromActionParam, jobId:{}, after replace, sql:「{}」, taskParams:「{}」", this.getClass().getName(), scheduleJob.getJobId(), sql, jobChainParamHandleResult.getTaskParams());

        actionParam.put(GlobalConst.sqlText, sql);
    }
}
