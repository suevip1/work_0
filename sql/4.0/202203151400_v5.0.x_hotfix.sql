UPDATE console_component_config SET `value` = 'true' WHERE component_id IN(-130,-107,-108) AND `key` = 'spark.yarn.security.credentials.hive.enabled';

DELETE FROM schedule_dict where dict_code = 'extra_version_template' AND dict_name IN ('HW MRS 3.x','HW HD6.x');

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'HW MRS 3.x', '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'HW HD6.x', '{
    "HDFS":{
        "SPARK":[
            {
                 "2.1":"-1004",
                 "2.4":"-1004"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);

DELETE FROM console_component_config where component_id IN(-1004) and `key` = 'spark.yarn.security.credentials.hive.enabled';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1004, 1, 'INPUT', 0, 'spark.yarn.security.credentials.hive.enabled', 'false', null, 'deploymode$perjob', null, null, now(),now(), 0);
