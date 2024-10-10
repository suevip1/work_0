delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.client.leader.election.type';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.client.leader.election.type' AS `key`,'zookeeper' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.driver.userClassPathFirst';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.driver.userClassPathFirst' AS `key`,'true' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.zookeeper.url';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.zookeeper.url' AS `key`,'' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.thrift.server.start.port';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.thrift.server.start.port' AS `key`,'20000' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.thrift.server.port.retries';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.thrift.server.port.retries' AS `key`,'1000' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.thrift.server.min.worker.threads';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.thrift.server.min.worker.threads' AS `key`,'5' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;

delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.thrift.server.max.worker.threads';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.thrift.server.max.worker.threads' AS `key`,'300' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component WHERE `component_type_code` = 1;


delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.zookeeper.namespace' and `component_id` IN (SELECT `id` FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.1');
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.zookeeper.namespace' AS `key`,'spark/2.1.3' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.1';

delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.zookeeper.namespace' and `component_id` IN (SELECT `id` FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.4');
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.zookeeper.namespace' AS `key`,'spark/2.4.8' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.4';

delete from `console_component_config` where  `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.zookeeper.namespace' and `component_id` IN (SELECT `id` FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '3.2');
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.zookeeper.namespace' AS `key`,'spark/3.2.2' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '3.2';

delete from `console_component_config` where `cluster_id` = -2 and `component_id` = -108 and `component_type_code` = 1 and `key` = 'spark.sql.hive.metastore.jars';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -108, 1, 'INPUT', 1, 'spark.sql.hive.metastore.jars', './__spark_libs__/*', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0);

delete from `console_component_config` where   `cluster_id` != -2 and `component_type_code` = 1 and `key` = 'spark.sql.hive.metastore.jars' and component_id IN (SELECT `id` FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.1');
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `cluster_id` AS `cluster_id`,`id` AS `component_id` , 1 AS `component_type_code` , 'INPUT' AS `type`,1 AS `required`,'spark.sql.hive.metastore.jars' AS `key`,'./__spark_libs__/*' AS `value`,null AS `values`,'deploymode$perjob' AS `dependencyKey`,null AS `dependencyValue`,null AS `desc`,now() AS `gmt_create`, now() AS `gmt_modified`,0 AS `is_deleted`
FROM console_component  WHERE `component_type_code` = 1  and id IN (SELECT `id` FROM console_component  WHERE `component_type_code` = 1 and `version_name` = '2.1');
