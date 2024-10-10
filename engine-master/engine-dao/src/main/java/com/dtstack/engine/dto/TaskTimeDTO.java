package com.dtstack.engine.dto;

import java.sql.Timestamp;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName TaskTimeDTO
 * @date 2022/7/20 10:39 AM
 */
public class TaskTimeDTO {

    /**
     * 查询任务创建（提交）时间的最小时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private Timestamp startCreateTime;

    /**
     * 查询任务创建（提交）时间的最大时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private Timestamp endCreateTime;

    /**
     * 查询任务更新时间的最小时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private Timestamp startUpdateTime;

    /**
     * 查询任务更新时间的最大时间：格式：yyyy-MM-dd HH:mm:ss
     */
    private Timestamp endUpdateTime;

    public Timestamp getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(Timestamp startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public Timestamp getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(Timestamp endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public Timestamp getStartUpdateTime() {
        return startUpdateTime;
    }

    public void setStartUpdateTime(Timestamp startUpdateTime) {
        this.startUpdateTime = startUpdateTime;
    }

    public Timestamp getEndUpdateTime() {
        return endUpdateTime;
    }

    public void setEndUpdateTime(Timestamp endUpdateTime) {
        this.endUpdateTime = endUpdateTime;
    }
}
