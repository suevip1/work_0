
DELETE FROM schedule_dict WHERE dict_code = 'hadoop_version' AND dict_name IN ('BMR 2.x');
DELETE FROM schedule_dict WHERE dict_code = 'component_model_config' AND dict_name IN ('BMR 2.x');
DELETE FROM schedule_dict WHERE dict_code = 'hdfs_type_name' AND dict_name IN ('BMR 2.x');


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'BMR 2.x', '3.0.0', null, 0, 1, 'STRING', 'Baidu', 0, now(),
        now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'BMR 2.x', '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);



