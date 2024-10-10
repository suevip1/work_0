package com.dtstack.engine.master.scheduler;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.JobGraphBuilderTriggerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 10:40 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobGraphBuilderTriggerMock.class)
public class JobGraphBuilderTriggerTest {

    JobGraphBuilderTrigger jobGraphBuilderTrigger = new JobGraphBuilderTrigger();

    @Test
    public void dealMasterTest() throws Exception {
        jobGraphBuilderTrigger.dealMaster(false);

        Thread.sleep(10000);

        jobGraphBuilderTrigger.dealMaster(true);

        Thread.sleep(20000);

    }
}
