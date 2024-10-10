update console_component_config SET `key` = 'slotmanager.number-of-slots.max' where `key` = 'flinkSessionSlotCount' and  dependencyKey = 'deploymode$session' and component_type_code = 0 and component_id IN (SELECT id FROM console_component WHERE component_type_code = 0 and version_name = 1.12);

INSERT IGNORE INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) select cluster_id, component_id,'0' AS `component_type_code`, 'INPUT' AS `type` , '1' AS  `required` , 'slotmanager.number-of-slots.max' AS `key`, '10' AS `value` , NULL AS `values`, 'deploymode$session' AS `dependencyKey`, NULL AS `dependencyValue`, NULL AS `desc`, NOW() AS `gmt_create`, NOW() AS `gmt_modified`, '0' AS `is_deleted` FROM console_component_config WHERE component_id NOT IN (SELECT component_id FROM console_component_config WHERE `key` = 'slotmanager.number-of-slots.max' AND component_id IN (  SELECT id FROM console_component WHERE component_type_code = 0 and version_name = 1.12)) AND component_id IN  (SELECT id FROM console_component where component_type_code = 0 and version_name = 1.12) GROUP BY cluster_id;