ALTER TABLE `schedule_task_shade`
    ADD COLUMN `job_build_type` tinyint(2) NOT NULL default 1 COMMENT '实例生成方式';

ALTER TABLE `schedule_job`
    ADD COLUMN `job_build_type` tinyint(2) NOT NULL default 1 COMMENT '实例生成方式';

CREATE TABLE `schedule_job_build_record`
(
    `id`               int(11) NOT NULL AUTO_INCREMENT,
    `job_build_type`   tinyint(2) NOT NULL default 2 COMMENT '实例生成方式, 1 为 T+1, 2 为立即生成',
    `job_build_status` tinyint(2) NOT NULL COMMENT '实例生成状态',
    `job_build_log`    longtext COMMENT '构建时的日志',
    `schedule_type`    tinyint(2) NOT NULL DEFAULT '0' COMMENT '0正常调度 1补数据 2临时运行 3重试',
    `task_id`          int(11) NOT NULL COMMENT '任务id',
    `app_type`         int(11) NOT NULL DEFAULT '0' COMMENT 'BATCH(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `gmt_create`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`       int(11) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY                `index_build_status` (`job_build_status`),
    KEY                `index_build_type` (`job_build_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务实例生成记录表';