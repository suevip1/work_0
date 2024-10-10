UPDATE `schedule_dict` SET `dict_name` = 'sql' WHERE `type` = 25 AND `dict_desc` = 1 AND `dict_name` = '其它';

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.crossJoin.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.crossJoin.enabled','是否开启笛卡尔积join',25,'sql','1', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.ranger.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.ranger.enabled','是否开启ranger',25,'安全','1', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.extensions' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.extensions','ranger所需相关扩展类类名',25,'安全','1', 8);

DELETE FROM console_component_config WHERE component_id = -132;
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
 (-2, -132, 1, 'CHECKBOX', 1, 'deploymode', '[\"perjob\"]', NULL, '', '', NULL, now(), now(), 0),
 (-2, -132, 1, 'GROUP', 1, 'perjob', '', NULL, 'deploymode', 'perjob', NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.eventLog.compress', 'false', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs://ns1/dtInsight/spark310/eventlogs', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.executor.cores', '1', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '10s', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.executor.instances', '1', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.executor.memory', '1g', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.network.timeout', '700s', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/anaconda3/bin/python3', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '4', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'sparkPythonExtLibPath', 'hdfs://ns1/dtInsight/spark310/pythons/pyspark.zip,hdfs://ns1/dtInsight/spark310/pythons/py4j-0.10.9-src.zip', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs://ns1/dtInsight/spark310/jars', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 0, 'spark.yarn.security.credentials.hive.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
 (-2, -132, 1, 'INPUT', 1, 'spark.sql.crossJoin.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0);


INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn3-hdfs3-spark310', '-132', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                            `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn2-hdfs2-spark310', '-132', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES ('spark_version', '3.1', '310', NULL, 2, 4, 'INTEGER', '', 0, now(), now(), 0);

UPDATE schedule_dict SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.1":"yarn2-hdfs2-spark310"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'Apache Hadoop 2.x' AND type = 14;


UPDATE schedule_dict SET dict_value = '{
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
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240",
                "3.1":"yarn3-hdfs3-spark310"
             }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}'
WHERE dict_name = 'Apache Hadoop 3.x' AND type = 14;


UPDATE schedule_dict SET dict_value = 'flink lib path' WHERE `type` = 25 AND dict_name = 'flinkLibDir' AND dict_desc IN ('20', '0');