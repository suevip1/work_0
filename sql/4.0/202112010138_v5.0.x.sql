alter table console_cluster_tenant add column `default_resource_id` bigint null COMMENT '默认资源组';
ALTER TABLE console_resource_group ADD UNIQUE INDEX `name_resource`(name, description, queue_path, cluster_id);

update console_cluster_tenant cct
         left join console_queue cq on cct.cluster_id = cq.cluster_id
         inner join console_resource_group crg  on crg.cluster_id = cq.cluster_id
         and crg.queue_path = cq.queue_path
         and cct.queue_id = cq.id
         and cct.cluster_id = crg.cluster_id
         set cct.default_resource_id = crg.id
         where cct.default_resource_id is null and cq.is_deleted = 0;


-- 5.0 版本新建的集群 在console_cluster_tenant 也需要刷默认资源组
insert ignore into console_resource_group (name, description, queue_path, cluster_id)
select concat('资源组', cq.queue_path), '兼容', cq.queue_path, cq.cluster_id from console_queue cq
where cq.id not in (select distinct parent_queue_id from console_queue where is_deleted = 0) and
      cq.parent_queue_id != -2 and cq.is_deleted = 0
      and cq.cluster_id in (select cluster_id from console_cluster_tenant where default_resource_id is null);

insert into console_resource_group_grant (resource_id, engine_project_id)
select ctg.id,sep.id from console_cluster_tenant cct
inner join  console_resource_group ctg on cct.cluster_id = ctg.cluster_id
inner join  schedule_engine_project sep on cct.dt_uic_tenant_id = sep.uic_tenant_id and sep.is_deleted = 0
where cct.default_resource_id is null;

update console_cluster_tenant cct
          left join console_queue cq on cct.cluster_id = cq.cluster_id
          inner join console_resource_group crg  on crg.cluster_id = cq.cluster_id
          and crg.queue_path = cq.queue_path
          and cct.queue_id = cq.id
          and cct.cluster_id = crg.cluster_id
          set cct.default_resource_id = crg.id
          where cct.default_resource_id is null and cq.is_deleted = 0;

update schedule_task_shade sts
        inner join console_cluster_tenant cct on cct.dt_uic_tenant_id = sts.dtuic_tenant_id
        set sts.resource_id = cct.default_resource_id
where sts.is_deleted = 0 and sts.resource_id is null and sts.app_type in (0, 1, 2, 3, 6, 7, 9, 17) and sts.task_type not in (10,-1,14,27,29,32,33,34,35,36)
     and cct.is_deleted = 0 and sts.resource_id is null;

update schedule_job sj inner join schedule_task_shade sts
 on sj.task_id = sts.task_id
 and sj.app_type = sts.app_type
   set sj.resource_id = sts.resource_id where sj.is_deleted = 0 and sts.is_deleted = 0 and sj.resource_id is null;

