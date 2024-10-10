-- auto-generated definition
create table schedule_task_calender
(
    id            int auto_increment
        primary key,
    task_id       int                                  not null comment '任务id',
    app_type      int        default 0                 not null comment 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    calender_id bigint(11)                           not null comment '日历id',
    is_deleted    tinyint(1) default 0                 not null comment '是否删除',
    gmt_create    datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified  datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    constraint task_calender
        unique (task_id, calender_id)
);


create table console_calender
(
    id            int auto_increment
        primary key,
    calender_name varchar(64)                                  not null comment '日历名称',
    calender_file_name varchar(128)                                  not null comment '日历文件名称',
    latest_calender_time      bigint(11)                       not null comment '最大调度时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否删除',
    gmt_create    datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified  datetime   default CURRENT_TIMESTAMP not null comment '修改时间'
);


create table console_calender_time
(
    id            int auto_increment
        primary key,
    calender_id int(64)                                  not null comment '日历id',
    calender_time      bigint(11)        default 0                 not null comment '日历时间',
    gmt_create    datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified  datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    constraint calender_name
        unique (calender_id,calender_time)
);


alter table schedule_job add COLUMN calender_id int(64) comment '日历id';