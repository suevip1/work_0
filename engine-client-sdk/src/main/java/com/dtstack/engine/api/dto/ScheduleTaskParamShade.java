package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.AppTenantEntity;
import io.swagger.annotations.ApiModel;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleTaskParamShade extends AppTenantEntity {

    private Long taskId;

    /**
     * 任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL
     */
    private Integer taskType;

    /**
     * 0系统参数, 1自定义参数, 2组件参数, 5输入参数, 6输出参数
     */
    private Integer type;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 内容
     */
    private String paramCommand;

    /**
     * 输出参数类型,1自定义参数,2常量,3计算结果
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
     * 使用偏移量
     * 离线支持基于全局参数的偏移
     * 使用偏移量的情况下，com.dtstack.engine.api.dto.ScheduleTaskParamShade#paramName 为任务脚本中实际要替换的值
     * com.dtstack.engine.api.dto.ScheduleTaskParamShade#paramCommand 以 # 号隔开，# 号前，对应控制台全局参数名称，# 后为具体偏移量 +1/-1
     * offset: @code com.dtstack.engine.api.domain.ScheduleTaskParam#offset
     * paramName: @code com.dtstack.engine.api.domain.ScheduleTaskParam#replaceTarget
     */
    private Boolean useOffset;

    private String replaceTarget;

    private String offset;

    /**
     * 工作流 id
     */
    private Long flowId;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamCommand() {
        return paramCommand;
    }

    public void setParamCommand(String paramCommand) {
        this.paramCommand = paramCommand;
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

    public String getFatherParamName() {
        return fatherParamName;
    }

    public void setFatherParamName(String fatherParamName) {
        this.fatherParamName = fatherParamName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getFatherAppType() {
        return fatherAppType;
    }

    public void setFatherAppType(Integer fatherAppType) {
        this.fatherAppType = fatherAppType;
    }

    public Boolean getUseOffset() {
        return useOffset;
    }

    public void setUseOffset(Boolean useOffset) {
        this.useOffset = useOffset;
    }

    public String getReplaceTarget() {
        return replaceTarget;
    }

    public void setReplaceTarget(String replaceTarget) {
        this.replaceTarget = replaceTarget;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}
