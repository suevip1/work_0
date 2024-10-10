package com.dtstack.engine.po;

import java.util.Date;

/**
 * 
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-29 22:27
 */
/**
    * 文件同步配置表
    */
public class ConsoleFileSync {
    private Integer id;

    /**
    * 集群id
    */
    private Long clusterId;

    /**
    * 需同步路径
    */
    private String syncPath;

    /**
    * 是否同步
    */
    private Integer isSync;

    /**
    * 上次文件同步的md5
    */
    private String lastSyncMd5;

    /**
    * 新增时间
    */
    private Date gmtCreate;

    /**
    * 修改时间
    */
    private Date gmtModified;

    /**
    * 0正常 1逻辑删除
    */
    private Integer isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getSyncPath() {
        return syncPath;
    }

    public void setSyncPath(String syncPath) {
        this.syncPath = syncPath;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }

    public String getLastSyncMd5() {
        return lastSyncMd5;
    }

    public void setLastSyncMd5(String lastSyncMd5) {
        this.lastSyncMd5 = lastSyncMd5;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}