package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.AgentExeArgs;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.PythonAgentExeArgsParser;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static com.dtstack.engine.master.impl.ScheduleTaskRefShadeService.SHIP_FILES_KEY;

/**
 * @author leon
 * @date 2023-04-18 15:52
 **/
public class AgentHandlerContext {

    private Map<String, Object> actionParam;

    private ScheduleTaskShade taskShade;

    private ScheduleJob scheduleJob;

    private AgentExeArgs agentExeArgs;

    private String jobId;

    private String sqlText;

    private EScheduleType scheduleType;

    private EScheduleJobType taskType;

    private String realUserScript;

    private String mainScriptFileName;

    private List<String> shipFiles;

    private String python2Path;

    private String python3Path;

    public Map<String, Object> getActionParam() {
        return actionParam;
    }

    public void setActionParam(Map<String, Object> actionParam) {
        this.actionParam = actionParam;
    }

    public ScheduleTaskShade getTaskShade() {
        return taskShade;
    }

    public void setTaskShade(ScheduleTaskShade taskShade) {
        this.taskShade = taskShade;
    }

    public ScheduleJob getScheduleJob() {
        return scheduleJob;
    }

    public void setScheduleJob(ScheduleJob scheduleJob) {
        this.scheduleJob = scheduleJob;
    }

    public AgentExeArgs getAgentExeArgs() {
        return agentExeArgs;
    }

    public void setPythonAgentExeArgs(AgentExeArgs agentExeArgs) {
        this.agentExeArgs = agentExeArgs;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }


    public EScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getRealUserScript() {
        return realUserScript;
    }

    public void setRealUserScript(String realUserScript) {
        this.realUserScript = realUserScript;
    }

    public void setScheduleType(EScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public void setAgentExeArgs(AgentExeArgs agentExeArgs) {
        this.agentExeArgs = agentExeArgs;
    }

    public EScheduleJobType getTaskType() {
        return taskType;
    }

    public void setTaskType(EScheduleJobType taskType) {
        this.taskType = taskType;
    }

    public String getMainScriptFileName() {
        return mainScriptFileName;
    }

    public void setMainScriptFileName(String mainScriptFileName) {
        this.mainScriptFileName = mainScriptFileName;
    }

    public List<String> getShipFiles() {
        return shipFiles;
    }

    public void setShipFiles(List<String> shipFiles) {
        this.shipFiles = shipFiles;
    }

    public static AgentHandlerContext of(Map<String, Object> actionParam,
                                         ScheduleTaskShade taskShade,
                                         ScheduleJob scheduleJob) throws IOException {
        AgentHandlerContext context = new AgentHandlerContext();
        context.setActionParam(actionParam);
        context.setTaskShade(taskShade);
        context.setScheduleJob(scheduleJob);

        // parse exeArgs
        String exeArgs = Objects.toString(actionParam.get(GlobalConst.EXE_ARGS), StringUtils.EMPTY);
        AgentExeArgs agentExeArgs = PythonAgentExeArgsParser.parse(exeArgs);
        context.setPythonAgentExeArgs(agentExeArgs);

        // parse jobId
        context.setJobId(Optional.ofNullable(scheduleJob).map(ScheduleJob::getJobId).orElse(null));

        // parse sqlText
        String sqlText = Objects.toString(actionParam.get(GlobalConst.sqlText), StringUtils.EMPTY);
        context.setSqlText(sqlText);

        // parse scheduleType
        EScheduleType scheduleType = Optional.ofNullable(scheduleJob)
                .map(ScheduleJob::getType)
                .map(EScheduleType::getScheduleType)
                .orElse(null);
        context.setScheduleType(scheduleType);

        // parse taskType
        EScheduleJobType taskType = Optional.ofNullable(context.getTaskShade())
                .map(ScheduleTaskShade::getTaskType)
                .map(EScheduleJobType::getEJobType)
                .orElse(null);
        context.setTaskType(taskType);

        // parse ship files: dtscript.ship-files=hdfs://ns1/dtInsight/batch/109_putong_test_putong_test.py
        String taskParams = Objects.toString(actionParam.get(GlobalConst.TASK_PARAMS), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(taskParams)) {
            Properties properties = PublicUtil.stringToProperties(taskParams);
            String shipFiles = properties.getProperty(SHIP_FILES_KEY);
            if (StringUtils.isNotBlank(shipFiles)) {
                String[] split = shipFiles.split(",");
                context.setShipFiles(Lists.newArrayList(split));
            }
        }

        return context;
    }

    public String getPython2Path() {
        return python2Path;
    }

    public void setPython2Path(String python2Path) {
        this.python2Path = python2Path;
    }

    public String getPython3Path() {
        return python3Path;
    }

    public void setPython3Path(String python3Path) {
        this.python3Path = python3Path;
    }
}
