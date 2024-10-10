package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 检查点详细信息
 *
 * @author ：wangchuan
 * date：Created in 14:31 2022/11/16
 * company: www.dtstack.com
 */
@ApiModel
public class CheckpointDetail {

    /**
     * checkpoint id
     */
    @ApiModelProperty(notes = "checkpoint id")
    private String checkpointId;

    /**
     * 任务实例 id
     */
    @ApiModelProperty(notes = "任务实例 id")
    private String jobId;

    /**
     * 执行引擎任务 id
     */
    @ApiModelProperty(notes = "执行引擎任务 id")
    private String engineJobId;

    /**
     * 调度引擎任务 id
     */
    @ApiModelProperty(notes = "调度引擎任务 id")
    private String applicationId;

    /**
     * checkpoint 触发时间
     */
    @ApiModelProperty(notes = "checkpoint 触发时间")
    private Long triggerTime;

    /**
     * checkpoint 存储路径
     */
    @ApiModelProperty(notes = "checkpoint 存储路径")
    private String savePath;

    /**
     * checkpoint 存储大小, 单位 byte
     */
    @ApiModelProperty(notes = "checkpoint 存储大小, 单位 byte")
    private Long storageSize = 0L;

    /**
     * checkpoint 持续时间, 单位 ms
     */
    @ApiModelProperty(notes = "checkpoint 持续时间, 单位 ms")
    private Long duration = 0L;

    /**
     * checkpoint 类型
     * 参考 {@link com.dtstack.engine.api.enums.CheckpointType}
     */
    @ApiModelProperty(notes = "checkpoint 类型")
    private Integer checkpointType;

    public String getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(String checkpointId) {
        this.checkpointId = checkpointId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getCheckpointType() {
        return checkpointType;
    }

    public void setCheckpointType(Integer checkpointType) {
        this.checkpointType = checkpointType;
    }
}
