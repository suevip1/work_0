package com.dtstack.engine.common.client.bean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2021/11/5 3:57 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class EngineJobCheckpointDTO {
    private long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * ck ID
     */
    private String taskEngineId;

    /**
     * ck ID
     */
    private String checkpointId;

    /**
     * ck 日期
     */
    private Timestamp checkpointTrigger;

    /**
     * ck 路径
     */
    private String checkpointSavepath;

    /**
     * 计数
     */
    private String checkpointCounts;

    private Timestamp triggerStart;

    private Timestamp triggerEnd;

    /**
     * page size
     */
    private Integer size;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskEngineId() {
        return taskEngineId;
    }

    public void setTaskEngineId(String taskEngineId) {
        this.taskEngineId = taskEngineId;
    }

    public String getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(String checkpointId) {
        this.checkpointId = checkpointId;
    }

    public Timestamp getCheckpointTrigger() {
        return checkpointTrigger;
    }

    public void setCheckpointTrigger(Timestamp checkpointTrigger) {
        this.checkpointTrigger = checkpointTrigger;
    }

    public String getCheckpointSavepath() {
        return checkpointSavepath;
    }

    public void setCheckpointSavepath(String checkpointSavepath) {
        this.checkpointSavepath = checkpointSavepath;
    }

    public String getCheckpointCounts() {
        return checkpointCounts;
    }

    public void setCheckpointCounts(String checkpointCounts) {
        this.checkpointCounts = checkpointCounts;
    }

    public Timestamp getTriggerStart() {
        return triggerStart;
    }

    public void setTriggerStart(Timestamp triggerStart) {
        this.triggerStart = triggerStart;
    }

    public Timestamp getTriggerEnd() {
        return triggerEnd;
    }

    public void setTriggerEnd(Timestamp triggerEnd) {
        this.triggerEnd = triggerEnd;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
