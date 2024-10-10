INSERT ignore INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('tips', '实时session参数', null, '0', 25, 5, 'STRING', '', 0, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0);

INSERT ignore INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
 VALUES ('tips', 'stream_classloader.resolve-order', '有三种可选 ① parent-first ② child-first ③ child-first-cache (数据同步专用)。ps : 如果使用 child-first-cache，内部会自动设置 pipeline.operator-chaining : false 和  classloader.check-leaked-classloader : false ，以确保此参数生效。', '0', 25, 1, 'STRING', '实时session参数', 0, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0);

INSERT ignore INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'stream_flinkSessionName', 'yarn session名称', '0', 25, 2, 'STRING', '实时session参数', 0, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0);

INSERT ignore INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('tips', 'stream_classloader.check-leaked-classloader', '是否检查类加载器中类的泄露', '0', 25, 3, 'STRING', '实时session参数', 0, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0);


INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -158, 0, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -115, 0, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -158, 0, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -115, 0, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -158, 0, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -115, 0, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true', null, 'deploymode$session', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -158, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -158, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -158, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);


INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES (-2, -115, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -115, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -115, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$stream_classloader.resolve-order', null, null, '2023-06-14 10:33:56', '2023-06-14 10:33:56', 0, null, null);



-- 历史集群数据处理
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 1;






