-- 更新 flink 部分参数为必填项
update console_component_config
SET `required` = 1
where `key` in ('prometheusHost',
                'prometheusPort',
                'state.savepoints.dir')
  and component_type_code = 0;

update console_component_config
SET `required` = 1
where `key` = 'pluginsDistDir'
  and dependencyKey = 'deploymode$session'
  and component_type_code = 0;

-- 更新 flink 部分参数为非必填项
update console_component_config
SET `required` = 0
where `key` in ('high-availability.zookeeper.quorum',
                'high-availability.zookeeper.path.root',
                'high-availability.storageDir',
                'high-availability.cluster-id')
  and component_type_code in (0, 20)
  and (dependencyKey IS NULL or dependencyKey = 'deploymode$standalone');

-- 更新 spark 部分参数为必填项
update console_component_config
SET `required` = 1
where `key` in ('metrics.prometheus.server.host',
                'metrics.prometheus.server.port',
                'metrics.prometheus.sink.pushgateway.host',
                'metrics.prometheus.sink.pushgateway.port',
                'spark.yarn.appMasterEnv.PYSPARK_PYTHON',
                'spark.eventLog.dir')
  and component_type_code = 1;

update console_component_config
SET `required` = 1
where `key` = 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON'
  and component_type_code = 1;