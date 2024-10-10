package com.dtstack.engine.api.param;

import javax.validation.constraints.NotNull;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2024-01-23 10:32
 */
public class RollingJobLogParam {

    @NotNull
    private String jobId;

    /**
     * 数据同步任务必传，1:"perjob"; 2:"session"
     */
    private Integer deployMode;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(Integer deployMode) {
        this.deployMode = deployMode;
    }
}