create table environment_param_template
(
    id           bigint auto_increment
        primary key,
    task_type    int        default 0                 null comment '任务类型',
    task_name    varchar(20)                          null comment '任务名称',
    task_version varchar(20)                          null comment '任务版本',
    app_type     int        default -1                null comment '子产品类型',
    params       text                                 null comment '参数模版',
    gmt_create   datetime   default CURRENT_TIMESTAMP null,
    gmt_modified datetime   default CURRENT_TIMESTAMP null,
    is_deleted   tinyint(1) default 0                 not null comment '0正常 1逻辑删除'
)
    charset = utf8;


INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (0, 'SPARK_SQL', '2.1', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (0, 'SPARK_SQL', '2.4', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (1, 'SPARK', '2.1', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (1, 'SPARK', '2.4', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:36:13', '2021-11-18 10:36:13', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (2, 'SYNC', '1.8', -1, '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
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
## 任务优先级, 范围:1-1000
## job.priority=10', '2021-11-18 10:37:24', '2021-11-18 10:37:24', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (2, 'SYNC', '1.10', -1, '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
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
## job.priority=10', '2021-11-18 10:37:24', '2021-11-18 10:37:24', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (2, 'SYNC', '1.12', -1, '## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
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
## job.priority=10', '2021-11-18 10:37:24', '2021-11-18 10:37:24', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (3, 'SPARK_PYTHON', '2.1', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:37:54', '2021-11-18 10:37:54', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (3, 'SPARK_PYTHON', '2.4', -1, '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead', '2021-11-18 10:37:54', '2021-11-18 10:37:54', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (6, 'PYTHON', null, -1, '## 每个worker所占内存，比如512m
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

##任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10 ', '2021-11-18 10:38:51', '2021-11-18 10:38:51', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (7, 'SHELL', null, -1, '## 每个worker所占内存，比如512m
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

##任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10 ', '2021-11-18 10:39:11', '2021-11-18 10:39:11', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (13, 'NOTEBOOK', null, -1, '## 每个worker所占内存，比如512m
worker.memory=512m

## 每个worker所占的cpu核的数量
worker.cores=1

## 是否独占机器节点
exclusive=false

## worker数量
worker.num=1

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', '2021-11-18 10:39:59', '2021-11-18 10:39:59', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (17, 'HIVE_SQL', null, -1, '## 指定mapreduce在yarn上的任务名称，默认为任务名称，可以重复
#hiveconf:mapreduce.job.name=

## 指定mapreduce运行的队列，默认走控制台配置的queue
# hiveconf:mapreduce.job.queuename=default_queue_name

## hivevar配置,用户自定义变量
#hivevar:ageParams=30', '2021-11-18 10:40:27', '2021-11-18 10:40:27', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (22, 'TENSORFLOW_1_X', null, -1, '## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', '2021-11-18 10:40:47', '2021-11-18 10:40:47', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (23, 'KERAS', null, -1, '## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', '2021-11-18 10:41:05', '2021-11-18 10:41:05', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (25, 'PYTORCH', null, -1, '## 每个worker所占内存，比如512m
worker.memory=1024m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', '2021-11-18 10:41:23', '2021-11-18 10:41:23', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (31, 'FLINK_SQL', '1.8', -1, '## sql任务并发度设置
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

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
# async.side.poolSize=5', '2021-11-18 10:42:19', '2021-11-18 10:42:19', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (31, 'FLINK_SQL', '1.10', -1, '## sql任务并发度设置
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

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
# async.side.poolSize=5', '2021-11-18 10:42:19', '2021-11-18 10:42:19', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (31, 'FLINK_SQL', '1.12', -1, '## 资源相关
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
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION', '2021-11-18 10:42:19', '2021-11-18 10:42:19', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (37, 'DATA_COLLECTION', '1.8', -1, '## per_job模式下jobManager配置的内存大小，默认1024（单位M）
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
## zookeeper.sasl.login-context-name=Client', '2021-11-18 10:43:42', '2021-11-18 10:43:42', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (37, 'DATA_COLLECTION', '1.10', -1, '## per_job模式下jobManager配置的内存大小，默认1024（单位M）
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
## zookeeper.sasl.login-context-name=Client', '2021-11-18 10:43:42', '2021-11-18 10:43:42', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (37, 'DATA_COLLECTION', '1.12', -1, '## 资源相关
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
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION', '2021-11-18 10:43:42', '2021-11-18 10:43:42', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (38, 'TONY', null, -1, '## 实例个数
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
', '2021-11-18 11:01:30', '2021-11-18 11:01:30', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (39, 'FLINK', '1.8', -1, '## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager数量
# container=1

## taskManager 对应 slot的数量
slots=1
', '2021-11-18 14:29:02', '2021-11-18 14:29:02', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (39, 'FLINK', '1.10', -1, '## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager数量
# container=1

## taskManager 对应 slot的数量
slots=1
', '2021-11-18 14:29:02', '2021-11-18 14:29:02', 0);
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES (39, 'FLINK', '1.12', -1, '## 资源相关
parallelism.default=1
taskmanager.numberOfTaskSlots=1
jobmanager.memory.process.size=1g
taskmanager.memory.process.size=2g
execution.checkpointing.externalized-checkpoint-retention=RETAIN_ON_CANCELLATION', '2021-11-18 14:29:02', '2021-11-18 14:29:02', 0);