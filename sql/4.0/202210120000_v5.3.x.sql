-- 开启数据安全后，hdfs 添加默认自定义参数，ref：http://zenpms.dtstack.cn/zentao/task-view-7678.html
-- 1) 调整 hdfs 数据安全参数模板
delete from console_component_config where component_id = -1005 and component_type_code = 4;
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -1005, 4, 'CUSTOM_CONTROL', 1, 'serviceName', 'hadoopdev', NULL, '', '', NULL, NOW(), NOW(), 0);
INSERT INTO console_component_config (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, is_deleted) VALUES
    (-2, -1005, 4, 'CUSTOM_CONTROL', 0, 'description', '', NULL, '', '', NULL, NOW(), NOW(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -1005, 4, 'CUSTOM_CONTROL', 1, 'username', 'dtstack_default', null, '', '', null, NOW(), NOW(), 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -1005, 4, 'CUSTOM_CONTROL', 1, 'password', '123456', null, '', '', null, NOW(), NOW(), 0);

-- 历史集群补充参数
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'CUSTOM_CONTROL', 1, 'serviceName', 'hadoopdev',null,null,null, null, now(), now(),0
from console_component t where t.component_type_code = 4 and t.is_deleted = 0 and t.cluster_id != -2
                           and t.id not in (
        -- 找到那些有新配置的组件
        select g.component_id from console_component_config g where g.component_type_code = 4 and g.`key` = 'serviceName' and g.cluster_id !=-2 )
                           and t.id in (
        -- b) 该集群若配置了 hdfs，则获取 hdfs 组件的 component_id
        select id from console_component where component_type_code = 4 and is_deleted = 0
                                           -- a) 同时配置了 ranger、ldap 的集群
                                           and cluster_id in (SELECT cluster_id  From console_component where component_type_code in (31, 32) and is_deleted = 0
                                                              group by cluster_id having count(*) >1)
);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'CUSTOM_CONTROL', 1, 'description', '',null,null,null, null, now(), now(),0
from console_component t where t.component_type_code = 4 and t.is_deleted = 0 and t.cluster_id != -2
                           and t.id not in (
        -- 找到那些有新配置的组件
        select g.component_id from console_component_config g where g.component_type_code = 4 and g.`key` = 'description' and g.cluster_id !=-2 )
                           and t.id in (
        -- b) 该集群若配置了 hdfs，则获取 hdfs 组件的 component_id
        select id from console_component where component_type_code = 4 and is_deleted = 0
                                           -- a) 同时配置了 ranger、ldap 的集群
                                           and cluster_id in (SELECT cluster_id  From console_component where component_type_code in (31, 32) and is_deleted = 0
                                                              group by cluster_id having count(*) >1)
);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'CUSTOM_CONTROL', 1, 'username', 'dtstack_default',null,null,null, null, now(), now(),0
from console_component t where t.component_type_code = 4 and t.is_deleted = 0 and t.cluster_id != -2
                           and t.id not in (
        -- 找到那些有新配置的组件
        select g.component_id from console_component_config g where g.component_type_code = 4 and g.`key` = 'username' and g.cluster_id !=-2 )
                           and t.id in (
        -- b) 该集群若配置了 hdfs，则获取 hdfs 组件的 component_id
        select id from console_component where component_type_code = 4 and is_deleted = 0
                                           -- a) 同时配置了 ranger、ldap 的集群
                                           and cluster_id in (SELECT cluster_id  From console_component where component_type_code in (31, 32) and is_deleted = 0
                                                              group by cluster_id having count(*) >1)
    );

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'CUSTOM_CONTROL', 1, 'password', '123456',null,null,null, null, now(), now(),0
from console_component t where t.component_type_code = 4 and t.is_deleted = 0 and t.cluster_id != -2
                           and t.id not in (
        -- 找到那些有新配置的组件
        select g.component_id from console_component_config g where g.component_type_code = 4 and g.`key` = 'password' and g.cluster_id !=-2 )
                           and t.id in (
        -- b) 该集群若配置了 hdfs，则获取 hdfs 组件的 component_id
        select id from console_component where component_type_code = 4 and is_deleted = 0
                                           -- a) 同时配置了 ranger、ldap 的集群
                                           and cluster_id in (SELECT cluster_id  From console_component where component_type_code in (31, 32) and is_deleted = 0
                                                              group by cluster_id having count(*) >1)
    );