CREATE TABLE `schedule_task_black` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT '引用类型',
  `uic_tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `task_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除，0未删除 1删除',
  PRIMARY KEY (`id`),
  KEY `index_uic_tenant_id_and_app_type` (`uic_tenant_id`,`app_type`,`project_id`,`task_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='任务白名单'