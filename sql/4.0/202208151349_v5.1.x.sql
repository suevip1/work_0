CREATE TABLE `schedule_task_ref_shade` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `tenant_id` int(11) NOT NULL COMMENT '租户id',
   `project_id` int(11) NOT NULL COMMENT '项目id',
   `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7)',
   `ref_app_type` int(11) DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7)',
   `task_id` int(11) NOT NULL COMMENT '任务id',
   `ref_task_id` int(11) DEFAULT NULL COMMENT '对应任务引用的id',
   `task_key` varchar(128) NOT NULL DEFAULT '' COMMENT '任务的标识',
   `ref_task_key` varchar(128) DEFAULT NULL COMMENT '引用任务的标识',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `index_batch_ref_task` (`task_id`,`ref_task_id`,`app_type`,`project_id`) USING BTREE,
   KEY `index_task_key` (`task_key`) USING BTREE,
   KEY `index_ref_task_key` (`ref_task_key`) USING BTREE,
   KEY `index_ref_task_id` (`ref_task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `schedule_job_resource_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT 'jobId',
  `type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '0正常调度 1补数据 2临时运行',
  `job_resource_files` varchar(256) DEFAULT NULL COMMENT 'job 资源文件信息',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_job_id` (`job_id`(128),`is_deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
