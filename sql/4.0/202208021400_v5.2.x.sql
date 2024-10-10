DELETE FROM console_component_config WHERE `key` like 'metrics%' AND component_type_code = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
 SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.server.host', '', null, 'deploymode$perjob', null, null, now(),now(), 0
                       FROM schedule_dict
                       WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
 SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.server.port', '9090', null, 'deploymode$perjob', null, null, now(),now(), 0
                       FROM schedule_dict
                       WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
 SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.host', '', null, 'deploymode$perjob', null, null, now(),now(), 0
                       FROM schedule_dict
                       WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.port', '9091', null, 'deploymode$perjob', null, null, now(),now(), 0
                      FROM schedule_dict
                      WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class', 'org.apache.spark.metrics.sink.PrometheusPushGatewaySink', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol', 'http', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period', '5', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector', 'true', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname', 'true', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.source.jvm.class.instance', '*', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
SELECT -2, dict_value, 1, 'INPUT', 0, 'metrics.prometheus.source.jvm.class', 'org.apache.spark.metrics.source.JvmSource', null, 'deploymode$perjob', null, null, now(),now(), 0
                    FROM schedule_dict
                    WHERE dict_name like 'yarn%spark%' and dict_code ='typename_mapping' group by dict_value;

-- 刷新历史集群

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.server.host',
       '',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.server.port',
       '9090',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.host',
       '',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.port',
       '9091',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.class.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.class',
       'org.apache.spark.metrics.sink.PrometheusPushGatewaySink',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.protocol.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.protocol',
       'http',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.period.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.period',
       '5',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-timestamp.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-timestamp',
       'false',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector',
       'true',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-hostname.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.sink.pushgateway.enable-hostname',
       'true',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.source.jvm.class.instance',
       '*',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
SELECT cluster_id,
       component_id,
       1,
       'INPUT',
       0,
       'metrics.prometheus.source.jvm.class',
       'org.apache.spark.metrics.source.JvmSource',
       null,
       'deploymode$perjob',
       null,
       null,
       now(),
       now(),
       0
FROM console_component_config
WHERE cluster_id != -2
  AND component_type_code = 1
  AND `key` = 'spark.submit.deployMode';



update console_component_config c1
    inner join
    (select *
     from console_component_config
     where component_type_code = 0
       and `key` = 'prometheusHost'
       and value != '') c2
    on c1.cluster_id = c2.cluster_id
set c1.value = c2.value
where c1.component_type_code = 1
  and c1.`key` = 'metrics.prometheus.server.host';



update console_component_config c1
  inner join
  (select *
   from console_component_config
   where component_type_code = 0
     and `key` = 'metrics.reporter.promgateway.host'
     and value != '') c2
  on c1.cluster_id = c2.cluster_id
set c1.value = c2.value
where c1.component_type_code = 1
and c1.`key` = 'metrics.prometheus.sink.pushgateway.host';

DELETE FROM schedule_dict WHERE dict_code = 'tips' AND dict_name like 'metric%' AND dict_desc = 1;


alter table schedule_dict modify dict_name varchar(128) null comment '字典名称';



INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.server.host', 'prometheus host', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.server.port', 'prometheus port', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.host', 'pushgateway host', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.port', 'pushgateway port', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.class.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.class', '声明用于将指标push到PushGatewaySink的类', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.protocol.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.protocol', 'pushgateway的地址协议', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.period.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.period', '指标sink到pushgateway的时间间隔', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector', '开启dropwizard-collector来收集Spark App metrics', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.enable-hostname.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.sink.pushgateway.enable-hostname', '开启URI地址以主机名的形式展现', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.source.jvm.class.instance', '取值为master、worker、executor、driver、applications，也可以取值为 * ，* 代表所有的instance', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('tips', 'metrics.prometheus.source.jvm.class', '开启JvmSource，收集各个instance的jvm信息', '1', 25, 0, 'STRING', 'metric', 0, now(),now(), 0);