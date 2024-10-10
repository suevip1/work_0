alter table console_security_log
	add detail_info MEDIUMTEXT comment '详细内容';

alter table console_security_log
	add project_info MEDIUMTEXT comment '项目信息';

alter table console_security_log modify operator varchar(128) not null comment '操作人';


