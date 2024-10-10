-- 5.3版本新增hudi 选项
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select t.cluster_id,
       t.id,
       t.component_type_code,
       '',
       1,
       'hudi',
       'hudi',
       null,
       'deploymode$perjob$spark.dataLake.type',
       null,
       null,
       now(),
       now(),
       0
from (select cluster_id,
             id,
             component_type_code
      from console_component
      where component_type_code = 1
        and is_deleted = 0
        AND hadoop_version = 320
      union
      select -2, -132, 1) t
where t.id not in
      (select g.component_id
       from console_component_config g
       where g.component_type_code = 1
         and g.`key` = 'hudi'
         and g.`dependencyKey` = 'deploymode$perjob$spark.dataLake.type'
      )
;
