-- 清理已被删除项目的授权记录, ref:http://zenpms.dtstack.cn/zentao/bug-view-63988.html
-- 1) 针对 `dt_pub_service`.`auth_project` 中逻辑删除的记录
update console_resource_group_grant gt, `dt_pub_service`.`auth_project` p set gt.is_deleted = 1
where gt.project_id = p.project_id and gt.app_type = p.app_type and p.is_deleted = 1;

-- 1) 针对 `dt_pub_service`.`auth_project` 中物理删除的记录
update console_resource_group_grant gt set gt.is_deleted = 1
where not exists (select 1 from `dt_pub_service`.`auth_project` p where gt.project_id = p.project_id and gt.app_type = p.app_type
    ) and gt.project_id is not null;