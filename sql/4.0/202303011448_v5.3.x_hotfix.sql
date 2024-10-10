-- 类加载隔离，参数补充
-- 1) flink 1.12 Session 新增参数
DELETE from console_component_config where component_id = -115 and `key` = 'classloader.check-leaked-classloader' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
    (-2, -115, 0, 'INPUT', 0, 'classloader.check-leaked-classloader', 'false', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);
-- 刷历史集群
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'classloader.check-leaked-classloader', 'false',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'classloader.check-leaked-classloader' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

DELETE from console_component_config where component_id = -115 and `key` = 'classloader.parent-first-patterns.default' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
    (-2, -115, 0, 'INPUT', 0, 'classloader.parent-first-patterns.default', 'java.;scala.;org.apache.flink.;com.esotericsoftware.kryo;javax.annotation.;org.xml;javax.xml;org.apache.xerces;org.w3c;org.rocksdb.;org.slf4j;org.apache.log4j;org.apache.logging;org.apache.commons.logging;ch.qos.logback', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'classloader.parent-first-patterns.default', 'java.;scala.;org.apache.flink.;com.esotericsoftware.kryo;javax.annotation.;org.xml;javax.xml;org.apache.xerces;org.w3c;org.rocksdb.;org.slf4j;org.apache.log4j;org.apache.logging;org.apache.commons.logging;ch.qos.logback',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'classloader.parent-first-patterns.default' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'pipeline.operator-chaining', 'false',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'pipeline.operator-chaining' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

-- 2) flink 1.12 Session 控制台参数修改
update console_component_config set `value` = 'classpath' where component_id = -115 and `key` = 'pluginLoadMode' and `dependencyKey` = 'deploymode$session';
-- 刷历史集群，只处理 flink1.12
update console_component_config set `value` = 'classpath', required = 1 where component_id in
     (select id from console_component t where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
        and t.version_name = '1.12'
        and t.deploy_type = 1) and `key` = 'pluginLoadMode' and `dependencyKey` = 'deploymode$session';

-- 修改 Flink 中的高级的 classloader.resolve-order 改为下拉框
-- 2.1) perjob
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$perjob$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'parent-first' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$perjob$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$perjob$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'child-first' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$perjob$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$perjob$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'child-first-cache' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$perjob$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

update console_component_config t set `type` = 'SELECT' where t.component_type_code = 0 and `type` = 'INPUT' and `key` = 'classloader.resolve-order' and dependencyKey = 'deploymode$perjob'
       and t.component_id in (select id from console_component where component_type_code = 0 and version_name = '1.12' and deploy_type = 1 and cluster_id !=-2);

-- 2.2) session
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$session$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'parent-first' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$session$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'child-first' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$session$classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink 1.12 on yarn 组件
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.key = 'child-first-cache' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session$classloader.resolve-order')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

update console_component_config t set `type` = 'SELECT', `value` = 'child-first-cache' where t.component_type_code = 0 and `type` = 'INPUT' and `key` = 'classloader.resolve-order' and dependencyKey = 'deploymode$session'
 and t.component_id in (select id from console_component where component_type_code = 0 and version_name = '1.12' and deploy_type = 1 and cluster_id !=-2);

-- 3) tips 优化
update schedule_dict set dict_value = '有三种可选 ① parent-first ② child-first ③ child-first-cache (数据同步专用)。ps : 如果使用 child-first-cache，内部会自动设置 pipeline.operator-chaining : false 和  classloader.check-leaked-classloader : false ，以确保此参数生效。'
WHERE `type` = 25 AND dict_name = 'classloader.resolve-order' AND dict_desc = '0';

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.check-leaked-classloader' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.check-leaked-classloader','是否检查类加载器中类的泄露', 25, '高级', '0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.parent-first-patterns.default' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.parent-first-patterns.default','父级类加载器优先加载的类列表', 25, '高级', '0', 9);
