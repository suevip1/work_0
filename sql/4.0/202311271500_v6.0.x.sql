DELETE FROM console_component_config WHERE component_id IN (SELECT id FROM console_component WHERE component_type_code = 0 and is_deleted = 0 and cluster_id != -2
                                                                                               and deploy_type = 2) AND `key` IN ('stream_classloader.resolve-order','stream_flinkSessionName','stream_classloader.check-leaked-classloader');

DELETE FROM console_component_config WHERE component_id IN (SELECT id FROM console_component WHERE component_type_code = 0 and is_deleted = 0 and cluster_id != -2
                                                                                               and deploy_type = 2) AND `dependencyKey` = 'deploymode$session$stream_classloader.resolve-order';

-- 历史k8s集群数据处理
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'SELECT', 1, 'stream_classloader.resolve-order', 'child-first',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'parent-first', 'parent-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first', 'child-first',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'child-first-cache', 'child-first-cache',null,'deploymode$session$stream_classloader.resolve-order',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;



INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'stream_flinkSessionName', 'stream_session',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.12'
                            and t.deploy_type = 2;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'stream_classloader.check-leaked-classloader', 'true',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;
