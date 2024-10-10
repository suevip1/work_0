CREATE TABLE `schedule_job_graph` (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `app_type` int(11) NOT NULL COMMENT '应用类型',
                                      `uic_tenant_id` int(11) NOT NULL COMMENT '租户id',
                                      `project_id` int(11) NOT NULL COMMENT '项目id',
                                      `date` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '生成日期 yyyyMMdd',
                                      `hour` int(11) NOT NULL DEFAULT '0' COMMENT '小时',
                                      `count` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
                                      `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                      `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `idx_tenant` (`uic_tenant_id`,`app_type`,`project_id`) USING BTREE,
                                      UNIQUE KEY `idx_name_type` (`uic_tenant_id`,`app_type`,`project_id`,`date`,`hour`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='曲线图数据记录表';