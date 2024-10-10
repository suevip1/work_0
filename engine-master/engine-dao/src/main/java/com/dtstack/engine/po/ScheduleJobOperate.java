package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/15 1:47 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobOperate {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 操作内容
     */
    private String operateContent;

    /**
     * 操作人
     */
    private Long operateId;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 更新时间
     */
    private Timestamp gmtModified;

    /**
     *
     */
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public Long getOperateId() {
        return operateId;
    }

    public void setOperateId(Long operateId) {
        this.operateId = operateId;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
