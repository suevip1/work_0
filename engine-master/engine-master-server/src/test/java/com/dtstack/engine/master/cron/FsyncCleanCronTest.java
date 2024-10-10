package com.dtstack.engine.master.cron;

import com.alibaba.testable.core.annotation.MockInvoke;
import com.alibaba.testable.core.tool.PrivateAccessor;
import com.dtstack.engine.master.manager.FsyncManager;
import org.junit.Test;

public class FsyncCleanCronTest {
    FsyncCleanCron cron = new FsyncCleanCron();

    @Test
    public void test() {
        PrivateAccessor.set(cron,"tempJobFsyncKeepInDay",2);
        cron.clean();
    }

    public static class Mock {
        @MockInvoke(
                targetClass = FsyncManager.class,
                targetMethod = "getTempJobFsyncDir"
        )
        public String getTempJobFsyncDir() {
            return System.getProperty("user.dir");
        }
    }
}
