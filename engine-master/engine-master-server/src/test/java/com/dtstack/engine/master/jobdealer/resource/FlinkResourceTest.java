package com.dtstack.engine.master.jobdealer.resource;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.master.mockcontainer.impl.AbstractJobExecutorMock;
import com.dtstack.engine.master.mockcontainer.impl.FlinkResourceMocK;
import org.junit.Test;

import java.util.Properties;

/**
 * @Auther: dazhi
 * @Date: 2022/6/30 9:05 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(FlinkResourceMocK.class)
public class FlinkResourceTest {

    FlinkResource flinkResource = new FlinkResource();

    @Test
    public void getComputeResourceTypeTest() {
        JobClient jobClient = new JobClient();
        jobClient.setTenantId(1L);
        jobClient.setConfProperties(new Properties());


        flinkResource.getComputeResourceType(jobClient);
    }
}
