package com.dtstack.engine.master.dto;

import com.dtstack.engine.common.enums.RdosTaskStatus;

/**
 * 任务上下游参数-任务实例状态判断结果
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-22 16:34
 */
public class JobChainParamStatusResult {
    private RdosTaskStatus rdosTaskStatus;
    private String log;

    public JobChainParamStatusResult() {
    }

    public JobChainParamStatusResult(RdosTaskStatus rdosTaskStatus, String log) {
        this.rdosTaskStatus = rdosTaskStatus;
        this.log = log;
    }

    public RdosTaskStatus getRdosTaskStatus() {
        return rdosTaskStatus;
    }

    public void setRdosTaskStatus(RdosTaskStatus rdosTaskStatus) {
        this.rdosTaskStatus = rdosTaskStatus;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "JobChainParamStatusResult{" +
                "rdosTaskStatus=" + rdosTaskStatus +
                ", log='" + log + '\'' +
                '}';
    }
}
