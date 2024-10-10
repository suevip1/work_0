ALTER TABLE `schedule_job_expand`
ADD COLUMN `run_sub_task_id` longtext NULL COMMENT '可以运行的下游任务';