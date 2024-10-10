package com.dtstack.engine.master.cron.constant;

/**
 * @author jiuwei@dtstack.com
 * 可配置的需要定时清理的表名
 **/
public interface DataLifeTable {
    String SCHEDULE_JOB = "schedule_job";
    String SCHEDULE_JOB_EXPAND = "schedule_job_expand";
    String SCHEDULE_FILL_DATA_JOB = "schedule_fill_data_job";
    String SCHEDULE_JOB_CHAIN_OUTPUT_PARAM = "schedule_job_chain_output_param";
    String SCHEDULE_JOB_GANTT_CHART = "schedule_job_gantt_chart";
    String SCHEDULE_JOB_JOB = "schedule_job_job";
    String SCHEDULE_PLUGIN_JOB_INFO = "schedule_plugin_job_info";

}
