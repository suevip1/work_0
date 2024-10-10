-- spark组件上增加的metastore参数移除掉

-- update schedule_dict set is_deleted = 1 where depend_name = 'HiveMetastore';

delete from schedule_dict where dict_name = 'spark.hadoop.hive.exec.stagingdir';
delete from console_component_config where `key` = 'spark.hadoop.hive.exec.stagingdir' and cluster_id = -2;

delete from console_component_config where `key` = 'spark.hadoop.hive.metastore.uris' and cluster_id = -2;
delete from console_component_config where `key` = 'spark.hadoop.hive.metastore.warehouse.dir' and cluster_id = -2;
delete from console_component_config where `key` = 'spark.hadoop.hive.metastore.sasl.enabled' and cluster_id = -2;
delete from console_component_config where `key` = 'spark.hadoop.hive.metastore.kerberos.principal' and cluster_id = -2;


update schedule_dict set depend_name = '主要',is_deleted = 0 where dict_name = 'spark.hadoop.hive.exec.scratchdir';


INSERT INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES
('tips', 'spark.hadoop.hive.exec.stagingdir', '用于指定Hive作业的临时阶段目录', '1', 25, 1, 'STRING', '主要', 0, '2023-09-19 17:23:03', '2023-09-19 17:23:03', 0);



INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -132, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.stagingdir', 'hdfs:///dtInsight/spark/tmp', null, 'deploymode$perjob', null, null, '2023-02-17 18:23:03', '2023-02-17 18:23:03', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -130, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.stagingdir', 'hdfs:///dtInsight/spark/tmp', null, 'deploymode$perjob', null, null, '2023-02-17 18:23:03', '2023-02-17 18:23:03', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -108, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.stagingdir', 'hdfs:///dtInsight/spark/tmp', null, 'deploymode$perjob', null, null, '2023-02-17 18:23:03', '2023-02-17 18:23:03', 0);
