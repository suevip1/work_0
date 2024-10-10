package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/6/2 4:20 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AlertTriggerRecordRepeatSend {

    private Long id;

    private String jobId;

    private Long ruleId;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

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

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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
