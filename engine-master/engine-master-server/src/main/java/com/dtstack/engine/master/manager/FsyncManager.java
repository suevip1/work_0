package com.dtstack.engine.master.manager;

import cn.hutool.core.io.FileUtil;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.KryoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-11-01 14:46
 * 结果集落盘
 */
@Component
public class FsyncManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsyncManager.class);

    @Value("${temp.job.fsync.path:${user.dir}/tempJobFsync}")
    private String tempJobFsyncDir;

    @Resource
    private ThreadPoolTaskExecutor commonExecutor;

    /**
     * 将对象序列化到文件
     *
     * @param obj
     * @param filePath
     * @param <T>
     * @throws IOException
     */
    public <T> void writeToFile(T obj, String filePath) throws IOException {
        FileUtil.touch(filePath);
        KryoUtil.writeToFile(obj, filePath);
    }

    /**
     * 将文件内容反序列化为原对象
     *
     * @param filePath
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T readFromFile(String filePath, Class<T> clazz) throws IOException {
        boolean exist = FileUtil.exist(filePath);
        if (!exist) {
            return null;
        }
        T object = KryoUtil.readFromFile(filePath, clazz);
        return object;
    }

    /**
     * 删除文件或者文件夹
     *
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        return FileUtil.del(filePath);
    }

    /**
     * 异步删除文件或者文件夹
     *
     * @param filePath
     */
    public void asyncDelete(String filePath) {
        commonExecutor.execute(() -> delete(filePath));
    }

    /**
     * 异步删除文件或者文件夹，且报错不抛异常
     *
     * @param filePath
     */
    public void asyncDeleteIgnoreException(String filePath) {
        try {
            asyncDelete(filePath);
        } catch (Exception e) {
            LOGGER.warn("del:{} error", filePath, e);
        }
    }

    /**
     * 拼接路径
     *
     * @param dir
     * @param fileName
     * @return
     */
    public String concatFilePath(String dir, String fileName) {
        return dir + File.separator + fileName;
    }

    /**
     * 生成落盘目录: yyyyMMdd/HH/job_id，以「小时」为粒度组织文件
     *
     * @return
     */
    public String generateFsyncDir(String cycTime, Timestamp gmtCreate, String jobId) {
        String fileDay;
        String fileHour;
        // 截取「yyyyMMdd」、「HH」
        if (StringUtils.length(cycTime) >= DateUtil.UN_STANDARD_DATETIME_FORMAT.length()) {
            fileDay = cycTime.substring(0, 8);
            fileHour = cycTime.substring(8, 10);
        } else {
            String dateTime = DateUtil.getFormattedDate(gmtCreate.getTime(), DateUtil.HOUR_FORMAT);
            fileDay = dateTime.substring(0, 8);
            fileHour = dateTime.substring(8, 10);
        }

        return tempJobFsyncDir + File.separator + fileDay + File.separator + fileHour + File.separator + jobId;
    }

    public String getTempJobFsyncDir() {
        return tempJobFsyncDir;
    }
}
