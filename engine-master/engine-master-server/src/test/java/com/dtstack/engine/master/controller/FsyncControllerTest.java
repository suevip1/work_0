package com.dtstack.engine.master.controller;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.manager.FsyncManager;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-01 23:42
 */
@MockWith(FsyncControllerTest.Mock.class)
public class FsyncControllerTest {
    private FsyncController controller = new FsyncController();

    public static class Mock{
        @MockInvoke(targetClass = ScheduleJobService.class)
        public ScheduleJob getSimpleByJobId(String jobId, Integer isDeleted)  {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setJobId(jobId);
            scheduleJob.setIsDeleted(isDeleted);
            scheduleJob.setStatus(RdosTaskStatus.FINISHED.getStatus());
            scheduleJob.setNodeAddress("127.0.0.1");
            return scheduleJob;
        }

        @MockInvoke(targetClass = EnvironmentContext.class)
        public String getLocalAddress() {
            return "127.0.0.1";
        }

        @MockInvoke(targetClass = FsyncManager.class)
        public String generateFsyncDir(ScheduleJob scheduleJob) {
            return "";
        }

        @MockInvoke(targetClass = FsyncManager.class)
        public String concatFilePath(String dir, String fileName) {
            return "";
        }

        @MockInvoke(targetClass = FsyncManager.class)
        public <T> T readFromFile(String filePath, Class<T> clazz) {
            return (T)Collections.emptyList();
        }
    }

    @Test
    public void testListFsyncData() {
        FsyncController.FsyncQueryDTO fsyncQueryDTO = new FsyncController.FsyncQueryDTO();
        fsyncQueryDTO.setJobId("jobId-a");
        fsyncQueryDTO.setSqlId("sqlId-1");
        HttpServletResponse response = null;
        controller.listFsyncData(fsyncQueryDTO, response);
    }
}
