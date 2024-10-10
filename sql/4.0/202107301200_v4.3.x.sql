alter table console_component
    add cluster_id int(11) comment '集群id';

alter table console_queue
    add cluster_id int(11) comment '集群id';

alter table console_component modify engine_id int null comment '引擎id';

alter table console_queue modify engine_id int null comment '队列id';

update console_component cc left join console_engine ce on cc.engine_id = ce.id
set cc.cluster_id = ce.cluster_id
where cc.cluster_id is null;


update console_queue cq left join console_engine ce on cq.engine_id = ce.id
set cq.cluster_id = ce.cluster_id
where cq.cluster_id is null;


create table console_cluster_tenant
(
    id               int auto_increment
        primary key,
    tenant_id        int                                  not null comment '租户id',
    dt_uic_tenant_id int                                  not null comment 'uic租户id',
    cluster_id       int                                  not null comment '集群id',
    queue_id         int                                  null comment '队列id',
    gmt_create       datetime   default CURRENT_TIMESTAMP not null comment '新增时间',
    gmt_modified     datetime   default CURRENT_TIMESTAMP not null comment '修改时间',
    is_deleted       tinyint(1) default 0                 not null comment '0正常 1逻辑删除'
)
    charset = utf8;

insert into console_cluster_tenant (tenant_id, dt_uic_tenant_id, cluster_id, queue_id,gmt_create,gmt_modified,is_deleted)
select tenant_id, -1, -1,cq.id, cet.gmt_create, cet.gmt_modified, cet.is_deleted from console_engine_tenant cet LEFT JOIN console_queue cq on cet.queue_id = cq.id
where cq.is_deleted = 0 and cet.is_deleted = 0  group by tenant_id;

update
    console_cluster_tenant cct
        left join console_engine_tenant cet on cet.tenant_id = cct.tenant_id
        left join console_engine ce on ce.id = cet.engine_id
set cct.cluster_id = ce.cluster_id where cct.cluster_id = -1 and ce.cluster_id is not null;


update console_cluster_tenant cct
    left join console_dtuic_tenant cdt on cct.tenant_id = cdt.id
set cct.dt_uic_tenant_id = cdt.dt_uic_tenant_id where cdt.dt_uic_tenant_id is not null;

