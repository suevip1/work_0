package com.dtstack.engine.api.vo.action;

import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 1:59 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActionRetryLogVO {

    /**
     * 重试次数
     */
    private Integer retryNum;
    /**
     * 调度日志
     */
    private String logInfo;
    /**
     * 运行参数
     */
    private String retryTaskParams;
    /**
     * 引擎日志
     */
    private String engineLog;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private String execStartTime;

    /**
     * 结束时间
     */
    private String execEndTime;

    private String cycTime;

    private String applicationId;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(String execStartTime) {
        this.execStartTime = execStartTime;
    }

    public String getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(String execEndTime) {
        this.execEndTime = execEndTime;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }
}
