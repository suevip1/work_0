package com.dtstack.engine.api.vo.job;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/10/26 2:36 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParentNodesVO {

    /**
     * 周期实例 jobkey
     */
    private String jobKey;

    /**
     * 周期实例 jobId
     */
    private String jobId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 上游实例（包括自依赖任务）
     */
    private List<ParentJobVO> parentJobVOS;

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParentJobVO> getParentJobVOS() {
        return parentJobVOS;
    }

    public void setParentJobVOS(List<ParentJobVO> parentJobVOS) {
        this.parentJobVOS = parentJobVOS;
    }
}
