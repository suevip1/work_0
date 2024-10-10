-- 需求：dtScript on k8s，思路：schedule_dict --> console_component_config，add by qiuyun
-- 修改 schedule_dict type = 14 依赖 k8s 的组件配置
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
        }
      ],
      "SPARK": [
        {
          "2.4": "k8s-hdfs2-spark240"
        }
      ],
      "DT_SCRIPT": "k8s-hdfs2-dtscript",
      "HDFS": "k8s-hdfs2-hadoop2"
    },
    "hdfs3": {
      "FLINK": [],
      "DT_SCRIPT": "k8s-hdfs3-dtscript",
      "SPARK":[],
      "HDFS": "k8s-hdfs3-hadoop3"
    }
  }
}'
where `type` = 14 and dict_code = 'component_model_config' and (dict_name = '' or dict_name = ' ')
  and depend_name = 'KUBERNETES';

-- 刷 k8s-hdfs-dtscript start ------
delete from schedule_dict where dict_code = 'typename_mapping' and dict_name in ('k8s-hdfs2-dtscript','k8s-hdfs3-dtscript');
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES ('typename_mapping', 'k8s-hdfs2-dtscript', '-135', null, 6, 0, 'LONG', '', 0, '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES ('typename_mapping', 'k8s-hdfs3-dtscript', '-135', null, 6, 0, 'LONG', '', 0, '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);

delete from console_component_config where component_id = -135;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.python.image', 'python:1.0.0', null, null, null, 'python镜像名，跟pyspark共用一个镜像', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.tensorflow.image', 'tensorflow-cpu:1.0.0', null, null, null, 'tensorflow镜像名，根据是否使用GPU区分，GPU版本镜像名;tensorflow-gpu:1.0.0，CPU版本镜像名;tensorflow-cpu:1.0.0，默认使用CPU镜像',
            '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.pytorch.image', 'pytorch-cpu:1.0.0', null, null, null, 'pytorch镜像名，根据是否使用GPU区分，GPU版本镜像名;pytorch-gpu:1.0.0，CPU版本镜像名;pytorch-cpu:1.0.0，默认使用CPU镜像', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.model.service.image', 'modelservice-cpu:1.0.0', null, null, null, 'model service镜像名，当前默认只能使用CPU进行推理，后期将扩展', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.py4j.image', 'py4j:1.0.0', null, null, null, 'py4j镜像名', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.filebeat.image', 'filebeat:1.0.0', null, null, null, 'filebeat镜像名', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'kubernetes.pods.service-account', 'default', null, null, null, 'pod对namespace的操作权限', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'image.pull.policy', 'IfNotPresent', null, null, null, '镜像拉取策略', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'image.pull.secrets', '', null, null, null, '镜像拉取鉴权', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'dtscript.env.HADOOP_HOST_ALIASES', '', null, null, null, '对接hdfs集群的host', '2022-03-01 19:50:24', '2022-03-01 19:50:24', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 1, 'hadoop.username', 'admin', null, null, null, null, '2022-03-03 19:12:54', '2022-03-03 19:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'dtscript.env.KUBERNETES_FLINKX_HOSTS', '', null, null, null, null, '2022-03-03 19:12:54', '2022-03-03 19:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'dtscript.env.jdbcUrl', '', null, null, null, null, '2022-03-03 19:12:54', '2022-03-03 19:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'dtscript.env.user', '', null, null, null, null, '2022-03-03 19:12:54', '2022-03-03 19:12:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
    VALUES (-2, -135, 3, 'INPUT', 0, 'dtscript.env.password', '', null, null, null, null, '2022-03-03 19:12:54', '2022-03-03 19:12:54', 0);
-- 刷 k8s-hdfs-dtscript end ------

