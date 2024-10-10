DELETE FROM console_component_config WHERE `key` = 'pluginsDistDir';
DELETE FROM schedule_dict WHERE dict_name = 'pluginsDistDir';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       0,
       'INPUT',
       0,
       'pluginsDistDir',
       '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs',
       null,
       'deploymode$session',
       null,
       null,
       now(),
       now(),
       0
from console_component_config
where component_type_code = 0
  and dependencykey = 'deploymode$session'
  and is_deleted = 0
  AND component_id in (SELECT id
                       from console_component
                       where component_type_code = 0
                       UNION
                       SELECT dict_value
                       from schedule_dict
                       where dict_code = 'typename_mapping'
                         AND dict_name like '%flink%')
group by component_id;



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       0,
       'INPUT',
       0,
       'pluginsDistDir',
       '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
from console_component_config
where component_type_code = 0
  and dependencykey = 'deploymode$perjob'
  and is_deleted = 0
  AND component_id in (SELECT id
                       from console_component
                       where component_type_code = 0
                       UNION
                       SELECT dict_value
                       from schedule_dict
                       where dict_code = 'typename_mapping'
                         AND dict_name like '%flink%')
group by component_id;


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'pluginsDistDir', 'EnginePlugins路径', '0', 25, 22, 'STRING', '数栈平台参数', 0, now(),now(), 0);