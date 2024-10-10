package com.dtstack.engine.api.vo;

/**
 * @auther: shuxing
 * @date: 2022/3/15 10:23 周二
 * @email: shuxing@dtstack.com
 * @description:
 */
public class JobIdAndStatusVo {

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

    @Override
    public String toString() {
        return "JobIdAndStatusVo{" +
                "jobId='" + jobId + '\'' +
                ", status=" + status +
                '}';
    }

}
