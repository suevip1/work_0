package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.failover.FailoverStrategy;
import com.dtstack.engine.master.listener.MasterListener;
import com.dtstack.engine.master.mockcontainer.BaseMock;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:06 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class HeartBeatCheckListenerMock extends BaseMock {


    @MockInvoke(targetClass = MasterListener.class)
    public boolean isMaster() {
        return Boolean.TRUE;
    }

    @MockInvoke(targetClass = FailoverStrategy.class)
    public void dataMigration(String node) {

    }
}
