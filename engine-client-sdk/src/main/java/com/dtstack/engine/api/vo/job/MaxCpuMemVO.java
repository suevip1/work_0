package com.dtstack.engine.api.vo.job;

public class MaxCpuMemVO {

    private String date;

    /**当天消耗的最大cpu核数**/
    private Integer maxCoreNum;

    /**当天消耗的最大内存大小，单位m**/
    private Integer maxMemNum;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMaxCoreNum() {
        return maxCoreNum;
    }

    public void setMaxCoreNum(Integer maxCoreNum) {
        this.maxCoreNum = maxCoreNum;
    }

    public Integer getMaxMemNum() {
        return maxMemNum;
    }

    public void setMaxMemNum(Integer maxMemNum) {
        this.maxMemNum = maxMemNum;
    }
}
