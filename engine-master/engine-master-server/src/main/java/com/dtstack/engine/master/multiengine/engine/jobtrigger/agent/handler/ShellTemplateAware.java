package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 需要套一层 shell 模板
 *
 * @author leon
 * @date 2023-04-19 11:18
 **/
public interface ShellTemplateAware {

    /**
     * 获取 shell 模板的名称
     * @return shell 模板的名称
     */
    @NotNull
    String shellTemplateName();

    /**
     * 替换值
     * @param context agent 任务处理上下文
     * @return 模板替换值
     */
    Map<String, Object> substitutions(AgentHandlerContext context);

    @NotNull
    default String readShellTemplate() throws IOException {
        String shellPath = System.getProperty("user.dir") + File.separator + "shellPath";
        Path shellScriptFilePath = Paths.get(shellPath, shellTemplateName());
        String shellScriptTemplate;

        if (Files.notExists(shellScriptFilePath)) {
            // 从 resources 目录中读取 template
            InputStream shellScriptInputStream = ShellTemplateAware.class.getResourceAsStream(File.separator + "shellPath" + shellTemplateName());
            if (shellScriptInputStream == null) {
                throw new RdosDefineException("shell template not found");
            }

            // 将文件拷贝到指定路径
            Files.createDirectories(shellScriptFilePath.getParent());
            Files.copy(shellScriptInputStream, shellScriptFilePath);
        }

        // 从指定路径读取文件
        try (BufferedReader reader = Files.newBufferedReader(shellScriptFilePath, StandardCharsets.UTF_8)) {
            shellScriptTemplate = reader.lines().collect(Collectors.joining("\n"));
        }

        return shellScriptTemplate;
    }

    default String replaceTemplate(AgentHandlerContext context, String shellScriptTemplate) {
        // 替换 ${code}, ${cmd} 和 ${pythonVersion}
        Map<String, Object> substitutions = substitutions(context);
        StringSubstitutor stringSubstitutor = new StringSubstitutor(substitutions);
        return stringSubstitutor.replace(shellScriptTemplate);
    }
}
