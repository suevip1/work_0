package com.dtstack.engine.master.jobdealer.resource;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.master.mockcontainer.impl.FlinkResourceMocK;
import com.dtstack.engine.master.mockcontainer.impl.JobComputeResourcePlainMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 10:08 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobComputeResourcePlainMock.class)
public class JobComputeResourcePlainTest {

    JobComputeResourcePlain jobComputeResourcePlain = new JobComputeResourcePlain();

    @Test
    public void getJobComputeResourcePlainTest() {
        JobClient jobClient =  new JobClient();
        jobClient.setTenantId(1L);
        jobClient.setEngineType("kubernetes");
        jobClient.setComputeType(ComputeType.BATCH);
        jobClient.setGroupName("ComputeType.BATCH");
        jobClient.setType(ComputeType.BATCH.getType());
        jobClient.setJobType(EJobType.SQL);
        jobComputeResourcePlain.getJobResource(jobClient);

        JobClient jobClient1 =  new JobClient();
        jobClient1.setTenantId(1L);
        jobClient1.setEngineType("spark");
        jobClient1.setComputeType(ComputeType.BATCH);
        jobClient1.setGroupName("ComputeType.BATCH");
        jobClient1.setType(ComputeType.BATCH.getType());
        jobClient1.setJobType(EJobType.SQL);
        jobComputeResourcePlain.getJobResource(jobClient1);
    }
}
