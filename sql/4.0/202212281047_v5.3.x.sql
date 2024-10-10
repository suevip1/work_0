-- starRocks
delete from schedule_dict where `type` = 12 and `dict_code` = 'component_model' and `dict_name` = 'STARROCKS';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'STARROCKS', '{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "starrocks",
}', NULL, 12, 0, 'STRING', '', 0, now(), now(), 0);

-- 配置模版
delete from console_component_config where `component_id` = -154 and `component_type_code` = 35;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required,
                                      `key`, value, `values`, dependencyKey, dependencyValue,
                                      `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -154, 35, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0),
       (-2, -154, 35, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -154, 35, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -154, 35, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0),
       (-2, -154, 35, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);

-- pluginName 和配置模版关系映射
delete from schedule_dict where `type` = 6 and `dict_code` = 'typename_mapping' and `dict_name` = 'starrocks';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'starrocks', '-154', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

-- 增加配置 tips
delete from schedule_dict where `dict_code` = 'tips' and `type` = 25 and `dict_desc` = '35';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`,
                            `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`,
                            `is_deleted`)
VALUES ('tips', 'jdbcUrl', 'jdbc:mysql://host:port/database', '35', 25, 1, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'username', 'jdbc连接用户名', '35', 25, 2, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'password', 'jdbc连接密码', '35', 25, 3, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'maxJobPoolSize', '任务最大线程数', '35', 25, 4, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'minJobPoolSize', '任务最小线程数', '35', 25, 5, 'STRING', '', 0, now(), now(), 0);

-- 任务运行方式
delete from schedule_dict where `type` = 23 and `dict_code` = 'task_client_type' and `dict_name` = 'starrocks';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('task_client_type', 'starrocks', '1', 'starrocks', 23, 0, 'INTEGER', null, 0, now(), now(), 0);


-- hashData
delete from schedule_dict where `type` = 12 and `dict_code` = 'component_model' and `dict_name` = 'HASHDATA';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('component_model', 'HASHDATA', '{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "hashdata",
}', NULL, 12, 0, 'STRING', '', 0, now(), now(), 0);

-- 配置模版
delete from console_component_config where `component_id` = -156 and `component_type_code` = 36;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required,
                                      `key`, value, `values`, dependencyKey, dependencyValue,
                                      `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -156, 36, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, now(), now(), 0),
       (-2, -156, 36, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -156, 36, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, now(), now(), 0),
       (-2, -156, 36, 'PASSWORD', 0, 'password', '', null, null, null, null, now(), now(), 0),
       (-2, -156, 36, 'INPUT', 0, 'username', '', null, null, null, null, now(), now(), 0);

-- pluginName 和配置模版关系映射
delete from schedule_dict where `type` = 6 and `dict_code` = 'typename_mapping' and `dict_name` = 'hashdata';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'hashdata', '-156', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

-- 增加配置 tips
delete from schedule_dict where `dict_code` = 'tips' and `type` = 25 and `dict_desc` = '36';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`,
                            `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`,
                            `is_deleted`)
VALUES ('tips', 'jdbcUrl', 'jdbc:postgresql://host:port/database', '36', 25, 1, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'username', 'jdbc连接用户名', '36', 25, 2, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'password', 'jdbc连接密码', '36', 25, 3, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'maxJobPoolSize', '任务最大线程数', '36', 25, 4, 'STRING', '', 0, now(), now(), 0),
       ('tips', 'minJobPoolSize', '任务最小线程数', '36', 25, 5, 'STRING', '', 0, now(), now(), 0);

-- 任务运行方式
delete from schedule_dict where `type` = 23 and `dict_code` = 'task_client_type' and `dict_name` = 'hashdata';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('task_client_type', 'hashdata', '1', 'hashdata', 23, 0, 'INTEGER', null, 0, now(), now(), 0);