-- 增加 hive server metric 配置
-- 插入 hive.monitor.resources.enable
delete from schedule_dict where dict_code = 'tips' and dict_desc = '9' and dict_name = 'hive.monitor.resources.enable';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'hive.monitor.resources.enable', '是否开启资源监控', '9', 25, 9, 'STRING', '', 0, now(),now(), 0);

-- 插入 metric 信息
delete from schedule_dict where dict_code = 'tips' and dict_desc = '9' and dict_name like 'metrics%';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.server.host', 'prometheus host', '9', 25, 21, 'STRING', '', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.server.port', 'prometheus port', '9', 25, 22, 'STRING', '', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.pushgateway.host', 'pushgateway host', '9', 25, 23, 'STRING', '', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.pushgateway.port', 'pushgateway port', '9', 25, 24, 'STRING', '', 0, now(),now(), 0);

delete from console_component_config where `component_type_code` = 9 and `key` in
    ('hive.monitor.resources.enable', 'metrics.prometheus.server.host', 'metrics.prometheus.server.port', 'metrics.prometheus.pushgateway.host', 'metrics.prometheus.pushgateway.port');

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 9, 'INPUT', 1, 'hive.monitor.resources.enable', 'true', null, '', null, null, now(), now(), 0
FROM schedule_dict
WHERE dict_name like 'hive%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 9, 'INPUT', 1, 'metrics.prometheus.server.host', '', null, '', null, null, now(), now(), 0
FROM schedule_dict
WHERE dict_name like 'hive%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 9, 'INPUT', 1, 'metrics.prometheus.server.port', '9090', null, '', null, null, now(), now(), 0
FROM schedule_dict
WHERE dict_name like 'hive%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 9, 'INPUT', 1, 'metrics.prometheus.pushgateway.host', '', null, '', null, null, now(), now(), 0
FROM schedule_dict
WHERE dict_name like 'hive%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 9, 'INPUT', 1, 'metrics.prometheus.pushgateway.port', '9091', null, '', null, null, now(), now(), 0
FROM schedule_dict
WHERE dict_name like 'hive%' and dict_code ='typename_mapping' group by dict_value;

-- 刷入数据
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       9,
       'INPUT',
       1,
       'hive.monitor.resources.enable',
       'true',
       null,
       null,
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 9
  AND `key` = 'password';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       9,
       'INPUT',
       1,
       'metrics.prometheus.server.host',
       '',
       null,
       null,
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 9
  AND `key` = 'password';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       9,
       'INPUT',
       1,
       'metrics.prometheus.server.port',
       '9090',
       null,
       null,
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 9
  AND `key` = 'password';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       9,
       'INPUT',
       1,
       'metrics.prometheus.pushgateway.host',
       '',
       null,
       null,
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 9
  AND `key` = 'password';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       9,
       'INPUT',
       1,
       'metrics.prometheus.pushgateway.port',
       '9091',
       null,
       null,
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 9
  AND `key` = 'password';

-- 更新信息
update console_component_config c1
    inner join
    (select *
     from console_component_config
     where component_type_code = 0
       and `key` = 'prometheusHost'
       and value != '') c2
    on c1.cluster_id = c2.cluster_id
set c1.value = c2.value
where c1.component_type_code = 9
  and c1.`key` = 'metrics.prometheus.server.host';

-- 更新信息
update console_component_config c1
    inner join
    (select *
     from console_component_config
     where component_type_code = 0
       and `key` = 'metrics.reporter.promgateway.host'
       and value != '') c2
    on c1.cluster_id = c2.cluster_id
set c1.value = c2.value
where c1.component_type_code = 9
  and c1.`key` = 'metrics.prometheus.pushgateway.host';

-- 资源占用超限告警
DELETE FROM `alert_rule` WHERE `id` = 11;
INSERT INTO `alert_rule` (`id`, `app_type`, `name`, `key`, `title`, `template`, `params`,
                          `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (11, 1, '资源占用超限', 'resource_over_limit', '[${sign!}]离线任务：${task.name!}资源占用超限告警',
        '[${sign!}]离线任务：${task.name!}资源占用超限告警\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：${jobScheduleType!}\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n资源占用：${jobResource!}\n责任人：${user.userName!}\n请及时处理!',
        '{}', now(), now(), 0);