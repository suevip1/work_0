-- volcano config, add by qiuyun
-- 运行 volcano 任务必须参数
DELETE from schedule_dict where dict_code = 'task_client_type' and dict_name = 'volcano';
INSERT INTO schedule_dict ( dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted )
VALUES
    ( 'task_client_type', 'volcano', '0', 'volcano', 23, 0, 'INTEGER', NULL, 0, '2022-05-16 10:28:27', '2022-05-16 10:28:27', 0 );

-- 修改volcano模板
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'logtracerDestPath';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'pvc.name';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'ingress.host';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'kubernetes.logtracer.image';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'kubernetes.py4j.image';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -137 AND component_type_code = 29 AND `key` = 'jdbcUrl';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, `value`, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'pvc.name', 'aiworks-pvc-nfs', null, null, null, 'k8s 集群 nfs pvc 名称', '2022-05-16 10:28:27', '2022-05-16 10:28:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, `value`, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'ingress.host', '', null, null, null, 'k8s 集群 host', '2022-05-16 10:28:27', '2022-05-16 10:28:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, `value`, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.logtracer.image', 'logtracer:1.0.0', null, null, null, '适配 hadoop 的容器内日志搜集镜像', '2022-05-16 10:28:27', '2022-05-16 10:28:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, `value`, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.py4j.image', 'py4j:1.0.0', null, null, null, '适配 hadoop 的 py4j 镜像', '2022-05-16 10:28:27', '2022-05-16 10:28:27', 0);
