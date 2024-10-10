package com.dtstack.engine.api.open.api;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName EventJobInfoResponse
 * @date 2022/8/5 9:56 AM
 */
public class EventJobInfoResponse {

    private String jobId;

    private Integer status;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
