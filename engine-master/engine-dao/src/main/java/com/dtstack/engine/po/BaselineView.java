package com.dtstack.engine.po;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/5/18 1:47 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class BaselineView {

    private Long id;

    private Timestamp businessDate;

    private Timestamp expectFinishTime;

    private Timestamp finishTime;

    private String cycTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Timestamp businessDate) {
        this.businessDate = businessDate;
    }

    public Timestamp getExpectFinishTime() {
        return expectFinishTime;
    }

    public void setExpectFinishTime(Timestamp expectFinishTime) {
        this.expectFinishTime = expectFinishTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }
}
