package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/17 3:49 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineJobPageVO {

    private Long id;

    /**
     * 业务日期： yyyy-mm-dd
     */
    private String businessTime;

    /**
     * 基线任务id
     */
    private Long baselineTaskId;

    /**
     * 基线名称
     */
    private String name;

    /**
     * 责任人
     */
    private Long ownerUserId;

    /**
     * 基线状态: 破线,安全,预警,其他,定时未完成
     */
    private Integer baselineStatus;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 完成状态:
     */
    private Integer finishStatus;

    /**
     * 完成时间
     */
    private String finishTime;

    /**
     * 基线时间: 承诺时间
     */
    private String replyTime;

    /**
     * 基线时间: 预警余量
     */
    private Integer earlyWarnMargin;

    /**
     * 当前余量
     */
    private Integer currentMargin;

    /**
     * 延迟时间
     */
    private Integer delayTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessTime() {
        return businessTime;
    }

    public void setBusinessTime(String businessTime) {
        this.businessTime = businessTime;
    }

    public Long getBaselineTaskId() {
        return baselineTaskId;
    }

    public void setBaselineTaskId(Long baselineTaskId) {
        this.baselineTaskId = baselineTaskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    public Integer getBaselineStatus() {
        return baselineStatus;
    }

    public void setBaselineStatus(Integer baselineStatus) {
        this.baselineStatus = baselineStatus;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(Integer finishStatus) {
        this.finishStatus = finishStatus;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getEarlyWarnMargin() {
        return earlyWarnMargin;
    }

    public void setEarlyWarnMargin(Integer earlyWarnMargin) {
        this.earlyWarnMargin = earlyWarnMargin;
    }

    public Integer getCurrentMargin() {
        return currentMargin;
    }

    public void setCurrentMargin(Integer currentMargin) {
        this.currentMargin = currentMargin;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }
}
