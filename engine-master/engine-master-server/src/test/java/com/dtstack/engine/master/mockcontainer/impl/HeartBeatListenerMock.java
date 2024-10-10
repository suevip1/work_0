package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import com.dtstack.engine.master.zookeeper.ZkService;

/**
 * @Auther: dazhi
 * @Date: 2022/6/27 12:25 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class HeartBeatListenerMock extends BaseMock {

    @MockInvoke(targetClass = ZkService.class)
    public String getLocalAddress() {
        return "127.0.0.1:8090";
    }
}
