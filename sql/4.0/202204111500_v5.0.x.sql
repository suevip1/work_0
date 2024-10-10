-- auto-generated definition
create table console_param
(
    id             int auto_increment
        primary key,
    param_name     varchar(128)                            not null comment '参数名称',
    param_value    varchar(128)                           not null comment '参数值',
    param_desc     varchar(256) default ''                null comment '描述',
    param_type     int          default 1                 null comment '全局参数类型',
    is_deleted     tinyint(1)   default 0                 not null comment '是否删除',
    gmt_create     datetime     default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified   datetime     default CURRENT_TIMESTAMP not null comment '修改时间',
    create_user_id mediumtext                             null comment '创建用户',
    constraint console_param
        unique (param_name)
);


INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.bizdate', 'yyyyMMdd-1', '业务日期', 0, 0, now(),now(), '-1');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.bizdate2', 'yyyy-MM-dd,-1', '业务日期', 0, 0, now(),now(), '-1');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.cyctime', 'yyyyMMddHHmmss', '计划时间', 0, 0, now(),now(), '-1');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.premonth', 'yyyyMM-1', '上个月', 0, 0, now(),now(), '-1');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.currmonth', 'yyyyMMdd-0', '当前月', 0, 0, now(),now(), '-1');
INSERT INTO console_param (param_name, param_value, param_desc, param_type, is_deleted, gmt_create, gmt_modified, create_user_id) VALUES ('bdp.system.runtime', '${bdp.system.currenttime}', '当前时间，即任务实际运行的时间', 0, 0, now(),now(), null);

alter table schedule_job_expand add COLUMN run_sql_text mediumtext comment '运行sql内容';

-- auto-generated definition
create table schedule_task_param
(
    id           int auto_increment
        primary key,
    task_id      int                                  not null comment '任务id',
    app_type     int        default 0                 not null comment 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    param_id     bigint(11)                           not null comment '任务id',
    is_deleted   tinyint(1) default 0                 not null comment '是否删除',
    gmt_create   datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    constraint task_param
        unique (task_id, param_id)
);

