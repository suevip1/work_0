package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2023/1/30 2:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskBatch {

    private Long id;

    private Long baselineTaskId;

    private String replyTime;

    private String cycTime;

    private Integer openStatus;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaselineTaskId() {
        return baselineTaskId;
    }

    public void setBaselineTaskId(Long baselineTaskId) {
        this.baselineTaskId = baselineTaskId;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
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
