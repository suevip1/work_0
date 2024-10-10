update console_component_config SET value = '/data/insight_plugin1.12/chunjun_lib'
where `key` = 'remoteFlinkLibDir'
  and component_type_code = 0
  and `value` = '/data/insight_plugin1.12/flink_lib'
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
                      UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin1.12/chunjun_lib'
where `key` = 'flinkLibDir'
  and component_type_code = 0
  and `value` = '/data/insight_plugin1.12/flink_lib'
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
                      UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin1.12/chunjunplugin'
where `key` = 'flinkxDistDir'
  and component_type_code = 0
  and value = '/data/insight_plugin1.12/flinkxplugin'
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
                      UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');

update console_component_config SET value = '/data/insight_plugin1.12/chunjunplugin'
where `key` = 'remoteFlinkxDistDir'
  and component_type_code = 0
  and value = '/data/insight_plugin1.12/flinkxplugin'
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
                      UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');

update console_component_config
SET `key` = 'spark.python.extLib.path'
where `key` = 'sparkPythonExtLibPath'
  and component_type_code = 1;

update console_component_config
SET `key` = 'yarn.acceptor.taskNumber'
where `key` = 'yarnAccepterTaskNumber'
  and component_type_code = 1;

update console_component_config
SET `is_deleted` = 1
where `key` = 'spark.yarn.security.credentials.hive.enabled'
  and component_type_code = 1;

update console_component_config
SET `is_deleted` = 1
where `key` = 'sparkSqlProxyPath'
  and component_type_code = 1;