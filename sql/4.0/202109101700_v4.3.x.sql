CREATE TABLE `schedule_job_history`
(
    `id`              INT(11)     NOT NULL AUTO_INCREMENT,
    `app_type`        INT(11)     NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `job_id`          VARCHAR(32) NOT NULL COMMENT '工作任务id',
    `exec_start_time` datetime             DEFAULT NULL COMMENT '执行开始时间',
    `exec_end_time`   datetime             DEFAULT NULL COMMENT '执行结束时间',
    `engine_job_id`   VARCHAR(256)         DEFAULT NULL COMMENT '离线任务计算引擎id',
    `application_id`  VARCHAR(256)         DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id',
    `gmt_create`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      TINYINT(1)  NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `index_job_id` (`job_id` (32), `is_deleted`),
    KEY `index_engine_job_id` (
                               `engine_job_id` (128))
) ENGINE = INNODB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8;

ALTER TABLE `schedule_engine_job_checkpoint`
    ADD COLUMN `checkpoint_size` int(11) NULL COMMENT 'checkpoint文件大小' AFTER `checkpoint_counts`;

ALTER TABLE `schedule_engine_job_checkpoint`
    ADD COLUMN `checkpoint_duration` int(11) NULL COMMENT 'checkpoint持续时间' AFTER `checkpoint_counts`;