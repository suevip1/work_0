package com.dtstack.engine.master.scheduler.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2023/2/27 10:50 AM
 * @Email: dazhi@dtstack.com
 * @Description: 实例状态上下文对象
 */
public class JobStatusSummitCheckContext {

    private Integer status;

    private ScheduleTaskShade scheduleTaskShade;

    private ScheduleBatchJob scheduleBatchJob;

    private JobCheckRunInfo jobCheckRunInfo;
/**/
    private List<JobCheckRunInfo> blockStatusJobCheckRunInfoList;

    /**
     * 其他信息
     */
    private JSONObject extraInfo;

    public static final String isSelectWaitReason = "isSelectWaitReason";

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ScheduleTaskShade getScheduleTaskShade() {
        return scheduleTaskShade;
    }

    public void setScheduleTaskShade(ScheduleTaskShade scheduleTaskShade) {
        this.scheduleTaskShade = scheduleTaskShade;
    }

    public ScheduleBatchJob getScheduleBatchJob() {
        return scheduleBatchJob;
    }

    public void setScheduleBatchJob(ScheduleBatchJob scheduleBatchJob) {
        this.scheduleBatchJob = scheduleBatchJob;
    }

    public JobCheckRunInfo getJobCheckRunInfo() {
        return jobCheckRunInfo;
    }

    public void setJobCheckRunInfo(JobCheckRunInfo jobCheckRunInfo) {
        this.jobCheckRunInfo = jobCheckRunInfo;
    }

    public List<JobCheckRunInfo> getBlockStatusJobCheckRunInfoList() {
        return blockStatusJobCheckRunInfoList;
    }

    public void setBlockStatusJobCheckRunInfoList(List<JobCheckRunInfo> blockStatusJobCheckRunInfoList) {
        this.blockStatusJobCheckRunInfoList = blockStatusJobCheckRunInfoList;
    }

    public JSONObject getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(JSONObject extraInfo) {
        this.extraInfo = extraInfo;
    }
}
