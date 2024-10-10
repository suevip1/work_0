-- 实例限制提交配置
CREATE TABLE IF NOT EXISTS `console_job_restrict` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `restrict_start_time` datetime NOT NULL COMMENT '限制时间起始',
   `restrict_end_time` datetime NOT NULL COMMENT '限制时间终止',
   `effective_time` datetime default NULL COMMENT '规则生效时间',
   `status` tinyint(4) not null default '0' COMMENT '应用状态:0等待执行、1执行中、2关闭、3无效、4过期',
   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
   `create_user_id` bigint(20) NOT NULL COMMENT '发起操作的用户',
   `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_restrict_time` (`restrict_start_time`,`restrict_end_time`)
) ENGINE=InnoDB COMMENT='实例限制提交配置表';

DELETE from `schedule_dict` WHERE `type` = 8 AND `dict_code` = 'data_clear_name' AND `dict_name` = 'console_job_restrict';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'console_job_restrict', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, now(), now());