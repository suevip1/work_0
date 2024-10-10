-- 项目级账号绑定, ref:http://zenpms.dtstack.cn/zentao/task-view-11753.html
CREATE TABLE `console_project_account` (
   `id` bigint(11) NOT NULL AUTO_INCREMENT,
   `project_id` bigint(11) NOT NULL COMMENT '项目id',
   `app_type` int(11) NOT NULL DEFAULT '0' COMMENT '应用类型',
   `user_name` varchar(255) NOT NULL COMMENT '用户名',
   `password` varchar(255) NOT NULL COMMENT '密码',
   `component_type_code` tinyint(4) NOT NULL COMMENT '组件类型',
   `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否启动，0否 1是',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
   `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
   PRIMARY KEY (`id`),
   KEY `idx_project_app_component` (`project_id`,`app_type`,`component_type_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='项目级绑定账号设置';