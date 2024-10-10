## spark 组件 ##
## 添加分组项 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '主要' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','主要',25,'1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '资源' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','资源',25,'1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '网络' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','网络',25,'1', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '事件日志' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','事件日志',25,'1', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'JVM' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','JVM',25,'1', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '环境变量' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','环境变量',25,'1', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','kubernetes',25,'1', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '安全' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','安全',25,'1', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '其它' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','其它',25,'1', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '自定义参数' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','自定义参数',25,'1', 10);

## 主要 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.submit.deployMode' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.submit.deployMode','spark driver的jvm扩展参数',25,'主要','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sparkPythonExtLibPath' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sparkPythonExtLibPath','远程存储系统上pyspark.zip和py4j-0.10.7-src.zip的路径
注：pyspark.zip和py4j-0.10.7-src.zip在$SPARK_HOME/python/lib路径下获取',25,'主要','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sparkSqlProxyPath' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sparkSqlProxyPath','远程存储系统上spark-sql-proxy.jar路径
注：spark-sql-proxy.jar是用来执行spark sql的jar包',25,'主要','1', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.files' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.files','用于将文件放置到executor工作目录下',25,'主要','1', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.yarn.maxAppAttempts' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.yarn.maxAppAttempts','spark driver最大尝试次数, 默认为yarn上yarn.resourcemanager.am.max-attempts配置的值
注：如果spark.yarn.maxAppAttempts配置的大于yarn.resourcemanager.am.max-attempts则无效',25,'主要','1', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sparkYarnArchive' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sparkYarnArchive','远程存储系统上spark jars的路径',25,'主要','1', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarnAccepterTaskNumber' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarnAccepterTaskNumber','允许yarn上同时存在状态为accepter的任务数量，当达到这个值后会禁止任务提交',25,'主要','1', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.speculation' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.speculation','spark任务推测行为',25,'主要','1', 8);

## 资源 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executor.cores' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executor.cores','每个executor可以使用的cpu核数',25,'资源','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executor.memory' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executor.memory','每个executor可以使用的内存量',25,'资源','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executor.instances' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executor.instances','executor实例数',25,'资源','1', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.cores.max' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.cores.max',' standalone模式下任务最大能申请的cpu核数',25,'资源','1', 4);

## 网络 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.network.timeout' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.network.timeout','spark中所有网络交互的最大超时时间',25,'网络','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.rpc.askTimeout' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.rpc.askTimeout','RPC 请求操作在超时之前等待的持续时间',25,'网络','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executor.heartbeatInterval' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executor.heartbeatInterval','driver和executor之间心跳时间间隔',25,'网络','1', 3);

## 事件日志 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.compress' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.eventLog.compress','是否对spark事件日志进行压缩',25,'事件日志','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.dir' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.eventLog.dir','spark事件日志存放路径',25,'事件日志','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.eventLog.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.eventLog.enabled','是否记录 spark 事件日志',25,'事件日志','1', 3);

## JVM ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.driver.extraJavaOptions' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.driver.extraJavaOptions','spark driver的jvm扩展参数',25,'JVM','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executor.extraJavaOptions' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executor.extraJavaOptions','spark executor的jvm扩展参数',25,'JVM','1', 2);

## 环境变量 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.kubernetes.driverEnv.KUBERNETES_HOST_ALIASES' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.kubernetes.driverEnv.KUBERNETES_HOST_ALIASES','用于在spark driver pod中添加自定义域名解析，格式: ip1 hostname1;ip2 hostname2',25,'环境变量','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON','driver中用于执行pyspark任务的python二进制可执行文件路径',25,'环境变量','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.yarn.appMasterEnv.PYSPARK_PYTHON' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.yarn.appMasterEnv.PYSPARK_PYTHON','用于执行pyspark任务的python二进制可执行文件路径',25,'环境变量','1', 3);

## kubernetes ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.kubernetes.container.image' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.kubernetes.container.image','用于spark任务的镜像名',25,'kubernetes','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.kubernetes.authenticate.driver.serviceAccountName' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.kubernetes.authenticate.driver.serviceAccountName','driver pod用于请求api server创建executor pod的 service account',25,'kubernetes','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.kubernetes.container.image.pullSecrets' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.kubernetes.container.image.pullSecrets','kubernetes secret用于从私有镜像仓库拉取镜像',25,'kubernetes','1', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.kubernetes.container.image.pullPolicy' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.kubernetes.container.image.pullPolicy','kubernetes容器镜像拉取策略，当前支持三种策略 1.IfNotPresent：当前节点如镜像存在则不拉取 2.Always：总是拉取镜像 3.Never：不拉取镜像',25,'kubernetes','1', 4);

## 安全 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.yarn.security.credentials.hive.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.yarn.security.credentials.hive.enabled','开启kerberos场景下是否获取hive 票据',25,'安全','1', 8);

## 其它 ##


## fink组件 ##
## 添加分组项 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '公共参数' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','公共参数',25,'0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.rpc.address' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.rpc.address','ha 模式下 jobmanager 远程调用地址',25,'公共参数','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.rpc.port' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.rpc.port','ha 模式下 jobmanager 远程调用端口',25,'公共参数','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.mb' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.memory.mb','jobmanager总内存',25,'公共参数','0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'rest.port' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','rest.port','客户端连接到jobmanger的端口。如果rest.bind-port未指定，则REST服务器将绑定到此端口。注意:只有在高可用性配置为NONE时才会考虑此选项。',25,'公共参数','0', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '高可用' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','高可用',25,'0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metric监控' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','metric监控',25,'0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '容错和checkpointing' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','容错和checkpointing',25,'0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '高级' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','高级',25,'0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'JVM参数' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','JVM参数',25,'0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'Yarn' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','Yarn',25,'0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','kubernetes',25,'0', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '数栈平台参数' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','数栈平台参数',25,'0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '其它' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','其它',25,'0', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '自定义参数' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','自定义参数',25,'0', 11);

