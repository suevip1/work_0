INSERT INTO `task_param_template` (`gmt_create`, `gmt_modified`, `is_deleted`, `compute_type`,
                                   `engine_type`, `task_type`, `params`)
VALUES (NOW(), NOW(), 0, 1, 4, 99,
        '## 每个worker所占内存，比如512m\r\nworker.memory=512m\r\n
        ## 每个worker所占的cpu核的数量\r\nworker.cores=1\r\n
        ## 任务优先级, 值越小，优先级越高，范围:1-1000\r\njob.priority=10\r\n
        ## 根据nodes的值 可将任务指定在某些节点运行\r\n
        ## nodes=node001\r\n');