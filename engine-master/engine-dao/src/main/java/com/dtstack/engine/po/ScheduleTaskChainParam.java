package com.dtstack.engine.po;

import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-03-22 00:32
 */
public class ScheduleTaskChainParam {
    private Integer id;

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
     * 参数种类，0系统参数, 1自定义参数, 2组件参数, 5输入参数, 6输出参数
     */
    private Integer type;

    /**
     * 输出参数类型,0:计算结果 1:常量 2:自定义参数
     */
    private Integer outputParamType;

    /**
     * 来源任务id
     */
    private Long fatherTaskId;

    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    private Integer fatherAppType;

    /**
     * 来源参数名称
     */
    private String fatherParamName;

    /**
     * 参数内容
     */
    private String paramCommand;

    /**
     * 新增时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 所属工作流 id
     */
    private Long flowId;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOutputParamType() {
        return outputParamType;
    }

    public void setOutputParamType(Integer outputParamType) {
        this.outputParamType = outputParamType;
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

    public String getFatherParamName() {
        return fatherParamName;
    }

    public void setFatherParamName(String fatherParamName) {
        this.fatherParamName = fatherParamName == null ? null : fatherParamName.trim();
    }

    public String getParamCommand() {
        return paramCommand;
    }

    public void setParamCommand(String paramCommand) {
        this.paramCommand = paramCommand == null ? null : paramCommand.trim();
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
}