## 公共参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.process.size' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.memory.process.size','JobManager 总内存(master)',25,'公共参数','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.memory.process.size' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.memory.process.size','TaskManager 总内存(slaves)',25,'公共参数','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.numberOfTaskSlots' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.numberOfTaskSlots','单个 TaskManager 可以运行的并行算子或用户函数实例的数量。',25,'公共参数','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.memory.mb' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.memory.mb','taskmanager堆内存',25,'公共参数','0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.heap.mb' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.heap.mb','taskmanager堆内存',25,'公共参数','0', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.mb' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.memory.mb','jobmanager总内存',25,'公共参数','0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'slotmanager.number-of-slots.max' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','slotmanager.number-of-slots.max','flink session允许的最大slot数',25,'公共参数','0', 11);
## 高可用 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability','flink ha类型',25,'高可用','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.quorum' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.zookeeper.quorum','zookeeper地址，当ha选择是zookeeper时必填',25,'高可用','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.path.root' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.zookeeper.path.root','ha节点路径',25,'高可用','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.storageDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.storageDir','ha元数据存储路径',25,'高可用','0', 4);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.cluster-id' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.cluster-id','Flink集群ID，用于多个Flink集群之间的隔离。',25,'高可用','0', 5);

## metric监控 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusHost' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusHost','prometheus地址，平台端使用',25,'metric监控','0', 0);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusPort' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusPort','prometheus，平台端使用',25,'metric监控','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusClass' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusClass','prometheus，平台端使用',25,'metric监控','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.class' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.class','用来推送指标类',25,'metric监控','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.host' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.host','promgateway地址',25,'metric监控','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.port' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.port','promgateway端口',25,'metric监控','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.deleteOnShutdown' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.deleteOnShutdown','任务结束后是否删除指标',25,'metric监控','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.jobName' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.jobName','指标任务名',25,'metric监控','0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.randomJobNameSuffix'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.randomJobNameSuffix','是否在任务名上添加随机值',25,'metric监控','0', 8);

## 容错和checkpointing ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy','none, off, disable:无重启策略。Fixed -delay, Fixed -delay:固定延迟重启策略。更多细节可以在这里找到。Failure -rate:故障率重启策略。更多细节可以在这里找到。如果检查点被禁用，默认值为none。如果检查点启用，默认值是fixed-delay with Integer。MAX_VALUE重启尝试和''1 s''延迟。',25,'容错和checkpointing','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.delay'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.delay','如果restart-strategy设置为根据失败率重试，则两次连续重启尝试之间的延迟。可以用“1分钟”、“20秒”来表示',25,'容错和checkpointing','0', 2);

-- 删掉原先的错误参数
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.failure-rate-intervalattempts'  AND dict_desc = '0';
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.failure-rate-interval'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.failure-rate-interval','如果重启策略设置为故障率，测量故障率的时间间隔。可以用“1分钟”、“20秒”来表示。',25,'容错和checkpointing','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.max-failures-per-interval'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.max-failures-per-interval','如果restart-strategy设置为根据失败率重试，在给定的时间间隔内，任务失败前的最大重启次数。',25,'容错和checkpointing','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.backend'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.backend','状态后端',25,'容错和checkpointing','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.backend.incremental'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.backend.incremental','是否开启增量',25,'容错和checkpointing','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.dir'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.checkpoints.dir','checkpoint路径地址',25,'容错和checkpointing','0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.num-retained'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.checkpoints.num-retained','checkpoint保存个数',25,'容错和checkpointing','0', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.savepoints.dir'  AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.savepoints.dir','savepoint路径',25,'容错和checkpointing','0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'checkpoint.retain.time' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','checkpoint.retain.time','检查点保留时间',25,'容错和checkpointing','0', 10);

## 高级 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.resolve-order' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.resolve-order','类加载模式',25,'高级','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.archive.fs.dir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.archive.fs.dir','任务结束后任务信息存储路径',25,'高级','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'akka.ask.timeout' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','akka.ask.timeout','akka通讯超时时间',25,'高级','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'akka.tcp.timeout' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','akka.tcp.timeout','tcp 连接的超时时间',25,'高级','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobstore.expiration-time' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobstore.expiration-time','',25,'高级','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'security.kerberos.login.contexts' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','security.kerberos.login.contexts','Flink 的 kafka 认证KafkaClient, Flink 的 ZK 认证 Client。如果两者都有，值为 Client,KafkaClient',25,'高级','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'blob.service.cleanup.interval' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','blob.service.cleanup.interval','任务管理器中blob缓存的清理间隔(秒)',25,'高级','0', 7);

## JVM参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts','jvm参数',25,'JVM参数','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts.jobmanager' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts.jobmanager','启动jobmanager时的JVM参数',25,'JVM参数','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts.taskmanager' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts.taskmanager','debug tm的jvm参数',25,'JVM参数','0', 3);

