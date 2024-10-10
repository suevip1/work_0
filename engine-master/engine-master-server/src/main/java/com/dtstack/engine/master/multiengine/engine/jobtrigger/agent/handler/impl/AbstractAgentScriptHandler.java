package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobResourceFileDao;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.AgentExeArgs;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentScriptHandler;
import com.dtstack.engine.po.ScheduleJobResourceFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.CMD;
import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.DEBUG;
import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.JOB_ID;
import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.PYTHON2_PATH;
import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.PYTHON3_PATH;
import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.PYTHON_VERSION;

/**
 * @author leon
 * @date 2023-04-18 15:46
 **/
public abstract class AbstractAgentScriptHandler implements AgentScriptHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAgentScriptHandler.class);

    @Autowired
    protected ScheduleTaskRefShadeService scheduleTaskRefShadeService;

    @Autowired
    protected ScheduleJobResourceFileDao scheduleJobResourceFileDao;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Override
    public void handle(AgentHandlerContext context) throws Exception {
        // 预处理
        preHandler(context);

        // 生成最终的执行脚本
        String finalScript = generateFinalScript(context);

        if (StringUtils.isNotBlank(finalScript)) {
            // 将最终的执行脚本放入 actionParam 中
            Map<String, Object> actionParam = context.getActionParam();
            actionParam.put(GlobalConst.sqlText, finalScript);
            LOGGER.info("agent job:{} script content:{}", context.getJobId(), finalScript);
        }

        // 后处理，将原始的脚本存下来
        postHandler(context);
    }

    abstract String generateFinalScript(AgentHandlerContext context) throws Exception;

    protected void preHandler(AgentHandlerContext context) throws Exception {
        // default do nothing
    }

    protected void postHandler(AgentHandlerContext context) throws IOException {
        // 临时运行不需要保存
        if (EScheduleType.TEMP_JOB.equals(context.getScheduleType())) {
            return;
        }

        String realUserScript = context.getRealUserScript();
        EScheduleType scheduleType = context.getScheduleType();
        ScheduleTaskShade taskShade = context.getTaskShade();
        String jobId = context.getJobId();

        Map<String, Object> actionParam = context.getActionParam();
        String exeArgs = Objects.toString(actionParam.get(GlobalConst.EXE_ARGS), org.apache.commons.lang.StringUtils.EMPTY);

        List<JobResourceFile> resourceFilesForAgentJob =
                scheduleTaskRefShadeService.getResourceFilesForAgentJob(exeArgs, taskShade, realUserScript);

        if (CollectionUtils.isNotEmpty(resourceFilesForAgentJob)) {
            ScheduleJobResourceFile scheduleJobResourceFile = new ScheduleJobResourceFile();
            scheduleJobResourceFile.setJobId(jobId);
            scheduleJobResourceFile.setType(scheduleType.getType());
            scheduleJobResourceFile.setJobResourceFiles(JSONObject.toJSONString(resourceFilesForAgentJob));
            scheduleJobResourceFileDao.deleteByJobIdAndType(jobId, scheduleType.getType());
            scheduleJobResourceFileDao.insert(scheduleJobResourceFile);
        }
    }


    public Map<String, Object> generateCommonSubstitutions(AgentHandlerContext context) {
        String jobId = context.getJobId();
        AgentExeArgs agentExeArgs = context.getAgentExeArgs();
        String cmdOpt = agentExeArgs.getCmdOpt();
        String pythonVersion = agentExeArgs.getPythonVersion();

        Map<String, Object> substitutions = new HashMap<>();
        substitutions.put(JOB_ID, jobId);
        substitutions.put(PYTHON_VERSION, pythonVersion);
        substitutions.put(CMD, StringUtils.isNotBlank(cmdOpt) ? cmdOpt: StringUtils.EMPTY);
        substitutions.put(PYTHON2_PATH, environmentContext.getAgentJobPython2Path());
        String python2Path = context.getPython2Path();
        if (StringUtils.isNotBlank(python2Path)) {
            substitutions.put(PYTHON2_PATH, python2Path);
        }

        substitutions.put(PYTHON3_PATH, environmentContext.getAgentJobPython3Path());
        String python3Path = context.getPython3Path();
        if (StringUtils.isNotBlank(python3Path)) {
            substitutions.put(PYTHON3_PATH, python3Path);
        }
        substitutions.put(DEBUG, environmentContext.openAgentJobArchiveDebug());
        return substitutions;
    }
}
