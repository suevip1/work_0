DELETE FROM schedule_dict WHERE dict_name IN ('HW MRS 3.x','HW HD6.x') AND type = 14 LIMIT 2;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HW MRS 3.x', '{
    "YARN":"yarn3hw",
    "HDFS":{
        "HDFS":"hdfs3hw",
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240hw"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw",
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'HW HD6.x', '{
    "YARN":"yarn3hw",
    "HDFS":{
        "HDFS":"hdfs3hw",
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240hw"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw",
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);