## Yarn ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempt-failures-validity-interval' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application-attempt-failures-validity-interval','以毫秒为单位的时间窗口，它定义了重新启动 AM 时应用程序尝试失败的次数。不在此窗口范围内的故障不予考虑。将此值设置为 -1 以便全局计数。',25,'Yarn','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempts' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application-attempts','ApplicationMaster 重新启动的次数。默认情况下，该值将设置为 1。如果启用了高可用性，则默认值为 2。重启次数也受 YARN 限制（通过 yarn.resourcemanager.am.max-attempts 配置）。注意整个 Flink 集群会重启，YARN Client 会失去连接。',25,'Yarn','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'queue' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','queue','yarn容器中的队列',25,'Yarn','0', 3);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application.queue' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application.queue','yarn 队列名称',25,'Yarn','0', 4);

## (flink on)kubernetes ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.container.image' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.container.image','flink镜像',25,'kubernetes','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.rest-service.exposed.type' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.rest-service.exposed.type','暴露job manager以nodePort方式进行通信',25,'kubernetes','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.container.image.pull-policy' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.container.image.pull-policy','拉镜像的策略',25,'kubernetes','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.service-account' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.service-account','k8s service account',25,'kubernetes','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.entry.path' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.entry.path','容器启动脚本',25,'kubernetes','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'containerized.master.env.KUBERNETES_HOST_ALIASES' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','containerized.master.env.KUBERNETES_HOST_ALIASES','job manager 域名配置',25,'kubernetes','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'containerized.taskmanager.env.KUBERNETES_HOST_ALIASES' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','containerized.taskmanager.env.KUBERNETES_HOST_ALIASES','task manager 域名配置',25,'kubernetes','0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS','job manager filebeat配置',25,'kubernetes','0', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS','task manager filebeat配置',25,'kubernetes','0', 9);

## 数栈平台参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'clusterMode' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','clusterMode','任务执行模式：perjob,session,standalone',25,'数栈平台参数','0', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkJarPath' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkJarPath','flink lib path',25,'数栈平台参数','0', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remoteFlinkJarPath' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remoteFlinkJarPath','flink lib远程路径',25,'数栈平台参数','0', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkPluginRoot' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkPluginRoot','flinkStreamSql和flinkx plugins父级本地目录',25,'数栈平台参数','0', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remotePluginRootDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remotePluginRootDir','flinkStreamSql和flinkx plugins父级远程目录',25,'数栈平台参数','0', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'pluginLoadMode' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','pluginLoadMode','插件加载类型',25,'数栈平台参数','0', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.dtstack-cache' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.dtstack-cache','是否缓存classloader',25,'数栈平台参数','0', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkx.hosts' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkx.hosts','dtlogger中flinkx地址',25,'数栈平台参数','0', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkSessionSlotCount' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkSessionSlotCount','10	flink session允许的最大slot数',25,'数栈平台参数','0', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sessionStartAuto' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sessionStartAuto','是否允许engine启动flink session',25,'数栈平台参数','0', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'submitTimeout' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','submitTimeout','单位分钟，任务提交超时时间',25,'数栈平台参数','0', 11);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'checkSubmitJobGraphInterval' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','checkSubmitJobGraphInterval','session check间隔（60 * 10s）',25,'数栈平台参数','0', 13);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkLibDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkLibDir','session check间隔（60 * 10s）',25,'数栈平台参数','0', 14);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkxDistDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkxDistDir','flinkx plugins父级本地目录',25,'数栈平台参数','0', 15);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remoteFlinkLibDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remoteFlinkLibDir','flink lib 远程路径',25,'数栈平台参数','0', 16);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remoteFlinkxDistDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remoteFlinkxDistDir','flinkx plugins父级远程目录',25,'数栈平台参数','0', 17);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'queue' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','queue','yarn队列',25,'数栈平台参数','0', 18);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkSessionName' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkSessionName','yarn session名称',25,'数栈平台参数','0', 19);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jarTmpDir' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jarTmpDir','jar包临时目录',25,'数栈平台参数','0', 20);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'gatewayJobName' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','gatewayJobName','',25,'数栈平台参数','0', 21);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarnAccepterTaskNumber' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarnAccepterTaskNumber','允许yarn accepter任务数量，达到这个值后不允许任务提交',25,'数栈平台参数','0', 22);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'monitorAcceptedApp' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','monitorAcceptedApp','是否监控yarn accepted状态任务',25,'数栈平台参数','0', 23);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sessionRetryNum' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sessionRetryNum','session重试次数，达到后会放缓重试的频率',25,'数栈平台参数','0', 24);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'web.timeout' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','web.timeout','web的超时时间',25,'数栈平台参数','0', 27);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hadoopUserName' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','hadoopUserName','hadoop用户名',25,'数栈平台参数','0', 28);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarnSessionName' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarnSessionName','session名字',25,'数栈平台参数','0', 29);

## fink组件 end ##

