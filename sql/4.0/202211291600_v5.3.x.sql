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