CREATE TABLE if not exists `schedule_task_priority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_type` int(4) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `task_id` bigint(20) NOT NULL COMMENT '任务id',
  `baseline_task_id` bigint(20) NOT NULL COMMENT '基线任务id',
  `priority` int(2) NOT NULL COMMENT '优先级 1-5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_task_id` (`task_id`,`app_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务优先级表';

## 添加索引index_job_resource_node_address
drop procedure if exists add_index_job_resource_node_address;
delimiter $
create procedure add_index_job_resource_node_address()
begin
  DECLARE indexCount int;
  select count(*) into indexCount from information_schema.statistics where table_name = 'schedule_engine_job_cache' and index_name = 'index_job_resource_node_address' and table_schema = database();
  IF indexCount<=0
  THEN
  ALTER TABLE schedule_engine_job_cache ADD INDEX  `index_job_resource_node_address` (`job_resource`,`node_address`,`stage`) USING BTREE;
  END IF;
end $
delimiter ;
call add_index_job_resource_node_address();
drop procedure if exists add_index_job_resource_node_address;

## 添加索引index_job_priority
drop procedure if exists add_index_job_priority;
delimiter $
create procedure index_job_priority()
begin
  DECLARE indexCount int;
  select count(*) into indexCount from information_schema.statistics where table_name = 'schedule_engine_job_cache' and index_name = 'index_job_priority' and table_schema = database();
  IF indexCount<=0
  THEN
  ALTER TABLE schedule_engine_job_cache ADD INDEX  `index_job_priority` (`job_priority`) USING BTREE;
  END IF;
end $
delimiter ;
call index_job_priority();
drop procedure if exists index_job_priority;

## 添加字段 priority
drop procedure if exists add_filed_priority;
delimiter $
create procedure add_filed_priority()
begin
  DECLARE indexCount int;
  select count(*) into indexCount from information_schema.`COLUMNS` where table_name = 'baseline_task' and `COLUMN_NAME` = 'priority' and table_schema = database();
  IF indexCount<=0
  THEN
  ALTER TABLE baseline_task ADD `priority` int(2) NOT NULL DEFAULT 1 COMMENT '优先级 1-5';
  END IF;
end $
delimiter ;
call add_filed_priority();
drop procedure if exists add_filed_priority;


UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 1 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# spark.driver.cores=1

## Driver程序使用内存大小,默认1g
# spark.driver.memory=1g

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# spark.driver.maxResultSize=1g

## 启动的executor的数量，默认为1
# spark.executor.instances=1

## 每个executor使用的CPU核数，默认为1
# spark.executor.cores=1

## 每个executor内存大小,默认1g
# spark.executor.memory=1g

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead=

## 设置spark sql shuffle分区数，默认200
# spark.sql.shuffle.partitions=200

## 开启spark推测行为，默认false
# spark.speculation=false' WHERE id = 3 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 5 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 7 and is_deleted = 0;

UPDATE environment_param_template SET params='## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
## container=1
## per_job模式下每个taskManager 对应 slot的数量
## slots=1
## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
' WHERE id = 9 and is_deleted = 0;

UPDATE environment_param_template SET params='## 任务运行方式：
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

pipeline.operator-chaining = false' WHERE id = 11 and is_deleted = 0;

UPDATE environment_param_template SET params='## 任务运行方式：
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

pipeline.operator-chaining = false' WHERE id = 13 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 15 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 17 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=512m

## 每个worker所占的cpu核的数量
worker.cores=1

## 是否独占机器节点
exclusive=false

## worker数量
worker.num=1

## 指定运行节点, 示例：nodes=hostname1,hostname2
## nodes=

## 指定机架, 示例：racks=racks,racks
## racks=
' WHERE id = 19 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=512m

## 每个worker所占的cpu核的数量
worker.cores=1

## 是否独占机器节点
exclusive=false

## worker数量
worker.num=1

## 指定运行节点, 示例：nodes=hostname1,hostname2
## nodes=

## 指定机架, 示例：racks=racks,racks
## racks=
' WHERE id = 21 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=512m

## 每个worker所占的cpu核的数量
worker.cores=1

## 是否独占机器节点
exclusive=false

## worker数量
worker.num=1

' WHERE id = 23 and is_deleted = 0;

UPDATE environment_param_template SET params='## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30' WHERE id = 25 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

' WHERE id = 27 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

' WHERE id = 29 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

' WHERE id = 31 and is_deleted = 0;

UPDATE environment_param_template SET params='## sql任务并发度设置
sql.env.parallelism=1

## 时间窗口类型（ProcessingTime或者EventTime）
time.characteristic=ProcessingTime

## 窗口提前触发时间，单位为秒(填写正整数即可)
# early.trigger=1

