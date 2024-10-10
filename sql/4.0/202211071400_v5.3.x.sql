-- spark 适配
DELETE FROM console_component_config where `key` = 'spark.yarn.appMasterEnv.SPARK_HOME' AND component_type_code= 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type,
                                                           required, `key`, value, `values`, dependencyKey,
                                                           dependencyValue, `desc`, gmt_create, gmt_modified,
                                                           is_deleted)
select -2, dict_value, 1, 'INPUT', 1, 'spark.yarn.appMasterEnv.SPARK_HOME', './', null, 'deploymode$perjob', null, null, now(),now(), 0
from schedule_dict where dict_code = 'typename_mapping' and dict_name like 'yarn%spark%' group by dict_value;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type,
                                                           required, `key`, value, `values`, dependencyKey,
                                                           dependencyValue, `desc`, gmt_create, gmt_modified,
                                                           is_deleted)
select cluster_id, id, 1, 'INPUT', 1, 'spark.yarn.appMasterEnv.SPARK_HOME', './', null, 'deploymode$perjob', null, null, now(),now(), 0
from console_component where component_type_code = 1 and is_deleted = 0 and cluster_id is not null;

DELETE FROM  schedule_dict WHERE dict_code = 'component_model_config' and dict_name IN ('CDH 6.2.x','CDP 7.x','HW MRS 3.x','HW HD6.x','TBDS 5.1.x','TDH 5.2.x','TDH 6.x','TDH 7.x') LIMIT 8;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.2.x', '{
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
               "2.4":"yarn3-hdfs3-spark240",
               "3.2":"yarn3-hdfs3-spark320"
           }
        ],
       "DT_SCRIPT":"yarn3-hdfs3-dtscript",
       "HDFS":"yarn3-hdfs3-hadoop3",
       "TONY":"yarn3-hdfs3-tony",
       "LEARNING":"yarn3-hdfs3-learning"
   }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);



INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDP 7.x', '{
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
                                  "2.4":"yarn3-hdfs3-spark240",
                                  "3.2":"yarn3-hdfs3-spark320"
                              }
                          ],
                          "DT_SCRIPT":"yarn3-hdfs3-dtscript",
                          "HDFS":"yarn3-hdfs3-hadoop3",
                          "TONY":"yarn3-hdfs3-tony",
                          "LEARNING":"yarn3-hdfs3-learning"
                      }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HW MRS 3.x', '{
    "YARN":"yarn3hw",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);



INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HW HD6.x', '{
    "YARN":"yarn3hw",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
           {
                "2.4":"yarn3hw-hdfs3hw-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);


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
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 5.2.x', '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 6.x', '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 7.x', '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2022-08-02 12:02:33', '2022-08-02 12:02:33', 0);


DELETE FROM console_component_config WHERE component_id = -1001 LIMIT 2;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1001, 1, 'CUSTOM_CONTROL', 0, 'spark.executorEnv.JAVA_HOME', '/usr/java/jdk1.8.0_25', null, 'deploymode$perjob', null, null, '2022-02-15 19:32:30', '2022-02-15 19:32:30', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1001, 1, 'CUSTOM_CONTROL', 0, 'spark.yarn.appMasterEnv.JAVA_HOME', '/usr/java/jdk1.8.0_25', null, 'deploymode$perjob', null, null, '2022-02-15 19:32:30', '2022-02-15 19:32:30', 0);

DELETE FROM console_component_config WHERE component_id = -1004 AND `key` = 'spark.yarn.appMasterEnv.HADOOP_CONF_DIR' LIMIT 1;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1004, 1, 'CUSTOM_CONTROL', 0, 'spark.yarn.appMasterEnv.HADOOP_CONF_DIR', './__hadoop_conf__', null, 'deploymode$perjob', null, null, '2022-03-15 14:21:38', '2022-03-15 14:21:38', 0);

DELETE FROM schedule_dict WHERE dict_code = 'tips' AND dict_desc = 1 AND `dict_name` = 'spark.yarn.appMasterEnv.SPARK_HOME' LIMIT 1;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.yarn.appMasterEnv.SPARK_HOME', '用于执行spark任务的环境变量', '1', 25, 3, 'STRING', '环境变量', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);

-- 2.4 3.2 移除
DELETE FROM console_component_config WHERE `key` IN ('sparkYarnArchive','sparkPythonExtLibPath','spark.sql.extensions')
                                       AND component_id in (select id
                                                            from console_component cc
                                                            where component_type_code = 1
                                                              and hadoop_version IN ('240', '320')
                                                            union all
                                                            select -130
                                                            union all
                                                            select -132);

