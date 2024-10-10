package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/8 2:10 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class FindParentJobVO {

    private String jobId;

    private List<ParentJobStatusVO> parentJobVOS;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<ParentJobStatusVO> getParentJobVOS() {
        return parentJobVOS;
    }

    public void setParentJobVOS(List<ParentJobStatusVO> parentJobVOS) {
        this.parentJobVOS = parentJobVOS;
    }
}
