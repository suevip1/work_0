use `dt_pub_service`;

begin ;
## 生命周期
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life', '生命周期管理', '生命周期管理', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global' limit 1) a), 1, '',now(),now(), 0);


### 查看
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_view', '查看列表', '查看列表', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life' limit 1) a), 1, '',now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_view_all', '有全部权限', '有全部权限', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life_view' limit 1) a), 1, 'console_global_data_life_view_all,console_global_data_life_view_no',now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_view_no', '无权限', '无权限', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life_view' limit 1) a), 1, 'console_global_data_life_view_all,console_global_data_life_view_no',now(),now(), 0);

INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_view_all' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '系统管理员' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_view_all' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '控制台管理员' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_view_no' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '租户所有者' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_view_no' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '租户管理员' and `app_type` = 6) r;


### 编辑
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_edit', '编辑', '编辑', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life' limit 1) a), 1, '',now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_edit_all', '有全部权限', '有全部权限', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life_edit' limit 1) a), 1, 'console_global_data_life_edit_all,console_global_data_life_edit_no',now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `group_key` ,`gmt_create`, `gmt_modified`, `is_deleted`)
VALUES (0, 6, 'console_global_data_life_edit_no', '无权限', '无权限', (SELECT * FROM (select id from auth_permission where app_type = 6 and code = 'console_global_data_life_edit' limit 1) a), 1, 'console_global_data_life_edit_all,console_global_data_life_edit_no',now(),now(), 0);

INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_edit_all' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '系统管理员' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_edit_all' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '控制台管理员' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_edit_no' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '租户所有者' and `app_type` = 6) r;
INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`) select 6 as `app_type`, r.role_id,r.permission_id from (select `id` as role_id, (select `id` from `auth_permission` where `code` = 'console_global_data_life_edit_no' and app_type = 6 and is_deleted = 0 limit 1) as permission_id  from `auth_role` where `role_name` = '租户管理员' and `app_type` = 6) r;

## 置灰
DELETE FROM auth_role_permission_setup WHERE app_type = 6 AND role_value IN (2,3) AND is_deleted = 0 AND permission_id IN (SELECT id FROM auth_permission WHERE app_type = 6 AND is_deleted = 0 AND `code` IN ('console_global_data_life_edit_all','console_global_data_life_view_all'));
INSERT INTO auth_role_permission_setup (role_value, app_type, permission_id, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id)
SELECT 2 AS role_value, 6 AS app_type, id,0 AS is_deleted, NOW() AS gmt_create ,NOW() AS gmt_modified,1 AS create_user_id,1 AS modify_user_id FROM auth_permission WHERE app_type = 6 AND is_deleted = 0 AND `code` IN ('console_global_data_life_edit_all','console_global_data_life_view_all');
INSERT INTO auth_role_permission_setup (role_value, app_type, permission_id, is_deleted, gmt_create, gmt_modified, create_user_id, modify_user_id)
SELECT 3 AS role_value, 6 AS app_type, id,0 AS is_deleted, NOW() AS gmt_create ,NOW() AS gmt_modified,1 AS create_user_id,1 AS modify_user_id FROM auth_permission WHERE app_type = 6 AND is_deleted = 0 AND `code` IN ('console_global_data_life_edit_all','console_global_data_life_view_all');

commit;
