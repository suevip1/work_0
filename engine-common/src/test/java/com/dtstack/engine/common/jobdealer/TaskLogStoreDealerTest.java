package com.dtstack.engine.common.jobdealer;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.BaseMock;
import org.junit.Test;

@MockWith(BaseMock.class)
public class TaskLogStoreDealerTest {

    private static TaskLogStoreDealer taskLogStoreDealer = new TaskLogStoreDealer();

    @Test
    public void init() {
        taskLogStoreDealer.init();
    }

    @Test
    public void run() {
        taskLogStoreDealer.run();
    }
}