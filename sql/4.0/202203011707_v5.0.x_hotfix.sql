-- dtscript任务新增参数，参数名 dtscript.hive.jdbcUrl，是否必填 非必填
delete from console_component_config where component_id = -100 and component_type_code = 3 and `key` = 'dtscript.hive.jdbcUrl';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -100, 3, 'INPUT', 0, 'dtscript.hive.jdbcUrl', '', null, 'commonConf', null, null, '2022-03-01 17:35:59', '2022-03-01 17:35:59', 0);

-- tony任务,参数名 tony.hive.jdbcUrl,是否必填 非必填
delete from console_component_config where component_id in (-119, -121)  and component_type_code = 27 and `key` = 'tony.hive.jdbcUrl';
INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -119, 27, 'INPUT', 0, 'tony.hive.jdbcUrl', '', null, null, null, '', '2022-03-01 17:35:59', '2022-03-01 17:35:59', 0);
INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -121, 27, 'INPUT', 0, 'tony.hive.jdbcUrl', '', null, null, null, '', '2022-03-01 17:35:59', '2022-03-01 17:35:59', 0);

