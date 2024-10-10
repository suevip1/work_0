-- sparkSqlRealTimeLog, modify Prometheus gateway value from '5' to '2'
update console_component_config set value = '2' where `key` = 'metrics.prometheus.sink.pushgateway.period'
 and component_type_code = 1
 and value = '5' and is_deleted = 0;