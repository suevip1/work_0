DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.client.leader.election.type' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.client.leader.election.type', '', '1', '25', '9', 'STRING', '主要', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.driver.userClassPathFirst' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.driver.userClassPathFirst', '', '1', '25', '10', 'STRING', '主要', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.python.extLib.path' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.python.extLib.path', '远程存储系统上pyspark.zip和py4j-0.10.7-src.zip的路径注：pyspark.zip和py4j-0.10.7-src.zip在$SPARK_HOME/python/lib路径下获取', '1', '25', '11', 'STRING', '主要', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'yarn.acceptor.taskNumber' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'yarn.acceptor.taskNumber', '允许yarn上同时存在状态为accepter的任务数量，当达到这个值后会禁止任务提交', '1', '25', '12', 'STRING', '主要', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.local.spark.home' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.local.spark.home', 'engine节点spark_home目录', '1', '25', '13', 'STRING', '主要', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.yarn.archive' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.yarn.archive', '远程存储系统上spark jars的路径', '1', '25', '14', 'STRING', '主要', '0', NOW(), NOW(), '0');


DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'Zookeeper' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'Zookeeper', NULL, '1', '25', '111', 'STRING', '', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.zookeeper.url' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.zookeeper.url', '', '1', '25', '1', 'STRING', 'Zookeeper', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.zookeeper.namespace' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.zookeeper.namespace', '', '1', '25', '2', 'STRING', 'Zookeeper', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'Spark ThriftServer' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'Spark ThriftServer', NULL, '1', '25', '112', 'STRING', '', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.thrift.server.start.port' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.thrift.server.start.port', '', '1', '25', '1', 'STRING', 'Spark ThriftServer', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.thrift.server.port.retries' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.thrift.server.port.retries', '', '1', '25', '2', 'STRING', 'Spark ThriftServer', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.thrift.server.min.worker.threads' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.thrift.server.min.worker.threads', '', '1', '25', '3', 'STRING', 'Spark ThriftServer', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.thrift.server.max.worker.threads' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.thrift.server.max.worker.threads', '', '1', '25', '4', 'STRING', 'Spark ThriftServer', '0', NOW(), NOW(), '0');

DELETE FROM schedule_dict WHERE `dict_code` = 'tips' AND dict_name = 'spark.sql.hive.metastore.jars' AND dict_desc = '1' AND `type` = 25;
INSERT INTO `schedule_dict` ( `dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('tips', 'spark.sql.hive.metastore.jars', '', '1', '25', '6', 'STRING', 'HiveMetastore', '0', NOW(), NOW(), '0');

-- 2.4 3.2 移除
DELETE FROM console_component_config WHERE `key` IN ('spark.yarn.archive','spark.python.extLib.path')
                                       AND component_id in (select id
                                                            from console_component cc
                                                            where component_type_code = 1
                                                              and hadoop_version IN ('240', '320')
                                                            union all
                                                            select -130
                                                            union all
                                                            select -132);


