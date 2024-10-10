package com.dtstack.engine.master.scheduler;


import com.dtstack.engine.common.enums.JobCheckStatus;
import org.apache.commons.lang3.StringUtils;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public class JobCheckRunInfo {

    private JobCheckStatus status;

    private String extInfo;

    /**
     * 实例运行异常信息上下文
     */
    private JobErrorContext jobErrorContext;

    public JobErrorContext getJobErrorContext() {
        return jobErrorContext;
    }

    public void setJobErrorContext(JobErrorContext jobErrorContext) {
        this.jobErrorContext = jobErrorContext;
    }

    public JobCheckStatus getStatus() {
        return status;
    }

    public void setStatus(JobCheckStatus status) {
        this.status = status;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public static JobCheckRunInfo createCheckInfo(JobCheckStatus status) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(status);
        jobCheckRunInfo.setExtInfo("");
        return jobCheckRunInfo;
    }

    public static JobCheckRunInfo createCheckInfo(JobCheckStatus status, String extInfo) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(status);
        jobCheckRunInfo.setExtInfo(extInfo);
        return jobCheckRunInfo;
    }

    public static JobCheckRunInfo createCheckInfo(JobCheckStatus status, String extInfo, JobErrorContext jobErrorContext) {
        JobCheckRunInfo jobCheckRunInfo = new JobCheckRunInfo();
        jobCheckRunInfo.setStatus(status);
        jobCheckRunInfo.setExtInfo(extInfo);
        jobCheckRunInfo.setJobErrorContext(jobErrorContext);
        return jobCheckRunInfo;
    }

    public String getErrMsg() {
        // 优先输出错误信息上下文，若没有该上下文，再打印粗略的错误信息
        if (jobErrorContext != null && StringUtils.isNotEmpty(jobErrorContext.toString())) {
            return jobErrorContext.toString();
        }
        extInfo = extInfo == null ? "" : extInfo;
        return status.getMsg() + extInfo;
    }
}