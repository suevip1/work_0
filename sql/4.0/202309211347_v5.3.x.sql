-- 将限制放宽
alter table console_param
    modify param_value varchar(2048) not null comment '参数值';

alter table schedule_job_param
    modify param_value varchar(2048) not null comment '参数值';


alter table schedule_project_param
    modify param_value varchar(2048) not null comment '参数值';

