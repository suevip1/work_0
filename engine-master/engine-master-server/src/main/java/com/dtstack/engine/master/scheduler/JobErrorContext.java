package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-24 13:55
 * 实例运行异常信息上下文，主要用于日志展示等
 */
public class JobErrorContext {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final String COLON = ":";

    /**
     * 实例校验信息
     */
    private JobCheckStatus jobCheckStatus;

    /**
     * 任务资源异常信息
     */
    private String taskResourceErrMsg;

    /**
     * 任务冻结原因
     */
    private String frozenReason;

    /**
     * 导致当前任务无法运行的直接父任务
     */
    private ScheduleTaskShade parentTaskShade;

    /**
     * 导致当前任务无法运行的直接父任务名称
     */
    private String parentTaskName;

    /**
     * 导致当前任务无法运行的直接父实例
     */
    private ScheduleJob parentJob;

    /**
     * 其他附加信息
     */
    private String extraMsg;

    private JobErrorContext() {
    }

    public static JobErrorContext newInstance() {
        return new JobErrorContext();
    }

    public JobErrorContext jobCheckStatus(JobCheckStatus jobCheckStatus) {
        this.jobCheckStatus = jobCheckStatus;
        return this;
    }

    public JobErrorContext taskResourceErrMsg(String taskResourceErrMsg) {
        this.taskResourceErrMsg = taskResourceErrMsg;
        return this;
    }

    public JobErrorContext frozenReason(String frozenReason) {
        this.frozenReason = frozenReason;
        return this;
    }

    public JobErrorContext parentTaskShade(ScheduleTaskShade parentTaskShade) {
        this.parentTaskShade = parentTaskShade;
        return this;
    }

    public JobErrorContext parentJob(ScheduleJob parentJob) {
        this.parentJob = parentJob;
        return this;
    }

    public JobErrorContext parentTaskName(String parentTaskName) {
        this.parentTaskName = parentTaskName;
        return this;
    }

    public JobErrorContext extraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
        return this;
    }

    public String acquireParentTaskName() {
        if (parentTaskShade != null && StringUtils.isNotEmpty(parentTaskShade.getName())) {
            return parentTaskShade.getName();
        } else {
            return Objects.toString(this.parentTaskName, StringUtils.EMPTY);
        }
    }

    public ScheduleTaskShade getParentTaskShade() {
        return parentTaskShade;
    }

    public void setParentTaskShade(ScheduleTaskShade parentTaskShade) {
        this.parentTaskShade = parentTaskShade;
    }

    public String getParentTaskName() {
        return parentTaskName;
    }

    public void setParentTaskName(String parentTaskName) {
        this.parentTaskName = parentTaskName;
    }

    public ScheduleJob getParentJob() {
        return parentJob;
    }

    public void setParentJob(ScheduleJob parentJob) {
        this.parentJob = parentJob;
    }

    @Override
    public String toString() {
        StringBuilder description = new StringBuilder();

        if (jobCheckStatus == null) {
            return StringUtils.EMPTY;
        }

        // 可以执行则不输出异常日志
        if (jobCheckStatus == JobCheckStatus.CAN_EXE) {
            return StringUtils.EMPTY;
        }

        description.append(LINE_SEPARATOR);
        description.append("实例异常原因");
        description.append(COLON);
        description.append(jobCheckStatus.getMsg());

        if (StringUtils.isNotEmpty(taskResourceErrMsg)) {
            description.append(LINE_SEPARATOR);
            description.append("任务资源异常信息");
            description.append(COLON);
            description.append(taskResourceErrMsg);
        }

        if (StringUtils.isNotEmpty(frozenReason)) {
            description.append(LINE_SEPARATOR);
            description.append("任务冻结原因");
            description.append(COLON);
            description.append(frozenReason);
        }

        String parentTaskName = acquireParentTaskName();
        if (parentJob != null) {
            description.append(LINE_SEPARATOR);
            description.append("上游实例名称");
            description.append(COLON);
            description.append(String.format("%s", parentTaskName));

            description.append(LINE_SEPARATOR);
            long mill = DateUtil.getTimestamp(parentJob.getCycTime(), DateUtil.UN_STANDARD_DATETIME_FORMAT);
            String dateTime = DateUtil.getFormattedDate(mill, DateUtil.STANDARD_DATETIME_FORMAT);
            description.append("上游实例计划时间");
            description.append(COLON);
            description.append(dateTime);

            description.append(LINE_SEPARATOR);
            description.append("上游实例状态");
            description.append(COLON);
            description.append(RdosTaskStatus.getShowStatusDesc(parentJob.getStatus()));
        } else {
            // 父任务实例不存在，但存在父任务名称
            if (StringUtils.isNotEmpty(parentTaskName)) {
                description.append(LINE_SEPARATOR);
                description.append("上游任务名称");
                description.append(COLON);
                description.append(String.format("%s", parentTaskName));
            }
        }

        if (StringUtils.isNotEmpty(extraMsg)) {
            description.append(LINE_SEPARATOR);
            description.append(extraMsg);
        }

        return description.toString();
    }
}
