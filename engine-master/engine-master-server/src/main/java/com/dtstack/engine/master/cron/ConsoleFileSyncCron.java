package com.dtstack.engine.master.cron;

import com.dtstack.engine.master.handler.AbstractMasterHandler;
import com.dtstack.engine.master.impl.ConsoleFileSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 控制台配置文件同步
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-30 16:23
 */
@Component
public class ConsoleFileSyncCron {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleFileSyncCron.class);

    @Autowired
    private ConsoleFileSyncService consoleFileSyncService;

    /**
     * 每隔 10 分钟同步一次
     */
    @EngineCron
    @Scheduled(cron = "${console.file.sync.cron:0 0/10 * * * ? } ")
    public void handle() {
        LOGGER.info("{} do handle", this.getClass().getSimpleName());
        this.sync();
    }

    public void sync() {
        consoleFileSyncService.sync();
    }
}
