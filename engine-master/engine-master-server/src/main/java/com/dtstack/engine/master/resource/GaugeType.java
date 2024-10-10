package com.dtstack.engine.master.resource;

public enum GaugeType {

    /**
     * 内存使用信息
     */
    JOB_RESOURCE_MEMORY("dagschedulex_job_resource_memory"),

    /**
     * cpu 使用信息
     */
    JOB_RESOURCE_CORE("dagschedulex_job_resource_core"),

    /**
     * 作业进度百分比
     */
    JOB_PROGRESS_REAL_PERCENTAGE("dagschedulex_job_progress_real_percentage");

    private final String gaugeName;

    GaugeType(String gaugeName) {
        this.gaugeName = gaugeName;
    }

    public String getGaugeName() {
        return gaugeName;
    }
}