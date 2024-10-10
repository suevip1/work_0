-- ref: http://zenpms.dtstack.cn/zentao/story-view-10559.html
ALTER TABLE console_component_config ADD COLUMN cascade_key varchar(256) DEFAULT NULL COMMENT '级联的键';
ALTER TABLE `console_component_config` ADD COLUMN cascade_value varchar(256) DEFAULT NULL COMMENT '级联的值';

# spark3.2: spark.dataLake.type 增加 hudi 下拉框选项
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -132 AND dependencyKey = 'deploymode$perjob$spark.dataLake.type' AND `key` = 'hudi';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -132, 0, '', 1, 'hudi', 'hudi', null, 'deploymode$perjob$spark.dataLake.type', null, null, now(), now(), 0);

# spark.sql.catalog.spark_catalog 增加级联的 key 和 value
UPDATE console_component_config SET cascade_key = 'spark.dataLake.type', cascade_value = 'iceberg' WHERE  cluster_id = -2 AND component_id = -132 AND `key` = 'spark.sql.catalog.spark_catalog' AND value = 'org.apache.iceberg.spark.SparkSessionCatalog' AND cascade_key is null;
# spark.sql.catalog.spark_catalog.type 增加级联的 key 和 value
UPDATE console_component_config SET cascade_key = 'spark.dataLake.type', cascade_value = 'iceberg' WHERE  cluster_id = -2 AND component_id = -132 AND `key` = 'spark.sql.catalog.spark_catalog.type' AND value = 'hive' AND cascade_key is null;

# hudi 增加 spark.sql.catalog.spark_catalog 和 spark.sql.catalog.spark_catalog.type 的级联选项
DELETE FROM console_component_config WHERE component_id = -132 AND cluster_id = -2 AND `key` ='spark.sql.catalog.spark_catalog' AND `value` = 'org.apache.spark.sql.hudi.catalog.HoodieCatalog';
INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `cascade_key`,
                                        `cascade_value`,
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 0, 'spark.sql.catalog.spark_catalog', 'org.apache.spark.sql.hudi.catalog.HoodieCatalog', NULL, 'deploymode$perjob', NULL,'spark.dataLake.type','hudi', NULL);

DELETE FROM console_component_config WHERE component_id = -132 AND cluster_id = -2 AND `key` ='spark.sql.catalog.spark_catalog.type' AND `value` = 'hudi';
INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `cascade_key`,
                                        `cascade_value`,
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 0, 'spark.sql.catalog.spark_catalog.type', 'hudi', NULL, 'deploymode$perjob', NULL,'spark.dataLake.type','hudi', NULL);

# iceberg 增加 spark.sql.iceberg.handle-timestamp-without-timezone 的级联选项
DELETE FROM console_component_config WHERE component_id = -132 AND cluster_id = -2 AND `key` ='spark.sql.iceberg.handle-timestamp-without-timezone';
INSERT INTO console_component_config (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `cascade_key`,
                                        `cascade_value`,
                                        `desc`)
VALUES (-2, -132, 1, 'SWITCH', 1, 'spark.sql.iceberg.handle-timestamp-without-timezone', 'true', NULL, 'deploymode$perjob', NULL,'spark.dataLake.type','iceberg', NULL);

# spark.sql.iceberg.handle-timestamp-without-timezone 增加 tips
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.iceberg.handle-timestamp-without-timezone' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.iceberg.handle-timestamp-without-timezone','注意：该参数会默认以utc时区解析时间戳，所以时间戳中带有时区的iceberg任务解析时可能会出现问题',25,'DataLake','1', 4);

# 历史集群 spark320 增加 spark.sql.iceberg.handle-timestamp-without-timezone
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue,`cascade_key`,
                                      `cascade_value`,`desc`, gmt_create, gmt_modified,
                                      is_deleted)
select t.cluster_id ,t.id, t.component_type_code, 'SWITCH', 1, 'spark.sql.iceberg.handle-timestamp-without-timezone', 'true',null,'deploymode$perjob',null,'spark.dataLake.type','iceberg', null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy' and g.cluster_id !=-2)
                            and t.version_name = '1.12';

