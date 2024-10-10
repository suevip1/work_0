package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.archive;

import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;

import java.util.List;

/**
 * @author leon
 * @date 2023-04-19 16:27
 **/
public abstract class AbstractAgentScriptArchiveAware implements AgentScriptArchiveAware {

    @Override
    public void archive(AgentHandlerContext context) throws Exception {
        // 获取涉及到到资源文件
        List<JobResourceFile> jobResourceFiles = collectResources(context);
        // 文本替换，本地临时存储 zip 归档文件
        doArchive(context, jobResourceFiles);
    }

    abstract List<JobResourceFile> collectResources(AgentHandlerContext context) throws Exception;

    abstract void doArchive(AgentHandlerContext context, List<JobResourceFile> jobResourceFiles) throws Exception;
}
