DELETE FROM schedule_dict WHERE dict_name IN ('HW MRS 3.x') AND type = 14 LIMIT 2;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HW MRS 3.x', '{
    "YARN":"yarn3hw",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            },{
                "1.12":"yarn3hw-hdfs3hw-flink112"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240hw"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3hw-hdfs3hw-flink112', '-115', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);

UPDATE schedule_dict
SET `is_deleted` = 1
WHERE `dict_code` = 'tips'
  AND `type` = 25
  AND `dict_name` = 'security.kerberos.login.contexts';