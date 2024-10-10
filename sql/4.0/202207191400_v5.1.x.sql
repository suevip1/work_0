DELETE FROM schedule_dict WHERE dict_name = 'TBDS 5.1.x' AND dict_code = 'component_model_config' LIMIT 1;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TBDS 5.1.x', '{
    "YARN":"yarn2tbds",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn2tbds-hdfs2tbds-flink110",
                "1.12":"yarn2tbds-hdfs2tbds-flink112"
            }
        ],
         "HDFS":"yarn2tbds-hdfs2tbds-hadoop2tbds",
         "SPARK":[
             {
                 "2.4":"yarn2tbds-hdfs2tbds-spark240"
             }
         ],
         "DT_SCRIPT":"yarn2tbds-hdfs2tbds-dtscript"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(),now(), 0);

DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name IN ('yarn2tbds-hdfs2tbds-spark240','yarn2tbds-hdfs2tbds-dtscript','yarn2tbds-hdfs2tbds-flink112');

INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn2tbds-hdfs2tbds-spark240', '-130', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn2tbds-hdfs2tbds-dtscript', '-100', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                              `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn2tbds-hdfs2tbds-flink112', '-115', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);


DELETE FROM schedule_dict WHERE dict_code IN('component_model_config','hive_version') AND dict_name = '2.x-tbds' LIMIT 1;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '2.x-tbds', '{
      "2.x-tbds":"hive2"
  }', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hive_version', '2.x-tbds', '2.x-tbds', null, 4, 2, 'STRING', '', 1, '2021-03-02 14:17:13', '2021-03-02 14:17:13', 0);


UPDATE schedule_dict t SET dict_value = '{"1.x":27,"2.x":7,"3.x-apache":50,"3.x-cdp":65,"2.x-tbds":94}' WHERE dict_code = 'version_datasource_x' AND dict_name = 'HiveServer';
