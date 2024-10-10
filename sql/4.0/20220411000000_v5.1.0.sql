use dt_pub_service;
begin;
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global', '全局设置 ', '全局设置', (SELECT * FROM (select id from auth_permission where app_type = 6 and name = 'root' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_queue_record', '查看/刷新队列及实例 ', '查看/刷新队列及实例', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_queue' limit 1) a), 2, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_queue_kill', '杀死实例 ', '杀死实例', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_queue' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_queue_graph_record', '查看周期实例生成记录 ', '查看周期实例生成记录', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_queue' limit 1) a), 2, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_queue_graph_alter_config', '配置周期实例异常告警 ', '配置周期实例异常告警', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_queue' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_bind_tenant_resource', '租户与集群绑定 ', '租户与集群绑定', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_cluster_resources', '查看资源全景 ', '查看资源全景', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_granted_projects', '查看资源组授权项目 ', '查看资源组授权项目', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_query_with_resource', '查看集群下的租户 ', '查看集群下的租户', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_group_change', '新建/编辑/删除资源组 ', '新建/编辑/删除资源组', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_group_project_grant', '项目资源组授权/取消授权 ', '项目资源组授权/取消授权', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_resource_limits', '租户默认资源组及资源限制配置 ', '租户默认资源组及资源限制配置', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_resource_bind_account', '数据库账号与数栈账号绑定及删改 ', '数据库账号与数栈账号绑定及删改', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_resource' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_alter_page', '查看告警通道 ', '查看告警通道', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_alter' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_alter_change', '新增/编辑/删除告警通道 ', '新增/编辑/删除告警通道', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_alter' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_alter_set_default', '设置默认通道 ', '设置默认通道', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_alter' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_alter_set_sftp', 'SFTP配置 ', 'SFTP配置', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_alter' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_cluster_page', '查看集群 ', '查看集群', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_cluster' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_cluster_change', '新增/删除/修改集群 ', '新增/删除/修改集群', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_cluster' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_calender_page', '查看自定义调度日期 ', '查看自定义调度日期', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_calender_change', '新增/编辑/删除自定义调度日期 ', '新增/编辑/删除自定义调度日期', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global' limit 1) a), 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_param_get', '查看全局参数 ', '查看全局参数', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_param_change', '新增/编辑/删除全局参数 ', '新增/编辑/删除全局参数', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global' limit 1) a), 1, now(),now(), 0);



DELETE FROM `auth_role_permission` WHERE `app_type` = 6;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`)
SELECT 6,ar.id as role_id,ap.id as permission_id FROM auth_permission ap
LEFT JOIN auth_role ar on ar.app_type = ap.app_type
where ap.app_type = 6 and ap.name != 'root'
  and ar.app_type = 6;
commit;