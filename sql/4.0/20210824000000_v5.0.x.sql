CREATE TABLE `schedule_job_expand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `retry_task_params` mediumtext COMMENT '重试任务参数',
  `job_graph` mediumtext COMMENT 'jobGraph构建json',
  `job_extra_info` mediumtext COMMENT '任务提交额外信息',
  `engine_log` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `log_info` longtext COMMENT '错误信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_id` (`job_id`(128))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO schedule_job_expand ( `job_id`, `retry_task_params`, `job_graph`, `job_extra_info`, `engine_log`, `log_info` ) SELECT
`job_id`,
`retry_task_params`,
`job_graph`,
`job_extra_info`,
`engine_log`,
`log_info`
FROM
	schedule_job WHERE is_deleted = 0;

ALTER TABLE `schedule_job` drop COLUMN `retry_task_params`;
ALTER TABLE `schedule_job` drop COLUMN `job_graph`;
ALTER TABLE `schedule_job` drop COLUMN `job_extra_info`;
ALTER TABLE `schedule_job` drop COLUMN `engine_log`;
ALTER TABLE `schedule_job` drop COLUMN `log_info`;


