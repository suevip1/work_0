INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('executor_config', 'FILL_DATA', '{
"threadNum": 5,
"scanNum":2000,
"takeEffect": true
}', ' 补数据实例调度器配置', 36, 1, 'STRING', '', 0, NOW(), NOW(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('executor_config', 'RESTART', '{
"threadNum": 3,
"scanNum":1500,
"takeEffect": true
}', ' 周期实例调度器配置', 36, 1, 'STRING', '', 0, NOW(), NOW(), 0);

