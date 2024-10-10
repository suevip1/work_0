package com.dtstack.engine.master.dto;

import java.util.Date;
import java.util.List;

/**
 * 文件同步 DTO
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-29 15:28
 */
public class ConsoleFileSyncDirectoryDTO {
    private Integer id;
    /**
     * 目录
     */
    private String directory;

    /**
     * 文件列表
     */
    private List<ConsoleFileSyncFileDTO> files;

    /**
     * 集群 id
     */
    private Long clusterId;

    /**
     * 是否开启同步
     */
    private Integer isSync;

    /**
     * 上次文件同步的md5
     */
    private String lastSyncMd5;

    public String getLastSyncMd5() {
        return lastSyncMd5;
    }

    public void setLastSyncMd5(String lastSyncMd5) {
        this.lastSyncMd5 = lastSyncMd5;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ConsoleFileSyncDirectoryDTO() {
    }

    public ConsoleFileSyncDirectoryDTO(String directory) {
        this.directory = directory;
    }

    public List<ConsoleFileSyncFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<ConsoleFileSyncFileDTO> files) {
        this.files = files;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }

    @Override
    public String toString() {
        return "ConsoleFileSyncDirectoryDTO{" +
                "id=" + id +
                ", directory='" + directory + '\'' +
                ", files=" + files +
                ", clusterId=" + clusterId +
                ", isSync=" + isSync +
                ", lastSyncMd5='" + lastSyncMd5 + '\'' +
                '}';
    }

    /**
     * 文件 DTO
     */
    public static class ConsoleFileSyncFileDTO {
        /**
         * 文件名称
         */
        private String fileName;

        /**
         * 是否选中
         */
        private Integer isChosen;

        /**
         * 上次同步时间
         */
        private Date lastSyncTime;

        /**
         * 集群id
         */
        private Long clusterId;

        public Long getClusterId() {
            return clusterId;
        }

        public void setClusterId(Long clusterId) {
            this.clusterId = clusterId;
        }

        public Date getLastSyncTime() {
            return lastSyncTime;
        }

        public void setLastSyncTime(Date lastSyncTime) {
            this.lastSyncTime = lastSyncTime;
        }

        public ConsoleFileSyncFileDTO() {
        }

        public ConsoleFileSyncFileDTO(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getIsChosen() {
            return isChosen;
        }

        public void setIsChosen(Integer isChosen) {
            this.isChosen = isChosen;
        }

        @Override
        public String toString() {
            return "ConsoleFileSyncFileDTO{" +
                    "fileName='" + fileName + '\'' +
                    ", isChosen=" + isChosen +
                    ", lastSyncTime=" + lastSyncTime +
                    ", clusterId=" + clusterId +
                    '}';
        }
    }
}
