CREATE TABLE `console_kerberos_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT '引用类型',
  `task_type_list` text COMMENT '任务类型',
  `name` varchar(127) NOT NULL COMMENT 'kerberos文件名称',
  `keytab_name` varchar(127) NOT NULL COMMENT 'kerberos文件名称',
  `conf_name` varchar(127) NOT NULL COMMENT 'kerberos文件名称',
  `remote_path` varchar(255) NOT NULL COMMENT 'sftp存储路径',
  `principal` varchar(255) NOT NULL COMMENT 'principal',
  `open_kerberos` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启项目 kerberos认证，0 开始，1 关闭',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_project_id` (`project_id`,`app_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8