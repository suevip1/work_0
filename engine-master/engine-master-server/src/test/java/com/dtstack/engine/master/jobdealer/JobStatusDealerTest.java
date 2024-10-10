package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.JobStatusDealerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/7/6 11:36 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobStatusDealerMock.class)
public class JobStatusDealerTest {

    JobStatusDealer jobStatusDealer = new JobStatusDealer();

    @Test
    public void test() {
        jobStatusDealer.setApplicationContext(null);
        jobStatusDealer.start();

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
