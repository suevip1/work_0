package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/10 11:04 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskPageVO {

    private Long id;

    private String name;

    private Long ownerUserId;

    private String replyTime;

    private Integer earlyWarnMargin;

    private Integer openStatus;

    /**
     * 1 单批次 2 多批次
     */
    private Integer batchType;

    /**
     * 优先级
     */
    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public Integer getBatchType() {
        return batchType;
    }

    public void setBatchType(Integer batchType) {
        this.batchType = batchType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
