ALTER TABLE schedule_engine_job_cache
    ADD COLUMN `task_name`  varchar(255) NULL COMMENT '应用任务名称';