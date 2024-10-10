package com.dtstack.engine.api.vo;

/**
 * @Auther: dazhi
 * @Date: 2021/12/8 1:56 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParentJobStatusVO {

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