## ttl状态控制
## 最小过期时间,大于0的整数,如1d、1h(dD:天,hH:小时,mM:分钟,ss:秒)
# sql.ttl.min=1h
## 最大过期时间,大于0的整数,如2d、2h(dD:天,hH:小时,mM:分钟,ss:秒),需同时设置最小时间,且比最小时间大5分钟
# sql.ttl.max=2h

## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
flink.checkpoint.interval=300000

## 设置checkpoint生成超时（以毫秒为单位），默认:10分钟
sql.checkpoint.timeout=600000

## 任务出现故障的时候一致性处理,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# sql.checkpoint.mode=EXACTLY_ONCE

## 最大并发生成 checkpoint 数量，默认：1 次
# sql.max.concurrent.checkpoints=1

## checkpoint 外存的清理动作
## true（任务结束之后删除checkpoint外部存储信息）
## false（任务结束之后保留checkpoint外部存储信息）
sql.checkpoint.cleanup.mode=false

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager 对应 slot的数量
slots=1

## logLevel: error,debug,info(默认),warn
logLevel=info

## Watermark发送周期，单位毫秒
# autoWatermarkInterval=200

## 设置输出缓冲区的最大刷新时间频率（毫秒）
# sql.buffer.timeout.millis=100

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client


## 异步访问维表是否开启连接池共享,开启则 1.一个tm上多个task共享该池, 2.一个tm上多个url相同的维表单/多个task共享该池 (默认false)
# async.side.clientShare=false
## 连接池中连接的个数,上面参数为true才生效(默认5)
# async.side.poolSize=5' WHERE id = 33 and is_deleted = 0;

UPDATE environment_param_template SET params='## sql任务并发度设置
sql.env.parallelism=1

## 时间窗口类型（ProcessingTime或者EventTime）
time.characteristic=ProcessingTime

## 窗口提前触发时间，单位为秒(填写正整数即可)
# early.trigger=1

## ttl状态控制
## 最小过期时间,大于0的整数,如1d、1h(dD:天,hH:小时,mM:分钟,ss:秒)
# sql.ttl.min=1h
## 最大过期时间,大于0的整数,如2d、2h(dD:天,hH:小时,mM:分钟,ss:秒),需同时设置最小时间,且比最小时间大5分钟
# sql.ttl.max=2h

## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
flink.checkpoint.interval=300000

## 设置checkpoint生成超时（以毫秒为单位），默认:10分钟
sql.checkpoint.timeout=600000

## 任务出现故障的时候一致性处理,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# sql.checkpoint.mode=EXACTLY_ONCE

## 最大并发生成 checkpoint 数量，默认：1 次
# sql.max.concurrent.checkpoints=1

## checkpoint 外存的清理动作
## true（任务结束之后删除checkpoint外部存储信息）
## false（任务结束之后保留checkpoint外部存储信息）
sql.checkpoint.cleanup.mode=false

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager 对应 slot的数量
slots=1

## logLevel: error,debug,info(默认),warn
logLevel=info

## Watermark发送周期，单位毫秒
# autoWatermarkInterval=200

## 设置输出缓冲区的最大刷新时间频率（毫秒）
# sql.buffer.timeout.millis=100

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client


## 异步访问维表是否开启连接池共享,开启则 1.一个tm上多个task共享该池, 2.一个tm上多个url相同的维表单/多个task共享该池 (默认false)
# async.side.clientShare=false
## 连接池中连接的个数,上面参数为true才生效(默认5)
# async.side.poolSize=5' WHERE id = 35 and is_deleted = 0;

UPDATE environment_param_template SET params='## 资源相关
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
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION' WHERE id = 37 and is_deleted = 0;

UPDATE environment_param_template SET params='## per_job模式下jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## per_job模式下taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
# container=1

## per_job模式下每个taskManager 对应 slot的数量
slots=1

## checkpoint保存时间间隔
flink.checkpoint.interval=3600000

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client' WHERE id = 39 and is_deleted = 0;

UPDATE environment_param_template SET params='## per_job模式下jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## per_job模式下taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
# container=1

## per_job模式下每个taskManager 对应 slot的数量
slots=1

## checkpoint保存时间间隔
flink.checkpoint.interval=3600000

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client' WHERE id = 41 and is_deleted = 0;

UPDATE environment_param_template SET params='## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g

## 时间相关
## 设置Flink时间选项，有ProcessingTime,EventTime,IngestionTime可选
## 默认为ProcessingTime
# pipeline.time-characteristic=EventTime

## Checkpoint相关
## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
execution.checkpointing.interval=5min
## 状态恢复语义,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# execution.checkpointing.mode=EXACTLY_ONCE

## Kerberos相关参数
# security.kerberos.login.contexts=Client,KafkaClient
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION' WHERE id = 43 and is_deleted = 0;

UPDATE environment_param_template SET params='## 实例个数
tony.am.instances = 1

# 内存大小，字符串格式
tony.am.memory = 2g

# 核数
tony.am.vcores = 1

