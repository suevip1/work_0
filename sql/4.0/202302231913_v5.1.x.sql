update console_component_config
set value = 1000
where `key` = 'dtscript.container.heartbeat.interval'
  and component_type_code = 3;