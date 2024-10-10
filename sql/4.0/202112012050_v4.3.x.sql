-- add by qiuyun

-- 版本字典：type 字段 跟 DictType 枚举值保持一致
delete from schedule_dict where dict_code = 's3_version' and dict_name = 'CSP' and type = 22; -- 幂等
INSERT INTO schedule_dict
    (id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES
(null, 's3_version', 'CSP', 'CSP', null, 22, 1, 'STRING', null, 1, now(), now(), 0);

-- s3(包名) 的 config 模板加载
-- select max(dict_value) from schedule_dict t where t.data_type = 'LONG' and t.dict_value < 0;
delete from schedule_dict where dict_code = 'typename_mapping' and dict_name = 's3' and dict_value = '-210'; -- 幂等
INSERT INTO schedule_dict
    (id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES
 (null, 'typename_mapping', 's3', '-210', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

delete from console_component_config where component_id = -210; -- 幂等
INSERT INTO console_component_config
    (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES
(null, -2, -210, 17, 'INPUT', 1, 'accessKey', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config
(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
(null, -2, -210, 17, 'INPUT', 1, 'secretKey', '', null, null, null, null, now(), now(), 0);
INSERT INTO console_component_config
(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
(null, -2, -210, 17, 'INPUT', 1, 'endpoint', '', null, null, null, null, now(), now(), 0);

-- k8s-s3-flink112 的 config 模板加载
delete from schedule_dict where dict_code = 'typename_mapping' and dict_name = 'k8s-s3-flink112' and dict_value = '-211'; -- 幂等
INSERT INTO schedule_dict
    (id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES
(null, 'typename_mapping', 'k8s-s3-flink112', '-211', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

delete from console_component_config where component_id = -211; -- 幂等
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'flinkLibDir', '/opt/dtstack/flink-1.12.5/lib', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'flinkxDistDir', '/Users/oukebin/git/company/flinkx/flinkx-dist', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/opt/flinkx-dist', null, 'deploymode$perjob', null, null,  now(), now(), 0);
-- 控制台中不需要配置，在后台组装的参数
-- INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'remoteDir', '/data/sftp/CONSOLE_dev_k8s/KUBERNETES', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'kubernetes.container.image', '172.16.8.120:5443/dtstack-dev/flinkx-s3:4.3.x', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'kubernetes.rest-service.exposed.type', 'NodePort', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'kubernetes.container.image.pull-policy', 'IfNotPresent', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'kubernetes.service-account', 'default', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'kubernetes.entry.path', '/docker-entrypoint.sh', null, 'deploymode$perjob', null, null,  now(), now(), 0);
-- 控制台中不需要配置，在后台组装的参数
-- INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'kubernetesConfigName', 'config.zip', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'SELECT', 0, 'classloader.check-leaked-classloader', 'false', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, '', 0, 'false', 'false', null, 'deploymode$perjob$classloader.check-leaked-classloader', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, '', 0, 'true', 'true', null, 'deploymode$perjob$classloader.check-leaked-classloader', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'containerized.master.env.KUBERNETES_HOST_ALIASES', '172.16.100.195 kudu1;172.16.101.161 kudu2;172.16.100.83 kudu3', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'containerized.taskmanager.env.KUBERNETES_HOST_ALIASES', '172.16.100.195 kudu1;172.16.101.161 kudu2;172.16.100.83 kudu3', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS', '172.16.83.189:8635', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS', '172.16.83.189:8635', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'high-availability', 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', null, 'deploymode$perjob', null, null, now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'high-availability.storageDir', 's3a://lingjiang01-1255000139/flink112/ha', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 's3a://lingjiang01-1255000139/flink112/completed-jobs', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'state.checkpoints.dir', 's3a://lingjiang01-1255000139/flink112/checkpoints', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'state.savepoints.dir', 's3a://lingjiang01-1255000139/flink112/savepoints', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'INPUT', 0, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, 'SELECT', 0, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, '', 0, 'true', 'true', null, 'deploymode$perjob$state.backend.incremental', null, null,  now(), now(), 0);
INSERT INTO console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (null, -2, -211, 0, '', 0, 'false', 'false', null, 'deploymode$perjob$state.backend.incremental', null, null,  now(), now(), 0);

-- flink 1.10/1.12 支持 k8s
update schedule_dict t set t.depend_name = '0,1,2' where t.dict_code = 'flink_version' and t.dict_name in ('1.10', '1.12');
