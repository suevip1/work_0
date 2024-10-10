package com.dtstack.engine.master.listener;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.QueueListenerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:37 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(QueueListenerMock.class)
public class QueueListenerTest {

    private QueueListener queueListener = new QueueListener();

    @Test
    public void afterPropertiesSetTest() throws Exception {
        queueListener.afterPropertiesSet();

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
