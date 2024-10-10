CREATE TABLE if not exists `schedule_job_rely_check` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(48) NOT NULL COMMENT '工作任务id',
  `direct_parent_job_id` varchar(48) NOT NULL COMMENT '直接的父实例实例',
  `parent_job_id` varchar(48) NOT NULL COMMENT '父实例影响实例',
  `parent_job_check_status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '校验状态',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_job_id` (`job_id`,`is_deleted`),
  KEY `index_direct_parent_job_id` (`direct_parent_job_id`,`is_deleted`),
  KEY `index_parent_job_id` (`parent_job_id`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='提交校验表';

