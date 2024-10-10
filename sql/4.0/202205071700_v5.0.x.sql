UPDATE environment_param_template
SET params = '## 任务运行方式：
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
## job.priority=10
pipeline.operator-chainning = false'
WHERE task_type = 2 and task_version IN (1.10,1.12);

