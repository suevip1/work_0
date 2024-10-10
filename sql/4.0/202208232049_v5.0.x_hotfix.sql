-- modify restart-strategy of yarn-hdfs-flink112, ref:http://zenpms.dtstack.cn/zentao/bug-view-63673.html
-- 1) 修改所有历史集群、模板集群的 'restart-strategy' 的值
update console_component_config set `value` = 'none' where `key` = 'restart-strategy';
-- 2) flink session 模式新增 'restart-strategy' 参数
delete from console_component_config where component_id = -115 and `key` = 'restart-strategy' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
 VALUES
(-2, -115, 0, 'INPUT', 0, 'restart-strategy', 'none', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);
-- 3) 历史集群 session 模式新增 'restart-strategy' 参数
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'restart-strategy', 'none',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink on yarn 组件 session 模式下 没有 'restart-strategy'
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'restart-strategy' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

