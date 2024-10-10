-- supporting spark faster, ref: http://zenpms.dtstack.cn/zentao/task-view-7300.html
DELETE FROM console_component_config WHERE component_id in (-108, -130, -132)
  and `key` = 'spark.resources.dir';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -108, 1, 'INPUT', 1, 'spark.resources.dir', 'hdfs:///dtInsight/spark', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
  (-2, -130, 1, 'INPUT', 1, 'spark.resources.dir', 'hdfs:///dtInsight/spark', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0),
  (-2, -132, 1, 'INPUT', 1, 'spark.resources.dir', 'hdfs:///dtInsight/spark', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0);

-- 处理历史集群
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'spark.resources.dir', 'hdfs:///dtInsight/spark',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 1 and g.key = 'spark.resources.dir' and g.cluster_id !=-2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.resources.dir' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.resources.dir','远程存储系统上hadoopConf,kerberos,sparkSqlProxy文件存放位置',25,'主要','1', 8);

-- 路径中的 hdfs://ns1/ 改为 hdfs:///
UPDATE `console_component_config` SET `value` = REPLACE(`value`, '://ns1', '://') WHERE `key` IN
  ('spark.eventLog.dir','sparkPythonExtLibPath','sparkSqlProxyPath','sparkYarnArchive')
AND component_id in (-108, -130, -132);