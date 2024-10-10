-- 控制台新增hive-3Huawei版本

DELETE FROM schedule_dict WHERE dict_code IN('component_model_config','hive_version') AND dict_name = '3Huawei' LIMIT 3;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', '3Huawei', '{
      "3Huawei":"hive3"
  }', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2023-03-06 10:31:00', '2023-03-06 10:31:00', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3Huawei', '3Huawei', null, 4, 2, 'STRING', '', 1, '2023-03-06 10:31:00', '2023-03-06 10:31:00', 0);


UPDATE schedule_dict t SET dict_value = '{"1.x":27,"2.x":7,"3Huawei":108,"3.x-apache":50,"3.x-cdp":65,"2.x-tbds":94}'
WHERE dict_code = 'version_datasource_x' AND dict_name = 'HiveServer';