-- 记录任务实例实际使用的任务参数
create table schedule_job_param
(
    id             int auto_increment
        primary key,
    `job_id`       varchar(256)                           not null comment '任务 job id',
    `param_name`   varchar(128)                           not null comment '参数名称',
    `param_value`  varchar(128)                           not null comment '参数值',
    `param_desc`   varchar(256) default '' null comment '描述',
    `param_type`   int          default 1 null comment '全局参数类型',
    `gmt_create`   datetime     default CURRENT_TIMESTAMP not null comment '新增时间',
    `gmt_modified` datetime     default CURRENT_TIMESTAMP not null comment '修改时间',
    `is_deleted`   tinyint(1) default 0 not null comment '是否删除',
    UNIQUE KEY `job_id_param_name` (`job_id`, `param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实例任务参数表';