DELETE FROM console_component_config WHERE `key` IN ('sparkSqlProxyPath')
                                       AND component_id in (select id
                                                            from console_component cc
                                                            where component_type_code = 1
                                                              and hadoop_version IN ('210', '240')
                                                            union all
                                                            select -130
                                                            union all
                                                            select -108);

DELETE FROM console_component_config WHERE `key` IN ('spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON')
                                       AND component_id in (select id
                                                            from console_component cc
                                                            where component_type_code = 1
                                                              and hadoop_version IN ('210')
                                                            union all
                                                            select -108);


-- 2.1 改名
UPDATE  console_component_config SET `key` = 'spark.yarn.archive' WHERE `key` = 'sparkYarnArchive'
                                                                    AND component_id in (select id from console_component cc
                                                                                         where component_type_code = 1
                                                                                           and hadoop_version = '210' union all select -108);


DELETE FROM schedule_dict WHERE dict_code = 'tips' AND dict_desc = 1 AND `dict_name` IN ('spark.local.spark.home','spark.yarn.archive','spark.yarn.appMasterEnv.SPARK_HOME','spark.executorEnv.JAVA_HOME','spark.yarn.appMasterEnv.JAVA_HOME','spark.yarn.appMasterEnv.HADOOP_CONF_DIR','hdp.version') LIMIT 7;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.local.spark.home', 'engine所在节点的DTSpark包所在位置', '1', 25, 3, 'STRING', '主要', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.yarn.archive', '远程存储系统上spark jars的路径', '1', 25, 3, 'STRING', '主要', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.yarn.appMasterEnv.SPARK_HOME', '指定appMaster环境变量中的SPARK_HOME', '1', 25, 3, 'STRING', '环境变量', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.executorEnv.JAVA_HOME', '指定executor环境变量中的JAVA_HOME', '1', 25, 3, 'STRING', '环境变量', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.yarn.appMasterEnv.JAVA_HOME', '指定appMaster环境变量中的JAVA_HOME', '1', 25, 3, 'STRING', '环境变量', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.yarn.appMasterEnv.HADOOP_CONF_DIR', '指定appMaster环境变量中的HADOOP_CONF_DIR', '1', 25, 3, 'STRING', '环境变量', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'hdp.version', 'hdp集群版本', '1', 25, 3, 'STRING', '其他', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);


UPDATE console_component_config SET type = 'INPUT' WHERE component_type_code = 1 AND `key` IN ('spark.local.spark.home','spark.yarn.archive','spark.yarn.appMasterEnv.SPARK_HOME','spark.executorEnv.JAVA_HOME','spark.yarn.appMasterEnv.JAVA_HOME','spark.yarn.appMasterEnv.HADOOP_CONF_DIR');

UPDATE schedule_dict SET dict_name = 'yarn3hw-hdfs3hw-spark240' WHERE dict_name = 'yarn3hw-hdfs3hw-spark240hw' and  dict_code = 'typename_mapping';

UPDATE console_component_config SET value = 'yarn3-hdfs3-spark240' WHERE `key` = 'typeName' AND value = 'yarn3-hdfs3-spark240cdh620' AND component_type_code = 1;
UPDATE console_component_config SET value = 'yarn3hw-hdfs3hw-spark240' WHERE `key` = 'typeName' AND value = 'yarn3hw-hdfs3hw-spark240hw' AND component_type_code = 1;

DELETE FROM console_component_config WHERE component_id in (-1003,-1006);


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1003, 1, 'CUSTOM_CONTROL', 0, 'spark.sql.hive.metastore.version', '3.0', null, 'deploymode$perjob', null, null, '2022-11-08 11:30:32', '2022-11-08 11:30:32', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1003, 1, 'CUSTOM_CONTROL', 0, 'spark.sql.hive.metastore.jars', '/opt/bmr/hive/lib/*', null, 'deploymode$perjob', null, null, '2022-11-08 11:30:32', '2022-11-08 11:30:32', 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -1006, 1, 'CUSTOM_CONTROL', 1, 'hdp.version', '', null, 'deploymode$perjob', null, null, '2022-02-15 19:39:38', '2022-02-15 19:39:38', 0);

DELETE FROM schedule_dict WHERE dict_name = 'HDP 2.6.x' and dict_code = 'extra_version_template';

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', 'HDP 2.6.x', '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1006",
                "3.2":"-1006"
            }
        ]
    }
}', null, 15, 1, 'STRING', 'YARN', 0, '2022-02-23 17:43:31', '2022-02-23 17:43:31', 0);