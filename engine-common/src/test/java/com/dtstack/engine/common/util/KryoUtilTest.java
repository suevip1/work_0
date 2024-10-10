package com.dtstack.engine.common.util;

import com.dtstack.engine.api.domain.ScheduleJob;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-01 23:23
 */
public class KryoUtilTest {

    @Test
    public void testWriteAndRead2Byte() throws IOException {
        ScheduleJob job = new ScheduleJob();
        job.setJobId("abc");
        byte[] bytes = KryoUtil.writeToByteArray(job);
        ScheduleJob jobCopy = KryoUtil.readFromByteArray(bytes);
        Assert.assertEquals(job.getJobId(), jobCopy.getJobId());

        byte[] bytesCopy = KryoUtil.writeObjectToByteArray(job);
        ScheduleJob jobCopy2 = KryoUtil.readObjectFromByteArray(bytesCopy, ScheduleJob.class);
        Assert.assertEquals(job.getJobId(), jobCopy2.getJobId());
    }

    @Test
    public void testWriteAndRead2File() throws IOException {
        ScheduleJob job = new ScheduleJob();
        job.setJobId("abc");

        String fsyncFilePath = getClass().getClassLoader().getResource("test.fsync").getPath();
        KryoUtil.writeToFile(job, fsyncFilePath);
        ScheduleJob jobCopy = KryoUtil.readFromFile(fsyncFilePath, ScheduleJob.class);
        Assert.assertEquals(jobCopy.getJobId(), job.getJobId());
    }
}