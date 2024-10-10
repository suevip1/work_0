package com.dtstack.engine.master.mockcontainer.impl;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.mockcontainer.BaseMock;

/**
 * @Auther: dazhi
 * @Date: 2022/6/10 10:59 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobRichOperatorMock extends BaseMock {

    @MockInvoke(targetClass = EnvironmentContext.class)
    public Integer getCycTimeDayGap() {
        return 1;
    }

}
