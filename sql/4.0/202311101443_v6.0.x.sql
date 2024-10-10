update schedule_job_history set exec_end_time = exec_start_time where exec_end_time is null;
alter table schedule_job_history
    add version_name varchar(32) null comment '任务版本 1.12 / 1.16';