## 添加分组项 ##
## flink on standalone ##
## 备注：standalone 情况下，由于历史遗留原因，导致新增时加载模板和保存后加载配置，用的 component_type_code 不同，所以要尽量让相同 key 的 sort 相同
## 公共参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.process.size' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.memory.process.size','JobManager 总内存(master)',25,'公共参数','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.memory.mb' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.memory.mb','jobmanager总内存',25,'公共参数','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.rpc.address' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.rpc.address','ha 模式下 jobmanager 远程调用地址',25,'公共参数','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.rpc.port' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.rpc.port','ha 模式下 jobmanager 远程调用端口',25,'公共参数','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.memory.process.size' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.memory.process.size','TaskManager 总内存(slaves)',25,'公共参数','20', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.numberOfTaskSlots' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.numberOfTaskSlots','单个 TaskManager 可以运行的并行算子或用户函数实例的数量。',25,'公共参数','20', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'taskmanager.memory.mb' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','taskmanager.memory.mb','taskmanager堆内存',25,'公共参数','20', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'rest.port' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','rest.port','客户端连接到jobmanger的端口。如果rest.bind-port未指定，则REST服务器将绑定到此端口。注意:只有在高可用性配置为NONE时才会考虑此选项。',25,'公共参数','20', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'slotmanager.number-of-slots.max' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','slotmanager.number-of-slots.max','flink session允许的最大slot数',25,'公共参数','20', 11);
## 高可用 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability','flink ha类型',25,'高可用','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.quorum' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.zookeeper.quorum','zookeeper地址，当ha选择是zookeeper时必填',25,'高可用','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.zookeeper.path.root' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.zookeeper.path.root','ha节点路径',25,'高可用','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.storageDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.storageDir','ha元数据存储路径',25,'高可用','20', 4);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'high-availability.cluster-id' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','high-availability.cluster-id','Flink集群ID，用于多个Flink集群之间的隔离。',25,'高可用','20', 5);

## metric监控 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusHost' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusHost','prometheus地址，平台端使用',25,'metric监控','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusPort' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusPort','prometheus，平台端使用',25,'metric监控','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.class' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.class','用来推送指标类',25,'metric监控','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.host' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.host','promgateway地址',25,'metric监控','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.port' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.port','promgateway端口',25,'metric监控','20', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.deleteOnShutdown' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.deleteOnShutdown','任务结束后是否删除指标',25,'metric监控','20', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.jobName' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.jobName','指标任务名',25,'metric监控','20', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'metrics.reporter.promgateway.randomJobNameSuffix'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','metrics.reporter.promgateway.randomJobNameSuffix','是否在任务名上添加随机值',25,'metric监控','20', 8);

## 容错和checkpointing ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy','none, off, disable:无重启策略。Fixed -delay, Fixed -delay:固定延迟重启策略。更多细节可以在这里找到。Failure -rate:故障率重启策略。更多细节可以在这里找到。如果检查点被禁用，默认值为none。如果检查点启用，默认值是fixed-delay with Integer。MAX_VALUE重启尝试和''1 s''延迟。',25,'容错和checkpointing','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.delay'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.delay','如果restart-strategy设置为根据失败率重试，则两次连续重启尝试之间的延迟。可以用“1分钟”、“20秒”来表示',25,'容错和checkpointing','20', 2);

-- 删掉原先的错误参数
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.failure-rate-intervalattempts'  AND dict_desc = '20';
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.failure-rate-interval'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.failure-rate-interval','如果重启策略设置为故障率，测量故障率的时间间隔。可以用“1分钟”、“20秒”来表示。',25,'容错和checkpointing','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'restart-strategy.failure-rate.max-failures-per-interval'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','restart-strategy.failure-rate.max-failures-per-interval','如果restart-strategy设置为根据失败率重试，在给定的时间间隔内，任务失败前的最大重启次数。',25,'容错和checkpointing','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.backend'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.backend','状态后端',25,'容错和checkpointing','20', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.backend.incremental'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.backend.incremental','是否开启增量',25,'容错和checkpointing','20', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.dir'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.checkpoints.dir','checkpoint路径地址',25,'容错和checkpointing','20', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.checkpoints.num-retained'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.checkpoints.num-retained','checkpoint保存个数',25,'容错和checkpointing','20', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'state.savepoints.dir'  AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','state.savepoints.dir','savepoint路径',25,'容错和checkpointing','20', 9);

## 高级 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.resolve-order' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.resolve-order','类加载模式',25,'高级','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobmanager.archive.fs.dir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobmanager.archive.fs.dir','任务结束后任务信息存储路径',25,'高级','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'akka.ask.timeout' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','akka.ask.timeout','akka通讯超时时间',25,'高级','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'akka.tcp.timeout' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','akka.tcp.timeout','tcp 连接的超时时间',25,'高级','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jobstore.expiration-time' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jobstore.expiration-time','',25,'高级','20', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'security.kerberos.login.contexts' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','security.kerberos.login.contexts','Flink 的 kafka 认证KafkaClient, Flink 的 ZK 认证 Client。如果两者都有，值为 Client,KafkaClient',25,'高级','20', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'blob.service.cleanup.interval' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','blob.service.cleanup.interval','任务管理器中blob缓存的清理间隔(秒)',25,'高级','20', 7);

## JVM参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts','jvm参数',25,'JVM参数','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts.jobmanager' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts.jobmanager','启动jobmanager时的JVM参数',25,'JVM参数','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'env.java.opts.taskmanager' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','env.java.opts.taskmanager','debug tm的jvm参数',25,'JVM参数','20', 3);

## Yarn ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempt-failures-validity-interval' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application-attempt-failures-validity-interval','以毫秒为单位的时间窗口，它定义了重新启动 AM 时应用程序尝试失败的次数。不在此窗口范围内的故障不予考虑。将此值设置为 -1 以便全局计数。',25,'Yarn','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application-attempts' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application-attempts','ApplicationMaster 重新启动的次数。默认情况下，该值将设置为 1。如果启用了高可用性，则默认值为 2。重启次数也受 YARN 限制（通过 yarn.resourcemanager.am.max-attempts 配置）。注意整个 Flink 集群会重启，YARN Client 会失去连接。',25,'Yarn','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'queue' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','queue','yarn容器中的队列',25,'Yarn','20', 3);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarn.application.queue' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarn.application.queue','yarn 队列名称',25,'Yarn','20', 4);

