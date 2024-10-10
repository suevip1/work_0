package com.dtstack.engine.api.domain;


import com.dtstack.engine.api.annotation.Unique;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ScheduleTaskShade extends ScheduleTask {

    /**
     * 是否发布到了生产环境
     */
    @ApiModelProperty(notes = "是否发布到了生产环境")
    private Long isPublishToProduce;

    @ApiModelProperty(notes = "扩展信息")
    private String extraInfo;

    @Unique
    private Long taskId;

    /**
     * batchJob执行的时候的vesion版本
     */
    @ApiModelProperty(notes = "batchJob执行的时候的vesion版本")
    private Integer versionId;
    /**
     * 选择的运行组件版本
     * e.g Flink 110
     */
    private String componentVersion;

    public String getComponentVersion() {
        return componentVersion;
    }

    /**
     * job 生成方式, 1 为 T+1 生成，2 为立即生成当天实例，默认为 T+1 生成
     */
    private Integer jobBuildType;

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    private Integer taskRule;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getIsPublishToProduce() {
        return isPublishToProduce;
    }

    public void setIsPublishToProduce(Long isPublishToProduce) {
        this.isPublishToProduce = isPublishToProduce;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public Integer getJobBuildType() {
        return jobBuildType;
    }

    public void setJobBuildType(Integer jobBuildType) {
        this.jobBuildType = jobBuildType;
    }
}
