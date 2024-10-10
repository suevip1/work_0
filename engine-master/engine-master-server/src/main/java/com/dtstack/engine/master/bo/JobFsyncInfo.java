package com.dtstack.engine.master.bo;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ClientTypeEnum;

import java.sql.Timestamp;

/**
 * 执行结果落盘
 *
 * @author ：wangchuan
 * date：Created in 19:04 2023/2/9
 * company: www.dtstack.com
 */
public class JobFsyncInfo {

    private String jobId;
    private Integer scheduleType;
    private ClientTypeEnum clientType;
    private JobIdentifier jobIdentifier;
    private String fsyncSql;
    private String cycTime;
    private Timestamp gmtCreateTime;

    public JobFsyncInfo(String jobId, Integer scheduleType, ClientTypeEnum clientType, JobIdentifier jobIdentifier, String fsyncSql, String cycTime, Timestamp gmtCreateTime) {
        this.jobId = jobId;
        this.scheduleType = scheduleType;
        this.clientType = clientType;
        this.jobIdentifier = jobIdentifier;
        this.fsyncSql = fsyncSql;
        this.cycTime = cycTime;
        this.gmtCreateTime = gmtCreateTime;
    }

    public JobFsyncInfo(String jobId, Integer scheduleType, ClientTypeEnum clientType, JobIdentifier jobIdentifier, String fsyncSql) {
        this.jobId = jobId;
        this.scheduleType = scheduleType;
        this.clientType = clientType;
        this.jobIdentifier = jobIdentifier;
        this.fsyncSql = fsyncSql;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Timestamp getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(Timestamp gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    public JobFsyncInfo() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public ClientTypeEnum getClientType() {
        return clientType;
    }

    public void setClientType(ClientTypeEnum clientType) {
        this.clientType = clientType;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public String getFsyncSql() {
        return fsyncSql;
    }

    public void setFsyncSql(String fsyncSql) {
        this.fsyncSql = fsyncSql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobFsyncInfo)) {
            return false;
        }
        JobFsyncInfo rdbSqJob = (JobFsyncInfo) o;
        return com.google.common.base.Objects.equal(getJobId(), rdbSqJob.getJobId());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(getJobId());
    }
}
