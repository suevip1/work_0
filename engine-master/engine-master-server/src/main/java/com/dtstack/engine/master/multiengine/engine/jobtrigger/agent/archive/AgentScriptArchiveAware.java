package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.archive;

import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;

/**
 * 需要归档
 *
 * @author leon
 * @date 2023-04-19 16:26
 **/
public interface AgentScriptArchiveAware {

    void archive(AgentHandlerContext context) throws Exception;
}
