delete from environment_param_template where task_type = 99;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (99, 'FILE_COPY', null, -1, '## 每个worker所占内存，比如512m
worker.memory=512m

        ## 每个worker所占的cpu核的数量
worker.cores=1

        ## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

        ## 根据nodes的值 可将任务指定在某些节点运行

        ## nodes=node001
', now(), now(), 0);