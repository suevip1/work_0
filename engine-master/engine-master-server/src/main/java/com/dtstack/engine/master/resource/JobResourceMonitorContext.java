package com.dtstack.engine.master.resource;

import com.dtstack.engine.common.JobClient;

/**
 * job resource monitor dto
 *
 * @author ：wangchuan
 * date：Created in 16:22 2023/1/28
 * company: www.dtstack.com
 */
public class JobResourceMonitorContext {

    private final JobClient jobClient;

    private final String host;

    private final PushGatewayInfo pushGatewayInfo;

    private final Integer runNum;

    private int coreNum;
    private int memNum;

    public JobResourceMonitorContext(JobClient jobClient, String host, PushGatewayInfo pushGatewayInfo, Integer runNum) {
        this.jobClient = jobClient;
        this.host = host;
        this.pushGatewayInfo = pushGatewayInfo;
        this.runNum = runNum;
    }

    public Integer getRunNum() {
        return runNum;
    }

    public int getCoreNum() {
        return coreNum;
    }

    public int getMemNum() {
        return memNum;
    }

    public void refreshCoreAndMem(int coreNum, int memNum) {
        this.coreNum = Math.max(coreNum, this.coreNum);
        this.memNum = Math.max(memNum, this.memNum);
    }

    public JobClient getJobClient() {
        return jobClient;
    }

    public void addMetric(GaugeType gaugeType, double val) {
        // 资源使用情况需要区分运行次数
        String jobIdWithRunNum = String.format("%s-%s", jobClient.getTaskId(), runNum);
        switch (gaugeType) {
            case JOB_RESOURCE_CORE:
                this.pushGatewayInfo.getCoreGauge().labels(host, jobIdWithRunNum).set(val);
                break;
            case JOB_RESOURCE_MEMORY:
                this.pushGatewayInfo.getMemoryGauge().labels(host, jobIdWithRunNum).set(val);
                break;
            case JOB_PROGRESS_REAL_PERCENTAGE:
                this.pushGatewayInfo.getProgressGauge().labels(host, jobClient.getTaskId()).set(val);
                break;
        }
    }
}
