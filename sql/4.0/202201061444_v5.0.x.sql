-- add by qiuyun 执行完需重启或手动清理缓存，防止读取的还是旧配置

-- 需求: 在spark版本中，原"Spark 2.4"改名为"Spark 2.4 (CDH 6.2)"，新增一个开源版本的"Spark 2.4"
-- 先修改配置表旧配置(如果存在的话)
update console_component_config set value = 'yarn3-hdfs3-spark240cdh620', gmt_modified = now() where `key` = 'typename'
                                                                           and value = 'yarn3-hdfs3-spark240';
update console_component_config set value = 'k8s-hdfs2-spark240cdh620', gmt_modified = now() where `key` = 'typename'
                                                                          and value = 'k8s-hdfs2-spark240';

-- 再增加新配置
DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'yarn3-hdfs3-spark240' and dict_value = '-108';
DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'yarn3-hdfs3-spark240' and dict_value = '-130';
DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'k8s-hdfs2-spark240' and dict_value = '-107';
DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'yarn2-hdfs2-spark240' and dict_value = '-108';
DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'yarn2-hdfs2-spark240' and dict_value = '-130';

DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'yarn3-hdfs3-spark240cdh620' and dict_value = '-130';
INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn3-hdfs3-spark240cdh620', '-130', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

DELETE FROM schedule_dict where dict_code = 'typename_mapping' and dict_name = 'k8s-hdfs2-spark240cdh620' and dict_value = '-107';
INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'k8s-hdfs2-spark240cdh620', '-107', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);


INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                             `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn3-hdfs3-spark240', '-130', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                            `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'k8s-hdfs2-spark240', '-107', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);

INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`,
                            `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('typename_mapping', 'yarn2-hdfs2-spark240', '-130', NULL, 6, 0, 'LONG', '', 0, now(), now(), 0);


-- console_component_config 对应的 -107 配置信息跟以前一样，不做变更
-- console_component_config 对应的 -130 配置信息
DELETE FROM console_component_config WHERE component_id = -130;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.eventLog.compress', 'false', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.eventLog.dir', 'hdfs://ns1/tmp/logs', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.eventLog.enabled', 'true', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.executor.cores', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=utf-8', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.executor.heartbeatInterval', '10s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.executor.instances', '1', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.executor.memory', '1g', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.network.timeout', '600s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.rpc.askTimeout', '600s', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.yarn.appMasterEnv.PYSPARK_PYTHON', '/data/anaconda3/bin/python3', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'spark.yarn.maxAppAttempts', '4', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'sparkPythonExtLibPath', 'hdfs://ns1/dtInsight/spark240/pythons/pyspark.zip,hdfs://ns1/dtInsight/spark240/pythons/py4j-0.10.7-src.zip', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark240/client/spark-sql-proxy.jar', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 1, 'sparkYarnArchive', 'hdfs://ns1/dtInsight/spark240/jars', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$perjob', null, null, now(), now(), 0);

-- 修改 schedule_dict 表，增加 spark240cdh620
DELETE FROM schedule_dict where dict_code = 'spark_version' and `dict_name` = '2.4(CDH 6.2)' and dict_value = '240cdh620';
INSERT INTO `schedule_dict`( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES ('spark_version', '2.4(CDH 6.2)', '240cdh620', NULL, 2, 3, 'INTEGER', '', 0, now(), now(), 0);
