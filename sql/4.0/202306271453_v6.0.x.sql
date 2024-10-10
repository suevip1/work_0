-- 先delete，保证幂等
delete from schedule_dict where dict_name = 'k8s-hdfs2-flink116' and dict_value = '-122';

delete from console_component_config where cluster_id = -2 and component_id = -122;

delete from schedule_dict where dict_code = 'tips' and dict_name in('containerized.taskmanager.env.HADOOP_USER_NAME','kubernetes.namespace','containerized.master.env.HADOOP_USER_NAME');

INSERT INTO dagschedulex.schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'k8s-hdfs2-flink116', '-122', null, 6, 0, 'LONG', '', 0, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);

-- 需求：flink on k8s 116，思路：schedule_dict --> console_component_config，add by qiuyun
update schedule_dict set dict_name = '', dict_value = '{
  "KUBERNETES": "kubernetes",
  "S3": {
    "CSP": {
      "FLINK": [
        {
          "1.12": "k8s-s3-flink112"
        }
      ],
      "S3": "s3",
      "DT_SCRIPT": "",
      "SPARK":[]
    }
  },
  "NFS": {
    "": {
      "FLINK": [
        {
          "1.10": "k8s-nfs-flink110"
        }
      ],
      "NFS": "nfs",
      "DT_SCRIPT": "",
      "SPARK":[]
    }
  },
  "HDFS": {
    "hdfs2": {
      "FLINK": [
        {
          "1.10": "k8s-hdfs2-flink110"
        },
        {
          "1.12": "k8s-hdfs2-flink112"
        },
       {
          "1.16": "k8s-hdfs2-flink116"
        }
      ],
      "SPARK": [
        {
          "2.4": "k8s-hdfs2-spark240"
        }
      ],
      "DT_SCRIPT": "k8s-hdfs2-dtscript",
      "VOLCANO":"k8s-hdfs2-volcano",
      "HDFS": "k8s-hdfs2-hadoop2"
    },
    "hdfs3": {
      "FLINK": [],
      "DT_SCRIPT": "k8s-hdfs3-dtscript",
      "SPARK":[],
      "VOLCANO":"k8s-hdfs3-volcano",
      "HDFS": "k8s-hdfs3-hadoop3"
    }
  }
}'
where `type` = 14 and dict_code = 'component_model_config' and (dict_name = '' or dict_name = ' ')
  and depend_name = 'KUBERNETES';

INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'CHECKBOX', 1, 'deploymode', '["perjob"]', null, '', '', null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'clusterMode', 'perjob', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.16/chunjun_lib', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'pluginLoadMode', 'shipfile', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -122, 0, 'INPUT', 1, 'classloader.dtstack-cache', 'true', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$classloader.dtstack-cache', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$classloader.dtstack-cache', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'containerized.master.env.KUBERNETES_HOST_ALIASES', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'containerized.taskmanager.env.KUBERNETES_HOST_ALIASES', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'jobmanager.memory.process.size', '1024m', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'kubernetes.container.image', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'kubernetes.rest-service.exposed.type', 'NodePort', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'kubernetes.container.image.pull-policy', 'IfNotPresent', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'kubernetes.service-account', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'kubernetes.entry.path', '/docker-entrypoint.sh', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'SELECT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'SELECT', 1, 'classloader.check-leaked-classloader', 'false', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$classloader.check-leaked-classloader', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$classloader.check-leaked-classloader', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'prometheusHost', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'prometheusPort', '9090', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.factory.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporterFactory', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'akka.client.timeout', '300s', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'akka.ask.timeout', '50s', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES ( -2, -122, 0, 'INPUT', 0, 'akka.tcp.timeout', '60s', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'high-availability', 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink112/ha', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 1, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'SELECT', 1, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$state.backend.incremental', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$state.backend.incremental', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'restart-strategy', 'none', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'restart-strategy.failure-rate.delay', '1s', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'restart-strategy.failure-rate.failure-rate-interval', '1min', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'restart-strategy.failure-rate.max-failures-per-interval', '2', null, 'deploymode$perjob', null, null, '2022-12-26 15:14:21', '2022-12-26 15:14:21', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION', null, 'deploymode$perjob', null, null, '2022-12-26 15:17:19', '2022-12-26 15:17:19', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, 'INPUT', 0, 'pluginsDistDir', '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs', null, 'deploymode$perjob', null, null, '2022-12-26 15:17:28', '2022-12-26 15:17:28', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, '2023-02-07 11:56:51', '2023-02-07 11:56:51', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$perjob$classloader.resolve-order', null, null, '2023-02-07 11:56:51', '2023-02-07 11:56:51', 0);
INSERT INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES (-2, -122, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$perjob$classloader.resolve-order', null, null, '2023-02-07 11:56:51', '2023-02-07 11:56:51', 0);






INSERT ignore INTO dagschedulex.schedule_dict
(dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES
('tips', 'kubernetes.namespace', 'k8s namespace', '0', 25, 5, 'STRING', 'kubernetes', 0, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
('tips', 'containerized.master.env.HADOOP_USER_NAME', 'hadoop用户', '0', 25, 9, 'STRING', 'kubernetes', 0, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
('tips', 'containerized.taskmanager.env.HADOOP_USER_NAME', 'hadoop用户', '0', 25, 10, 'STRING', 'kubernetes', 0, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0)
;

update console_component_config set value  = '["perjob","session"]' where component_id = '-122' and cluster_id = -2
and `key` = 'deploymode';

-- -122,k8s-hdfs2-flink116、k8s-hdfs3-flink116，公共参数和k8s参数
INSERT ignore INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
(-2, -122, 0, 'GROUP', 1, 'session', 'session', null, 'deploymode', 'session', null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'jobmanager.memory.process.size', '1024m', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'taskmanager.memory.process.size', '2048m', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'taskmanager.numberOfTaskSlots', '1', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'slotmanager.number-of-slots.max', '10', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'slotmanager.number-of-slots.debug.max', '2', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'SELECT', 0, 'high-availability', 'NONE', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'high-availability.storageDir', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'kubernetes.container.image', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'SELECT', 1, 'kubernetes.rest-service.exposed.type', 'NodePort', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'SELECT', 0, 'kubernetes.container.image.pull-policy', 'IfNotPresent', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 0, 'kubernetes.service-account', 'default', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 0, 'kubernetes.namespace', 'default', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 1, 'kubernetes.entry.path', '/docker-entrypoint.sh', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 0, 'containerized.master.env.KUBERNETES_HOST_ALIASES', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 0, 'containerized.taskmanager.env.KUBERNETES_HOST_ALIASES', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0),
(-2, -122, 0, 'INPUT', 0, 'containerized.master.env.HADOOP_USER_NAME', 'admin', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0)
;

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'NONE', 'NONE', null, 'deploymode$session$high-availability', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'ZOOKEEPER', 'ZOOKEEPER', null, 'deploymode$session$high-availability', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', null, 'deploymode$session$high-availability', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'ClusterIP', 'ClusterIP', null, 'deploymode$session$kubernetes.rest-service.exposed.type', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'NodePort', 'NodePort', null, 'deploymode$session$kubernetes.rest-service.exposed.type', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'LoadBalancer', 'LoadBalancer', null, 'deploymode$session$kubernetes.rest-service.exposed.type', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'IfNotPresent', 'IfNotPresent', null, 'deploymode$session$kubernetes.container.image.pull-policy', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'Always', 'Always', null, 'deploymode$session$kubernetes.container.image.pull-policy', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
(-2, -122, 0, '', 1, 'Never', 'Never', null, 'deploymode$session$kubernetes.container.image.pull-policy', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);


-- -122,k8s-hdfs2-flink112、k8s-hdfs3-flink112，metric监控参数
INSERT ignore INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
    VALUES
(-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.factory.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporterFactory', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'INPUT', 1, 'prometheusHost', '', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null),
(-2, -122, 0, 'INPUT', 1, 'prometheusPort', '9090', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null)
;

INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);



-- -122,k8s-hdfs2-flink112、k8s-hdfs3-flink112，容错和checkpointing
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'state.checkpoints.dir', 'hdfs://ns1/dtInsight/flink112/checkpoints', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'state.savepoints.dir', 'hdfs://ns1/dtInsight/flink112/savepoints', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'execution.checkpointing.externalized-checkpoint-retention', 'RETAIN_ON_CANCELLATION', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'restart-strategy', 'none', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

-- -122,k8s-hdfs2-flink112、k8s-hdfs3-flink112，高级参数
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'SELECT', 1, 'classloader.resolve-order', 'parent-first', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'hdfs://ns1/dtInsight/flink112/completed-jobs', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'pipeline.operator-chaining', 'false', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'classloader.check-leaked-classloader', 'false', null, 'deploymode$session', null, null, '2023-03-23 19:16:05', '2023-03-23 19:16:05', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'classloader.parent-first-patterns.default', 'java.;scala.;org.apache.flink.;com.esotericsoftware.kryo;javax.annotation.;org.xml;javax.xml;org.apache.xerces;org.w3c;org.rocksdb.;org.slf4j;org.apache.log4j;org.apache.logging;org.apache.commons.logging;ch.qos.logback', null, 'deploymode$session', null, null, '2023-03-23 19:16:05', '2023-03-23 19:16:05', 0, null, null);

INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'parent-first', 'parent-first', null, 'deploymode$session$classloader.resolve-order', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'child-first', 'child-first', null, 'deploymode$session$classloader.resolve-order', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, '', 1, 'child-first-cache', 'child-first-cache', null, 'deploymode$session$classloader.resolve-order', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);

-- -122,k8s-hdfs2-flink112、k8s-hdfs3-flink112，JVM参数
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);


INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'checkSubmitJobGraphInterval', '60', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'clusterMode', 'session', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'flinkLibDir', '/data/insight_plugin1.16/chunjun_lib', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'flinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'monitorAcceptedApp', 'false', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'pluginLoadMode', 'classpath', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'sessionRetryNum', '5', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'sessionStartAuto', 'true', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 0, 'yarnAccepterTaskNumber', '3', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'remoteFlinkxDistDir', '/data/insight_plugin1.16/chunjunplugin', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'flinkSessionName', 'flink_session', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'remoteFlinkLibDir', '/data/insight_plugin1.16/chunjun_lib', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 0, null, null);
INSERT ignore INTO dagschedulex.console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value) VALUES (-2, -122, 0, 'INPUT', 1, 'pluginsDistDir', '/opt/dtstack/DTEnginePlugin/EnginePlugin/pluginLibs', null, 'deploymode$session', null, null, '2023-06-08 19:30:52', '2023-06-08 19:30:52', 1, null, null);



