package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler;

/**
 * @author leon
 * @date 2023-04-17 20:35
 **/
public interface AgentScriptHandler {

    void handle(AgentHandlerContext context) throws Exception;
}
