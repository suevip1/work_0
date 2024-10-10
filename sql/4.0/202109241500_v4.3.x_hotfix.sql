update console_component_config SET `key` = 'remoteFlinkLibDir',value = '/data/insight_plugin112/flink112_lib'
where `key` = 'remoteFlinkxLibDir'
  and component_type_code = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '112'
UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin112/flink112_lib'
where `key` = 'flinkLibDir'
  and component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '/data/insight_plugin112/flinkxplugin'
where `key` = 'flinkxDistDir'
  and component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');

update console_component_config SET value = '/data/insight_plugin112/flinkxplugin'
where `key` = 'remoteFlinkxDistDir'
  and component_type_code = 0
  AND component_id in (SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112');


update console_component_config SET value = '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=300m -Dfile.encoding=UTF-8 -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl'
where `key` = 'env.java.opts' and dependencyKey = 'deploymode$session'
  AND component_type_code = 0
  AND component_id in (SELECT id from console_component where component_type_code = 0 and hadoop_version = '110'
                       UNION SELECT dict_value FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink110');


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2,dict_value,0,'INPUT', 0, 'restart-strategy', 'failurerate', null, 'deploymode$perjob', null, null, now(),now(), 0 FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112' GROUP BY dict_value;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2,dict_value,0,'INPUT', 0, 'restart-strategy.failure-rate.delay', '1s', null, 'deploymode$perjob', null, null, now(),now(), 0 FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112' GROUP BY dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2,dict_value,0,'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-intervalattempts', '1 min', null, 'deploymode$perjob', null, null, now(),now(), 0 FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112' GROUP BY dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2,dict_value,0,'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '2', null, 'deploymode$perjob', null, null, now(),now(), 0 FROM schedule_dict WHERE dict_code = 'typename_mapping' and dict_name like '%-flink112' GROUP BY dict_value;
