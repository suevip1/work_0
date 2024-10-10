-- 修正参数: pipeline.operator-chainning --> pipeline.operator-chaining
update environment_param_template set params = REPLACE(params,'pipeline.operator-chainning','pipeline.operator-chaining')
  where task_type = 2 and task_version IN (1.10,1.12) and instr(params, 'pipeline.operator-chainning') > 0;

update schedule_task_shade set task_params = replace(task_params, 'pipeline.operator-chainning','pipeline.operator-chaining'),
   extra_info = replace(extra_info, 'pipeline.operator-chainning','pipeline.operator-chaining')
    where task_type = 2 and app_type = 1 and INSTR(task_params, 'pipeline.operator-chainning')> 0;

-- 控制台新增参数: pipeline.operator-chaining
DELETE from console_component_config where component_id = -115 and `key` = 'pipeline.operator-chaining' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
    (-2, -115, 0, 'INPUT', 0, 'pipeline.operator-chaining', 'false', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'pipeline.operator-chaining' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','pipeline.operator-chaining',' Flink 会将运行图中相同并行度的算子尽可能的绑在一起，避免数据上下游传输的序列化、反序列化额外开销。但是数据同步中，因为涉及两端数据源都是 IO，因此设置为 false 会使同步任务速度更快。', 25, '高级', '0', 8);

