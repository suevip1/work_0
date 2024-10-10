ALTER TABLE console_security_log ADD COLUMN `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '修改时间' AFTER `gmt_create`;
UPDATE console_security_log SET `gmt_modified` = `gmt_create`;

ALTER TABLE `schedule_job_expand` ADD COLUMN `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0正常 1逻辑删除' AFTER `gmt_modified`;

-- 刷新清理实例配置
delete from schedule_dict where `type` = 8 and dict_code = 'data_clear_name' AND is_deleted = 0;
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_job', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_job_job', '{\"clearDateConfig\":366, \"deleteDateConfig\":30,}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_fill_data_job', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_plugin_job_info', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'console_security_log', '{\"clearDateConfig\":90, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'alert_record', '{\"clearDateConfig\":90, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'alert_content', '{\"clearDateConfig\":90, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-18 11:09:00', '2022-04-18 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_job_expand', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, '2022-04-25 11:09:00', '2022-04-25 11:09:00');
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_job_chain_output_param', '{\"deleteDateConfig\":396}', NULL, 8, 1, 'STRING', '', 0, '2022-04-20 20:38:00', '2022-04-20 20:38:00');