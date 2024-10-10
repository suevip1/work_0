ALTER TABLE schedule_fill_data_job
    ADD COLUMN fill_data_type tinyint(1) default 0 not null comment '0:批量生成,1:按照工程补数据,2:手动任务';
ALTER TABLE schedule_task_shade
    ADD COLUMN task_group tinyint(1) default 0 not null comment '0: 周期任务,1:手动任务';