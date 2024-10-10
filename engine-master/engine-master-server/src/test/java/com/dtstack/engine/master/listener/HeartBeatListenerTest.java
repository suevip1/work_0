package com.dtstack.engine.master.listener;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.HeartBeatListenerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:22 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(HeartBeatListenerMock.class)
public class HeartBeatListenerTest {

    @Test
    public void  test () {
        HeartBeatListener heartBeatListener = new HeartBeatListener(null);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
