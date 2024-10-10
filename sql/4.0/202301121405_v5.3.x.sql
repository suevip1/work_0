update alert_rule
set template = replace(template, '周期调度', '${jobScheduleType!}')
where `key` in ('error_status', 'finish_status', 'stop_status', 'timing_no_finish', 'time_out_no_finish'
    );