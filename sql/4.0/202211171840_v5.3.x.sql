-- 修改 flink（1.12） 任务 环境参数模板
UPDATE environment_param_template SET `params` = '## 资源相关
## per_job:单独为任务创建flink yarn
flinkTaskRunMode=per_job
## jar包任务没设置并行度时的默认并行度
parallelism.default=1
#t# askmanager的slot数
taskmanager.numberOfTaskSlots=1
## jobmanager进程内存大小
jobmanager.memory.process.size=1g
## taskmanager进程内存大小
taskmanager.memory.process.size=2g'

WHERE task_type = 39 AND task_name = 'FLINK' AND task_version = '1.12';