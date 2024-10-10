package com.dtstack.engine.master.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;

/**
 * @author yuebai
 * @date 2021-09-08
 */
public class TestDtJobId {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDtJobId.class);

    public static void main(String[] args) throws Exception {
        //163108095200110000
        DtJobIdWorker dtJob = DtJobIdWorker.getInstance(1, 0);
        HashSet<String> jobIds = new HashSet<>();
        for (int j = 0; j < 10; j++) {
            String nextJobId = dtJob.nextJobId();
            if (jobIds.contains(nextJobId) || nextJobId.length() >= 12) {
                LOGGER.error("job id already exist " + nextJobId);
            }
            jobIds.add(nextJobId);
            System.out.println(nextJobId);
        }
    }
}
