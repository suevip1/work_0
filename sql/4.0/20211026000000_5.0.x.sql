ALTER TABLE schedule_job_job ADD COLUMN   `rely_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0 默认值 正常依赖 1 去依赖';

update console_component_config SET `key` = 'slotmanager.number-of-slots.max'
where `key` = 'flinkSessionSlotCount' and  dependencyKey = 'deploymode$session'
  and component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like 'yarn%-flink112');