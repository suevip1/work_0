package com.dtstack.engine.master.listener;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.impl.AppReadyListenerMock;
import org.junit.Test;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:01 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@MockWith(AppReadyListenerMock.class)
public class AppReadyListenerTest {
    AppReadyListener appReadyListener = new AppReadyListener();

    @Test
    public void onApplicationEventTest() {
        appReadyListener.onApplicationEvent(null);

    }

}
