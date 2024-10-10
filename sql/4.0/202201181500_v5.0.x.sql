
DELETE FROM schedule_dict WHERE dict_code = 'hadoop_version' AND dict_name IN ('HW MRS 3.x','HW HD6.x','BMR 2.x');
DELETE FROM schedule_dict WHERE dict_code = 'component_model_config' AND dict_name IN ('HW MRS 3.x','HW HD6.x','BMR 2.x');
DELETE FROM schedule_dict WHERE dict_code = 'hdfs_type_name' AND dict_name IN ('HW MRS 3.x','HW HD6.x','BMR 2.x');
DELETE FROM schedule_dict WHERE dict_code = 'typename_mapping' AND dict_name IN ('yarn3hw-hdfs3hw-flink110','yarn3hw-hdfs3hw-spark240hw',
'yarn3hw-hdfs3hw-dtscript');

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'HW MRS 3.x', '3.0.0', null, 0, 1, 'STRING', 'Huawei', 0, now(),
        now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'HW HD6.x', '3.0.0', null, 0, 1, 'STRING', 'Huawei', 0, now(),
        now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hadoop_version', 'BMR 2.x', '2.7.0', null, 0, 1, 'STRING', 'Baidu', 0, now(),
        now(), 0);

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
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('component_model_config', 'BMR 2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, now(), now(), 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hdfs_type_name', 'HW MRS 3.x', 'hdfs3hw', null, 16, 1, 'STRING', null, 0, now(),
        now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                                   gmt_create, gmt_modified, is_deleted)
        VALUES ('hdfs_type_name', 'HW HD6.x', 'hdfs3hw', null, 16, 1, 'STRING', null, 0, now(),
                now(), 0);
                
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                                                depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3hw-hdfs3hw-flink110', '-109', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                                                depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3hw-hdfs3hw-spark240hw', '-130', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                                                depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3hw-hdfs3hw-dtscript', '-100', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);



