INSERT INTO `environment_param_template` (
                                          `task_type`,
                                          `task_name`,
                                          `task_version`,
                                          `app_type`,
                                          `params`
                                          )
VALUES

 (
  '0',
  'SPARK_SQL',
  '3.1',
  '-1',
  '## Driver程序使用的CPU核数,默认为1\n# spark.driver.cores=1\n\n## Driver程序使用内存大小,默认1g\n# spark.driver.memory=1g\n\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n# spark.driver.maxResultSize=1g\n\n## 启动的executor的数量，默认为1\n# spark.executor.instances=1\n\n## 每个executor使用的CPU核数，默认为1\n# spark.executor.cores=1\n\n## 每个executor内存大小,默认1g\n# spark.executor.memory=1g\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\njob.priority=10\n\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n# logLevel = INFO\n\n## spark中所有网络交互的最大超时时间\n# spark.network.timeout=120s\n\n## executor的OffHeap内存，和spark.executor.memory配置使用\n# spark.yarn.executor.memoryOverhead=\n\n## 设置spark sql shuffle分区数，默认200\n# spark.sql.shuffle.partitions=200\n\n## 开启spark推测行为，默认false\n# spark.speculation=false'
  ),

 (
  '3',
  'SPARK_PYTHON',
  '3.1',
  '-1',
  '## Driver程序使用的CPU核数,默认为1\n# driver.cores=1\n\n## Driver程序使用内存大小,默认512m\n# driver.memory=512m\n\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n# driver.maxResultSize=1g\n\n## 启动的executor的数量，默认为1\nexecutor.instances=1\n\n## 每个executor使用的CPU核数，默认为1\nexecutor.cores=1\n\n## 每个executor内存大小,默认512m\nexecutor.memory=512m\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\njob.priority=10\n\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n# logLevel = INFO\n\n## spark中所有网络交互的最大超时时间\n# spark.network.timeout=120s\n\n## executor的OffHeap内存，和spark.executor.memory配置使用\n# spark.yarn.executor.memoryOverhead'
 ),

 ('1',
  'SPARK',
  '3.1',
  '-1',
  '## Driver程序使用的CPU核数,默认为1\n# driver.cores=1\n\n## Driver程序使用内存大小,默认512m\n# driver.memory=512m\n\n## 对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。\n## 若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g\n# driver.maxResultSize=1g\n\n## 启动的executor的数量，默认为1\nexecutor.instances=1\n\n## 每个executor使用的CPU核数，默认为1\nexecutor.cores=1\n\n## 每个executor内存大小,默认512m\nexecutor.memory=512m\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\njob.priority=10\n\n## spark 日志级别可选ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN\n# logLevel = INFO\n\n## spark中所有网络交互的最大超时时间\n# spark.network.timeout=120s\n\n## executor的OffHeap内存，和spark.executor.memory配置使用\n# spark.yarn.executor.memoryOverhead'
 );