-- add hana, ref:http://zenpms.dtstack.cn/zentao/task-view-6694.html
-- 1 模板配置
delete from schedule_dict where dict_code = 'component_model' and dict_name = 'HANA';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted)
 VALUES ('component_model','HANA','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "hana",
}',NULL,12,0,'STRING','',0,'2022-07-19 10:20:58','2022-07-19 10:20:58',0);

delete from console_component_config where component_id = -152 and `component_type_code` = 33;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -152, 33, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -152, 33, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -152, 33, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -152, 33, 'PASSWORD', 1, 'password', '', null, null, null, null, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -152, 33, 'INPUT', 1, 'username', '', null, null, null, null, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);

delete from schedule_dict where dict_code = 'typename_mapping' and dict_name = 'hana';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('typename_mapping', 'hana', '-152', null, 6, 0, 'LONG', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);

delete from schedule_dict where dict_code = 'tips' and dict_desc = '33';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'jdbcUrl', 'jdbc url地址', '33', 25, 1, 'STRING', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'username', 'jdbc连接用户名', '33', 25, 2, 'STRING', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'password', 'jdbc连接密码', '33', 25, 3, 'STRING', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'maxJobPoolSize', '任务最大线程数', '33', 25, 4, 'STRING', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'minJobPoolSize', '任务最小线程数', '33', 25, 5, 'STRING', '', 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
-- 2 任务运行
delete from schedule_dict where dict_code = 'task_client_type' and dict_name = 'saphana';
delete from schedule_dict where dict_code = 'task_client_type' and dict_name = 'hana';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('task_client_type', 'hana', '1', 'hana', 23, 0, 'INTEGER', null, 0, '2022-07-19 10:20:58', '2022-07-19 10:20:58', 0);
