-- http://zenpms.dtstack.cn/zentao/story-view-9804.html
-- 1. 修改 Flink 中的高级的 classloader.resolve-order 改为下拉框
-- 修改 flink1.12 模板
-- -115:yarn2-hdfs2-flink112, yarn3-hdfs3-flink112, yarn2tbds-hdfs2tbds-flink112, yarn3hw-hdfs3hw-flink112
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -115 AND dependencyKey = 'deploymode$session$classloader.resolve-order';
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -115 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -115, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -115 AND `key` = 'classloader.resolve-order';
UPDATE console_component_config SET `value` = 'child-first-cache' WHERE cluster_id = -2 and component_id = -115 AND `key` = 'classloader.resolve-order' AND `dependencyKey` = 'deploymode$session';

-- -118: k8s-hdfs2-flink112, k8s-hdfs3-flink112
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -118 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -118, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -118 AND `key` = 'classloader.resolve-order';

-- -211: k8s-s3-flink112
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -211 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -211, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -211, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -211, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -211 AND `key` = 'classloader.resolve-order';

-- 修改 flink1.10 模板
-- -109:yarn3-hdfs3-flink110, yarn2-hdfs2-flink110, yarn2tbds-hdfs2tbds-flink110, yarn3hw-hdfs3hw-flink110
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -109 AND dependencyKey = 'deploymode$session$classloader.resolve-order';
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -109 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -109, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -109 AND `key` = 'classloader.resolve-order';

-- -102:k8s-hdfs2-flink110
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -102 AND dependencyKey = 'deploymode$session$classloader.resolve-order';
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -102 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -102, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -102 AND `key` = 'classloader.resolve-order';


-- -136:k8s-nfs-flink110
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -136 AND dependencyKey = 'deploymode$session$classloader.resolve-order';
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -136 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -136, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE  cluster_id = -2 and component_id = -136 AND `key` = 'classloader.resolve-order';


-- 修改 flink1.80 模板
-- -110:yarn2-hdfs2-flink180,yarn3-hdfs3-flink180
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -110 AND dependencyKey = 'deploymode$session$classloader.resolve-order';
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -110 AND dependencyKey = 'deploymode$perjob$classloader.resolve-order';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, now(), now(), 0);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -110, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -110 AND `key` = 'classloader.resolve-order';


-- 2. 修改 Spark3.2 spark.dataLake.type 改为下拉框
DELETE FROM console_component_config WHERE cluster_id = -2 AND component_id = -132 AND dependencyKey = 'deploymode$perjob$spark.dataLake.type';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -132, 0, '', 1, 'none', 'none', null, 'deploymode$perjob$spark.dataLake.type', null, null, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -132, 0, '', 1, 'iceberg', 'iceberg', null, 'deploymode$perjob$spark.dataLake.type', null, null, now(), now(), 0);

UPDATE console_component_config SET `type` = 'SELECT' WHERE cluster_id = -2 and component_id = -132 AND `key` = 'spark.dataLake.type';
-- 修改 spark.dataLake.type 的 tips 提示
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.dataLake.type' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.dataLake.type','Spark 对接的数据湖插件类型，可选项为 ① none：不支持数据湖；② iceberg：支持使用 iceberg 表结构处理数据',25,'DataLake','1', 1);


-- 3. spark.hadoop.hive.metastore.sasl.enabled 改为开关形式，默认关闭，
UPDATE console_component_config SET `type` = 'SWITCH' WHERE `key` = 'spark.hadoop.hive.metastore.sasl.enabled' and cluster_id = -2;

-- 4. hadoop.proxy.enable 改为开关形式，默认关闭，hover时显示字段说明改为「是否开启hadoop代理模式」
UPDATE console_component_config SET `type` = 'SWITCH' WHERE `key` = 'hadoop.proxy.enable' AND cluster_id = -2;

DELETE FROM schedule_dict WHERE dict_code = 'jdbc_url_tip' AND dict_name ='Common' AND depend_name = 'hadoop.proxy.enable';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Common', '是否开启hadoop代理模式', NULL, 10, 0, 'STRING', 'hadoop.proxy.enable', 0, now(), now(), 0);



