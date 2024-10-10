-- add by qiuyun
-- 需求：控制台的集群变更与em打通

CREATE TABLE `console_file_sync` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `cluster_id` bigint(11) NOT NULL COMMENT '集群id',
                                     `sync_path` varchar(255) DEFAULT NULL COMMENT '需同步路径',
                                     `is_sync` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否同步',
                                     `last_sync_md5` varchar(64) DEFAULT NULL COMMENT '上次文件同步的md5',
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `idx_cluster_id` (`cluster_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件同步配置表';

CREATE TABLE `console_file_sync_detail` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `file_name` varchar(255) NOT NULL COMMENT '文件名称',
                                            `is_chosen` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否勾选',
                                            `last_sync_time` datetime DEFAULT NULL COMMENT '上次同步时间',
                                            `cluster_id` bigint(11)  NOT NULL COMMENT '集群id',
                                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                                            `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                            `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_cluster_id_file_name` (`cluster_id`,`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件同步配置细节表';