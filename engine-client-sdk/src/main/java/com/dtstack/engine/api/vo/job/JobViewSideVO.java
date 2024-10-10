package com.dtstack.engine.api.vo.job;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 10:31 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class JobViewSideVO {

    private String jobKey;

    private String parentJobKey;

    /**
     * NORMAL(0, "正常依赖"),TO_RELY_ON(1,"去依赖")
     */
    private Integer relyType;

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getParentJobKey() {
        return parentJobKey;
    }

    public void setParentJobKey(String parentJobKey) {
        this.parentJobKey = parentJobKey;
    }

    public Integer getRelyType() {
        return relyType;
    }

    public void setRelyType(Integer relyType) {
        this.relyType = relyType;
    }
}
