package com.dtstack.engine.api.vo.job;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName WorkflowJobViewRetrunVO
 * @date 2022/7/19 3:09 PM
 */
public class WorkflowJobViewReturnVO {

    private String jobId;

    private JobViewVO jobViewVO;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobViewVO getJobViewVO() {
        return jobViewVO;
    }

    public void setJobViewVO(JobViewVO jobViewVO) {
        this.jobViewVO = jobViewVO;
    }
}
