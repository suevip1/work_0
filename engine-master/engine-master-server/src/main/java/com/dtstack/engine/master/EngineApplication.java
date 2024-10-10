package com.dtstack.engine.master;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.rpc.annotation.RpcEnable;
import com.dtstack.rpc.enums.RpcRemoteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
@EnableScheduling
@EnableAsync
@ComponentScan("com.dtstack")
@RpcEnable(basePackage = "com.dtstack.engine.common.api,com.dtstack.dtcenter.loader.client",remoteType = RpcRemoteType.DAGSCHEDULEX_CLIENT)
public class EngineApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(EngineApplication.class);

    public static void main(String[] args) {
        try {
            SystemPropertyUtil.setSystemUserDir(args);
            System.setProperty("datasourcex.rpc.scan.open","false");
            System.setProperty("datasourcex.deploy.mode","remote");
            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
            SpringApplication application = new SpringApplication(EngineApplication.class);
            application.setAllowBeanDefinitionOverriding(true);
            application.setAllowCircularReferences(true);
            application.run(args);
            System.setSecurityManager(new NoExitSecurityManager());
        } catch (Throwable t) {
            LOGGER.error("start error:", t);
            System.exit(-1);
        } finally {
            LOGGER.info("engine-master start end...");
        }
    }
}