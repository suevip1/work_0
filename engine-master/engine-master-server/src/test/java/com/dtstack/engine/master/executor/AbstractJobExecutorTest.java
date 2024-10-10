package com.dtstack.engine.master.executor;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.AbstractJobExecutorMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/28 9:11 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(AbstractJobExecutorMock.class)
public class AbstractJobExecutorTest {

    @Test
    public void  test () throws Exception {
        CronJobExecutor cronJobExecutor = new CronJobExecutor();

        cronJobExecutor.afterPropertiesSet();
        try {
            new Thread(cronJobExecutor).start();
            Thread.sleep(10000L);
//            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cronJobExecutor.stop();
    }

}
