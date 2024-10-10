package com.dtstack.engine.master.listener;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.HeartBeatCheckListenerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:06 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(HeartBeatCheckListenerMock.class)
public class HeartBeatCheckListenerTest {

    @Test
    public void  test () {
        HeartBeatCheckListener heartBeatCheckListener = new HeartBeatCheckListener(null, null, null);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
