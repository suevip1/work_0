-- auto-generated definition
create table project_statistics
(
    id            int auto_increment
        primary key,
    tenant_id     int      default -1                not null comment 'uic租户id',
    project_id    int      default -1                not null comment '项目id',
    app_type      int      default 0                 not null comment 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    total_job     int      default 0                 not null comment '实例总数',
    un_submit_job int      default 0                 not null comment '未提交总数',
    run_job       int      default 0                 not null comment '运行总数',
    fail_job      int      default 0                 not null comment '失败实例总数',
    alarm_total   int      default 0                 not null comment '告警总数',
    user_id       int      default 0                 not null comment '用户id',
    gmt_create    datetime default CURRENT_TIMESTAMP not null comment '新增时间'
)
    charset = utf8;

create index project_statistics
    on project_statistics (tenant_id, project_id, app_type, user_id, gmt_create);

