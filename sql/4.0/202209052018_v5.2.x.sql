-- change spark 310 to 320, ref:http://zenpms.dtstack.cn/zentao/task-view-7679.html
update `schedule_dict` set `dict_name` = 'yarn2-hdfs2-spark320' where `dict_name` = 'yarn2-hdfs2-spark310'
  and `dict_code` = 'typename_mapping';
update `schedule_dict` set `dict_name` = 'yarn3-hdfs3-spark320' where `dict_name` = 'yarn3-hdfs3-spark310'
  and `dict_code` = 'typename_mapping';

update `schedule_dict` set dict_name = '3.2', dict_value = '320' where dict_code = 'spark_version' and dict_name = '3.1';

-- change order
DELETE from  `schedule_dict` where `dict_code` =  'tips' and dict_name = 'metric' and `dict_desc` = '0';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ('tips', 'metric', NULL, '1', 25, 9, 'STRING', '', 0, now(), now(), 0);

UPDATE schedule_dict SET dict_value = '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'Apache Hadoop 2.x' AND type = 14;


UPDATE schedule_dict SET dict_value = '{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240",
                "3.2":"yarn3-hdfs3-spark320"
             }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}'
WHERE dict_name = 'Apache Hadoop 3.x' AND type = 14;

delete from console_component_config where component_id = -132 and `key` in ('spark.ranger.enabled', 'spark.sql.extensions');
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -132, 1, 'INPUT', 0, 'spark.ranger.enabled', 'false', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0);
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -132, 1, 'INPUT', 0, 'spark.sql.extensions', 'org.apache.spark.ranger.security.api.RangerSparkSQLExtension', NULL, 'deploymode$perjob', NULL, NULL, now(), now(), 0);

delete from console_component_config where component_id = -132 and component_type_code = 1 and `key` like 'metrics.prometheus%';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.server.host', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.server.port', '9090', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.host', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.port', '9091', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class', 'org.apache.spark.metrics.sink.PrometheusPushGatewaySink', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol', 'http', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period', '5', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:55', '2022-09-02 18:28:55', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:56', '2022-09-02 18:28:56', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.source.jvm.class.instance', '*', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:56', '2022-09-02 18:28:56', 0);
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (-2, -132, 1, 'INPUT', 0, 'metrics.prometheus.source.jvm.class', 'org.apache.spark.metrics.source.JvmSource', NULL, 'deploymode$perjob', NULL, NULL, '2022-09-02 18:28:56', '2022-09-02 18:28:56', 0);

-- 历史数据修正
update console_component set hadoop_version = '320', version_name = '3.2' where component_type_code = 1
  and  (version_name = '3.1' or hadoop_version = '310');
update console_component_config set `value` = 'yarn2-hdfs2-spark320' where component_type_code = 1
  and `type` = 'OTHER' and `key` = 'typeName' and `value` = 'yarn2-hdfs2-spark310';
update console_component_config set `value` = 'yarn3-hdfs3-spark320' where component_type_code = 1
  and `type` = 'OTHER' and `key` = 'typeName' and `value` = 'yarn3-hdfs3-spark310';

UPDATE `console_component_config` SET `value` = REPLACE(`value`, 'spark310', 'spark320') WHERE `key` IN
  ('spark.eventLog.dir','sparkPythonExtLibPath','sparkSqlProxyPath','sparkYarnArchive') and `value` like '%spark310%'
 and component_type_code = 1;

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.ranger.enabled', 'false',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'spark.ranger.enabled' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.sql.extensions', 'org.apache.spark.ranger.security.api.RangerSparkSQLExtension',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'spark.sql.extensions' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

-- ref : 202208021400_v5.2.x.sql
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.server.host', '',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.server.host' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.server.port', '9090',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.server.port' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.host', '',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.host' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.port', '9091',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.port' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.class.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.class', 'org.apache.spark.metrics.sink.PrometheusPushGatewaySink',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.class' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.protocol.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.protocol', 'http',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.protocol' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.period.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.period', '5',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.period' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector', 'true',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.enable-dropwizard-collector' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.enable-hostname.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.sink.pushgateway.enable-hostname', 'true',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.sink.pushgateway.enable-hostname' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.source.jvm.class.instance', '*',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.source.jvm.class.instance' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'metrics.prometheus.source.jvm.class', 'org.apache.spark.metrics.source.JvmSource',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2 and t.id not in (
    select g.component_id from console_component_config g where g.component_type_code = 1 and g.`key` = 'metrics.prometheus.source.jvm.class' and g.cluster_id !=-2)
                            and t.version_name = '3.2';

update console_component_config SET `value` = REPLACE(`value`, 'py4j-0.10.9-src.zip', 'py4j-0.10.9.5-src.zip') WHERE `key` IN
  ('sparkPythonExtLibPath') and `value` like '%py4j-0.10.9-src.zip%' and component_type_code = 1;

-- 针对 2.4 和 3.2，修改模板的默认的 pyspark 路径
update console_component_config set `value` = REPLACE(`value`, 'anaconda3', 'miniconda3') WHERE `key` IN
  ('spark.yarn.appMasterEnv.PYSPARK_PYTHON') and `value` like '%anaconda3%' and component_type_code = 1
 and component_id in (-130, -132);

-- 修改离线任务环境参数模板
update environment_param_template set task_version = '3.2' where task_type = 0 and task_name = 'SPARK_SQL' and task_version = '3.1';
update environment_param_template set task_version = '3.2' where task_type = 1 and task_name = 'SPARK' and task_version = '3.1';
update environment_param_template set task_version = '3.2' where task_type = 3 and task_name = 'SPARK_PYTHON' and task_version = '3.1';