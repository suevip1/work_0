package com.dtstack.engine.api.vo;

import java.util.List;

public class ResourceGroupUsedVO {
    private List<TimeUsedNodeVO> yesterday;
    private List<TimeUsedNodeVO> today;

    public List<TimeUsedNodeVO> getYesterday() {
        return yesterday;
    }

    public void setYesterday(List<TimeUsedNodeVO> yesterday) {
        this.yesterday = yesterday;
    }

    public List<TimeUsedNodeVO> getToday() {
        return today;
    }

    public void setToday(List<TimeUsedNodeVO> today) {
        this.today = today;
    }
}
