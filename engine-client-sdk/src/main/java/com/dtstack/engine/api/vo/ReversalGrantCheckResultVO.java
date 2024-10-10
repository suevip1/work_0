package com.dtstack.engine.api.vo;

import java.util.List;

public class ReversalGrantCheckResultVO {

    //当前项目授权状态，1 不能删除 2 允许删除但要交接 3 允许删除同时不要交接
    private Integer state;
    //可以交接的资源组
    private List<ResourceGroupDropDownVO> remainAccessed;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<ResourceGroupDropDownVO> getRemainAccessed() {
        return remainAccessed;
    }

    public void setRemainAccessed(List<ResourceGroupDropDownVO> remainAccessed) {
        this.remainAccessed = remainAccessed;
    }

}
