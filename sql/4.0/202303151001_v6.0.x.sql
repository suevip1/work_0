-- add flink 1.16 version
-- ref 202212271440_v5.3.x.sql

-- ****************************************** on yarn ******************************
-- 1. 修改外层展示版本，ref: com.dtstack.engine.master.impl.ScheduleDictService.getFlinkVersionByDepends
DELETE FROM schedule_dict WHERE dict_code = 'flink_version' AND dict_name  = '1.16';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('flink_version', '1.16', '116', null, 1, 2, 'INTEGER', '0,1,2', 0, now(), now(), 0);

-- 2. 修改控件展示参数
DELETE FROM schedule_dict WHERE dict_value = '-158' AND dict_code = 'typename_mapping' AND dict_name IN ('yarn2-hdfs2-flink116','yarn3-hdfs3-flink116');
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn2-hdfs2-flink116', '-158', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'yarn3-hdfs3-flink116', '-158', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);

-- 先删后插
DELETE FROM console_component_config WHERE component_id = -158 AND cluster_id = -2 AND component_type_code = 0;

INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'CHECKBOX', 1, 'deploymode', '[\"perjob\",\"session\"]', NULL, '', '', NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'GROUP', 1, 'perjob', 'perjob', NULL, 'deploymode', 'perjob', NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'akka.ask.timeout', '60 s', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 0, 'classloader.resolve-order', 'child-first', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'clusterMode', 'perjob', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.16/chunjun_lib', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink116/ha', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink116', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink116/completed-jobs', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.factory.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporterFactory', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'false', 'false', NULL, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'true', 'true', NULL, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.hostUrl', '', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '116job', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'false', 'false', NULL, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'true', 'true', NULL, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'pluginLoadMode', 'shipfile', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'prometheusHost', '', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'prometheusPort', '9090', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'state.backend', 'RocksDB', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'state.backend.incremental', 'true', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink116/checkpoints', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink116/savepoints', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarn.application-attempts', '3', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'GROUP', 1, 'session', 'session', NULL, 'deploymode', 'session', NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 1, 'classloader.resolve-order', 'child-first-cache', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'clusterMode', 'session', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.16/chunjun_lib', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'slotmanager.number-of-slots.max', '10', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'jobmanager.memory.process.size', '1600m', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'high-availability', 'NONE', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink116/ha', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink116', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink116/completed-jobs', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.factory.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporterFactory', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'false', 'false', NULL, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'true', 'true', NULL, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.hostUrl', '', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '116job', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'false', 'false', NULL, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'true', 'true', NULL, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'pluginLoadMode', 'classpath', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'prometheusHost', '', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'prometheusPort', '9090', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'sessionRetryNum', '5', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'sessionStartAuto', 'true', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'state.backend', 'RocksDB', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'state.backend.incremental', 'true', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink116/checkpoints', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink116/savepoints', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarn.application-attempt-failures-validity-interval', '3600000', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarn.application-attempts', '3', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'akka.tcp.timeout', '60 s', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'flinkSessionName', 'flink_session', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'checkpoint.retain.time', '7', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.16/chunjun_lib', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.16/chunjun_lib', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'restart-strategy', 'none', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '1s', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-interval', '1 min', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '2', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'yarn.application.queue', '', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'pluginsDistDir', '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'pluginsDistDir', '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs', NULL, 'deploymode$perjob', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'restart-strategy', 'none', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 1, 'slotmanager.number-of-slots.debug.max', '2', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'pipeline.operator-chaining', 'false', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'classloader.check-leaked-classloader', 'false', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, 'INPUT', 0, 'classloader.parent-first-patterns.default', 'java.;scala.;org.apache.flink.;com.esotericsoftware.kryo;javax.annotation.;org.xml;javax.xml;org.apache.xerces;org.w3c;org.rocksdb.;org.slf4j;org.apache.log4j;org.apache.logging;org.apache.commons.logging;ch.qos.logback', NULL, 'deploymode$session', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'parent-first', 'parent-first', NULL, 'deploymode$session$classloader.resolve-order', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'child-first', 'child-first', NULL, 'deploymode$session$classloader.resolve-order', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'child-first-cache', 'child-first-cache', NULL, 'deploymode$session$classloader.resolve-order', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'parent-first', 'parent-first', NULL, 'deploymode$perjob$classloader.resolve-order', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'child-first', 'child-first', NULL, 'deploymode$perjob$classloader.resolve-order', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -158, 0, '', 1, 'child-first-cache', 'child-first-cache', NULL, 'deploymode$perjob$classloader.resolve-order', NULL, NULL);

