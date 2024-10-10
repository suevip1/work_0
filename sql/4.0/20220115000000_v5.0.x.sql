CREATE TABLE `schedule_job_operate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `operate_type` varchar(256) NOT NULL DEFAULT '' COMMENT '操作类型',
  `operate_content` varchar(256) NOT NULL DEFAULT '' COMMENT '操作内容',
  `operate_id` int(11) NOT NULL DEFAULT '0' COMMENT '任务id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=387520709 DEFAULT CHARSET=utf8