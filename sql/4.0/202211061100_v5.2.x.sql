-- spark 适配
DELETE FROM console_component_config WHERE `key` IN ('spark.local.spark.home') AND component_type_code = 1 ;


-- 历史集群添加
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type,
                                      required, `key`, value, `values`, dependencyKey,
                                      dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select cluster_id, id, 1, 'INPUT', 1, 'spark.local.spark.home', replace('/opt/dtstack/DTSparkX.X/spark_pkg','X.X',version_name), null, 'deploymode$perjob', null, null, now(),now(), 0
from console_component where component_type_code = 1 and is_deleted = 0 and cluster_id is not null and version_name in ('2.4','3.2') ;


-- 模版添加
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type,
                                      required, `key`, value, `values`, dependencyKey,
                                      dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
    value(-2, -130, 1, 'INPUT', 1, 'spark.local.spark.home', '/opt/dtstack/DTSpark2.4/spark_pkg', null, 'deploymode$perjob', null, null, now(),now(), 0),
    (-2, -132, 1, 'INPUT', 1, 'spark.local.spark.home', '/opt/dtstack/DTSpark3.2/spark_pkg', null, 'deploymode$perjob', null, null, now(),now(), 0);



DELETE FROM schedule_dict WHERE dict_code = 'tips' AND dict_desc = 1 AND `dict_name` IN ('spark.local.spark.home') LIMIT 1;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'spark.local.spark.home', 'engine所在节点的DTSpark包所在位置', '1', 25, 3, 'STRING', '主要', 0, '2022-09-01 13:56:21', '2022-09-01 13:56:21', 0);
