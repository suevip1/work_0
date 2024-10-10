-- flink session pluginsDistDir参数移除，只处理模版数据
update console_component_config
set is_deleted = 1
where component_id in(
select dict_value from schedule_dict where dict_code = 'typename_mapping'
and dict_name like '%flink%') and `key`= 'pluginsDistDir' and dependencyKey = 'deploymode$session';