-- 3. 资源组件匹配
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'Apache Hadoop 2.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn3-hdfs3-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'Apache Hadoop 3.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn3-hdfs3-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'HDP 3.0.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn3-hdfs3-flink116"
            }
        ],
        "SPARK":[
            {   "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'CDH 6.0.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn3-hdfs3-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'CDH 6.1.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'HDP 2.6.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'CDH 5.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn3-hdfs3-flink116"
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
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'HDP 3.x' and depend_name = 'YARN';
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
           },
           {
               "1.16":"yarn3-hdfs3-flink116"
           }
       ],
       "SPARK":[
           {
               "2.4":"yarn3-hdfs3-spark240",
               "3.2":"yarn3-hdfs3-spark320"
           }
        ],
       "DT_SCRIPT":"yarn3-hdfs3-dtscript",
       "HDFS":"yarn3-hdfs3-hadoop3",
       "TONY":"yarn3-hdfs3-tony",
       "LEARNING":"yarn3-hdfs3-learning"
   }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'CDH 6.2.x' and depend_name = 'YARN';
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
                              },
                              {
                                  "1.16":"yarn3-hdfs3-flink116"
                              }
                          ],
                          "SPARK":[
                              {
                                  "2.4":"yarn3-hdfs3-spark240",
                                  "3.2":"yarn3-hdfs3-spark320"
                              }
                          ],
                          "DT_SCRIPT":"yarn3-hdfs3-dtscript",
                          "HDFS":"yarn3-hdfs3-hadoop3",
                          "TONY":"yarn3-hdfs3-tony",
                          "LEARNING":"yarn3-hdfs3-learning"
                      }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'CDP 7.x' and depend_name = 'YARN';