## 数栈平台参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'clusterMode' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','clusterMode','任务执行模式：perjob,session,standalone',25,'数栈平台参数','20', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkJarPath' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkJarPath','flink lib path',25,'数栈平台参数','20', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remoteFlinkJarPath' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remoteFlinkJarPath','flink lib远程路径',25,'数栈平台参数','20', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkPluginRoot' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkPluginRoot','flinkStreamSql和flinkx plugins父级本地目录',25,'数栈平台参数','20', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remotePluginRootDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remotePluginRootDir','flinkStreamSql和flinkx plugins父级远程目录',25,'数栈平台参数','20', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'pluginLoadMode' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','pluginLoadMode','插件加载类型',25,'数栈平台参数','20', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'classloader.dtstack-cache' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','classloader.dtstack-cache','是否缓存classloader',25,'数栈平台参数','20', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkx.hosts' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkx.hosts','dtlogger中flinkx地址',25,'数栈平台参数','20', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkSessionSlotCount' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkSessionSlotCount','10	flink session允许的最大slot数',25,'数栈平台参数','20', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sessionStartAuto' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sessionStartAuto','是否允许engine启动flink session',25,'数栈平台参数','20', 10);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'submitTimeout' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','submitTimeout','单位分钟，任务提交超时时间',25,'数栈平台参数','20', 11);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'checkpoint.retain.time' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','checkpoint.retain.time','检查点保留时间',25,'数栈平台参数','20', 12);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'prometheusClass' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','prometheusClass','prometheus，平台端使用',25,'数栈平台参数','20', 13);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'checkSubmitJobGraphInterval' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','checkSubmitJobGraphInterval','session check间隔（60 * 10s）',25,'数栈平台参数','20', 14);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkLibDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkLibDir','session check间隔（60 * 10s）',25,'数栈平台参数','20', 15);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkxDistDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkxDistDir','flinkx plugins父级本地目录',25,'数栈平台参数','20', 16);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'remoteFlinkxDistDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','remoteFlinkxDistDir','flinkx plugins父级远程目录',25,'数栈平台参数','20', 17);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'queue' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','queue','yarn队列',25,'数栈平台参数','20', 18);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'flinkSessionName' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','flinkSessionName','yarn session名称',25,'数栈平台参数','20', 19);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'jarTmpDir' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','jarTmpDir','jar包临时目录',25,'数栈平台参数','20', 20);

-- DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'gatewayJobName' AND dict_desc = '20';
-- INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
-- VALUES ('tips','gatewayJobName','',25,'数栈平台参数','20', 21);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarnAccepterTaskNumber' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarnAccepterTaskNumber','允许yarn accepter任务数量，达到这个值后不允许任务提交',25,'数栈平台参数','20', 22);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'monitorAcceptedApp' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','monitorAcceptedApp','是否监控yarn accepted状态任务',25,'数栈平台参数','20', 23);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'sessionRetryNum' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','sessionRetryNum','session重试次数，达到后会放缓重试的频率',25,'数栈平台参数','20', 24);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'web.timeout' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','web.timeout','web的超时时间',25,'数栈平台参数','20', 26);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hadoopUserName' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','hadoopUserName','hadoop用户名',25,'数栈平台参数','20', 28);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'yarnSessionName' AND dict_desc = '20';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','yarnSessionName','session名字',25,'数栈平台参数','20', 29);

## volcano ##
## 分组 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '镜像参数' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','镜像参数',25,'29', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hdfs参数' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','hdfs参数',25,'29', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'ingress参数' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','ingress参数',25,'29', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'pvc参数' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','pvc参数',25,'29', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = '自定义参数' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','自定义参数',25,'29', 4);


## 镜像参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'image.pull.policy' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','image.pull.policy','镜像拉取策略，默认存在则不拉取',25,'镜像参数','29', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'image.pull.secrets' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','image.pull.secrets','k8s secrets',25,'镜像参数','29', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'image.url' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','image.url','镜像仓库地址',25,'镜像参数','29', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.model.service.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.model.service.image','适配cpu的模型推理服务镜像',25,'镜像参数','29', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.py4j.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.py4j.image','适配hadoop的py4j镜像（默认hadoop2）',25,'镜像参数','29', 5);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.logtracer.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.logtracer.image','适配hadoop的容器内日志搜集镜像（默认hadoop2）',25,'镜像参数','29', 6);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.python.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.python.image','python镜像',25,'镜像参数','29', 7);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.pytorch.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.pytorch.image','适配cpu的pytorch镜像',25,'镜像参数','29', 8);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.tensorflow.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.tensorflow.image','适配cpu的tensorflow镜像',25,'镜像参数','29', 9);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.jupyter.image' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','kubernetes.jupyter.image','适配cpu的jupyter镜像',25,'镜像参数','29', 10);

## hdfs参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hadoop.username' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','hadoop.username','hadoop运行用户名',25,'hdfs参数','29', 1);

## ingress参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'ingress.host' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','ingress.host','k8s 集群 host',25,'ingress参数','29', 1);

## pvc参数 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'pvc.name' AND dict_desc = '29';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','pvc.name','k8s 集群 nfs pvc 名称',25,'pvc参数','29', 1);

