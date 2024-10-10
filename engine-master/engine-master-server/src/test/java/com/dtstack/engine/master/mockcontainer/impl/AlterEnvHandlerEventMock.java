package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;

/**
 * @Auther: dazhi
 * @Date: 2022/6/26 8:15 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterEnvHandlerEventMock {


    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getHttpAddress() {
        return "";
    }

    @MockInvoke(targetClass = EnvironmentContext.class)
    public String getLocalAddress() {
        return "";
    }

}
