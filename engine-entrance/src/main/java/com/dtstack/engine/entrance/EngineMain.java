package com.dtstack.engine.entrance;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.JavaPolicyUtils;
import com.dtstack.engine.common.util.ShutdownHookUtil;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/29
 */
@SpringBootApplication(scanBasePackages = {"com.dtstack.engine.*"})
public class EngineMain implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineMain.class);

    @Value("${datasourcex.pluginLibs.path:${user.dir}/plugins}")
    private String path;


    public static void main(String[] args){
        try {
            // add hook
            SystemPropertyUtil.setSystemUserDir(args);
            System.setProperty("remote.rpcEnable", Boolean.FALSE.toString());
            new SpringApplicationBuilder(EngineMain.class).allowCircularReferences(true).run(args);
            ShutdownHookUtil.addShutdownHook(EngineMain::shutdown, EngineMain.class.getSimpleName(), LOGGER);
            System.setSecurityManager(new NoExitSecurityManager());
            JavaPolicyUtils.checkJavaPolicy();
        } catch (Throwable e) {
            LOGGER.error("EngineMain start error:", e);
            System.exit(-1);
        }
    }

    private static void shutdown() {
        LOGGER.info("EngineMain is shutdown...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClientCache.setPluginPath(path);
    }
}
