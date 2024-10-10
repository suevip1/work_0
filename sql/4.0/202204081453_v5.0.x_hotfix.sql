-- hadoop版本为bmr时变更spark参数
DELETE FROM console_component_config WHERE component_id = -1003 AND `key` = 'spark.sql.hive.metastore.jars';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1003, 1, 'CUSTOM_CONTROL', 0, 'spark.sql.hive.metastore.jars', '/opt/bmr/hive/lib/*', null, 'deploymode$perjob', null, null, now(),now(), 0);
