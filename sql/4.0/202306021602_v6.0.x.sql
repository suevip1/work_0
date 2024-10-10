-- oushdb
delete from schedule_dict where `type` = 12 and `dict_code` = 'component_model' and `dict_name` = 'OUSHUDB';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'OUSHUDB', '{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "oushudb"
}', NULL, 12, 0, 'STRING', '', 0, now(), now(), 0);

-- 配置模版
delete from console_component_config where `component_id` = -161 and `component_type_code` = 37;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required,
                                      `key`, value, `values`, dependencyKey, dependencyValue,
                                      `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -161, 37, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0),
       (-2, -161, 37, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -161, 37, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -161, 37, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0),
       (-2, -161, 37, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);

-- pluginName 和配置模版关系映射
delete from schedule_dict where `type` = 6 and `dict_code` = 'typename_mapping' and `dict_name` = 'oushudb';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'oushudb', '-161', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

-- 增加配置 tips
delete from schedule_dict where `dict_code` = 'tips' and `type` = 25 and `dict_desc` = '37';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`,
                            `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`,
                            `is_deleted`)
VALUES ('tips', 'jdbcUrl', 'jdbc:postgresql://host:port/database', '37', 25, 1, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'username', 'jdbc连接用户名', '37', 25, 2, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'password', 'jdbc连接密码', '37', 25, 3, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'maxJobPoolSize', '任务最大线程数', '37', 25, 4, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'minJobPoolSize', '任务最小线程数', '37', 25, 5, 'STRING', '', 0, now(), now(), 0);

-- 任务运行方式
delete from schedule_dict where `type` = 23 and `dict_code` = 'task_client_type' and `dict_name` = 'oushudb';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('task_client_type', 'oushudb', '1', 'oushudb', 23, 0, 'INTEGER', null, 0, now(), now(), 0);

-- 任务参数模板
DELETE FROM environment_param_template WHERE `task_type` IN (46);
INSERT INTO `environment_param_template` (`task_type`, `task_name`, `task_version`, `app_type`, `params`) VALUES (46, 'OUSHUDB', NULL, -1, '');


