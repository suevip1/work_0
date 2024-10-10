package com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.archive;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.engine.PluginInfoToSourceDTO;
import com.dtstack.engine.api.domain.JobResourceFile;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.StringUtils;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ScheduleTaskRefShadeService;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.AgentExeArgs;
import com.dtstack.engine.master.multiengine.engine.jobtrigger.agent.handler.AgentHandlerContext;
import com.dtstack.engine.master.plugininfo.PluginInfoManager;
import com.dtstack.engine.master.worker.RdosWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 归档
 *
 * @author leon
 * @date 2023-04-19 16:34
 **/
@Component
public class AgentScriptArchiver extends AbstractAgentScriptArchiveAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentScriptArchiver.class);

    private final ScheduleTaskRefShadeService scheduleTaskRefShadeService;

    private static final String TMP_AGENT_JOB_ARCHIVE_DIR_NAME = "agentJobArchive";

    private final RdosWrapper rdosWrapper;

    private final ComponentService componentService;

    private final PluginInfoManager pluginInfoManager;

    public AgentScriptArchiver(ScheduleTaskRefShadeService scheduleTaskRefShadeService,
                               RdosWrapper rdosWrapper,
                               ComponentService componentService,
                               PluginInfoManager pluginInfoManager) {
        this.scheduleTaskRefShadeService = scheduleTaskRefShadeService;
        this.rdosWrapper = rdosWrapper;
        this.componentService = componentService;
        this.pluginInfoManager = pluginInfoManager;
    }

    @Override
    List<JobResourceFile> collectResources(AgentHandlerContext context) throws Exception {
        Map<String, Object> actionParam = context.getActionParam();
        ScheduleJob scheduleJob = context.getScheduleJob();
        return scheduleTaskRefShadeService.getJobRefResourceFiles(actionParam, scheduleJob);
    }

    @Override
    void doArchive(AgentHandlerContext context, List<JobResourceFile> jobResourceFiles) throws Exception {
        replaceContent(context, jobResourceFiles);
        packing(context, jobResourceFiles);
    }

    private void replaceContent(AgentHandlerContext context, List<JobResourceFile> jobResourceFiles) {
        String sqlText = context.getSqlText();
        sqlText = scheduleTaskRefShadeService.replaceRefFileContent(sqlText, jobResourceFiles);
        context.setSqlText(sqlText);
    }

    private void packing(AgentHandlerContext context, List<JobResourceFile> jobResourceFiles) throws Exception {
        // 创建临时目录
        File destTmpDir = createTmpDir(context.getJobId());
        // 写入文件
        writeFile(context, jobResourceFiles, destTmpDir);
        // zip
        zipAll(destTmpDir, context.getJobId());
    }


    private File createTmpDir(String jobId) throws IOException {
        if (jobId == null || jobId.isEmpty()) {
            throw new RdosDefineException("jobId cannot be null or empty");
        }

        File tmpDir = new File(System.getProperty("user.dir") + "/tmp");
        createDirIfNotExists(tmpDir, "Unable to create tmp directory");

        File agentJobArchiveDir = new File(tmpDir, TMP_AGENT_JOB_ARCHIVE_DIR_NAME);
        createDirIfNotExists(agentJobArchiveDir, "Unable to create tmp/agentJobArchive directory");

        File jobDir = new File(agentJobArchiveDir, jobId);
        // jobId 存在时，先删除，再创建
        createOrReplaceDir(jobDir, "Unable to create /tmp/agentJobArchive/" + jobId + " directory");

        return jobDir;
    }

    private void createDirIfNotExists(File dir, String errorMessage) throws IOException {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException(errorMessage);
            }
        }
    }

    private void createOrReplaceDir(File dir, String errorMessage) throws IOException {
        if (dir.exists()) {
            deleteDirectory(dir);
        }
        if (!dir.mkdir()) {
            throw new IOException(errorMessage);
        }
    }

    private void deleteDirectory(File dir) throws IOException {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        if (!dir.delete()) {
            throw new IOException("Unable to delete directory " + dir);
        }
    }


    private void writeFile(AgentHandlerContext context,
                           List<JobResourceFile> jobResourceFiles,
                           File destTmpDir) {
        writeMainFile(context, destTmpDir);
        writeShipFile(context, destTmpDir);
        writeResourceFiles(context, jobResourceFiles, destTmpDir);
    }

    private void zipAll(File destTmpDir, String jobId) {
        checkArguments(jobId, destTmpDir.toPath());
        // 创建 zip 文件名
        String zipFileName = jobId + "_" + System.currentTimeMillis() + ".zip";
        File zipFile = new File(destTmpDir, zipFileName);

        // 获取 destTmpDir 下的所有文件
        File[] files = destTmpDir.listFiles();

        if (Objects.isNull(files)) {
            return;
        }

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            for (File file : files) {
                // 将文件添加到 zip 文件中
                addToZip(file, zos);
            }
        } catch (IOException e) {
            String errorMessage = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("agent job:{} failed to create zip file:{}",
                    jobId,
                    errorMessage,
                    e);
            throw new RdosDefineException("Failed to create zip file: %s", errorMessage);
        }
    }

    private void addToZip(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }


    private void writeMainFile(AgentHandlerContext context, File destTmpDir) {
        String realUserScript = context.getRealUserScript();
        ScheduleTaskShade taskShade = context.getTaskShade();
        String fileName = taskShade.getName();

        if (StringUtils.isNotBlank(realUserScript)) {
            // 将脚本内容写到本地文件中
            writeSingleFile(fileName, realUserScript, context.getTaskType(), destTmpDir);
        }

        AgentExeArgs agentExeArgs = context.getAgentExeArgs();
        String files = agentExeArgs.getFiles();
        if (StringUtils.isNotBlank(files)) {
            // 从 hdfs 拉取资源文件到本地
            copyFileFromHdfs(taskShade, files, destTmpDir);
        }

        // 目标脚本的文件名记录下来
        context.setMainScriptFileName(getMainFileName(destTmpDir));
    }


    private void writeSingleFile(String fileName,
                                 String fileContent,
                                 EScheduleJobType taskType,
                                 File destTmpDir) {

        Path destTmpPath = destTmpDir.toPath();
        checkArguments(fileName, destTmpPath);

        FileSuffix fileSuffix = FileSuffix.judgeFileSuffixByTaskType(taskType);
        String fileExtension = fileSuffix.getSuffix();
        Path outputPath = destTmpPath.resolve(fileName + fileExtension);

        try {
            Files.write(outputPath, fileContent.getBytes());
        } catch (IOException e) {
            throw new RdosDefineException("Failed to write content to file: " + outputPath.toAbsolutePath(), e);
        }
    }

    private void writeShipFile(AgentHandlerContext context, File destTmpDir) {
        ScheduleTaskShade taskShade = context.getTaskShade();
        List<String> shipFiles = context.getShipFiles();
        if (CollectionUtils.isNotEmpty(shipFiles)) {
            // 从 hdfs 拉取资源文件到本地
            for (String shipFile : shipFiles) {
                if (StringUtils.isBlank(shipFile)) {
                    continue;
                }
                copyFileFromHdfs(taskShade, shipFile, destTmpDir);
            }
        }
    }


    private void writeResourceFiles(AgentHandlerContext context,
                                    List<JobResourceFile> jobResourceFiles,
                                    File destTmpDir) {
        if (CollectionUtils.isEmpty(jobResourceFiles)) {
            return;
        }
        ScheduleTaskShade taskShade = context.getTaskShade();

        for (JobResourceFile jobResourceFile : jobResourceFiles) {
            String hdfsPath = jobResourceFile.getPath();
            String content = jobResourceFile.getContent();

            if (StringUtils.isNotBlank(hdfsPath)) {
                String[] splitHdfsPath = hdfsPath.split(",");
                Arrays.stream(splitHdfsPath)
                        .filter(StringUtils::isNotBlank)
                        .forEach(path -> copyFileFromHdfs(taskShade, path, destTmpDir));
            }

            if (StringUtils.isNotBlank(content)) {
                writeSingleFile(jobResourceFile.getFileName(), content, context.getTaskType(), destTmpDir);
            }
        }
    }

    private void copyFileFromHdfs(ScheduleTaskShade taskShade, String hdfsPath, File destTmpDir) {
        Integer dataSourceCodeByDiceName = getDataSourceCodeByDiceName(taskShade);
        ISourceDTO sourceDTO = buildHdfsSourceDTO(taskShade, dataSourceCodeByDiceName);
        IHdfsFile hdfsFileClient = ClientCache.getHdfs(dataSourceCodeByDiceName);
        hdfsFileClient.copyToLocal(sourceDTO, hdfsPath, destTmpDir.getPath());
    }

    private Integer getDataSourceCodeByDiceName(ScheduleTaskShade taskShade) {
        Long dtUicTenantId = taskShade.getDtuicTenantId();
        String typeName = componentService.buildHdfsTypeName(dtUicTenantId, null);
        return rdosWrapper.getDataSourceCodeByDiceName(typeName, EComponentType.HDFS);
    }


    private ISourceDTO buildHdfsSourceDTO(ScheduleTaskShade taskShade, Integer dataSourceCodeByDiceName) {
        Integer taskType = taskShade.getTaskType();
        Long projectId = taskShade.getProjectId();
        Long dtUicTenantId = taskShade.getDtuicTenantId();
        Integer appType = taskShade.getAppType();
        Long uicUserId = taskShade.getOwnerUserId();
        JSONObject pluginInfo =
                pluginInfoManager.buildTaskPluginInfo(
                        projectId,
                        appType,
                        taskType,
                        dtUicTenantId,
                        ScheduleEngineType.Hadoop.getEngineName(),
                        uicUserId, null, null, null);
        pluginInfo.put("dataSourceType", dataSourceCodeByDiceName);
        return PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString(), dtUicTenantId);
    }

    private void checkArguments(String subject, Path destTmpPath) {
        if (subject == null || subject.isEmpty()) {
            throw new RdosDefineException("name cannot be null or empty.");
        }

        if (!Files.exists(destTmpPath)) {
            throw new RdosDefineException("Destination directory does not exist.");
        }

        if (!Files.isDirectory(destTmpPath)) {
            throw new RdosDefineException("Destination path is not a directory.");
        }

        if (!Files.isWritable(destTmpPath)) {
            throw new RdosDefineException("Destination directory is not writable.");
        }
    }

    private String getMainFileName(File destTmpDir) {
        String fileName = null;
        if (destTmpDir != null && destTmpDir.isDirectory()) {
            File[] files = destTmpDir.listFiles();
            if (files != null && files.length > 0) {
                fileName = files[0].getName();
            }
        }
        return fileName;
    }


    enum FileSuffix {
        PY(".py"),
        SH(".sh"),
        ;
        private final String suffix;

        FileSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }

        static FileSuffix judgeFileSuffixByTaskType(EScheduleJobType scheduleJobType) {
            switch (scheduleJobType) {
                case PYTHON_ON_AGENT:
                    return PY;
                case SHELL_ON_AGENT:
                    return SH;
                default:
                    throw new RdosDefineException("Unsupported task type: " + scheduleJobType);
            }
        }
    }

}
