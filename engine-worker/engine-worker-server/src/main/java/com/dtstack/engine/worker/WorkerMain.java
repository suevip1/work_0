package com.dtstack.engine.worker;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.worker.log.WorkerLogbackComponent;
import com.dtstack.rpc.annotation.RpcEnable;
import com.dtstack.rpc.enums.RpcRemoteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dtstack.engine.*"})
@RpcEnable(remoteType = RpcRemoteType.DAGSCHEDULEX_SERVER)
public class WorkerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerMain.class);

    public static void main(String[] args){
        try {
            LOGGER.info("engine-worker start begin...");
            SystemPropertyUtil.setSystemUserDir(args);
            setWorkerBean();
            new SpringApplication(WorkerLogbackComponent.class, EnvironmentContext.class, WorkerBeanConfig.class, WorkerMain.class).run(args);
            ShutdownHookUtil.addShutdownHook(WorkerMain::shutdown, WorkerMain.class.getSimpleName(), LOGGER);
            System.setSecurityManager(new NoExitSecurityManager());
            LOGGER.info("engine-worker start end...");

        } catch (Throwable e) {
            LOGGER.error("engine-worker start error:", e);
            System.exit(-1);
        }
    }

    private static void setWorkerBean() {
        System.setProperty("flink.lock.open", "true");
        LOGGER.info("flink.lock.open:{}","true");
    }


    private static void shutdown() {
        LOGGER.info("WorkerMain is shutdown...");
    }
}
