use dt_pub_service;
begin;
DELETE FROM `auth_role` WHERE app_type = 6;
DELETE FROM `auth_permission` WHERE app_type = 6;
DELETE FROM `auth_role_permission` WHERE app_type = 6;

INSERT INTO `auth_role` (`app_type`,`dtuic_tenant_id`,`project_id`,`role_name`,`role_type`,`role_value`,`role_desc`,`modify_user_id`,`create_user_id`,`gmt_create`,`gmt_modified`,`is_deleted`,`old_role_id`) VALUES (6,-1,-1,'管理员',1,1,'可操作控制台内所有内容',0,NULL,now(),now(),0,0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (0, 6, 1, 'root', 'root', 0, 1, now(),now(), 0);

INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (0, 6, 'console_queue', '队列管理 ', '队列管理', (SELECT * FROM (select id from auth_permission where app_type = 6 and name = 'root' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (0, 6, 'console_resource', '资源管理 ', '资源管理', (SELECT * FROM (select id from auth_permission where app_type = 6 and name = 'root' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (0, 6, 'console_alter', '告警通道 ', '告警通道', (SELECT * FROM (select id from auth_permission where app_type = 6 and name = 'root' limit 1) a), 1, now(),now(), 0);
INSERT INTO `auth_permission`(`old_permission_id`, `app_type`, `code`, `name`, `display`, `parent_id`, `type`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES (0, 6, 'console_cluster', '多集群管理 ', '多集群管理', (SELECT * FROM (select id from auth_permission where app_type = 6 and name = 'root' limit 1) a), 1, now(),now(), 0);


INSERT INTO `auth_role_permission`(`app_type`, `role_id`, `permission_id`)
SELECT 6,ar.id as role_id,ap.id as permission_id FROM auth_permission ap
LEFT JOIN auth_role ar on ar.app_type = ap.app_type
where ap.app_type = 6 and ap.name != 'root'
  and ar.app_type = 6;
commit;
