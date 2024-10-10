package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;

/**
 * 任务输入参数变量配置
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-18 20:37
 */
public class ScheduleTaskChainParamDTO {
    /**
     * 输入参数名称
     */
    private String paramName;

    /**
     * 输入任务实例 -- 当前任务实例
     */
    private ScheduleJob sourceJob;

    /**
     * 目标任务id
     */
    private Long fatherTaskId;

    /**
     * 目标任务的应用类型
     */
    private Integer fatherAppType;

    /**
     * 目标任务类型
     */
    private Integer fatherTaskType;

    /**
     * 目标参数名称
     */
    private String fatherParamName;

    /**
     * 目标参数类型
     */
    private Integer outputParamType;

    /**
     * 目标参数内容
     */
    private String paramCommand;

    /**
     * 目标参数值
     */
    private String fatherParamValue;

    /**
     * 目标任务实例
     */
    private ScheduleJob fatherJob;

    /**
     * 目标任务
     */
    private ScheduleTaskShade fatherTaskShade;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Long getFatherTaskId() {
        return fatherTaskId;
    }

    public void setFatherTaskId(Long fatherTaskId) {
        this.fatherTaskId = fatherTaskId;
    }

    public Integer getFatherAppType() {
        return fatherAppType;
    }

    public void setFatherAppType(Integer fatherAppType) {
        this.fatherAppType = fatherAppType;
    }

    public Integer getFatherTaskType() {
        return fatherTaskType;
    }

    public void setFatherTaskType(Integer fatherTaskType) {
        this.fatherTaskType = fatherTaskType;
    }

    public String getFatherParamName() {
        return fatherParamName;
    }

    public void setFatherParamName(String fatherParamName) {
        this.fatherParamName = fatherParamName;
    }

    public Integer getOutputParamType() {
        return outputParamType;
    }

    public void setOutputParamType(Integer outputParamType) {
        this.outputParamType = outputParamType;
    }

    public String getParamCommand() {
        return paramCommand;
    }

    public void setParamCommand(String paramCommand) {
        this.paramCommand = paramCommand;
    }

    public ScheduleJob getSourceJob() {
        return sourceJob;
    }

    public void setSourceJob(ScheduleJob sourceJob) {
        this.sourceJob = sourceJob;
    }

    public ScheduleJob getFatherJob() {
        return fatherJob;
    }

    public void setFatherJob(ScheduleJob fatherJob) {
        this.fatherJob = fatherJob;
    }

    public ScheduleTaskShade getFatherTaskShade() {
        return fatherTaskShade;
    }

    public void setFatherTaskShade(ScheduleTaskShade fatherTaskShade) {
        this.fatherTaskShade = fatherTaskShade;
    }

    public String getFatherParamValue() {
        return fatherParamValue;
    }

    public void setFatherParamValue(String fatherParamValue) {
        this.fatherParamValue = fatherParamValue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduleTaskChainParamDTO{");
        sb.append("paramName='").append(paramName).append('\'');
        sb.append(", sourceJob=").append(sourceJob);
        sb.append(", fatherTaskId=").append(fatherTaskId);
        sb.append(", fatherAppType=").append(fatherAppType);
        sb.append(", fatherTaskType=").append(fatherTaskType);
        sb.append(", fatherParamName='").append(fatherParamName).append('\'');
        sb.append(", outputParamType=").append(outputParamType);
        sb.append(", paramCommand='").append(paramCommand).append('\'');
        sb.append(", fatherParamValue='").append(fatherParamValue).append('\'');
        sb.append(", fatherJob=").append(fatherJob);
        sb.append(", fatherTaskShade=").append(fatherTaskShade);
        sb.append('}');
        return sb.toString();
    }
}