## DTScript ##
## yarn-hdfs-dtscript ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.java.opts' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.java.opts','dtscript container jvm扩展参数',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.am.memory' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.am.memory','am container使用的内存量',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.am.cores' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.am.cores','am container使用的cpu核数',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.worker.memory' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.worker.memory','work container使用的内存量',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.worker.cores' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.worker.cores','work container使用的cpu核数',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.worker.num' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.worker.num','work container实例数量',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'container.staging.dir' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','container.staging.dir','任务临时文件路径',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.container.heartbeat.interval' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.container.heartbeat.interval','am和work之间的心跳间隔，单位毫秒',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.container.heartbeat.timeout' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.container.heartbeat.timeout','am和work之间的心跳超时时间，单位毫秒',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.python2.path' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.python2.path','python2.x二进制可执行文件地址',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.python3.path' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.python3.path','python3.x二进制可执行文件地址',25,'3');

## k8s-hdfs-dtscript ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hadoop.username' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','hadoop.username','操作hdfs的用户名',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.tensorflow.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.tensorflow.image','tensorflow镜像名，根据是否使用GPU区分，GPU版本镜像名
tensorflow-gpu:1.0.0，CPU版本镜像名
tensorflow-cpu:1.0.0，默认使用CPU镜像',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.python.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.python.image','python镜像名',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.pytorch.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.pytorch.image','pytorch镜像名，根据是否使用GPU区分，GPU版本镜像名
pytorch-gpu:1.0.0，CPU版本镜像名
pytorch-cpu:1.0.0，默认使用CPU镜像',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.model.service.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.model.service.image','model service镜像名，当前默认只能使用CPU进行推理，后期将扩展',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.py4j.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.py4j.image','py4j镜像名',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.filebeat.image' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.filebeat.image','filebeat镜像名',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'kubernetes.pods.service-account' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','kubernetes.pods.service-account','pod对namespace的操作权限',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'image.pull.policy' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','image.pull.policy','镜像拉取策略',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'image.pull.secrets' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','image.pull.secrets','镜像拉取鉴权',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.env.HADOOP_HOST_ALIASES' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.env.HADOOP_HOST_ALIASES','对接hdfs集群的host',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.env.KUBERNETES_FLINKX_HOSTS' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.env.KUBERNETES_FLINKX_HOSTS','logstash服务所在机器及端口',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.env.jdbcUrl' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.env.jdbcUrl','对接hive的连接地址',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.env.user' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.env.user','对接hive的用户名',25,'3');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'dtscript.env.password' AND dict_desc = '3';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','dtscript.env.password','对接hive的密码',25,'3');

## Tony组件 ##
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.application.single-node' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.application.single-node','是否为单节点模式',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hadoopUserName' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','hadoopUserName','hadoop提交用户',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'python3.path' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','python3.path','python3路径',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.am.instances' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.am.instances','实例个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.am.memory' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.am.memory','内存大小，字符串格式',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.am.vcores' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.am.vcores','核数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.am.gpus' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.am.gpus','gpu个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.ps.instances' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.ps.instances','实例个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.ps.memory' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.ps.memory','内存大小，字符串格式',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.ps.vcores' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.ps.vcores','核数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.ps.gpus' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.ps.gpus','gpu个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.worker.instances' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.worker.instances','worker实例个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.worker.memory' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.worker.memory','内存大小，字符串格式',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.worker.vcores' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.worker.vcores','核数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.worker.gpus' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.worker.gpus','gpu个数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.task.executor.jvm.opts' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.task.executor.jvm.opts','task的JVM参数',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.containers.envs' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.containers.envs','构建container环境的命令',25,'27');

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'tony.execution.envs' AND dict_desc = '27';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','tony.execution.envs','任务执行前调用的命令',25,'27');

## RDBS归类 ##
-- 处理脏数据
DELETE FROM schedule_dict WHERE `type` = 25 and dict_desc = -1;
DELETE FROM schedule_dict WHERE `type` = 25
   AND dict_name in ('jdbcUrl','username','password','maxJobPoolSize','minJobPoolSize')
   AND dict_desc in (6, 9, 11, 16, 12, 8, 13, 14, 19, 21, 22, 23, 24, 25, 26);

drop table if exists t_rdbs_component_id;
create temporary table t_rdbs_component_id (id int);
insert into t_rdbs_component_id (id) values (6), (9),(11),(16),(12),(8),(13),(14),(19),(21),(22),(23),(24),(25),(26);

drop table if exists t_rdbs_component_key;
create temporary table t_rdbs_component_key (tipKey varchar(100), tipDesc varchar(200), sort int);
insert into t_rdbs_component_key values ('jdbcUrl','jdbc url地址', 1),('username', 'jdbc连接用户名', 2),('password','jdbc连接密码', 3),
                                        ('maxJobPoolSize','任务最大线程数', 4),('minJobPoolSize', '任务最小线程数', 5);
-- 组织成笛卡尔积插入
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc, sort)
select 'tips', t2.tipKey, t2.tipDesc, 25, t1.id, t2.sort from t_rdbs_component_id t1 join t_rdbs_component_key t2;

drop table if exists t_rdbs_component_id;
drop table if exists t_rdbs_component_key;

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'hive.metastore.uris' AND dict_desc = '9';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,dict_desc)
VALUES ('tips','hive.metastore.uris','hivemetastore服务地址,仅数据同步涉及hive事物表才会被使用。',25,'9');

