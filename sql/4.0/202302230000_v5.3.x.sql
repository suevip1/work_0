CREATE TABLE `schedule_task_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `tag_id` int(11) NOT NULL COMMENT '标签id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
   KEY `index_tag_id` (`tag_id`,`app_type`),
   KEY `index_task_id` (`task_id`,`app_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务标签表';