-- shell agent label resource
-- 新增字段 component_type_code
drop procedure if exists add_field_component_type;
delimiter $
create procedure add_field_component_type()
begin
    DECLARE num int;
    select count(*) into num from information_schema.`COLUMNS` where table_name = 'console_resource_group' and `COLUMN_NAME` = 'component_type_code' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE console_resource_group ADD `component_type_code` tinyint(1) NULL COMMENT '组件类型';
    END IF;
end $
delimiter ;
call add_field_component_type();
drop procedure if exists add_field_component_type;

-- 新增字段 is_project_default
drop procedure if exists add_field_is_project_default;
delimiter $
create procedure add_field_is_project_default()
begin
    DECLARE num int;
    select count(*) into num from information_schema.`COLUMNS` where table_name = 'console_resource_group_grant' and `COLUMN_NAME` = 'is_project_default' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE console_resource_group_grant ADD `is_project_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否项目级默认，0否1是';
    END IF;
end $
delimiter ;
call add_field_is_project_default();
drop procedure if exists add_field_is_project_default;

-- 新增字段 default_label_resource_id
drop procedure if exists add_field_default_label_resource_id;
delimiter $
create procedure add_field_default_label_resource_id()
begin
    DECLARE num int;
    select count(*) into num from information_schema.`COLUMNS` where table_name = 'console_cluster_tenant' and `COLUMN_NAME` = 'default_label_resource_id' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE `console_cluster_tenant` ADD COLUMN `default_label_resource_id` bigint(20) NULL COMMENT '默认标签资源id' AFTER `default_resource_id`;
    END IF;
end $
delimiter ;
call add_field_default_label_resource_id();
drop procedure if exists add_field_default_label_resource_id;

-- 删除索引
drop procedure if exists drop_index_name_resource;
delimiter $
create procedure drop_index_name_resource()
begin
    DECLARE num int;
    select count(*) into num from information_schema.statistics where table_name = 'console_resource_group' and index_name = 'name_resource' and table_schema = database();
    IF num > 0
    THEN
        drop index name_resource on console_resource_group;
    END IF;
end $
delimiter ;
call drop_index_name_resource();
drop procedure if exists drop_index_name_resource;

-- 增加索引 idx_name_resource_component
drop procedure if exists add_index_idx_name_resource_component;
delimiter $
create procedure add_index_idx_name_resource_component()
begin
    DECLARE num int;
    select count(*) into num from information_schema.statistics where table_name = 'console_resource_group' and index_name = 'idx_name_resource_component' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE console_resource_group ADD INDEX idx_name_resource_component(name, description, queue_path, cluster_id, component_type_code) USING BTREE;
    END IF;
end $
delimiter ;
call add_index_idx_name_resource_component();
drop procedure if exists add_index_idx_name_resource_component;

-- 增加索引 idx_cluster_component
drop procedure if exists add_index_idx_cluster_component;
delimiter $
create procedure add_index_idx_cluster_component()
begin
    DECLARE num int;
    select count(*) into num from information_schema.statistics where table_name = 'console_resource_group' and index_name = 'idx_cluster_component' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE `console_resource_group` ADD INDEX `idx_cluster_component`(`cluster_id`, `component_type_code`) USING BTREE;
    END IF;
end $
delimiter ;
call add_index_idx_cluster_component();
drop procedure if exists add_index_idx_cluster_component;

-- 增加索引 idx_uic_tenant_id
drop procedure if exists add_index_idx_uic_tenant_id;
delimiter $
create procedure add_index_idx_uic_tenant_id()
begin
    DECLARE num int;
    select count(*) into num from information_schema.statistics where table_name = 'console_cluster_tenant' and index_name = 'idx_uic_tenant_id' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE `console_cluster_tenant` ADD INDEX `idx_uic_tenant_id`(`dt_uic_tenant_id`) USING BTREE;
    END IF;
end $
delimiter ;
call add_index_idx_uic_tenant_id();
drop procedure if exists add_index_idx_uic_tenant_id;

-- 增加索引 idx_cluster_id
drop procedure if exists add_index_idx_cluster_id;
delimiter $
create procedure add_index_idx_cluster_id()
begin
    DECLARE num int;
    select count(*) into num from information_schema.statistics where table_name = 'console_cluster_tenant' and index_name = 'idx_cluster_id' and table_schema = database();
    IF num <= 0
    THEN
        ALTER TABLE `console_cluster_tenant` ADD INDEX `idx_cluster_id`(`cluster_id`) USING BTREE;
    END IF;
end $
delimiter ;
call add_index_idx_cluster_id();
drop procedure if exists add_index_idx_cluster_id;

/*历史集群刷标签资源组*/
-- 增加资源组
insert ignore into console_resource_group (`name`, description, queue_path, cluster_id, component_type_code)
    select r.label, '', r.user_name, r.cluster_id, r.component_type_code
    from console_component_user r inner join console_component t
    on r.cluster_id = t.cluster_id and r.component_type_code = t.component_type_code
    where r.component_type_code = 18 and r.is_deleted = 0 and t.is_deleted = 0
    and length(r.user_name) > 0;

-- 租户默认资源组
update console_cluster_tenant cct
    inner join
    (SELECT  p.id as resource_id, p.cluster_id from console_resource_group p
     inner join console_component_user r
     on p.cluster_id = r.cluster_id and p.name = r.label and p.queue_path = r.user_name
     where p.component_type_code = 18
       and r.component_type_code = 18
       and r.is_deleted  = 0
       -- 优先取集群默认的
     order by r.is_default desc, r.id asc
     limit 1) t
    on cct.cluster_id = t.cluster_id
set cct.default_label_resource_id = t.resource_id where cct.default_label_resource_id is null;

-- 资源组赋权
insert ignore into console_resource_group_grant (resource_id, project_id, app_type, dtuic_tenant_id)
    select p.id, pap.project_id, pap.app_type, t.dt_uic_tenant_id
    from console_resource_group p
    -- 资源组授权给集群下的所有租户
    inner join console_cluster_tenant t on p.cluster_id = t.cluster_id
    -- 集群下所有租户的所有离线项目
    inner join `dt_pub_service`.`auth_project` pap on t.dt_uic_tenant_id = pap.dtuic_tenant_id
where p.is_deleted = 0 and p.component_type_code = 18
  and t.is_deleted = 0
  and pap.app_type = 1 and pap.is_deleted = 0 and pap.`status` = 1
  -- 防止重复插入
  and not exists (SELECT 1 from console_resource_group_grant t where t.resource_id = p.id and t.project_id = pap.project_id
      and t.app_type = pap.app_type and t.dtuic_tenant_id = pap.dtuic_tenant_id);

-- 任务刷资源组
update schedule_task_shade e
    inner join console_cluster_tenant t on e.dtuic_tenant_id = t.dt_uic_tenant_id
    inner join console_resource_group g on g.cluster_id = t.cluster_id
        and  SUBSTRING_INDEX(SUBSTRING_INDEX(e.task_params, 'node.label=', -1), '\n', 1) = g.`name`
        and SUBSTRING_INDEX(SUBSTRING_INDEX(e.task_params, 'user.name=', -1), '\n', 1) = g.queue_path
set e.resource_id = g.id, e.gmt_modified = now()
where e.task_type in (29, 100) and e.app_type = 1 and e.resource_id is null
  and INSTR(e.task_params,'node.label=')> 0 and INSTR(e.task_params,'user.name=')> 0
  and g.component_type_code = 18 and g.is_deleted = 0
  and t.is_deleted = 0;

-- 前一步没有找到资源组，再取租户默认的标签资源组
update schedule_task_shade e
    inner join console_cluster_tenant t on e.dtuic_tenant_id = t.dt_uic_tenant_id
set e.resource_id = t.default_label_resource_id, e.gmt_modified = now()
where e.task_type in (29, 100) and e.app_type = 1 and e.resource_id is null
  and t.default_label_resource_id is not null
  and t.is_deleted = 0;

/**
update schedule_job b inner join schedule_task_shade e
    on b.task_id = e.task_id and b.app_type = e.app_type
set b.resource_id = e.resource_id, b.gmt_modified = now()
where e.resource_id is not null
 and e.task_type in (29, 100) and e.app_type = 1
 and b.is_deleted = 0 and b.resource_id is not null
 and b.task_type in (29, 100) and b.app_type = 1;
 **/

-- 分批次更新
DROP PROCEDURE IF EXISTS `batch_update_schedule_job_label_resource_id`;

DELIMITER $$
CREATE PROCEDURE `batch_update_schedule_job_label_resource_id`(IN tableName VARCHAR(50), IN updateSql TEXT, IN batchSize INT)
BEGIN
    SET @minId = 0, @maxId = 0;
    SET @rangeSql = CONCAT('SELECT min(id), max(id) into @minId, @maxId from ', tableName, ' ;');
    PREPARE rangeSql FROM @rangeSql;
    EXECUTE rangeSql;
    DEALLOCATE PREPARE rangeSql;
    SELECT @minId, @maxId;

    SELECT ceil((@maxId -  @minId)/batchSize) INTO @batchCount;
    SET @startIdx = 0;
    WHILE @startIdx <= @batchCount DO
            -- 每个批次开启一个事务
            START TRANSACTION;
            SET @execSql = CONCAT(updateSql, ' and b.id >= ', @minId, ' and b.id < ', @minId + batchSize, ' ;');
            IF @startIdx = 0 OR @startIdx = @batchCount THEN
                -- 检查批次是否全部执行了
                SELECT @execSql;
            END IF;
            PREPARE execSql FROM @execSql;
            EXECUTE execSql;
            DEALLOCATE PREPARE execSql;
            COMMIT;
            SET @startIdx = @startIdx + 1;
            SET @minId = @minId + batchSize;
        END WHILE;
END $$
DELIMITER ;

call `batch_update_schedule_job_label_resource_id`('schedule_job', 'update schedule_job b inner join schedule_task_shade e
    on b.task_id = e.task_id and b.app_type = e.app_type
set b.resource_id = e.resource_id, b.gmt_modified = now()
where e.resource_id is not null
 and e.task_type in (29, 100) and e.app_type = 1
 and b.is_deleted = 0 and b.resource_id is not null
 and b.task_type in (29, 100) and b.app_type = 1', 500);

DROP PROCEDURE IF EXISTS `batch_update_schedule_job_label_resource_id`;