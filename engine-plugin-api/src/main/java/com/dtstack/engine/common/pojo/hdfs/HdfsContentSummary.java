package com.dtstack.engine.common.pojo.hdfs;

import java.io.Serializable;

/**
 * <p>HDFS文件内容摘要</>
 * 包括包括文件的数量，文件夹的数量，以及这个文件夹的大小等内容
 *
 * @author ：wangchuan
 * date：Created in 11:39 上午 2020/12/11
 * company: www.dtstack.com
 */
public class HdfsContentSummary implements Serializable {
    /**
     * 文件在hdfs的绝对路径
     */
    private String hdfsPath;
    /**
     * 文件数量
     */
    private Long fileCount;
    /**
     * 文件夹数量
     */
    private Long directoryCount;
    /**
     * 占用存储
     */
    private Long spaceConsumed;
    /**
     * 文件(夹)更新时间
     */
    private Long ModifyTime;
    /**
     * 路径是否存在
     */
    private Boolean isExists;

    public HdfsContentSummary(Long fileCount, Long directoryCount, Long spaceConsumed, Long modifyTime, Boolean isExists, String hdfsPath) {
        this.fileCount = fileCount;
        this.directoryCount = directoryCount;
        this.spaceConsumed = spaceConsumed;
        ModifyTime = modifyTime;
        this.isExists = isExists;
        this.hdfsPath = hdfsPath;
    }

    public Long getFileCount() {
        return fileCount;
    }

    public void setFileCount(Long fileCount) {
        this.fileCount = fileCount;
    }

    public Long getDirectoryCount() {
        return directoryCount;
    }

    public void setDirectoryCount(Long directoryCount) {
        this.directoryCount = directoryCount;
    }

    public Long getSpaceConsumed() {
        return spaceConsumed;
    }

    public void setSpaceConsumed(Long spaceConsumed) {
        this.spaceConsumed = spaceConsumed;
    }

    public Long getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        ModifyTime = modifyTime;
    }

    public Boolean getExists() {
        return isExists;
    }

    public void setExists(Boolean exists) {
        isExists = exists;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }
}
