-- ref: http://zenpms.dtstack.cn/zentao/bug-view-80321.html
-- 修改历史集群的 spark.dataLake.type 为下拉框，增加选项 none,iceberg,hudi

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'none', 'none',null,'deploymode$perjob$spark.dataLake.type',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 AND t.hadoop_version = 320
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 1 and g.`key` = 'none'
                                                                   and g.value = 'none' and g.`dependencyKey` = 'deploymode$perjob$spark.dataLake.type' and g.cluster_id !=-2
                                                                 );


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'iceberg', 'iceberg',null,'deploymode$perjob$spark.dataLake.type',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 AND t.hadoop_version = 320
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 1 and g.`key` = 'iceberg'
                                                                   and g.value = 'iceberg' and g.`dependencyKey` = 'deploymode$perjob$spark.dataLake.type' and g.cluster_id !=-2
                                );

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'hudi', 'hudi',null,'deploymode$perjob$spark.dataLake.type',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 AND t.hadoop_version = 320
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 1 and g.`key` = 'hudi'
                                                                   and g.value = 'hudi' and g.`dependencyKey` = 'deploymode$perjob$spark.dataLake.type' and g.cluster_id !=-2
                                );


UPDATE console_component_config SET  `type` = 'SELECT'
WHERE cluster_id != -2
  AND `type` = 'INPUT'
  AND `key`  = 'spark.dataLake.type'
  AND component_id in(
    SELECT id FROM console_component WHERE component_type_code = 1 AND hadoop_version = 320
);

-- 修改 spark.dataLake.type 的 tips 提示
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.dataLake.type' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.dataLake.type','Spark 对接的数据湖插件类型，可选项为 ① none：不支持数据湖；② iceberg：支持使用 iceberg 表结构处理数据；③ hudi：支持使用 hudi 表结构处理数据；',25,'DataLake','1', 1);


-- 更正历史下拉框选项模板的 component_type_code
UPDATE console_component_config SET component_type_code = 1
WHERE cluster_id = -2
AND  dependencyKey = 'deploymode$perjob$spark.dataLake.type'
AND  component_type_code = 0;


-- 历史集群，如果 spark.dataLake.type = none, 删除 spark.sql.catalog.spark_catalog.type 和 spark.sql.catalog.spark_catalog 配置项
DELETE FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` in ('spark.sql.catalog.spark_catalog.type','spark.sql.catalog.spark_catalog')
  AND component_id in (
    SELECT component_id FROM (
                                 SELECT component_id FROM console_component_config WHERE cluster_id != -2 AND component_type_code = 1 AND `key` = 'spark.dataLake.type' AND value = 'none'
                             ) AS temp
);