UPDATE schedule_dict SET dict_value = '{
    "YARN":"yarn2tbds",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn2tbds-hdfs2tbds-flink110",
                "1.12":"yarn2tbds-hdfs2tbds-flink112",
                "1.16":"yarn2tbds-hdfs2tbds-flink116"
            }
        ],
         "HDFS":"yarn2tbds-hdfs2tbds-hadoop2tbds",
         "SPARK":[
             {
                 "2.4":"yarn2tbds-hdfs2tbds-spark240"
             }
         ],
         "DT_SCRIPT":"yarn2tbds-hdfs2tbds-dtscript"
    }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'TBDS 5.1.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'TDH 5.2.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'TDH 6.x' and depend_name = 'YARN';
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
            },
            {
                "1.16":"yarn2-hdfs2-flink116"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', gmt_modified = now() where dict_code = 'component_model_config' and dict_name = 'TDH 7.x' and depend_name = 'YARN';

-- 4. 离线配置参数
delete from environment_param_template where task_type = 39 and task_name = 'FLINK' and task_version = '1.16';
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params)
 VALUES (39, 'FLINK', '1.16', -1, '## 资源相关
## per_job:单独为任务创建flink yarn
flinkTaskRunMode=per_job
## jar包任务没设置并行度时的默认并行度
parallelism.default=1
#t# askmanager的slot数
taskmanager.numberOfTaskSlots=1
## jobmanager进程内存大小
jobmanager.memory.process.size=1g
## taskmanager进程内存大小
taskmanager.memory.process.size=2g');

delete from environment_param_template where task_type = 31 and task_name = 'FLINK_SQL' and task_version = '1.16';
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params)
VALUES (31, 'FLINK_SQL', '1.16', -1, '## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g

## 时间相关
## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选
## 非脚本模式会根据Kafka自动设置。脚本模式默认为ProcessingTime
# pipeline.time-characteristic=EventTime

## Checkpoint相关
## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
execution.checkpointing.interval=5min
## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# execution.checkpointing.mode=EXACTLY_ONCE

# Flink SQL独有，状态过期时间
table.exec.state.ttl=1d

log.level=INFO

## 使用Iceberg和Hive维表开启
# table.dynamic-table-options.enabled=true

## Kerberos相关
# security.kerberos.login.contexts=Client,KafkaClient


## 高阶参数
## 窗口提前触发时间
# table.exec.emit.early-fire.enabled=true
# table.exec.emit.early-fire.delay=1s

## 当一个源在超时时间内没有收到任何元素时，它将被标记为临时空闲
# table.exec.source.idle-timeout=10ms

## 是否开启minibatch
## 可以减少状态开销。这可能会增加一些延迟，因为它会缓冲一些记录而不是立即处理它们。这是吞吐量和延迟之间的权衡
# table.exec.mini-batch.enabled=true
## 状态缓存时间
# table.exec.mini-batch.allow-latency=5s
## 状态最大缓存条数
# table.exec.mini-batch.size=5000

## 是否开启Local-Global 聚合。前提需要开启minibatch
## 聚合是为解决数据倾斜问题提出的，类似于 MapReduce 中的 Combine + Reduce 模式
# table.optimizer.agg-phase-strategy=TWO_PHASE

## 是否开启拆分 distinct 聚合
## Local-Global 可以解决数据倾斜，但是在处理 distinct 聚合时，其性能并不令人满意。
## 如：SELECT day, COUNT(DISTINCT user_id) FROM T GROUP BY day 如果 distinct key （即 user_id）的值分布稀疏，建议开启
# table.optimizer.distinct-agg.split.enabled=true

## Flink算子chaining开关。默认为true。排查性能问题时会暂时设置成false，但降低性能。
# pipeline.operator-chaining=true
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION');


delete from environment_param_template where task_type = 2 and task_name = 'SYNC' and task_version = '1.16';
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params)
VALUES (2, 'SYNC', '1.16', -1, '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下每个taskManager 对应 slot的数量
## slots=1

## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
## 任务优先级, 范围:1-1000
## job.priority=10
pipeline.operator-chaining = false');

delete from environment_param_template where task_type = 37 and task_name = 'DATA_COLLECTION' and task_version = '1.16';
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params)
VALUES (37, 'DATA_COLLECTION', '1.16', -1, '## per_job模式下jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## per_job模式下taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
# container=1

## per_job模式下每个taskManager 对应 slot的数量
slots=1

## 任务优先级, 范围:1-1000
job.priority=10

## checkpoint保存时间间隔
flink.checkpoint.interval=3600000

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client');

-- ****************************************** standalone *********************************************
DELETE from schedule_dict WHERE dict_code = 'component_model_config' and dict_name = '1.16' and depend_name = 'FLINK';
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`)
VALUES ('component_model_config', '1.16', '{"1.16":"flink116-standalone"}', NULL, 14, 1, 'STRING', 'FLINK', 0);

DELETE FROM schedule_dict WHERE dict_value = '-160' AND dict_code = 'typename_mapping' AND dict_name = 'flink116-standalone';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'flink116-standalone', '-160', null, 6, 0, 'LONG', '', 0, now(),
        now(), 0);

-- 先删后插
DELETE FROM console_component_config WHERE component_id = -160 AND cluster_id = -2 AND component_type_code = 20;

INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'jobmanager.rpc.address', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'jobmanager.rpc.port', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'prometheusHost', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'prometheusPort', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'high-availability', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'high-availability.zookeeper.quorum', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'high-availability.zookeeper.path.root', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'high-availability.storageDir', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'high-availability.cluster-id', '/default', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin/flink116_lib', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin116/flinkxplugin', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin116/flinkxplugin', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'clusterMode', 'standalone', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, '', 0, 'false', 'false', NULL, 'metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, '', 0, 'true', 'true', NULL, 'metrics.reporter.promgateway.deleteOnShutdown', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, '', 0, 'false', 'false', NULL, 'metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, '', 0, 'true', 'true', NULL, 'metrics.reporter.promgateway.randomJobNameSuffix', NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'rest.port', '8081', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'metrics.reporter.promgateway.factory.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporterFactory', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'metrics.reporter.promgateway.hostUrl', '', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '116job', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'state.backend', 'jobmanager', NULL, NULL, NULL, NULL);
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`) VALUES (-2, -160, 20, 'INPUT', 0, 'pluginLoadMode', 'classpath', NULL, NULL, NULL, NULL);

-- 补充新增属性的提示信息
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.hostUrl' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.hostUrl','promgateway地址(包括端口)',25,'metric监控','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.hostUrl' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.hostUrl','promgateway地址(包括端口)',25,'metric监控','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.factory.class' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.factory.class','用来推送指标类',25,'metric监控','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.factory.class' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.factory.class','用来推送指标类',25,'metric监控','20', 3);