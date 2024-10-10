package com.dtstack.engine.master.mockcontainer;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.dtstack.engine.master.impl.ConsoleFileSyncService;

/**
 * @author leon
 * @date 2022-06-27 17:51
 **/
public class ConsoleFileSyncCronMock {

    @MockInvoke(targetClass = ConsoleFileSyncService.class)
    public void sync() {}
}
