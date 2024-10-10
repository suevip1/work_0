-- 逻辑删除table_name前缀为select_sql_temp_table_的lineage_table_table_unique_key_ref记录
update lineage_table_table_unique_key_ref set is_deleted = 1 where lineage_table_table_id in(
    select id from lineage_table_table where input_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     ) or result_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     )
);

-- 逻辑删除table_name前缀为select_sql_temp_table_的lineage_table_table记录
update lineage_table_table set is_deleted = 1 where  input_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     ) or result_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     );

-- 逻辑删除table_name前缀为select_sql_temp_table_的lineage_column_column_unique_key_ref记录
update lineage_column_column_unique_key_ref set is_deleted = 1 where lineage_column_column_id in(
    select id from lineage_column_column where input_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     ) or result_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     )
);

-- 逻辑删除table_name前缀为select_sql_temp_table_的lineage_table_table记录
update lineage_column_column set is_deleted = 1 where  input_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     ) or result_table_id in(
        select id from lineage_data_set_info where table_name like "select_sql_temp_table%"
     );



-- 逻辑删除table_name前缀为select_sql_temp_table_的记录
update lineage_data_set_info set is_deleted = 1 where table_name like "select_sql_temp_table%";