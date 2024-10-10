package com.dtstack.engine.api.vo.job;

/**
 * @Auther: dazhi
 * @Date: 2021/10/26 2:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ParentJobVO {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 实例状态
     */
    private Integer status;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 周期实例key
     */
    private String jobKey;

    /**
     * 周期实例 jobId
     */
    private String jobId;

    /**
     * 依赖类型 0 正常依赖，1 已经去依赖
     */
    private Integer relyType;

    /**
     * 应用类型
     */
    private Integer appType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

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

    public Integer getRelyType() {
        return relyType;
    }

    public void setRelyType(Integer relyType) {
        this.relyType = relyType;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
