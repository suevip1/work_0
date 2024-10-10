update console_component_config SET value = '/data/insight_plugin1.12/chunjun_lib'
where `key` = 'remoteFlinkLibDir'
  and component_type_code = 0
  and `value` = '/data/insight_plugin1.12/flink_lib'
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin1.12/chunjun_lib'
where `key` = 'flinkLibDir'
  and component_type_code = 0
  and `value` = '/data/insight_plugin1.12/flink_lib'
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin1.12/chunjunplugin'
where `key` = 'flinkxDistDir'
  and component_type_code = 0
  and value = '/data/insight_plugin1.12/flinkxplugin'
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');

update console_component_config SET value = '/data/insight_plugin1.12/chunjunplugin'
where `key` = 'remoteFlinkxDistDir'
  and component_type_code = 0
  and value = '/data/insight_plugin1.12/flinkxplugin'
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');