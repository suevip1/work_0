package com.dtstack.engine.master.bo;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ClientTypeEnum;
import com.dtstack.engine.common.enums.EJobLogType;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class JobLogInfo implements Delayed {

    private String jobId;
    private JobIdentifier jobIdentifier;
    private String engineType;
    private int computeType;
    private long expired;
    private String customLog;
    private EJobLogType logType;
    private ClientTypeEnum clientType;

    public JobLogInfo(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, long delay, EJobLogType logType, ClientTypeEnum clientType) {
        this.jobId = jobId;
        this.jobIdentifier = jobIdentifier;
        this.engineType = engineType;
        this.computeType = computeType;
        this.expired = System.currentTimeMillis() + delay;
        this.logType = logType;
        this.clientType = clientType;
    }

    public JobLogInfo() {
    }

    public EJobLogType getLogType() {
        return logType;
    }

    public String getCustomLog() {
        return customLog;
    }

    public void setCustomLog(String customLog) {
        this.customLog = customLog;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public int getComputeType() {
        return computeType;
    }

    public void setComputeType(int computeType) {
        this.computeType = computeType;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    public ClientTypeEnum getClientType() {
        return clientType;
    }

    public void setClientType(ClientTypeEnum clientType) {
        this.clientType = clientType;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
