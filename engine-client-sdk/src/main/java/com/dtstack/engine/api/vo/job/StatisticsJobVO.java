package com.dtstack.engine.api.vo.job;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName StatisticsJobVO
 * @date 2022/7/19 2:54 PM
 */
public class StatisticsJobVO {

    private String statusEnum;

    private Integer statusCount;

    public String getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(String statusEnum) {
        this.statusEnum = statusEnum;
    }

    public Integer getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(Integer statusCount) {
        this.statusCount = statusCount;
    }
}
