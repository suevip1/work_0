DELETE FROM console_component_config WHERE `key` = 'execution.checkpointing.externalized-checkpoint-retention' AND
component_type_code = 0 AND dependencyKey IN ('deploymode$perjob', 'deploymode$session') AND is_deleted = 0;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT  cluster_id ,component_id, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION',null,'deploymode$perjob',null, null, now(), now(),0
from console_component_config
where component_type_code = 0
  and dependencykey = 'deploymode$perjob'
  and is_deleted = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
  UNION SELECT dict_value from schedule_dict where dict_code = 'typename_mapping' AND dict_name like '%flink112')
group by component_id,dependencykey;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT  cluster_id ,component_id, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION',null,'deploymode$session',null, null, now(), now(),0
from console_component_config
where component_type_code = 0
  and dependencykey = 'deploymode$session'
  and is_deleted = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
  UNION SELECT dict_value from schedule_dict where dict_code = 'typename_mapping' AND dict_name like '%flink112')
group by component_id,dependencykey;

DELETE FROM schedule_dict where dict_code = 'tips' and dict_name = 'execution.checkpointing.externalized-checkpoint-retention' and dict_desc = '0';

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'execution.checkpointing.externalized-checkpoint-retention', 'checkpoint保存策略外部化配置', '0', 25, 10, 'STRING', '容错和checkpointing', 0, now(),now(), 0);