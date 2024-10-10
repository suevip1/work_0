UPDATE console_component_config SET value = '/data/insight_plugin1.16/flinkSql/flinkSqlPlugin_116'
WHERE `key` IN ('remoteFlinkxDistDir','flinkxDistDir')
  AND component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' AND dict_name LIKE '%-flink116')
  AND cluster_id = -2;

UPDATE console_component_config set `value` = '/data/insight_plugin1.16/flinkSql/flinkSqlPlugin_116'
where component_id in (
select id
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16') and `key` IN ('remoteFlinkxDistDir','flinkxDistDir');



UPDATE console_component_config SET value = '/data/insight_plugin1.16/flinkSql/flinkSqlLib_116'
WHERE `key` IN ('remoteFlinkLibDir','flinkLibDir')
  AND component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' AND dict_name LIKE '%-flink116')
  AND cluster_id = -2;

UPDATE console_component_config set `value` = '/data/insight_plugin1.16/flinkSql/flinkSqlLib_116'
where component_id in (
    select id
    from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                                and t.version_name = '1.16') and `key` IN ('remoteFlinkLibDir','flinkLibDir');
