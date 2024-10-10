package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;

import java.util.List;

public class ScheduleTaskParamReplaceVO {

    /**
     * 参数替换格式
     */
    private List<ScheduleTaskParamShade> taskParamShades;

    /**
     * 需要替换的文本
     */
    private String replaceText;

    /**
     * 替换时间
     */
    private String cycTime;

    /**
     * 当前需要替换的任务
     */
    private ScheduleTaskShade taskShade;

    /**
     * 0:正常调度
     * 2:临时运行
     */
    private Integer scheduleType;

    private String jobId;

    public List<ScheduleTaskParamShade> getTaskParamShades() {
        return taskParamShades;
    }

    public void setTaskParamShades(List<ScheduleTaskParamShade> taskParamShades) {
        this.taskParamShades = taskParamShades;
    }

    public String getReplaceText() {
        return replaceText;
    }

    public void setReplaceText(String replaceText) {
        this.replaceText = replaceText;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public ScheduleTaskShade getTaskShade() {
        return taskShade;
    }

    public void setTaskShade(ScheduleTaskShade taskShade) {
        this.taskShade = taskShade;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}