
-- 增加 schedule_task_commit 表的清理配置 http://zenpms.dtstack.cn/zentao/bug-view-81781.html
DELETE from schedule_dict WHERE `type` = 8 AND `dict_code` = 'data_clear_name' AND `dict_name` = 'schedule_task_commit';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_task_commit', '{\"clearDateConfig\":90, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, now(), now());
