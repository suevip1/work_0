package com.dtstack.engine.api.param;

import java.io.Serializable;

public class ScheduleJobAuthParam implements Serializable {
    private static final long serialVersionUID = -5381028763501349402L;

    private String jobId;

    /**
     * 运行次数
     */
    private Integer num;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduleJobAuthParam{");
        sb.append("jobId='").append(jobId).append('\'');
        sb.append(", num=").append(num);
        sb.append('}');
        return sb.toString();
    }
}