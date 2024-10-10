//package com.dtstack.engine.master.listener;
//
//import com.dtstack.dtcenter.common.enums.AppType;
//import com.dtstack.engine.api.domain.ScheduleJob;
//import com.dtstack.engine.common.enums.EScheduleJobType;
//import com.dtstack.engine.common.env.EnvironmentContext;
//import com.dtstack.engine.common.exception.ExceptionUtil;
//import com.dtstack.engine.master.event.ScheduleJobEventPublisher;
//import com.dtstack.engine.master.lineage.SqlJobFinishedListener;
//import com.google.common.collect.Sets;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.nio.file.FileVisitResult;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.SimpleFileVisitor;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.util.Set;
//
///**
// * @author leon
// * @date 2023-04-24 11:29
// **/
//@Component
//public class AgentJobFinishedListener extends SqlJobFinishedListener {
//
//    @Autowired
//    private EnvironmentContext environmentContext;
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(AgentJobFinishedListener.class);
//
//    @PostConstruct
//    public void registerEvent(){
//        ScheduleJobEventPublisher.getInstance().register(this);
//    }
//
//    @Override
//    protected void onFocusedJobFinished(Integer type,
//                                        String engineTye,
//                                        String sqlText,
//                                        Long taskId,
//                                        ScheduleJob scheduleJob,
//                                        Integer status) {
//        String jobId = scheduleJob.getJobId();
//        deleteAgentJobArchiveTmpFileIfExist(jobId);
//    }
//
//    /**
//     * 删除 /tmp/agentJobArchive/{jobId} 下文件，包括 jobId 这层目录
//     *
//     * @param jobId jobId
//     */
//    private void deleteAgentJobArchiveTmpFileIfExist(String jobId) {
//        // debug 模式，不删临时文件
//        if (environmentContext.openAgentJobArchiveDebug()) {
//            return;
//        }
//        // tmp/agentJobArchive
//        String agentJobArchiveTmpDir = environmentContext.getAgentJobArchiveTmpDir();
//        Path jobIdPath = Paths.get(agentJobArchiveTmpDir, jobId);
//
//        if (Files.exists(jobIdPath)) {
//            try {
//                Files.walkFileTree(jobIdPath, new SimpleFileVisitor<Path>() {
//                    @Override
//                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                        Files.delete(file);
//                        return FileVisitResult.CONTINUE;
//                    }
//
//                    @Override
//                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                        Files.delete(dir);
//                        return FileVisitResult.CONTINUE;
//                    }
//                });
//            } catch (IOException e) {
//                LOGGER.error("jobId:{} delete agent job archive tmp file error:{}",
//                        jobId,
//                        ExceptionUtil.getErrorMessage(e),
//                        e);
//            }
//        }
//    }
//
//    @Override
//    public Set<EScheduleJobType> focusedJobTypes() {
//        return Sets.newHashSet(EScheduleJobType.SHELL_ON_AGENT, EScheduleJobType.PYTHON_ON_AGENT);
//    }
//
//    @Override
//    public AppType focusedAppType() {
//        return AppType.RDOS;
//    }
//}
