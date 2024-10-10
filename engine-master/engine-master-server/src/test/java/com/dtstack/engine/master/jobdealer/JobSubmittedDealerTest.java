package com.dtstack.engine.master.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.JobSubmittedDealerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/7/3 9:35 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobSubmittedDealerMock.class)
public class JobSubmittedDealerTest {

    JobSubmittedDealer jobSubmittedDealer = new JobSubmittedDealer();

    @Test
    public void test() {
        new Thread(jobSubmittedDealer).start();

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