## 修改rdos参数 ##
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -106 AND component_type_code = 9 AND `key` = 'queue';
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_id` = -117 AND component_type_code = 9 AND `key` = 'queue';

## 修改dtscript控制台参数 ##
DELETE FROM console_component_config WHERE `cluster_id` = -2 AND `component_type_code` = 3 AND `component_id` = -100;
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('-2', '-100', '3', 'INPUT', '1', 'dtscript.java.opts', '-Dfile.encoding=UTF-8', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.am.memory', '512m', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.am.cores', '1', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.worker.memory', '512m', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.worker.cores', '1', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.worker.num', '1', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'container.staging.dir', '/dtInsight/dtscript/staging', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.container.heartbeat.interval', '10000', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.container.heartbeat.timeout', '120000', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.python2.path', '/data/miniconda2/bin/python2', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0'),
('-2', '-100', '3', 'INPUT', '1', 'dtscript.python3.path', '/data/miniconda3/bin/python3', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', '0');

## dtscript环境参数 ##
UPDATE task_param_template SET `params` = '## 每个worker所占内存，比如512m
# dtscript.worker.memory=512m

## 每个worker所占的cpu核的数量
# dtscript.worker.cores=1

## worker数量
# dtscript.worker.num=1

## 是否独占机器节点
# dtscript.worker.exclusive=false

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## 指定work运行节点，需要注意不要写ip应填写对应的hostname
# dtscript.worker.nodes=

## 指定work运行机架
# dtscript.worker.racks=

## 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
logLevel=INFO' WHERE `compute_type` = 1 AND `engine_type` IN (5,6,7) AND `task_type` = 0;


## 修改spark模板  ##
## yarn 210 ##
UPDATE `console_component_config` SET `value` = '4' WHERE `key` = 'spark.yarn.maxAppAttempts' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = 'false' WHERE `key` = 'spark.eventLog.compress' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;
DELETE FROM `console_component_config` WHERE `key` = 'addColumnSupport' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;

## yarn 240 ##
UPDATE `console_component_config` SET `value` = '4' WHERE `key` = 'spark.yarn.maxAppAttempts' AND  `cluster_id` = -2 AND `component_id` = -130 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = 'false' WHERE `key` = 'spark.eventLog.compress' AND  `cluster_id` = -2 AND `component_id` = -130 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = 'hdfs://ns1/dtInsight/spark240/eventlogs' WHERE `key` = 'spark.eventLog.dir' AND  `cluster_id` = -2 AND `component_id` = -130 AND component_type_code = 1;
DELETE FROM `console_component_config` WHERE `key` = 'addColumnSupport' AND  `cluster_id` = -2 AND `component_id` = -130 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = '700s' WHERE `key` = 'spark.network.timeout' AND  `cluster_id` = -2 AND `component_id` = -130 AND component_type_code = 1;

# yarn 210 ##
UPDATE `console_component_config` SET `value` = '4' WHERE `key` = 'spark.yarn.maxAppAttempts' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = 'false' WHERE `key` = 'spark.eventLog.compress' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;
UPDATE `console_component_config` SET `value` = 'hdfs://ns1/tmp/spark-yarn-logs' WHERE `key` = 'spark.eventLog.dir' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;
DELETE FROM `console_component_config` WHERE `key` = 'addColumnSupport' AND  `cluster_id` = -2 AND `component_id` = -108 AND component_type_code = 1;

## k8s ##
DELETE FROM `console_component_config` WHERE `cluster_id` = -2 AND `component_id` = -107 AND component_type_code = 1;
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('-2', '-107', '1', 'INPUT', '1', 'spark.submit.deployMode', 'cluster', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'sparkSqlProxyPath', 'hdfs://ns1/dtInsight/spark240/client/spark-sql-proxy.jar', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.files', 'file:///opt/spark/conf/*', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.executor.cores', '1', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.executor.memory', '1g', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.executor.instances', '1', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.network.timeout', '700s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.rpc.askTimeout', '600s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.executor.heartbeatInterval', '10s', null, 'deploymode$perjob', null, null, '2021-02-25 18:12:53', '2021-02-25 18:12:53', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.eventLog.compress', 'false', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.eventLog.dir', 'hdfs://ns1/dtInsight/spark240/eventlogs', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.eventLog.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.driver.extraJavaOptions', '-Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.executor.extraJavaOptions', '-Dfile.encoding=UTF-8', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.kubernetes.driverEnv.KUBERNETES_HOST_ALIASES', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.kubernetes.container.image', '172.16.84.121/dtstack-dev/spark:v2.4.8', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.kubernetes.authenticate.driver.serviceAccountName', 'default', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '0', 'spark.kubernetes.container.image.pullSecrets', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'INPUT', '1', 'spark.kubernetes.container.image.pullPolicy', 'IfNotPresent', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'CHECKBOX', '1', 'deploymode', '[\"perjob\"]', NULL, '', '', NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0'),
('-2', '-107', '1', 'GROUP', '1', 'perjob', '', NULL, 'deploymode', 'perjob', NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', '0');

-- 参数变更, append by qiuyun
## flink 110 ##
DELETE FROM `console_component_config` WHERE `key` = 'queue' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$perjob';

DELETE from `console_component_config`  where  `key` = 'taskmanager.heap.mb' and cluster_id = -2 and component_id = -109 and component_type_code = 0 and dependencyKey = 'deploymode$perjob';

DELETE FROM `console_component_config` WHERE `key` = 'queue' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';

DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.heap.mb' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.jobmanager' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$perjob';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -109, 0, 'INPUT', 0, 'env.java.opts.jobmanager', '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5065', NULL, 'deploymode$perjob', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.memory.mb' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -109, 0, 'INPUT', 0, 'jobmanager.memory.mb', '2048', NULL, 'deploymode$session', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'yarnSessionName' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -109, 0, 'INPUT', 1, 'yarnSessionName', '', NULL, 'deploymode$session', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'hadoopUserName' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  (-2, -109, 0, 'INPUT', 1, 'hadoopUserName', '', NULL, 'deploymode$session', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'taskmanager.numberOfTaskSlots' AND  `cluster_id` = -2 AND `component_id` = -109 AND component_type_code = 0 and dependencyKey = 'deploymode$session';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
  ( -2, -109, 0, 'INPUT', 0, 'taskmanager.numberOfTaskSlots', '1', NULL, 'deploymode$session', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

## flink 110 standalone
DELETE FROM `console_component_config` WHERE `key` = 'taskmanager.numberOfTaskSlots' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

-- 订正错误数据:前面多打了空格
DELETE FROM `console_component_config` WHERE `key` = ' jobmanager.memory.process.size' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.memory.process.size' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'rest.port' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 0, 'rest.port', '8081', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'taskmanager.memory.process.size' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'high-availability' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 0, 'high-availability', 'ZOOKEEPER', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'high-availability.cluster-id' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 1, 'high-availability.cluster-id', '/default', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'high-availability.storageDir' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 1, 'high-availability.storageDir', 'hdfs://ns1/dtInsight/flink110/ha', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'high-availability.zookeeper.path.root' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 1, 'high-availability.zookeeper.path.root', '/flink110', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'high-availability.zookeeper.quorum' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.delay' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

-- 删掉原先的错误参数
DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.failure-rate-intervalattempts' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.failure-rate-interval' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.max-failures-per-interval' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.backend' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 0, 'state.backend', 'jobmanager', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'state.backend.incremental' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.checkpoints.dir' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.checkpoints.num-retained' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.savepoints.dir' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'checkpoint.retain.time' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'akka.ask.timeout' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'akka.tcp.timeout' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'classloader.resolve-order' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.archive.fs.dir' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarn.application-attempt-failures-validity-interval' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarn.application-attempts' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.taskmanager' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'classloader.dtstack-cache' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarnAccepterTaskNumber' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'monitorAcceptedApp' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'remotePluginRootDir' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'pluginLoadMode' AND  `cluster_id` = -2 AND `component_id` = -113 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -113, 20, 'INPUT', 0, 'pluginLoadMode', 'classpath', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

## flink 112 ##
DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.taskmanager' AND  `cluster_id` = -2 AND `component_id` = -115 AND component_type_code = 0 and dependencyKey = 'deploymode$perjob';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    (-2, -115, 0, 'INPUT', 0, 'env.java.opts.taskmanager', '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9751', NULL, 'deploymode$perjob', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.taskmanager' AND  `cluster_id` = -2 AND `component_id` = -115 AND component_type_code = 0 and dependencyKey = 'deploymode$standalone';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -115, 0, 'INPUT', 0, 'env.java.opts.taskmanager', '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9751', NULL, 'deploymode$standalone', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'yarn.application.queue' AND  `cluster_id` = -2 AND `component_id` = -115 AND component_type_code = 0 and dependencyKey = 'deploymode$session';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    (-2, -115, 0, 'INPUT', 0, 'yarn.application.queue', '', NULL, 'deploymode$session', NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

## flink 112 standalone ##
DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.memory.process.size' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'rest.port' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 0, 'rest.port', '8081', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'taskmanager.memory.process.size' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'taskmanager.numberOfTaskSlots' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.class' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.class', 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.deleteOnShutdown' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.deleteOnShutdown', 'true', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.host' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.host', '', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.jobName' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.jobName', '112job', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.port' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.port', '9091', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'metrics.reporter.promgateway.randomJobNameSuffix' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 1, 'metrics.reporter.promgateway.randomJobNameSuffix', 'true', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.delay' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.failure-rate-intervalattempts' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.failure-rate-interval' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'restart-strategy.failure-rate.max-failures-per-interval' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.backend' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 0, 'state.backend', 'jobmanager', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);


DELETE FROM `console_component_config` WHERE `key` = 'state.backend.incremental' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.checkpoints.dir' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.checkpoints.num-retained' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'state.savepoints.dir' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'checkpoint.retain.time' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'akka.ask.timeout' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'akka.tcp.timeout' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'classloader.resolve-order' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'jobmanager.archive.fs.dir' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarn.application-attempt-failures-validity-interval' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarn.application-attempts' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.taskmanager' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'classloader.dtstack-cache' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'yarnAccepterTaskNumber' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'monitorAcceptedApp' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;

DELETE FROM `console_component_config` WHERE `key` = 'pluginLoadMode' AND  `cluster_id` = -2 AND `component_id` = -120 AND component_type_code = 20;
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -120, 20, 'INPUT', 0, 'pluginLoadMode', 'classpath', NULL, NULL, NULL, NULL, '2022-05-23 14:02:00', '2022-05-23 14:02:00', 0);

-- 订正原先的错误参数
update console_component_config set `key` = 'restart-strategy.failure-rate.failure-rate-interval' where `key` = 'restart-strategy.failure-rate.failure-rate-intervalattempts' and component_type_code in (0, 20);
delete from console_component_config where `key` in ('org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter') and component_type_code in (0, 20);