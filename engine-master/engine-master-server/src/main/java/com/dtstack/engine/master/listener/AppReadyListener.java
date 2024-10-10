package com.dtstack.engine.master.listener;

import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.multiengine.jobchainparam.JobChainParamCleaner;
import com.dtstack.engine.master.plugininfo.PluginInfoCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用启动执行
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-28 14:59
 */
@Component
public class AppReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private final static Logger logger = LoggerFactory.getLogger(AppReadyListener.class);

    @Autowired
    private ComponentService componentService;

    @Autowired
    private JobChainParamCleaner jobChainParamCleaner;

    @Autowired
    private PluginInfoCacheManager pluginInfoCacheManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("appReady listener start...");
        try {
            componentService.clearConfigCache();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            jobChainParamCleaner.scheduledCleanNotUsedOutputParams();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            pluginInfoCacheManager.clearPluginInfoCache();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        logger.info("appReady listener end.");
    }
}
