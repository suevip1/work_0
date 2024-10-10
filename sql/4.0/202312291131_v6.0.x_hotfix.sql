update `environment_param_template`
set `params` = '## 每个worker所占内存，比如512m\nworker.memory=512m\n\n## 每个worker所占的cpu核的数量\nworker.cores=1\n\n## 任务优先级, 值越小，优先级越高，范围:1-1000\n\n\n## 根据nodes的值 可将任务指定在某些节点运行\n\n## nodes=node001\n'
    where `task_name` = 'FILE_COPY' and `task_type` = 99 and `app_type` = -1;
