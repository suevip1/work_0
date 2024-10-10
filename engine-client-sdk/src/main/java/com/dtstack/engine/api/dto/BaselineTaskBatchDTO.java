package com.dtstack.engine.api.dto;

/**
 * @Auther: dazhi
 * @Date: 2023/2/2 3:10 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BaselineTaskBatchDTO {

    private Long baselineTaskId;

    private String replyTime;

    private String cycTime;

    private Integer openStatus;


    public Long getBaselineTaskId() {
        return baselineTaskId;
    }

    public void setBaselineTaskId(Long baselineTaskId) {
        this.baselineTaskId = baselineTaskId;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
