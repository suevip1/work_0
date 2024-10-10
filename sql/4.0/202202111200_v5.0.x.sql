update environment_param_template set params = '## Driver程序使用的CPU核数,默认为1
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

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
# logLevel = INFO

## spark中所有网络交互的最大超时时间
# spark.network.timeout=120s

## executor的OffHeap内存，和spark.executor.memory配置使用
# spark.yarn.executor.memoryOverhead=

## 设置spark sql shuffle分区数，默认200
# spark.sql.shuffle.partitions=200

## 开启spark推测行为，默认false
# spark.speculation=false'
where task_name = 'SPARK_SQL' and task_version = '2.4';