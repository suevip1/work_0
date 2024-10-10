package com.dtstack.engine.api.vo.alert;

/**
 * @Auther: dazhi
 * @Date: 2022/5/18 11:09 AM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineViewVO {

    private String businessDate;

    private Long expectFinishTime;

    private Long finishTime;

    private Long replyTime;

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public Long getExpectFinishTime() {
        return expectFinishTime;
    }

    public void setExpectFinishTime(Long expectFinishTime) {
        this.expectFinishTime = expectFinishTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public Long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Long replyTime) {
        this.replyTime = replyTime;
    }
}