# gpu个数
tony.am.gpus = 0

# 实例个数
tony.ps.instances = 0

# 内存大小，字符串格式
tony.ps.memory = 2g

# 核数
tony.ps.vcores = 1

# gpu个数
tony.ps.gpus = 0

# worker实例个数
tony.worker.instances = 0

# 内存大小，字符串格式
tony.worker.memory = 2g

# 核数
tony.worker.vcores = 1

# gpu个数
tony.worker.gpus = 0
' WHERE id = 45 and is_deleted = 0;

UPDATE environment_param_template SET params='

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager数量
# container=1

## taskManager 对应 slot的数量
slots=1
' WHERE id = 47 and is_deleted = 0;

UPDATE environment_param_template SET params='

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager数量
# container=1

## taskManager 对应 slot的数量
slots=1
' WHERE id = 49 and is_deleted = 0;

UPDATE environment_param_template SET params='## 资源相关
## per_job:单独为任务创建flink yarn
flinkTaskRunMode=per_job
## jar包任务没设置并行度时的默认并行度
parallelism.default=1
#t# askmanager的slot数
taskmanager.numberOfTaskSlots=1
## jobmanager进程内存大小
jobmanager.memory.process.size=1g
## taskmanager进程内存大小
taskmanager.memory.process.size=2g' WHERE id = 51 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# spark.driver.cores=1

## Driver程序使用内存大小,默认1g
# spark.driver.memory=1g

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# spark.driver.maxResultSize=1g

## 启动的executor的数量，默认为1
# spark.executor.instances=1

## 每个executor使用的CPU核数，默认为1
# spark.executor.cores=1

## 每个executor内存大小,默认1g
# spark.executor.memory=1g

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead=

## 设置spark sql shuffle分区数，默认200
# spark.sql.shuffle.partitions=200

## 开启spark推测行为，默认false
# spark.speculation=false' WHERE id = 55 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 57 and is_deleted = 0;

UPDATE environment_param_template SET params='## Driver程序使用的CPU核数,默认为1
# driver.cores=1

## Driver程序使用内存大小,默认512m
# driver.memory=512m

## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
# driver.maxResultSize=1g

## 启动的executor的数量，默认为1
executor.instances=1

## 每个executor使用的CPU核数，默认为1
executor.cores=1

## 每个executor内存大小,默认512m
executor.memory=512m

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead' WHERE id = 59 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=512m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

' WHERE id = 61 and is_deleted = 0;

UPDATE environment_param_template SET params='##Driver程序使用的CPU核数,默认为1
##driver.cores=1

##Driver程序使用内存大小,默认512m
##driver.memory=512m

##对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
##若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
##driver.maxResultSize=1g

##SparkContext 启动时是否记录有效 SparkConf信息,默认false
##logConf=false

##启动的executor的数量，默认为1
executor.instances=1

#每个executor使用的CPU核数，默认为1
executor.cores=1

##每个executor内存大小,默认512m
##executor.memory=512m
isCarbondata=true
' WHERE id = 65 and is_deleted = 0;

UPDATE environment_param_template SET params='## 每个worker所占内存，比如512m
worker.memory=512m

        ## 每个worker所占的cpu核的数量
worker.cores=1

        ## 根据nodes的值 可将任务指定在某些节点运行

        ## nodes=node001
' WHERE id = 89 and is_deleted = 0;

UPDATE environment_param_template SET params='## 资源相关
## per_job:单独为任务创建flink yarn
flinkTaskRunMode=per_job
## jar包任务没设置并行度时的默认并行度
parallelism.default=1
#t# askmanager的slot数
taskmanager.numberOfTaskSlots=1
## jobmanager进程内存大小
jobmanager.memory.process.size=1g
## taskmanager进程内存大小
taskmanager.memory.process.size=2g' WHERE id = 91 and is_deleted = 0;

UPDATE environment_param_template SET params='## 资源相关
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
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION' WHERE id = 93 and is_deleted = 0;

UPDATE environment_param_template SET params='## 任务运行方式：
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

pipeline.operator-chaining = false' WHERE id = 95 and is_deleted = 0;

UPDATE environment_param_template SET params='## per_job模式下jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## per_job模式下taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024
## per_job模式下启动的taskManager数量
# container=1

## per_job模式下每个taskManager 对应 slot的数量
slots=1

## checkpoint保存时间间隔
flink.checkpoint.interval=3600000

## kafka kerberos相关参数
## security.kerberos.login.use-ticket-cache=true
## security.kerberos.login.contexts=Client,KafkaClient
## security.kerberos.login.keytab=/opt/keytab/kafka.keytab
## security.kerberos.login.principal=kafka@HADOOP.COM
## zookeeper.sasl.service-name=zookeeper
## zookeeper.sasl.login-context-name=Client' WHERE id = 97 and is_deleted = 0;



