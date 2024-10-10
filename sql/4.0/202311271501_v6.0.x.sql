
alter table schedule_task_calender
    add candler_batch_type tinyint default 0 comment '批次类型0单批次1多批次';
