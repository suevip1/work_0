-- flink 1.10 & 1.12 session模式的yarn.application-attempts参数值变更

-- 更新模板
update console_component_config set `value` = 1 where component_id in(
select dict_value from schedule_dict where dict_code = 'typename_mapping'
and (dict_name like '%flink112' or dict_name like '%flink110') and dict_name like 'yarn%')
and `value` = 3 and `key` = 'yarn.application-attempts' and   dependencyKey =  'deploymode$session';

-- 更新历史集群
update console_component_config
set `value` = 1
where component_id in(
select id from console_component  where component_type_code = 0
and hadoop_version in('112','110')) and `key` = 'yarn.application-attempts'
and `value` = 3  and `key` = 'yarn.application-attempts'
and dependencyKey = 'deploymode$session';

