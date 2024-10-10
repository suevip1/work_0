-- remove index, ref:http://zenpms.dtstack.cn/zentao/bug-view-60109.html
-- 全表扫描性能更佳
set @x := (select count(*) from information_schema.statistics where table_name = 'schedule_dict' and index_name = 'index_type' and table_schema = database());
set @sql := if( @x > 0, 'drop index index_type on schedule_dict;', 'select ''schedule_dict.index_type not exists, so skip.''');
PREPARE stmt FROM @sql;
EXECUTE stmt;