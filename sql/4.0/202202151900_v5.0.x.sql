DELETE FROM schedule_dict where dict_code = 'extra_version_template' AND dict_name IN ('TDH 5.2.x','TDH 6.x','TDH 7.x','BMR 2.x');
DELETE FROM console_component_config where component_id IN(-1001,-1002,-1003);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'TDH 5.2.x', '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'TDH 6.x', '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);



INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'TDH 7.x', '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'BMR 2.x', '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1003"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, now(),now(), 0);



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1001, 1, 'CUSTOM_CONTROL', 0, 'spark.executorEnv.JAVA_HOME', '', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1001, 1, 'CUSTOM_CONTROL', 0, 'spark.yarn.appMasterEnv.JAVA_HOME', '', null, 'deploymode$perjob', null, null, now(),now(), 0);



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1002, 1, 'CUSTOM_CONTROL', 0, 'containerized.master.env.JAVA_HOME', '', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1002, 1, 'CUSTOM_CONTROL', 0, 'containerized.taskmanager.env.JAVA_HOME', '', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1002, 1, 'CUSTOM_CONTROL', 0, 'containerized.master.env.JAVA_HOME', '', null, 'deploymode$session', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1002, 1, 'CUSTOM_CONTROL', 0, 'containerized.taskmanager.env.JAVA_HOME', '', null, 'deploymode$session', null, null, now(),now(), 0);



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1003, 1, 'CUSTOM_CONTROL', 0, 'spark.sql.hive.metastore.version', '3.0', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1003, 1, 'CUSTOM_CONTROL', 0, 'spark.sql.hive.metastore.jars', './metastore/standalone-metastore-1.21.2.3.1.4.0-315-hive3.jar', null, 'deploymode$perjob', null, null, now(),now(), 0);

DELETE FROM console_component_config WHERE component_id IN(-130,-107,-108) AND `key` = 'spark.yarn.security.credentials.hive.enabled';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -107, 1, 'INPUT', 0, 'spark.yarn.security.credentials.hive.enabled', 'false', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.yarn.security.credentials.hive.enabled', 'false', null, 'deploymode$perjob', null, null, now(),now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.yarn.security.credentials.hive.enabled', 'false', null, 'deploymode$perjob', null, null, now(),now(), 0);
