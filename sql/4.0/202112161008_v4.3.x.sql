CREATE TABLE `console_resource_hand_over` (
                                              `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                              `engine_project_id` bigint(11) NOT NULL COMMENT '引擎项目ID',
                                              `old_resource_id` bigint(11) DEFAULT NULL COMMENT '旧资源ID',
                                              `target_resource_id` bigint(11) NOT NULL COMMENT '目标资源ID',
                                              `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                              `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `console_resource_queue_used` (
                                               `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                               `cluster_id` bigint(11) NOT NULL COMMENT '集群ID',
                                               `queue_name` varchar(256) DEFAULT NULL COMMENT '队列路径',
                                               `used` varchar(127) NOT NULL COMMENT '已用资源',
                                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

alter table schedule_engine_project add column default_resource_id bigint(11) null comment '项目默认资源组ID';