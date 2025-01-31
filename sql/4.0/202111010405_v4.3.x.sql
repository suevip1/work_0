INSERT INTO console_component_config (cluster_id,component_id,component_type_code,`type`,required,`key`,value,`values`,dependencyKey,dependencyValue,`desc`,gmt_create,gmt_modified,is_deleted) VALUES
    (-2,-118,0,'CHECKBOX',1,'deploymode','["perjob"]',NULL,'','',NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'GROUP',1,'perjob','',NULL,'deploymode','perjob',NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'clusterMode','perjob',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'flinkLibDir','/data/insight_plugin112/flink112_lib',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'flinkxDistDir','/data/insight_plugin112/flinkxplugin',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'remoteFlinkxDistDir','/opt/flinkx-dist',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'pluginLoadMode','shipfile',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'classloader.dtstack-cache','true',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'false','false',NULL,'deploymode$perjob$classloader.dtstack-cache',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'true','true',NULL,'deploymode$perjob$classloader.dtstack-cache',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'containerized.master.env.KUBERNETES_HOST_ALIASES','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'containerized.taskmanager.env.KUBERNETES_HOST_ALIASES','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'jobmanager.memory.process.size','1024m',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'taskmanager.memory.process.size','2048m',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'taskmanager.numberOfTaskSlots','1',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'kubernetes.container.image','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'kubernetes.rest-service.exposed.type','NodePort',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'kubernetes.container.image.pull-policy','IfNotPresent',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'kubernetes.service-account','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'kubernetes.entry.path','/docker-entrypoint.sh',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'classloader.resolve-order','parent-first',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'SELECT',1,'classloader.check-leaked-classloader','false',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'false','false',NULL,'deploymode$perjob$classloader.check-leaked-classloader',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'true','true',NULL,'deploymode$perjob$classloader.check-leaked-classloader',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'prometheusHost','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'prometheusPort','9090',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'metrics.reporter.promgateway.class','org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'metrics.reporter.promgateway.host','',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'metrics.reporter.promgateway.port','9091',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'metrics.reporter.promgateway.jobName','112job',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'SELECT',1,'metrics.reporter.promgateway.deleteOnShutdown','true',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'false','false',NULL,'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'true','true',NULL,'deploymode$perjob$metrics.reporter.promgateway.deleteOnShutdown',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'SELECT',1,'metrics.reporter.promgateway.randomJobNameSuffix','true',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'false','false',NULL,'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'true','true',NULL,'deploymode$perjob$metrics.reporter.promgateway.randomJobNameSuffix',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'akka.client.timeout','300s',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'akka.ask.timeout','50s',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'akka.tcp.timeout','60s',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'high-availability','org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'high-availability.storageDir','hdfs://ns1/dtInsight/flink112/ha',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'jobmanager.archive.fs.dir','hdfs://ns1/dtInsight/flink112/completed-jobs',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'state.backend','RocksDB',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'state.checkpoints.dir','hdfs://ns1/dtInsight/flink112/checkpoints',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',1,'state.savepoints.dir','hdfs://ns1/dtInsight/flink112/savepoints',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'state.checkpoints.num-retained','11',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'SELECT',1,'state.backend.incremental','true',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'false','false',NULL,'deploymode$perjob$state.backend.incremental',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'',1,'true','true',NULL,'deploymode$perjob$state.backend.incremental',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'restart-strategy','failurerate',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'restart-strategy.failure-rate.delay','1s',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'restart-strategy.failure-rate.failure-rate-intervalattempts','1min',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
    (-2,-118,0,'INPUT',0,'restart-strategy.failure-rate.max-failures-per-interval','2',NULL,'deploymode$perjob',NULL,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES('typename_mapping', 'k8s-hdfs2-flink112', '-118', NULL, 6, 0, 'LONG', '', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES('typename_mapping', 'k8s-hdfs3-flink112', '-118', NULL, 6, 0, 'LONG', '', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);