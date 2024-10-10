package com.dtstack.engine.dto;

/**
 * @author zhaozhenzhi
 * @version 1.0
 * @ClassName JobTimeDTO
 * @date 2022/7/21 3:28 PM
 */
public class JobTimeDTO {

    /**
     * 查询实例计划时间的最小时间：
     * 格式：yyyyMMddHHmmss
     */
    private String cycTimeStartTime;

    /**
     * 查询实例计划时间的最大时间：
     * 格式：yyyyMMddHHmmss
     */
    private String cycTimeEndTime;

    private String cycTimeBussStartTime;

    private String cycTimeBussEndTime;


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

    public String getCycTimeBussStartTime() {
        return cycTimeBussStartTime;
    }

    public void setCycTimeBussStartTime(String cycTimeBussStartTime) {
        this.cycTimeBussStartTime = cycTimeBussStartTime;
    }

    public String getCycTimeBussEndTime() {
        return cycTimeBussEndTime;
    }

    public void setCycTimeBussEndTime(String cycTimeBussEndTime) {
        this.cycTimeBussEndTime = cycTimeBussEndTime;
    }
}
