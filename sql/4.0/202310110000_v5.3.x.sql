ALTER TABLE schedule_task_task_shade ADD `up_down_rely_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '上下游依赖类型 0 默认 1 自定义';
ALTER TABLE schedule_task_task_shade ADD `custom_offset` int(4) NOT NULL DEFAULT '0' COMMENT '偏移量 ，当upDownRelyType值是 1 的时候 才会生效';

ALTER TABLE schedule_fill_data_job ADD `task_run_order` int(4) NOT NULL DEFAULT '0' COMMENT '任务运行顺序： 0 无 默认 1按业务日期升序';