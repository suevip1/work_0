-- console_component 唯一索引变更, ref: http://zenpms.dtstack.cn/zentao/bug-view-56404.html, add by qiuyun
-- 1 找到脏数据对应的 id
create temporary table t_dirty_component_id as
  select id from console_component where (cluster_id, component_type_code, hadoop_version)
    in (
        -- 全部重复记录
      select cluster_id, component_type_code, hadoop_version
         from console_component t where (cluster_id, component_type_code, hadoop_version) in
            (select cluster_id, component_type_code, hadoop_version from `console_component` group by cluster_id, component_type_code, hadoop_version having count(*) > 1)
       )
     and id not in (
        -- 需要保留的重复记录中的唯一一条
        select max(id) from console_component group by cluster_id, component_type_code, hadoop_version having count(*) > 1
);
create index idx_id on t_dirty_component_id(id);
/*
  -- 用于比对哪些脏数据 id 需要被删除(测试同学可以使用该语句比对)
  select cluster_id, component_type_code, hadoop_version
         from console_component t where (cluster_id, component_type_code, hadoop_version) in
            (select cluster_id, component_type_code, hadoop_version from `console_component` group by cluster_id, component_type_code, hadoop_version having count(*) > 1)
*/

-- 2 删除脏数据对应的配置信息
delete from console_component_config where component_id in (SELECT id from t_dirty_component_id);

-- 3 删除脏数据对应的组件信息
delete from console_component where id in (SELECT id from t_dirty_component_id);

-- 4 删除旧索引
set @x := (select count(*) from information_schema.statistics where table_name = 'console_component' and index_name = 'index_component' and table_schema = database());
set @sql := if( @x > 0, 'drop index index_component on console_component;', 'select ''index_component not exists, so skip.''');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- 5 增加新的唯一索引
set @x := (select count(*) from information_schema.statistics where table_name = 'console_component' and index_name = 'uk_cluster_type_version' and table_schema = database());
set @sql := if( @x > 0, 'select ''uk_cluster_type_version exists, so skip''', 'ALTER TABLE `console_component` ADD UNIQUE INDEX `uk_cluster_type_version`(`cluster_id`, `component_type_code`, `hadoop_version`) USING BTREE;');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- 6 清理使用的临时表
drop table t_dirty_component_id;