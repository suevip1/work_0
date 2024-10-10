DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND `dict_name` = '其它' AND `dict_desc` = '1';
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', '其它', NULL, '1', '25', '120', 'STRING', '', '0', NOW(), NOW(), '0');