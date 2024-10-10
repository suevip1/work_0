delete
from console_component_config
where component_id = -107
  and `key` = 'sparkPythonExtLibPath';

update console_component_config
set value = 'file:///opt/spark-sql-proxy.jar'
where component_id = -107
  and `key` = 'sparkSqlProxyPath';