package com.dtstack.engine.master.dto;

import com.dtstack.engine.api.vo.ReversalGrantCheckResultVO;

import java.util.List;

/**
 * @see ReversalGrantCheckResultVO
 */
public class LabelReversalGrantCheckResultDTO {

    //当前项目授权状态，1 不能删除 2 允许删除但要交接 3 允许删除同时不要交接
    private Integer state;
    //可以交接的资源组
    private List<LabelResourceGroupDTO> remainAccessed;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<LabelResourceGroupDTO> getRemainAccessed() {
        return remainAccessed;
    }

    public void setRemainAccessed(List<LabelResourceGroupDTO> remainAccessed) {
        this.remainAccessed = remainAccessed;
    }
}
