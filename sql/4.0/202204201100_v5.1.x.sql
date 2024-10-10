alter table console_account_tenant
	add dtuic_user_id int null comment 'uic用户id';

alter table console_account_tenant
	add dtuic_tenant_id int null comment 'uic租户id';

update console_account_tenant cat
inner join console_dtuic_tenant cdt on cat.tenant_id = cdt.id
set cat.dtuic_tenant_id = cdt.dt_uic_tenant_id;

update console_account_tenant cat
inner join console_user cu on cat.user_id = cu.id
set cat.dtuic_user_id = cu.dtuic_user_id;

alter table console_resource_group_grant
	add project_id int null comment '项目id';

alter table console_resource_group_grant
    	add app_type int null comment '子产品类型';

alter table console_resource_group_grant
	add dtuic_tenant_id int null comment '租户id';

update console_resource_group_grant inner join schedule_engine_project sep
    on console_resource_group_grant.engine_project_id = sep.id
set console_resource_group_grant.project_id = sep.project_id,console_resource_group_grant.app_type = sep.app_type;

update console_resource_group_grant inner join schedule_engine_project sep
    on console_resource_group_grant.engine_project_id = sep.id
set console_resource_group_grant.dtuic_tenant_id = sep.uic_tenant_id;

alter table console_resource_hand_over
	add project_id int null comment '项目id';

alter table console_resource_hand_over
    	add app_type int null comment '子产品类型';

update console_resource_hand_over inner join schedule_engine_project sep
    on console_resource_hand_over.engine_project_id = sep.id
set console_resource_hand_over.project_id = sep.project_id,console_resource_hand_over.app_type = sep.app_type;

-- 废弃字段
alter table console_resource_group_grant modify engine_project_id bigint null comment '引擎内部项目ID';
alter table console_resource_hand_over modify engine_project_id bigint null comment '引擎内部项目ID';
alter table console_account_tenant modify user_id int null comment '数栈绑定用户';
alter table console_account_tenant modify tenant_id int null comment '数栈绑定租户';
alter table console_cluster_tenant modify tenant_id int null comment '数栈绑定租户';
alter table console_cluster_tenant modify tenant_id int null comment '数栈绑定租户';
alter table console_tenant_resource modify tenant_id int null comment '租户id';


