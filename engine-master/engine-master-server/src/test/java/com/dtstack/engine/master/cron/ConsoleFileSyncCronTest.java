package com.dtstack.engine.master.cron;

import com.alibaba.testable.core.annotation.MockWith;
import com.dtstack.engine.master.mockcontainer.ConsoleFileSyncCronMock;
import org.junit.Test;

@MockWith(ConsoleFileSyncCronMock.class)
public class ConsoleFileSyncCronTest {

    private static final ConsoleFileSyncCron  consoleFileSyncCron = new ConsoleFileSyncCron();

    @Test
    public void handle() {
        consoleFileSyncCron.handle();
    }

    @Test
    public void sync() {
        consoleFileSyncCron.sync();
    }
}