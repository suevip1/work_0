-- dataSecurity, add by qiuyun
-- add Ranger start -----
delete from schedule_dict where dict_code = 'component_model' and `type` = 12 and dict_name = 'RANGER';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
 ('component_model', 'RANGER', '{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "ranger"
}', null, 12, 0, 'STRING', '', 0, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

-- jdbc url tip
delete from schedule_dict where dict_code = 'jdbc_url_tip' and dict_name in ('Ldap', 'Ranger');
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Ldap', 'ldap://host:port', NULL, 10, 0, 'STRING', 'url', 0, '2022-05-09 17:41:00', '2022-05-09 17:41:00', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Ranger', 'http://host:port', NULL, 10, 0, 'STRING', 'url', 0, '2022-05-09 17:41:00', '2022-05-09 17:41:00', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Ldap', 'BaseDN：dc=${dc},dc=${dc}，通常指定一个域名，例如dc=example,dc=com', NULL, 10, 0, 'STRING', 'BaseDN', 0, '2022-05-09 17:41:00', '2022-05-09 17:41:00', 0);
update schedule_dict set depend_name = 'jdbcUrl' where dict_code = 'jdbc_url_tip' and `type` = 10 and dict_value like 'jdbc%';

delete from schedule_dict where dict_code = 'typename_mapping' and `type` = 6 and dict_name = 'ranger';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
 ('typename_mapping', 'ranger', '-140', null, 6, 0, 'LONG', '', 0, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

delete from console_component_config where component_id = -140 and component_type_code = 31;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
 (-2, -140, 31, 'INPUT', 1, 'url', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
 (-2, -140, 31, 'INPUT', 1, 'username', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
 (-2, -140, 31, 'PASSWORD', 1, 'password', '', null, '', '', null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
-- add Ranger end -----

-- add Ldap start---
delete from schedule_dict where dict_code = 'component_model' and `type` = 12 and dict_name = 'LDAP';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('component_model', 'LDAP', '{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "ldap"
}', null, 12, 0, 'STRING', '', 0, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

delete from schedule_dict where dict_code = 'typename_mapping' and `type` = 6 and dict_name = 'ldap';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('typename_mapping', 'ldap', '-141', null, 6, 0, 'LONG', '', 0, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

delete from console_component_config where component_id = -141 and component_type_code = 32;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'INPUT', 1, 'url', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'INPUT', 1, 'BaseDN', 'dc=${dc},dc=${dc}', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'INPUT', 1, 'user_ou', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'INPUT', 1, 'userGroup_ou', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'INPUT', 1, 'username', '', null, null, null, null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -141, 32, 'PASSWORD', 1, 'password', '', null, '', '', null, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
-- add Ldap end---

-- add hiveServer param
delete from schedule_dict where dict_code = 'extra_version_template' and type = 15 and depend_name = 'HIVE_SERVER' and dict_name = '1.x';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
('extra_version_template', '1.x', '{
    "1.x":"-213"
}', null, 15, 1, 'STRING', 'HIVE_SERVER', 0, '2022-01-10 17:36:43', '2022-01-10 17:36:43', 0);

delete from schedule_dict where dict_code = 'extra_version_template' and type = 15 and depend_name = 'HIVE_SERVER' and dict_name = '2.x';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('extra_version_template', '2.x', '{
    "2.x":"-214"
}', null, 15, 1, 'STRING', 'HIVE_SERVER', 0, '2022-01-10 17:36:43', '2022-01-10 17:36:43', 0);

delete from console_component_config where component_id = -213 and component_type_code = 9 and `key` = 'serviceName';
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -213, 9, 'INPUT', 0, 'serviceName', 'hive_default', NULL, NULL, NULL, NULL, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

delete from console_component_config where component_id = -214 and component_type_code = 9 and `key` = 'serviceName';
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -214, 9, 'INPUT', 0, 'serviceName', 'hive_default', NULL, NULL, NULL, NULL, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

delete from console_component_config where component_id = -212 and component_type_code = 9 and `key` = 'serviceName';
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
   (-2, -212, 9, 'INPUT', 0, 'serviceName', 'hive_default', NULL, NULL, NULL, NULL, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);
-- 处理历史数据(支持幂等)
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select cluster_id, component_id, 9,'INPUT', 0, 'serviceName', 'hive_default', null, null, null, null, '2022-04-27 13:36:00',
       '2022-04-27 13:36:00', 0 from console_component_config where component_type_code = 9 and cluster_id != -2 and is_deleted = 0
                                                                and component_id not in(select component_id from console_component_config where component_type_code = 9 and cluster_id != -2 and `key` = 'serviceName')
group by component_id;

-- add spark param (yarn,spark240)
delete from console_component_config where component_id = -130 and `key` in ('spark.ranger.enabled', 'spark.sql.extensions');
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -130, 1, 'INPUT', 0, 'spark.ranger.enabled', 'false', NULL, 'deploymode$perjob', NULL, NULL, '2022-05-09 20:58:00', '2022-05-09 20:58:00', 0);
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -130, 1, 'INPUT', 0, 'spark.sql.extensions', 'org.apache.spark.ranger.security.api.RangerSparkSQLExtension', NULL, 'deploymode$perjob', NULL, NULL, '2022-05-09 20:58:00', '2022-05-09 20:58:00', 0);

-- add trino param
-- 1) 修正历史数据: component_type_code=16 代表 presto
delete from console_component_config where component_id = -105 and `component_type_code` = 16;
-- 清理测试的脏数据
delete from console_component_config where component_id = -105 and `component_type_code` = 26;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -105, 16, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -105, 16, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -105, 16, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -105, 16, 'PASSWORD', 0, 'password', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -105, 16, 'INPUT', 1, 'username', '', null, null, null, null, '2021-02-25 18:12:54', '2021-02-25 18:12:54', 0);
-- component_type_code=26 代表的是 trino
delete from console_component_config where component_id = -145 and `component_type_code` = 26;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -145, 26, 'INPUT', 1, 'jdbcUrl', '', null, null, null, null, '2022-06-01 23:06:34', '2022-06-01 23:06:34', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -145, 26, 'INPUT', 0, 'maxJobPoolSize', '', null, null, null, null, '2022-06-01 23:06:34', '2022-06-01 23:06:34', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -145, 26, 'INPUT', 0, 'minJobPoolSize', '', null, null, null, null, '2022-06-01 23:06:34', '2022-06-01 23:06:34', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -145, 26, 'PASSWORD', 0, 'password', '', null, null, null, null, '2022-06-01 23:06:34', '2022-06-01 23:06:34', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -145, 26, 'INPUT', 1, 'username', '', null, null, null, null, '2022-06-01 23:06:34', '2022-06-01 23:06:34', 0);
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -145, 26, 'INPUT', 0, 'serviceName', 'trino_default', NULL, NULL, NULL, NULL, '2022-04-27 13:36:00', '2022-04-27 13:36:00', 0);

update schedule_dict set dict_value = -145 where dict_code = 'typename_mapping' and type = 6 and dict_value = -105 and dict_name = 'trino';

-- 处理历史数据(支持幂等)
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select cluster_id, component_id, 26,'INPUT', 0, 'serviceName', 'trino_default', null, null, null, null, '2022-04-27 13:36:00',
       '2022-04-27 13:36:00', 0 from console_component_config where component_type_code = 26 and cluster_id != -2 and is_deleted = 0
                                                                and component_id not in(select component_id from console_component_config where component_type_code = 26 and cluster_id != -2 and `key` = 'serviceName')
group by component_id;
