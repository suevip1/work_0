package com.dtstack.engine.api.vo.project;


public class ProjectStatisticsInfoVO {


    /**
     * 告警总数
     */
    private int alarmCount;

    /**
     * 实例总数
     */
    private int totalJob;

    /**
     * 失败总数
     */
    private int failJob;

    /**
     * 失败总数
     */
    private int runJob;

    /**
     * 未运行
     */
    private int unSubmitJob;

    /**
     * 告警趋势
     */
    private String alarmTrend;

    /**
     * 实例总数趋势
     */
    private String totalJobTrend;

    /**
     * 失败总数趋势
     */
    private String failJobTrend;

    /**
     * 运行总数趋势
     */
    private String runJobTrend;

    /**
     * 未运行趋势
     */
    private String unSubmitJobTrend;


    public String getAlarmTrend() {
        return alarmTrend;
    }

    public void setAlarmTrend(String alarmTrend) {
        this.alarmTrend = alarmTrend;
    }

    public String getTotalJobTrend() {
        return totalJobTrend;
    }

    public void setTotalJobTrend(String totalJobTrend) {
        this.totalJobTrend = totalJobTrend;
    }

    public String getFailJobTrend() {
        return failJobTrend;
    }

    public void setFailJobTrend(String failJobTrend) {
        this.failJobTrend = failJobTrend;
    }

    public String getRunJobTrend() {
        return runJobTrend;
    }

    public void setRunJobTrend(String runJobTrend) {
        this.runJobTrend = runJobTrend;
    }

    public String getUnSubmitJobTrend() {
        return unSubmitJobTrend;
    }

    public void setUnSubmitJobTrend(String unSubmitJobTrend) {
        this.unSubmitJobTrend = unSubmitJobTrend;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public int getTotalJob() {
        return totalJob;
    }

    public void setTotalJob(int totalJob) {
        this.totalJob = totalJob;
    }

    public int getFailJob() {
        return failJob;
    }

    public void setFailJob(int failJob) {
        this.failJob = failJob;
    }

    public int getRunJob() {
        return runJob;
    }

    public void setRunJob(int runJob) {
        this.runJob = runJob;
    }

    public int getUnSubmitJob() {
        return unSubmitJob;
    }

    public void setUnSubmitJob(int unSubmitJob) {
        this.unSubmitJob = unSubmitJob;
    }
}
