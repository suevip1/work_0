package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.constrant.GlobalConst;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.AgentExeArgs;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl.ComplexAgentScriptHandler;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl.SimplePythonAgentScriptHandler;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl.SimpleShellAgentScriptHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.dtstack.engine.common.constrant.TaskConstant.UPLOADPATH;
import static com.dtstack.engine.master.impl.ScheduleTaskRefShadeService.TEMP_JOB_REF_TASK_IDS_KEY;

/**
 * @author leon
 * @date 2023-04-17 20:34
 **/
@Component
public class AgentScriptHandlerManager implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${agent.code.complex.length.threshold:500}")
    private Integer agentCodeComplexLengthThreshold;

    private final ComplexAgentScriptHandler complexAgentScriptHandler;

    private final SimplePythonAgentScriptHandler simplePythonAgentScriptHandler;

    private final SimpleShellAgentScriptHandler simpleShellAgentScriptHandler;

    private final ScheduleTaskRefShadeService scheduleTaskRefShadeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentScriptHandlerManager.class);

    private static final List<String> FILES_TO_DELETE = Arrays.asList(
            "simplePythonShellTemplate.sh",
            "complexShellTemplate.sh");


    public AgentScriptHandlerManager(ComplexAgentScriptHandler complexAgentScriptHandler,
                                     SimplePythonAgentScriptHandler simplePythonAgentScriptHandler,
                                     SimpleShellAgentScriptHandler simpleShellAgentScriptHandler,
                                     ScheduleTaskRefShadeService scheduleTaskRefShadeService) {
        this.complexAgentScriptHandler = complexAgentScriptHandler;
        this.simplePythonAgentScriptHandler = simplePythonAgentScriptHandler;
        this.simpleShellAgentScriptHandler = simpleShellAgentScriptHandler;
        this.scheduleTaskRefShadeService = scheduleTaskRefShadeService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        CompletableFuture.runAsync(this::deleteLocalShellTemplate);
    }

    public AgentScriptHandler chooseHandler(AgentHandlerContext agentHandlerContext) {
        AgentExeArgs agentExeArgs = agentHandlerContext.getAgentExeArgs();
        ScheduleJob scheduleJob = agentHandlerContext.getScheduleJob();
        ScheduleTaskShade taskShade = agentHandlerContext.getTaskShade();
        List<String> shipFiles = agentHandlerContext.getShipFiles();

        Map<String, Object> actionParam = agentHandlerContext.getActionParam();
        String sqlText = Objects.toString(actionParam.get(GlobalConst.sqlText), StringUtils.EMPTY);

        // 依赖资源的情况
        if (CollectionUtils.isNotEmpty(shipFiles)) {
            return complexAgentScriptHandler;
        }

        // 资源引用的情况，对应 python on agent 资源上传的场景，exeArgs 中 --files 对应资源的 hdfs 路径
        if (Objects.nonNull(agentExeArgs)
                && StringUtils.isNotBlank(agentExeArgs.getFiles())
                && isNotPlaceholder(agentExeArgs.getFiles())) {
            return complexAgentScriptHandler;
        }

        Integer scheduleType = scheduleJob.getType();

        // 任务引用的情况
        if (EScheduleType.TEMP_JOB.getType().equals(scheduleType)) {
            String refTaskIdsStr = (String) actionParam.get(TEMP_JOB_REF_TASK_IDS_KEY);
            if (StringUtils.isNotBlank(refTaskIdsStr)) {
                return complexAgentScriptHandler;
            }
        }

        // 非临时运行的情况，判断是 schedule_task_ref_shade 中是否有引用关系
        if (!EScheduleType.TEMP_JOB.getType().equals(scheduleType)) {
            List<Long> refTaskIdsByJob = scheduleTaskRefShadeService.getRefTaskIdsByJob(scheduleJob);
            if (CollectionUtils.isNotEmpty(refTaskIdsByJob)) {
                return complexAgentScriptHandler;
            }
        }

        // 单个 shell 脚本的情况
        if (Objects.nonNull(taskShade)
                && EScheduleJobType.SHELL_ON_AGENT.getType().equals(taskShade.getTaskType())) {
            return simpleShellAgentScriptHandler;
        }


        // 单个 python 脚本的情况，需要判断下代码长度
        if (sqlText.length() > agentCodeComplexLengthThreshold) {
            return complexAgentScriptHandler;
        } else {
            return simplePythonAgentScriptHandler;
        }
    }

    private static boolean isNotPlaceholder(String files) {
        return !UPLOADPATH.equals(files) && files.trim().startsWith("hdfs://");
    }

    private void deleteLocalShellTemplate() {
        String shellPath = System.getProperty("user.dir") + File.separator + "shellPath";

        for (String filename : FILES_TO_DELETE) {
            Path filePath = Paths.get(shellPath, filename);

            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    LOGGER.error("deleteLocalShellTemplate fail:{}", e.getMessage(), e);
                }
            }
        }
    }
}
