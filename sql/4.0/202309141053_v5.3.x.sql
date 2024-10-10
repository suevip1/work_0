alter table console_resource_group
	add resource_type tinyint default 0 null comment '资源组类型：0 yarn资源组；1 trino资源组';

