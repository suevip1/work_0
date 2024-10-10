-- flink112 的 部分配置信息在历史集群没有加, ref: http://zenpms.dtstack.cn/zentao/bug-view-59262.html
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'restart-strategy', 'failurerate',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy' and g.cluster_id !=-2)
                            and t.version_name = '1.12';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '1s',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy.failure-rate.delay' and g.cluster_id !=-2)
                            and t.version_name = '1.12';


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-interval', '1 min',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy.failure-rate.failure-rate-interval' and g.cluster_id !=-2)
                            and t.version_name = '1.12';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '2',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy.failure-rate.max-failures-per-interval' and g.cluster_id !=-2)
                            and t.version_name = '1.12';