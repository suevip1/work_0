package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.controller.DownloadController;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.archive.AgentScriptArchiveAware;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.archive.AgentScriptArchiver;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentScriptHandlerManager;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.ShellTemplateAware;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 复杂的 agent 任务处理，复杂是指 python 脚本过长，或者 python 和 shell 存在任务引用的情况 </br>
 * @see AgentScriptHandlerManager#chooseHandler(AgentHandlerContext)
 *
 * @author leon
 * @date 2023-04-17 19:40
 **/
@Component
public class ComplexAgentScriptHandler
        extends AbstractAgentScriptHandler implements ShellTemplateAware, AgentScriptArchiveAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexAgentScriptHandler.class);

    private final AgentScriptArchiver delegateArchiver;

    private final EnvironmentContext environment;

    @Value("${agent.job.archive.download.retry.count:3}")
    private String agentJobArchiveDownloadRetryCount;

    // 10s
    @Value("${agent.job.archive.download.connect.timeout:10}")
    private Integer agentJobArchiveDownloadConnectTimeout;

    // 5min
    @Value("${agent.job.archive.download.max.time:500}")
    private Integer agentJobArchiveDownloadMaxTime;

    /**
     * 是否校验联通性, 1是 0否, 默认 0
     */
    @Value("${agent.job.archive.download.tryTelnetOrPing:0}")
    private Integer agentJobArchiveDownloadTryTelnetOrPing;

    private static final String COMPLEX_SHELL_TEMPLATE = "complexShellTemplate.sh";

    /**
     * @see DownloadController#downloadAgentJobArchive(HttpServletResponse, String)
     */
    private static final String DOWNLOAD_URL = "http://%s/node/download/downloadAgentJobArchive";

    public ComplexAgentScriptHandler(AgentScriptArchiver archiver,
                                     EnvironmentContext environment) {
        this.delegateArchiver = archiver;
        this.environment = environment;
    }

    @Override
    protected void preHandler(AgentHandlerContext context) throws Exception {
        context.setRealUserScript(context.getSqlText());
        // 归档 => zip #_#
        archive(context);
    }

    @Override
    String generateFinalScript(AgentHandlerContext context) throws IOException {
        String shellScriptTemplate = readShellTemplate();
        return replaceTemplate(context, shellScriptTemplate);
    }

    @Override
    public void archive(AgentHandlerContext context) throws Exception {
        LOGGER.info("agent script job: {}, begin archive", context.getJobId());
        delegateArchiver.archive(context);
    }

    @Override
    public Map<String, Object> substitutions(AgentHandlerContext context) {
        Map<String, Object> substitutions = generateCommonSubstitutions(context);
        substitutions.put(HandleConstant.DOWNLOAD_URL, generateDownloadUrl());
        substitutions.put(HandleConstant.PARAMS, generateDownloadParams(context.getJobId()));
        substitutions.put(HandleConstant.DT_TOKEN, environment.getSdkToken());
        substitutions.put(HandleConstant.TARGET_SCRIPT, context.getMainScriptFileName());
        substitutions.put(HandleConstant.RETRY_COUNT, agentJobArchiveDownloadRetryCount);
        substitutions.put(HandleConstant.CONNECT_TIMEOUT, agentJobArchiveDownloadConnectTimeout);
        substitutions.put(HandleConstant.MAX_TIME, agentJobArchiveDownloadMaxTime);
        substitutions.put(HandleConstant.TRY_TELNET_OR_PING, agentJobArchiveDownloadTryTelnetOrPing);

        LOGGER.info("complex agent script job:{} substitutions:{}",
                context.getJobId(),
                JSONObject.toJSONString(substitutions));

        return substitutions;
    }

    private String generateDownloadUrl() {
        String httpAddress = environment.getHttpAddress();
        // 如果配置了，就取配置的 url 路径
        String configDownloadUrl = environment.getAgentJobArchiveDownloadUrl();
        if (StringUtils.isBlank(configDownloadUrl)) {
            // 没有配置根据 http.address, http.port 拼接起来
            String port = environment.getConfigHttpPort();
            if (StringUtils.isNotBlank(port)) {
                httpAddress = httpAddress + ":" + port;
            }
            return String.format(DOWNLOAD_URL, httpAddress);
        }
        return configDownloadUrl;
    }

    private String generateDownloadParams(String jobId) {
        return "jobId=" + jobId;
    }

    @Override
    public @NotNull String shellTemplateName() {
        return File.separator + COMPLEX_SHELL_TEMPLATE;
    }
}
