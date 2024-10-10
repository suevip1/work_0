package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.master.mockcontainer.impl.JobDealerMock;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/7/3 8:17 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobDealerMock.class)
public class JobDealerTest {

    JobDealer jobDealer = new JobDealer();

    @Test
    public void afterPropertiesSetTest() throws Exception {
        jobDealer.setApplicationContext(null);
        try {
            jobDealer.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllNodesGroupQueueInfoTest() {
        jobDealer.getGroupPriorityQueue("1234");
        jobDealer.getAllNodesGroupQueueInfo();
    }

    @Test
    public void addSubmitJobTest() {
        JobClient jobClient = new JobClient();
        jobClient.setType(1);
        jobClient.setComputeType(ComputeType.BATCH);
        jobClient.setJobType(EJobType.SQL);
        jobDealer.addSubmitJob(jobClient);
        jobDealer.updateCache(jobClient,1);
    }

    @Test
    public void afterSubmitJobVastTest() {
        JobClient jobClient = new JobClient();
        jobClient.setTaskId("123");
        jobDealer.afterSubmitJobVast(Lists.newArrayList(jobClient));
    }

    @Test
    public void getAndUpdateEngineLogTest() {
        jobDealer.getAndUpdateEngineLog("1234", "1234", "1234", 1L);
    }
}
