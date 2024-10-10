package com.dtstack.engine.master.cron;

import cn.hutool.extra.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.regex.Pattern;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2023-05-15 11:02
 */
@Component
public class DirtyCleanCron implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirtyCleanCron.class);

    /**
     * 匹配 ddtbXxx.tmp 临时文件
     */
    public static final Pattern DDTB_TMP = Pattern.compile("^ddtb\\S{0,}\\.tmp$");

    /**
     * 定时清理
     */
    @Scheduled(cron = "${dirty.clean.cron:0 0/30 * * * ?} ")
    public void clean() {
        deleteTomcatDdtbTmpFileNoException();
    }

    /**
     * Bean 销毁时清理
     */
    @PreDestroy
    public void destory() {
        deleteTomcatDdtbTmpFileNoException();
    }

    /**
     * 系统初始化完成后清理
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        deleteTomcatDdtbTmpFileNoException();
    }

    private void deleteTomcatDdtbTmpFileNoException() {
        try {
            deleteTomcatDdtbTmpFile();
        } catch (Exception e) {
            LOGGER.error("delete Tomcat ddtbTmpFile error", e);
        }
    }

    /**
     * 删除 tomcat 产生的 ddtbXxx.tmp 临时文件，用于解决文件过大的问题
     * http://zenpms.dtstack.cn/zentao/bug-view-79105.html
     */
    private void deleteTomcatDdtbTmpFile() {
        ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
        ServerProperties.Tomcat tomcat = serverProperties.getTomcat();
        if (tomcat == null) {
            return;
        }
        File basedir = tomcat.getBasedir();
        if (!basedir.exists()){
            return;
        }
        File[] files = basedir.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            // 删除 ddtbXxx.tmp 临时文件
            if (file.isFile() && DDTB_TMP.matcher(fileName).matches()) {
                file.delete();
                LOGGER.info("delete Tomcat ddtbTmpFile:{} ok", file.getName());
            }
        }
    }
}