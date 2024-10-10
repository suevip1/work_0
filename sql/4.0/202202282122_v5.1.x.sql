-- 血缘解析迁移到元数据中心
CREATE TABLE if not exists `schedule_job_status_monitor` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `app_type_key` varchar(25) NOT NULL COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
     `callback_url` varchar(500) NOT NULL COMMENT '回调地址，域名形式',
     `listener_policy` text COMMENT '监听策略, {"listenerStatus":[], "listenerAppType":[], "listenerJobType":[]}',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE KEY `uk_app_type_key` (`app_type_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_job_status_callback_fail` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `job_id` varchar(32) NOT NULL COMMENT '工作任务id',
   `retry_num` int(10) NOT NULL DEFAULT '0' COMMENT '重试次数 默认0',
   `last_retry_fail_reason` text COMMENT '最后一次失败的原因',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
   `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
   `monitor_id` int(11) NOT NULL COMMENT '监控记录id',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_monitor_id_job_id` (`monitor_id`,`job_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;