package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.mockcontainer.impl.JobCheckpointDealerMock;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * @Auther: dazhi
 * @Date: 2022/7/1 10:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobCheckpointDealerMock.class)
public class JobCheckpointDealerTest {

    JobCheckpointDealer jobCheckpointDealer = new JobCheckpointDealer();

    @Test
    public void getRetryCheckPointPathTest( ) {
        JobClient jobClient = new JobClient();
        jobCheckpointDealer.getRetryCheckPointPath(jobClient);
    }

    @Test
    public void addCheckpointTaskForQueueTest() throws ExecutionException {
        JobIdentifier jobIdentifier = new JobIdentifier("1234", "1234", "1234");
        jobIdentifier.setEngineType("flink");
        jobCheckpointDealer.addCheckpointTaskForQueue(ComputeType.BATCH.getType(),"1234",
                jobIdentifier,"1234");
    }

    @Test
    public void afterPropertiesSetTest() throws InterruptedException {
        JobIdentifier jobIdentifier = new JobIdentifier("1234", "1234", "1234");
        jobIdentifier.setEngineType("flink");
        JobCheckpointInfo jobCheckpointInfo = new JobCheckpointInfo(jobIdentifier,"flink");
        jobCheckpointDealer.updateCheckpointImmediately(jobCheckpointInfo, "1234", 4);

        jobCheckpointDealer.onApplicationEvent(null);
        Thread.sleep(10000L);
    }
}
