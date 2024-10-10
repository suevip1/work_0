package com.dtstack.engine.po;


import com.dtstack.engine.api.annotation.Unique;
import com.dtstack.engine.api.domain.AppTenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleFillDataJob extends AppTenantEntity {

    /**
     * 补数据名称
     */
    @Unique
    private String jobName;

    /**
     * 生成时间
     */
    private String runDay;

    /**
     * 补数据开始运行时间
     */
    private String fromDay;

    /**
     * 补数据结束时间
     */
    private String toDay;

    /**
     * 发起操作的用户
     */
    @ApiModelProperty(notes = "发起操作的用户")
    private Long createUserId;

    /**
     * 最大并行数
     */
    private Integer maxParallelNum;

    /**
     * 补数据上下文
     */
    private String fillDataInfo;

    /**
     * 补数据状态
     */
    private Integer fillGeneratStatus;

    /**
     * 运行补数据的服务器地址
     */
    private String nodeAddress;

    /**
     * 当前并发运行实例数
     */
    private Integer numberParallelNum;

    private String fillDataFailure;

    private Long resourceId;

    /**
     * 补数据类型
     */
    private Integer fillDataType;

    /**
     * 任务运行顺序： 0 无 默认
     * 1. 按业务日期升序
     * 2. 按业务日期降序 暂不支持
     */
    private Integer taskRunOrder;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public Integer getMaxParallelNumAndIncrement() {
        return maxParallelNum;
    }

    public void setMaxParallelNum(Integer maxParallelNum) {
        this.maxParallelNum = maxParallelNum;
    }

    public String getFillDataInfo() {
        return fillDataInfo;
    }

    public void setFillDataInfo(String fillDataInfo) {
        this.fillDataInfo = fillDataInfo;
    }

    public Integer getFillGeneratStatus() {
        return fillGeneratStatus;
    }

    public void setFillGeneratStatus(Integer fillGeneratStatus) {
        this.fillGeneratStatus = fillGeneratStatus;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getNumberParallelNum() {
        return numberParallelNum;
    }

    public void setNumberParallelNum(Integer numberParallelNum) {
        this.numberParallelNum = numberParallelNum;
    }

    public Integer getMaxParallelNum() {
        return maxParallelNum;
    }

    public String getFillDataFailure() {
        return fillDataFailure;
    }

    public void setFillDataFailure(String fillDataFailure) {
        this.fillDataFailure = fillDataFailure;
    }

    public Integer getFillDataType() {
        return fillDataType;
    }

    public void setFillDataType(Integer fillDataType) {
        this.fillDataType = fillDataType;
    }

    public Integer getTaskRunOrder() {
        return taskRunOrder;
    }

    public void setTaskRunOrder(Integer taskRunOrder) {
        this.taskRunOrder = taskRunOrder;
    }
}
