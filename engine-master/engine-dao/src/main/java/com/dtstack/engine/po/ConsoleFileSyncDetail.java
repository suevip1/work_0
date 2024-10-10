package com.dtstack.engine.po;

import java.util.Date;

/**
 * 
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2021-12-30 11:43
 */
/**
    * 文件同步配置细节表
    */
public class ConsoleFileSyncDetail {
    private Integer id;

    /**
    * 文件名称
    */
    private String fileName;

    /**
    * 是否勾选
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

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
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