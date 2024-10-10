package com.dtstack.engine.master.executor;


import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.JobExecutorTriggerMock;
import org.junit.Test;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * @Auther: dazhi
 * @Date: 2022/6/29 11:29 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(JobExecutorTriggerMock.class)
public class JobExecutorTriggerTest {

    JobExecutorTrigger jobExecutorTrigger = new JobExecutorTrigger();


    @Test
    public void  getAllNodesJobQueueInfoTest () throws Exception {
        jobExecutorTrigger.getAllNodesJobQueueInfo();
        jobExecutorTrigger.recoverOtherNode();
//        jobExecutorTrigger.onApplicationEvent(null);
//        jobExecutorTrigger.destroy();
    }

}
