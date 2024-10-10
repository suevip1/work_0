package com.dtstack.engine.master.cron;

import cn.hutool.core.io.FileUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.manager.FsyncManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 临时任务落盘文件定期清理
 * 备注：每台服务器都可能产生文件，故此处不能指定 master 节点执行
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-01 21:15
 */
@Component
public class FsyncCleanCron {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsyncCleanCron.class);

    @Autowired
    private FsyncManager fsyncManager;

    /**
     * 默认留存最近 2 天(今天、昨天)
     */
    @Value("${temp.job.fsync.keepInDay:2}")
    private Integer tempJobFsyncKeepInDay;

    /**
     * 每天凌晨 2 点清理
     */
    @Scheduled(cron = "${temp.job.fsync.clean.cron:0 0 2 * * ? } ")
    public void clean() {
        // 最终留存的日期节点
        String endFileDay = new DateTime(DateUtil.getTodayStart(System.currentTimeMillis(), "MS"))
                .minusDays(tempJobFsyncKeepInDay).toString("yyyyMMdd");
        String tempJobFsync = fsyncManager.getTempJobFsyncDir();
        boolean exist = FileUtil.exist(tempJobFsync);
        if (!exist) {
            LOGGER.info("tempJobFsync not exist, skip clean");
            return;
        }
        // 约定文件夹的第一层级为 「yyyyMMdd」，筛选出超过留存期的文件夹
        List<String> fileDays = Arrays.stream(new File(tempJobFsync).listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
                .filter(fileDay -> StringUtils.compare(fileDay, endFileDay) <= 0)
                .sorted(String::compareTo)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fileDays)) {
            return;
        }
        for (String fileDay : fileDays) {
            String dir = fsyncManager.concatFilePath(tempJobFsync, fileDay);
            try {
                fsyncManager.delete(dir);
            } catch (Exception e) {
                LOGGER.error("tempJobFsync clean {} error", dir, e);
            }
        }
        LOGGER.info("tempJobFsync clean ok, size:{}", fileDays.size());
    }
}