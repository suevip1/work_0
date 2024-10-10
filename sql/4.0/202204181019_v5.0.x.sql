CREATE TABLE `console_component_auxiliary` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `cluster_id` bigint(11) NOT NULL COMMENT '集群id',
   `component_type_code` tinyint(4) NOT NULL COMMENT '组件类型',
   `type` varchar(10) NOT NULL COMMENT '配置类型,如 KNOX',
   `open` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否开启，默认为关',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
   `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `uk_cluster_component_type` (`cluster_id`,`component_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组件附属信息';

CREATE TABLE `console_component_auxiliary_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `auxiliary_id` int(11) NOT NULL COMMENT '附属信息id',
  `key` varchar(256) NOT NULL COMMENT '配置键',
  `value` text COMMENT '配置项',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_affiliate_id_key` (`auxiliary_id`,`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组件附属信息配置';

delete from `schedule_dict` where dict_code = 'KNOX' and `type` = 30;
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('KNOX', 'KNOX', '-1000', NULL, 30, 0, 'LONG', '', 0, '2022-04-18 17:50:24', '2022-04-18 17:50:24');

delete from `console_component_config` where `component_id` = -1000 and `component_type_code` = 5;
INSERT INTO  `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1000, 5, 'INPUT', 1, 'url', '', NULL, '', '', '地址', '2022-04-18 17:50:24', '2022-04-18 17:50:24', 0);
INSERT INTO  `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1000, 5, 'INPUT', 1, 'user', '', NULL, '', '', '用户名', '2022-04-18 17:50:24', '2022-04-18 17:50:24', 0);
INSERT INTO  `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1000, 5, 'PASSWORD', 1, 'password', '', NULL, '', '', '密码', '2022-04-18 17:50:24', '2022-04-18 17:50:24', 0);