-- http://zenpms.dtstack.cn/zentao/story-view-9851.html
-- 5. Spark 3.2 增加 AQE 相关参数
-- 增加 AQE 分组
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'AQE' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','AQE',25,'1', 92);

DELETE FROM console_component_config WHERE component_id = -132 AND `key` in ('spark.sql.adaptive.advisoryPartitionSizeInBytes','spark.sql.adaptive.coalescePartitions.minPartitionSize','spark.sql.adaptive.coalescePartitions.initialPartitionNum','spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes','spark.sql.adaptive.skewJoin.skewedPartitionFactor');
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
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 0, 'spark.sql.adaptive.advisoryPartitionSizeInBytes', '64MB', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.adaptive.coalescePartitions.minPartitionSize', '1MB', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.adaptive.coalescePartitions.initialPartitionNum', '200', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes', '256MB', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.adaptive.skewJoin.skewedPartitionFactor', '5', NULL, 'deploymode$perjob', NULL, NULL);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.adaptive.advisoryPartitionSizeInBytes' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.adaptive.advisoryPartitionSizeInBytes','AQE 优化期间 shuffle 分区的建议大小',25,'AQE','1', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.adaptive.coalescePartitions.minPartitionSize' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.adaptive.coalescePartitions.minPartitionSize','AQE 优化 shuffle 分区合并的最小值',25,'AQE','1', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.adaptive.coalescePartitions.initialPartitionNum' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.adaptive.coalescePartitions.initialPartitionNum','合并前 shuffle 分区的初始数',25,'AQE','1', 11);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes','倾斜分区阈值大小',25,'AQE','1', 12);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.adaptive.skewJoin.skewedPartitionFactor' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.adaptive.skewJoin.skewedPartitionFactor','倾斜分区因子，如果一个分区的大小大于这个因子乘以分区中位数并且也大于''spark.sql.adaptive.skewJoin.skewedPartitionThresholdInBytes''，则该分区被认为是倾斜的',25,'AQE','1', 13);


-- 6. 添加 Spark 事务表参数，只支持 yarn3-hdfs3-spark320，以拓展参数的形式添加
UPDATE schedule_dict SET dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1003",
                "3.2":"-3001"
            }
        ]
    }
}' WHERE type = 15 AND depend_name = 'yarn' AND dict_name in ('BMR 2.x','HDP 3.x');

-- Apache Hadoop 3.x
-- HDP 3.0.x
-- CDH 6.0.x
-- CDH 6.1.x
-- CDH 6.2.x
-- CDP 7.x
DELETE FROM  `schedule_dict` WHERE type = 15 AND depend_name = 'yarn' AND dict_name in ('Apache Hadoop 3.x','HDP 3.0.x','CDH 6.0.x','CDH 6.1.x','CDH 6.2.x','CDP 7.x');

INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'Apache Hadoop 3.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');

INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'HDP 3.0.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');

INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'CDH 6.0.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');

INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'CDH 6.1.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');

INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'CDH 6.2.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');


INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ('extra_version_template', 'CDP 7.x', '{
    "HDFS":{
        "SPARK":[
            {
                "3.2":"-3001"
            }
        ]
    }
}', NULL, '15', '1', 'STRING', 'YARN', '0', now(), now(), '0');

DELETE FROM console_component_config WHERE component_id = -3001 AND `key` =  'spark.hive.acid.enabled';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -3001, 1, 'SWITCH', 0, 'spark.hive.acid.enabled', 'false', null, 'deploymode$perjob', null, null, now(),now(), 0);

-- 增加 Hive分组
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'Hive' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','Hive',25,'1', 91);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hive.acid.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hive.acid.enabled','开启支持 Hive 事务表机制',25,'Hive','1', 1);


-- 7. spark.hadoop.hive.metastore.uris，spark.zookeeper.url 历史集群修改为非必填
UPDATE console_component_config SET required = 0 WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.metastore.uris';
UPDATE console_component_config SET required = 0 WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.zookeeper.url';