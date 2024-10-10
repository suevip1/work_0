package com.dtstack.engine.api.vo.components;

import java.sql.Timestamp;

/**
 * @author yuebai
 * @date 2023/4/12
 */
public class ClusterFilePathVO {
    private String sftpConf;
    private Timestamp fileModifiedTime;
    private String filePath;

    public String getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(String sftpConf) {
        this.sftpConf = sftpConf;
    }

    public Timestamp getFileModifiedTime() {
        return fileModifiedTime;
    }

    public void setFileModifiedTime(Timestamp fileModifiedTime) {
        this.fileModifiedTime = fileModifiedTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
