-- add trino param, add by qiuyun
delete from environment_param_template where task_type = 36;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
(36, 'TRINO', null, -1, '', '2022-05-17 16:13:50', '2022-05-17 16:13:50', 0);