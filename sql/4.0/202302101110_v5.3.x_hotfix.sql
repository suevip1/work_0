ALTER TABLE `schedule_job_expand` ADD COLUMN `enable_job_monitor` TINYINT NOT NULL DEFAULT 0 COMMENT '是否开启资源监控';

ALTER TABLE `schedule_job_expand` ADD COLUMN `diagnosis_info` LONGTEXT COMMENT '任务诊断信息';