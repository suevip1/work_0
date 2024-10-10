package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.impl;

import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.ShellTemplateAware;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

import static com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.HandleConstant.CODE;

/**
 * @author leon
 * @date 2023-04-17 19:39
 **/
@Component
public class SimplePythonAgentScriptHandler
        extends AbstractAgentScriptHandler implements ShellTemplateAware {

    private static final String SIMPLE_PYTHON_SHELL_TEMPLATE = "simplePythonShellTemplate.sh";

    @Override
    String generateFinalScript(AgentHandlerContext context) throws Exception {
        context.setRealUserScript(context.getSqlText());

        String shellScriptTemplate = readShellTemplate();
        return replaceTemplate(context, shellScriptTemplate);
    }

    @Override
    public Map<String, Object> substitutions(AgentHandlerContext context) {
        Map<String, Object> substitutions = generateCommonSubstitutions(context);

        String sqlText = context.getSqlText();
        substitutions.put(CODE, sqlText);
        return substitutions;
    }

    @Override
    public @NotNull String shellTemplateName() {
        return File.separator + SIMPLE_PYTHON_SHELL_TEMPLATE;
    }
}
