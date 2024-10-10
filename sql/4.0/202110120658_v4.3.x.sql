INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 1, 'jdbcUrl', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 0, 'maxJobPoolSize', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 0, 'minJobPoolSize', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'PASSWORD', 0, 'password', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 0, 'queue', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 0, 'username', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES(-2, -117, 9, 'INPUT', 0, 'hive.metastore.uris', '', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

UPDATE schedule_dict set dict_value=-117 where `type`=6 and dict_name = 'hive' and is_deleted=0;
UPDATE schedule_dict set dict_value=-117 where `type`=6 and dict_name = 'hive2' and is_deleted=0;
UPDATE schedule_dict set dict_value=-117 where `type`=6 and dict_name = 'hive3' and is_deleted=0;