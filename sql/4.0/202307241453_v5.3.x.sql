-- 控制台 common 组件增加 hadoop.user.name
delete from console_component_config where component_id = -143 and component_type_code = 34 and `key` = 'hadoop.user.name';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -143, 34, 'INPUT', 0, 'hadoop.user.name', '', null, null, null, null, now(), now(), 0);

CREATE TABLE IF NOT EXISTS `schedule_job_auth` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uic_user_id` bigint(20) NOT NULL COMMENT 'uic用户id',
    `dtuic_tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT 'uic租户id',
    `auth_type` varchar(15) NOT NULL COMMENT '认证类型:KERBEROS、LDAP、TBDS、PROXY',
    `auth_biz_name` varchar(125) NOT NULL COMMENT '认证业务名',
    `auth_info` longtext COMMENT '认证信息',
    `submit_user_name` varchar(125) DEFAULT NULL COMMENT '任务提交用户名',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tenant_auth_biz` (`uic_user_id`,`dtuic_tenant_id`,`auth_type`,`auth_biz_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 添加字段 auth_id
drop procedure if exists add_field_auth_id;
delimiter $
create procedure add_field_auth_id()
begin
    DECLARE size int;
    select count(*) into size from information_schema.`COLUMNS` where table_name = 'schedule_job_expand' and `COLUMN_NAME` = 'auth_id' and table_schema = database();
    IF size <= 0 THEN
        ALTER TABLE `schedule_job_expand`
            ADD COLUMN `auth_id` bigint(20) NULL COMMENT '认证信息id' AFTER `engine_job_id`;
    END IF;
end $
delimiter ;
call add_field_auth_id();
drop procedure if exists add_field_auth_id;

-- 清理
delete FROM schedule_dict WHERE `type` = 8 AND dict_code = 'data_clear_name' AND dict_name = 'schedule_job_auth';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('data_clear_name', 'schedule_job_auth', '{\"clearDateConfig\":366, \"deleteDateConfig\":30}', NULL, 8, 1, 'STRING', '', 0, now(), now());