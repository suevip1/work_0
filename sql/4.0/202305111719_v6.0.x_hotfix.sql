-- 变更集群唯一索引为普通索引
-- 删除旧的唯一索引
set @x := (select count(*) from information_schema.statistics where table_name = 'console_cluster' and index_name = 'idx' and table_schema = database());
set @sql := if( @x > 0, 'drop index idx on console_cluster;', 'select ''idx not exists, so skip.''');
PREPARE stmt FROM @sql;
EXECUTE stmt;

-- 增加新的普通索引
set @x := (select count(*) from information_schema.statistics where table_name = 'console_cluster' and index_name = 'idx_name' and table_schema = database());
set @sql := if( @x > 0, 'select ''idx_name exists, so skip''', 'ALTER TABLE console_cluster ADD INDEX idx_name(cluster_name) USING BTREE;');
PREPARE stmt FROM @sql;
EXECUTE stmt;