-- 刷 k8s-nfs-flink110 start ------
delete from schedule_dict where dict_code = 'typename_mapping' and dict_name in ('k8s-nfs-flink110');
delete from console_component_config where component_id = -136;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'k8s-nfs-flink110', '-136', null, 6, 0, 'LONG', '', 0, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
    (-2, -136, 0, 'CHECKBOX', 1, 'deploymode', '["perjob","session"]', null, '', '', null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'GROUP', 1, 'perjob', '', null, 'deploymode', 'perjob', null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8', null,
     'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'remoteFlinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.cluster-id', '/default', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.storageDir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/ha', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/completed-jobs', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '110job', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'false', 'false', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'true', 'true', null, 'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'state.checkpoints.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/checkpoints', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.savepoints.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/savepoints', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'kubernetes.host.aliases', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.rest-service.exposed.type', 'NodePort', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.jobmanager.service-account', 'default', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image.pull-policy', 'IfNotPresent', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image.pull-secrets', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'flinkx.hosts', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusClass', 'com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'pluginLoadMode', 'classpath', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'submitTimeout', '5', null, 'deploymode$perjob', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'GROUP', 1, 'session', '', null, 'deploymode', 'session', null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'classloader.dtstack-cache', 'true', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'classloader.resolve-order', 'parent-first', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'env.java.opts', '-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:MaxMetaspaceSize=500m -Dfile.encoding=UTF-8',
     null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'flinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'flinkPluginRoot', '/data/insight_plugin', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'remoteFlinkJarPath', '/data/insight_plugin/flink110_lib', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.cluster-id', '/default', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.storageDir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/ha', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'jobmanager.archive.fs.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/completed-jobs', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'SELECT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.deleteOnShutdown', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '110job', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'SELECT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'false', 'false', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, '', 1, 'true', 'true', null, 'deploymode$session$metrics.reporter.promgateway.randomJobNameSuffix', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.backend', 'RocksDB', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.backend.incremental', 'true', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'state.checkpoints.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/checkpoints', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'state.checkpoints.num-retained', '11', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'state.savepoints.dir', 'nfs://${nfs_service}:${nfs_port}/dtInsight/flink110/savepoints', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.host.aliases', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.rest-service.exposed.type', 'NodePort', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.jobmanager.service-account', 'default', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image.pull-policy', 'IfNotPresent', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'kubernetes.container.image.pull-secrets', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'flinkx.hosts', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusClass', 'com.dtstack.jlogstash.metrics.promethues.PrometheusPushGatewayReporter', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusHost', '', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'prometheusPort', '9090', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'pluginLoadMode', 'classpath', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'flinkSessionSlotCount', '10', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'sessionStartAuto', 'true', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 0, 'submitTimeout', '5', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
    (-2, -136, 0, 'INPUT', 1, 'remotePluginRootDir', '/data/insight_plugin', null, 'deploymode$session', null, null, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0);
-- 刷 k8s-nfs-flink110 end ------

-- 刷 k8s-hdfs-spark start-------
delete from console_component_config where component_id = -107;
INSERT INTO `console_component_config`
    (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
 (-2, -107, 1, 'CHECKBOX', 1, 'deploymode', '[\"perjob\"]', NULL, '', '', NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'GROUP', 1, 'perjob', '', NULL, 'deploymode', 'perjob', NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.driver.extraJavaOptions', '-Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.executor.extraJavaOptions', '-Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.eventLog.dir', 'hdfs://ns1/tmp/logs', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.executor.instances', '1', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.executor.cores', '1', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.executor.memory', '1g', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.submit.deployMode', 'cluster', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.eventLog.compress', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.eventLog.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.kubernetes.authenticate.driver.serviceAccountName', 'default', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'spark.kubernetes.container.image', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.files', 'file:///opt/spark/conf/*', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.kubernetes.driverEnv.KUBERNETES_HOST_ALIASES', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.executorEnv.KUBERNETES_HOST_ALIASES', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'sparkPythonExtLibPath', 'hdfs://ns1/dtInsight/spark240/pythons/pyspark.zip,hdfs://ns1/dtInsight/spark240/pythons/py4j-0.10.7-src.zip', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 1, 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark240/client/spark-sql-proxy.jar', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'spark.kubernetes.container.image.pullSecrets', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0),
 (-2, -107, 1, 'INPUT', 0, 'flinkxHosts', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0) ;
-- 刷 k8s-hdfs-spark end-------