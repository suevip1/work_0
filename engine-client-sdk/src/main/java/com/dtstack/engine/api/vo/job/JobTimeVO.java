package com.dtstack.engine.api.vo.job;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName JobTimeVO
 * @date 2022/7/19 2:12 PM
 */
public class JobTimeVO {

    /**
     * 查询实例计划时间的最小时间：
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private String cycTimeStartTime;

    /**
     * 查询实例计划时间的最大时间：
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private String cycTimeEndTime;

    /**
     * 查询实例计划时间的最小时间：
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private String businessStartTime;

    /**
     * 查询实例计划时间的最大时间：
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private String businessEndTime;

    public String getCycTimeStartTime() {
        return cycTimeStartTime;
    }

    public void setCycTimeStartTime(String cycTimeStartTime) {
        this.cycTimeStartTime = cycTimeStartTime;
    }

    public String getCycTimeEndTime() {
        return cycTimeEndTime;
    }

    public void setCycTimeEndTime(String cycTimeEndTime) {
        this.cycTimeEndTime = cycTimeEndTime;
    }

    public String getBusinessStartTime() {
        return businessStartTime;
    }

    public void setBusinessStartTime(String businessStartTime) {
        this.businessStartTime = businessStartTime;
    }

    public String getBusinessEndTime() {
        return businessEndTime;
    }

    public void setBusinessEndTime(String businessEndTime) {
        this.businessEndTime = businessEndTime;
    }
}
