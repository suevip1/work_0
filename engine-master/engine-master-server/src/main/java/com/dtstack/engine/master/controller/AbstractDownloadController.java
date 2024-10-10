package com.dtstack.engine.master.controller;

import com.dtstack.engine.common.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;

public class AbstractDownloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDownloadController.class);

    protected void downloadWithFile(File downFile, HttpServletResponse response, String charset, boolean autoDeleted) {
        if (StringUtils.isNotBlank(charset)) {
            response.setHeader("Content-Type", "application/octet-stream;charset=" + charset);
        } else {
            response.setHeader("Content-Type", "application/octet-stream;charset=UTF-8");
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        try {
            if (null != downFile && downFile.isFile()) {
                response.setHeader("Content-Disposition", "attachment;filename=" + encodeURIComponent(downFile.getName()));
                ServletOutputStream outputStream = response.getOutputStream();
                byte[] bytes = FileUtils.readFileToByteArray(downFile);
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            response.setHeader("Content-Disposition", "attachment;filename=error.log");
            LOGGER.error("", e);
            try {
                response.getWriter().write("下载文件异常:" + e.getMessage());
            } catch (Exception eMsg) {
                LOGGER.error("", eMsg);
            }
        } finally {
            if (null != downFile && autoDeleted) {
                downFile.delete();
            }
        }
    }

    private static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
        }
        return value;
    }
}
