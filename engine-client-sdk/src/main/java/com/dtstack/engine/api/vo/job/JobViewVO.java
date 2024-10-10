package com.dtstack.engine.api.vo.job;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 1:33 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobViewVO {

    private List<JobViewElementVO> jobViewElementVOS;

    private List<JobViewSideVO> jobViewSideVOS;

    public List<JobViewElementVO> getJobViewElementVOS() {
        return jobViewElementVOS;
    }

    public void setJobViewElementVOS(List<JobViewElementVO> jobViewElementVOS) {
        this.jobViewElementVOS = jobViewElementVOS;
    }

    public List<JobViewSideVO> getJobViewSideVOS() {
        return jobViewSideVOS;
    }

    public void setJobViewSideVOS(List<JobViewSideVO> jobViewSideVOS) {
        this.jobViewSideVOS = jobViewSideVOS;
    }
}
