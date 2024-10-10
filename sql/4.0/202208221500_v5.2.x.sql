-- auto-generated definition
create table schedule_job_gantt_chart
(
    id                   int auto_increment
        primary key,
    job_id               varchar(32)                          not null comment '任务id',
    cyc_time             datetime                             null comment '起调时间',
    status_time          datetime                             null comment '实例状态时间',
    parent_depend_time   datetime                             null comment '上下依赖时间',
    tenant_resource_time datetime                             null comment '资源限制时间',
    job_submit_time      datetime                             null comment '实例提交时间',
    resource_match_time  datetime                             null comment '资源匹配时间',
    run_job_time         datetime                             null comment '实例运行时间',
    valid_job_time       datetime                             null comment '质量校验时间',
    gmt_create           datetime   default CURRENT_TIMESTAMP null comment '新增时间',
    gmt_modified         datetime   default CURRENT_TIMESTAMP null comment '修改时间',
    is_deleted           tinyint(1) default 0                 not null comment '0正常 1逻辑删除'
)
    charset = utf8;

create index index_job_id
    on schedule_job_gantt_chart (job_id);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('data_clear_name', 'schedule_job_gantt_chart', '{"deleteDateConfig":30,"clearDateConfig":180}', null, 8, 1, 'STRING', '', 0, now(),now(), 0);