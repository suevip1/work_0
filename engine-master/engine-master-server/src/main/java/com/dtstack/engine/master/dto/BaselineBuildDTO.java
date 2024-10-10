package com.dtstack.engine.master.dto;

import com.dtstack.engine.po.BaselineJobJob;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/10 11:25 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineBuildDTO {

    private String cycTime;

    private List<BaselineJobJob> baselineJobJobs;

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public List<BaselineJobJob> getBaselineJobJobs() {
        return baselineJobJobs;
    }

    public void setBaselineJobJobs(List<BaselineJobJob> baselineJobJobs) {
        this.baselineJobJobs = baselineJobJobs;
    }
}
