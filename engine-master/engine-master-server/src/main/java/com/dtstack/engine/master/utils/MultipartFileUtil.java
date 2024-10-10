package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.controller.ConsoleParamController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-07-14 15:44
 */
public class MultipartFileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFileUtil.class);

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";

    public static File parse2File(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS);
        }
        String fileOriginalName = multipartFile.getOriginalFilename();
        String path = uploadsDir + File.separator + fileOriginalName;
        File saveFile = new File(path);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        try {
            multipartFile.transferTo(saveFile);
        } catch (Exception e) {
            LOGGER.error("{} error", multipartFile.getOriginalFilename(), e);
            throw new RdosDefineException(ErrorCode.FILE_STORE_FAIL);
        }

        return new File(path);
    }
}
