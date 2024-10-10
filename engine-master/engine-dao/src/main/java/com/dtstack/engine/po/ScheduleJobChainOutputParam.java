package com.dtstack.engine.po;

import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-22 20:12
 */
public class ScheduleJobChainOutputParam {
    private Integer id;

    /**
     * 任务实例id
     */
    private String jobId;

    /**
     * 任务运行类型，0正常调度, 1补数据,2临时运行
     */
    private Integer jobType;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    private Integer appType;

    /**
     * 任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL
     */
    private Integer taskType;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数类型:1自定义参数,2常量,3计算结果
     */
    private Integer outputParamType;

    /**
     * 参数内容
     */
    private String paramCommand;

    /**
     * 替换变量后的参数内容
     */
    private String parsedParamCommand;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 新增时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId == null ? null : jobId.trim();
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName == null ? null : paramName.trim();
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
        this.paramCommand = paramCommand == null ? null : paramCommand.trim();
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue == null ? null : paramValue.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getParsedParamCommand() {
        return parsedParamCommand;
    }

    public void setParsedParamCommand(String parsedParamCommand) {
        this.parsedParamCommand = parsedParamCommand;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduleJobChainOutputParam{");
        sb.append("id=").append(id);
        sb.append(", jobId='").append(jobId).append('\'');
        sb.append(", jobType=").append(jobType);
        sb.append(", taskId=").append(taskId);
        sb.append(", appType=").append(appType);
        sb.append(", taskType=").append(taskType);
        sb.append(", paramName='").append(paramName).append('\'');
        sb.append(", outputParamType=").append(outputParamType);
        sb.append(", paramCommand='").append(paramCommand).append('\'');
        sb.append(", parsedParamCommand='").append(parsedParamCommand).append('\'');
        sb.append(", paramValue='").append(paramValue).append('\'');
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append('}');
        return sb.toString();
    }
}