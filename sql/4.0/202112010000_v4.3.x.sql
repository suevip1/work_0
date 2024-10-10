CREATE TABLE `console_resource_group` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `name` varchar(64) NOT NULL COMMENT '资源组名称(支持中文、英文、数字、下划线，1-64字符。同一集群唯一)',
                                          `description` varchar(255) NULL COMMENT '资源组描述',
                                          `queue_path` varchar(255) not null COMMENT '队列名称',
                                          `cluster_id` bigint not null COMMENT '集群ID',
                                          `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `console_resource_group_grant` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源组授权条目ID',
                                                `resource_id` bigint not null COMMENT '资源组ID',
                                                `engine_project_id` bigint not null COMMENT '引擎内部项目ID',
                                                `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                                `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
                                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

alter table console_engine_tenant add column `default_resource_id` bigint null COMMENT '默认资源组';
alter table schedule_task_shade add column `resource_id` bigint null COMMENT '任务运行资源组';
alter table schedule_job add column `resource_id` bigint null COMMENT '实例运行资源组';

insert into console_resource_group (name, description, queue_path, cluster_id)
select concat('资源组', cq.queue_path), '兼容', cq.queue_path, ce.cluster_id  from console_engine_tenant cet
left join console_engine ce  on cet.engine_id = ce.id
left join console_queue cq on cq.id = cet.queue_id
where cet.is_deleted = 0 and cq.is_deleted = 0 and ce.is_deleted = 0 and cet.queue_id is not null
group by cluster_id,queue_path;

update console_engine_tenant cet
    inner join console_queue cq on cet.queue_id = cq.id and cq.is_deleted = 0
    inner join console_engine ce on cet.engine_id = ce.id and ce.is_deleted = 0
    inner join console_resource_group crg on cq.queue_path = crg.queue_path and ce.cluster_id = crg.cluster_id and crg.is_deleted = 0
        set cet.default_resource_id = crg.id where cet.is_deleted = 0 and cet.default_resource_id is null;

insert into console_resource_group_grant (resource_id, engine_project_id)
select cet.default_resource_id, sep.id from console_engine_tenant cet
    inner join console_dtuic_tenant cdt on cet.tenant_id = cdt.id and cdt.is_deleted = 0
    inner join schedule_engine_project sep on cdt.dt_uic_tenant_id = sep.uic_tenant_id and sep.is_deleted = 0
    where cet.is_deleted = 0 and cet.default_resource_id is not null;

update schedule_task_shade sts
    inner join console_dtuic_tenant cdt on sts.dtuic_tenant_id = cdt.dt_uic_tenant_id and cdt.is_deleted = 0
        inner join console_engine_tenant cet on cdt.id = cet.tenant_id and cet.is_deleted = 0 and cet.default_resource_id is not null
        set sts.resource_id = cet.default_resource_id
where sts.is_deleted = 0 and sts.resource_id is null and sts.app_type in (0, 1, 2, 3, 6, 7, 9, 17) and sts.task_type not in (10,-1,14,27,29,32,33,34,35,36);

update schedule_job sj inner join schedule_task_shade sts
 on sj.task_id = sts.task_id
 and sj.app_type = sts.app_type
  and sts.is_deleted = 0 and sts.resource_id is not null set sj.resource_id = sts.resource_id where sj.is_deleted = 0;