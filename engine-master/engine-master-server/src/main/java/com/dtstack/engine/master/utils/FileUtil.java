package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-25
 */
public class FileUtil extends org.apache.commons.io.FileUtils {

    public static List<String> safeExtends = Lists.newArrayList("json", "xml", "zip", "excel", "csv");

    /**
     * 解析文件 每一行带换行符
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static String getContentFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder content = new StringBuilder();
            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            } catch (IOException e) {
                throw new RuntimeException(ErrorCode.FILE_PARSE_ERROR.getMsg(), e);
            }
            return content.toString();
        }
        throw new FileNotFoundException(ErrorCode.FILE_NOT_FOUND.getMsg() + ":" + filePath);
    }

    public static String getUploadFileName(Integer taskType, String jobId) {
        EScheduleJobType jobType = EScheduleJobType.getEJobType(taskType);
        if (null == jobType) {
            throw new RdosDefineException("not support upload file taskType " + taskType);
        }
        String fileEndFix = ".py";
        if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
            fileEndFix = ".sh";
        }
        return String.format("%s_%s_%s%s", jobType.name(), jobId, System.currentTimeMillis(), fileEndFix);
    }


    public static String getTempJobRefUploadFileName(Integer taskType, Long taskId) {
        EScheduleJobType jobType = EScheduleJobType.getEJobType(taskType);
        if (null == jobType) {
            throw new RdosDefineException("not support upload file taskType " + taskType);
        }
        String fileEndFix = ".py";
        if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
            fileEndFix = ".sh";
        }
        return String.format("%s_%s_ref_%s%s", jobType.name(), taskId, System.currentTimeMillis(), fileEndFix);
    }

    public static String getOriginalFileName(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.EMPTY;
        }
        path = path.substring(path.lastIndexOf('/') + 1);
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return path;
        }
        return path.substring(0, path.lastIndexOf("."));
    }


    public static void mkdirsIfNotExist(String directoryPath) {
        if (StringUtils.isEmpty(directoryPath)) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            try {
                FileUtils.forceMkdir(directory);
            } catch (IOException e) {
                throw new RdosDefineException(e.getMessage());
            }
        }
    }

    public static boolean del(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

}
