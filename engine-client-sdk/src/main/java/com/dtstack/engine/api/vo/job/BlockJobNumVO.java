package com.dtstack.engine.api.vo.job;

/**
 * @Auther: dazhi
 * @Date: 2023/3/2 2:47 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BlockJobNumVO {

    /**
     * 上游数
     */
    private Integer upstreamNum;

    /**
     * 下游数
     */
    private Integer downstreamNum;

    public Integer getUpstreamNum() {
        return upstreamNum;
    }

    public void setUpstreamNum(Integer upstreamNum) {
        this.upstreamNum = upstreamNum;
    }

    public Integer getDownstreamNum() {
        return downstreamNum;
    }

    public void setDownstreamNum(Integer downstreamNum) {
        this.downstreamNum = downstreamNum;
    }
}
