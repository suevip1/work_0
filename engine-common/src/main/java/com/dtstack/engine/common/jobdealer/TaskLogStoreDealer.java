package com.dtstack.engine.common.jobdealer;

import com.dtstack.dtcenter.common.security.rsa.RSAUtils;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.logstore.AbstractLogStore;
import com.dtstack.engine.common.logstore.LogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class TaskLogStoreDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private AbstractLogStore logStore;
    private ScheduledExecutorService scheduledService;
    private Map<String, String> dbConfig = new HashMap<>();

    @Autowired
    private EnvironmentContext environmentContext;

    @PostConstruct
    public void init() {
        dbConfig.put(ConfigConstant.JDBCURL, environmentContext.getJdbcUrl());
        dbConfig.put(ConfigConstant.USERNAME, environmentContext.getJdbcUser());
        String jdbcPassword = null;
        try {
            jdbcPassword = RSAUtils.decrypt(environmentContext.getJdbcPassword());
        } catch (Exception e) {
            jdbcPassword = environmentContext.getJdbcPassword();
        }
        dbConfig.put(ConfigConstant.PASSWORD, jdbcPassword);
        dbConfig.put(ConfigConstant.INITIAL_SIZE, String.valueOf(environmentContext.getInitialPoolSize()));
        dbConfig.put(ConfigConstant.MINIDLE, String.valueOf(environmentContext.getMinPoolSize()));
        dbConfig.put(ConfigConstant.MAXACTIVE, String.valueOf(environmentContext.getMaxPoolSize()));
        dbConfig.put(ConfigConstant.RDB_TIME_OUT, String.valueOf(environmentContext.getRdbTimeOut()));

        logStore = LogStoreFactory.getLogStore(dbConfig);

        this.scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("TaskLogStoreDealer start again...");
            logStore.clearJob();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

}
