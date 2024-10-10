package com.dtstack.engine.master.manager;

import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.api.domain.ScheduleJob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-01 23:43
 */
public class FsyncManagerTest {


    public static final String MOCK_TEST_FSYNC = "test.fsync";

    private String fsyncFilePath = getClass().getClassLoader().getResource(MOCK_TEST_FSYNC).getPath();

    FsyncManager fsyncManager = new FsyncManager();

    @Before
    public void before() {
        PrivateAccessor.set(fsyncManager, "tempJobFsyncDir", fsyncFilePath);

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.setKeepAliveSeconds(0);
        threadPoolTaskExecutor.setQueueCapacity(5);
        threadPoolTaskExecutor.initialize();
        ReflectionTestUtils.setField(fsyncManager, "commonExecutor", threadPoolTaskExecutor);
    }

    @Test
    public void testWriteToFile() throws IOException {
        ScheduleJob job = new ScheduleJob();
        job.setJobId("abc");
        fsyncManager.writeToFile(job, fsyncFilePath);
    }

    @Test
    public void testReadFromFile() throws IOException {
        ScheduleJob scheduleJob = fsyncManager.readFromFile(fsyncFilePath, ScheduleJob.class);
        Assert.assertNotNull(scheduleJob);
    }

    @Test
    public void testDelete() {
        fsyncManager.delete(fsyncFilePath);
    }

    @Test
    public void testAsyncDelete() {
        fsyncManager.asyncDelete(fsyncFilePath);
    }

    @Test
    public void testAsyncDeleteIgnoreException() {
        fsyncManager.asyncDeleteIgnoreException(fsyncFilePath);
    }

    @Test
    public void testConcatFilePath() {
        fsyncManager.concatFilePath("dir", "fileName");
    }

    @Test
    public void testGenerateFsyncDir() {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setCycTime("20221222100000");
        fsyncManager.generateFsyncDir(scheduleJob.getCycTime(), scheduleJob.getGmtCreate(), scheduleJob.getJobId());
    }

    @Test
    public void testGetTempJobFsyncDir() {
        fsyncManager.getTempJobFsyncDir();
    }
}
