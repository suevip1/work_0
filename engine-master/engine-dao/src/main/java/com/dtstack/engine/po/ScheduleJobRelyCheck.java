package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:26 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobRelyCheck {

    private Long id;

    private String jobId;

    private String directParentJobId;

    private String parentJobId;

    private Integer parentJobCheckStatus;

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

    public String getDirectParentJobId() {
        return directParentJobId;
    }

    public void setDirectParentJobId(String directParentJobId) {
        this.directParentJobId = directParentJobId;
    }

    public String getParentJobId() {
        return parentJobId;
    }

    public void setParentJobId(String parentJobId) {
        this.parentJobId = parentJobId;
    }

    public Integer getParentJobCheckStatus() {
        return parentJobCheckStatus;
    }

    public void setParentJobCheckStatus(Integer parentJobCheckStatus) {
        this.parentJobCheckStatus = parentJobCheckStatus;
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
