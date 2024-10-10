package com.dtstack.engine.api.vo;

/**
 * @Auther: dazhi
 * @Date: 2023/2/6 10:11 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineBatchVO {

    private String cycTime;

    private String estimatedFinishTime;

    private String replyTime;

    private Integer openStatus;

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public String getEstimatedFinishTime() {
        return estimatedFinishTime;
    }

    public void setEstimatedFinishTime(String estimatedFinishTime) {
        this.estimatedFinishTime = estimatedFinishTime;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }
}
