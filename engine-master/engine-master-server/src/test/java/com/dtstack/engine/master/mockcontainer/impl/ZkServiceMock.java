package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.mockcontainer.BaseMock;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;

/**
 * @author leon
 * @date 2022-06-24 10:13
 **/
public class ZkServiceMock extends BaseMock {

    @MockInvoke(targetClass = CuratorFrameworkImpl.class)
    public void start() {}

    @MockInvoke(targetClass = CuratorFrameworkImpl.class)
    public CreateBuilder create() {
        return null;
    }
}
