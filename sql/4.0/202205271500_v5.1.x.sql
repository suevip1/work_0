drop index index_select on alert_record;

create index index_select
	on alert_record (tenant_id, app_type, is_deleted, user_id);