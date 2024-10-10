package com.dtstack.engine.master.bo;


import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.po.ScheduleJobExpand;
import com.dtstack.engine.common.JobClient;
import org.apache.commons.lang3.StringUtils;

/**
 * @author toutian
 */
public class EngineJobRetry extends com.dtstack.engine.po.EngineJobRetry {

    public static EngineJobRetry toEntity(ScheduleJob batchJob, JobClient jobClient, ScheduleJobExpand expand,String engineLog) {
        EngineJobRetry batchJobRetry = new EngineJobRetry();
        batchJobRetry.setJobId(batchJob.getJobId());
        batchJobRetry.setExecStartTime(batchJob.getExecStartTime());
        batchJobRetry.setExecEndTime(batchJob.getExecEndTime());
        batchJobRetry.setRetryNum(batchJob.getRetryNum());
        batchJobRetry.setStatus(batchJob.getStatus());
        batchJobRetry.setGmtCreate(batchJob.getGmtCreate());
        batchJobRetry.setGmtModified(batchJob.getGmtModified());
        batchJobRetry.setRetryTaskParams(expand.getRetryTaskParams());
        batchJobRetry.setJobExpandId(expand.getId());
        if (StringUtils.isNotBlank(engineLog)) {
            batchJobRetry.setEngineLog(engineLog);
        } else {
            batchJobRetry.setEngineLog(expand.getEngineLog());
        }
        batchJobRetry.setRetryTaskParams(batchJob.getRetryTaskParams());

        if (batchJob.getApplicationId() == null) {
            batchJobRetry.setApplicationId(jobClient.getApplicationId());
        } else {
            batchJobRetry.setApplicationId(batchJob.getApplicationId());
        }
        if (batchJob.getEngineJobId() == null) {
            batchJobRetry.setEngineJobId(jobClient.getEngineTaskId());
        } else {
            batchJobRetry.setEngineJobId(batchJob.getEngineJobId());
        }
        try {
            if (StringUtils.isEmpty(expand.getLogInfo()) && jobClient.getJobResult() != null) {
                batchJobRetry.setLogInfo(jobClient.getJobResult().getMsgInfo());
            } else {
                batchJobRetry.setLogInfo(expand.getLogInfo());
            }
        } catch (Throwable e) {
            batchJobRetry.setLogInfo("commit job error，parses log error:" + e.getMessage());
        }
        return batchJobRetry;
    }

}
