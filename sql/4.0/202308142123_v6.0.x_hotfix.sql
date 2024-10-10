-- 订正 202306271453_v6.0.x.sql
-- 1) http://zenpms.dtstack.cn/zentao/bug-view-87505.html
update console_component_config set required = 0, gmt_modified = now()
where component_type_code = 0
 and `key` in ('containerized.master.env.FILEBEAT_LOG_COLLECTOR_HOSTS',
  'containerized.taskmanager.env.FILEBEAT_LOG_COLLECTOR_HOSTS')
 and required = 1;

-- 2) http://zenpms.dtstack.cn/zentao/bug-view-87504.html
update console_component_config set required = 1, gmt_modified = now()
 where component_type_code = 0
  and `key` = 'kubernetes.namespace'
  and  dependencyKey = 'deploymode$session'
  and required = 0;

delete from console_component_config where cluster_id = -2 and component_id = -122 and component_type_code = 0 and
    `key` = 'kubernetes.namespace' and dependencyKey = 'deploymode$perjob';
INSERT INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
 (-2, -122, 0, 'INPUT', 1, 'kubernetes.namespace', 'default', null, 'deploymode$perjob', null, null, now(), now(), 0);

-- 历史集群如果没有 'kubernetes.namespace', 需要新增
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'kubernetes.namespace', 'default',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink on k8s 组件 perjob 模式下 没有 'kubernetes.namespace'
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'kubernetes.namespace' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$perjob')
                            and t.version_name = '1.16'
                            and t.deploy_type = 2;

-- 3) http://zenpms.dtstack.cn/zentao/bug-view-87502.html
update console_component_config set type = 'SELECT', value = 'NONE', gmt_modified = now()
 where cluster_id = -2 and component_id = -122 and component_type_code = 0 and
        `key` in ('high-availability') and dependencyKey = 'deploymode$perjob' and type = 'INPUT';

delete from console_component_config where `key` in
('NONE', 'ZOOKEEPER', 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory')
    and cluster_id = -2 and component_id = -122 and component_type_code = 0 and
    dependencyKey = 'deploymode$perjob$high-availability';

INSERT INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
    (-2, -122, 0, '', 1, 'NONE', 'NONE', null, 'deploymode$perjob$high-availability', null, null, now(), now(), 0, null, null);

INSERT INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
    (-2, -122, 0, '', 1, 'ZOOKEEPER', 'ZOOKEEPER', null, 'deploymode$perjob$high-availability', null, null, now(), now(), 0, null, null);

INSERT INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted, cascade_key, cascade_value)
VALUES
    (-2, -122, 0, '', 1, 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory',
     'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', null,
     'deploymode$perjob$high-availability', null, null, now(), now(), 0, null, null);

delete from console_component_config where `key` in
                                           ('high-availability.zookeeper.quorum',
                                            'high-availability.zookeeper.path.root')
                                       and cluster_id = -2 and component_id = -122 and component_type_code = 0
                                        and dependencyKey = 'deploymode$perjob' and type = 'INPUT';

INSERT INTO console_component_config
(cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES
(-2, -122, 0, 'INPUT', 1, 'high-availability.zookeeper.quorum', '', null, 'deploymode$perjob', null, null, now(), now(), 0),
(-2, -122, 0, 'INPUT', 1, 'high-availability.zookeeper.path.root', '', null, 'deploymode$perjob', null, null, now(), now(), 0);

-- 新增历史集群 perjob 中 high-availability 配置项
UPDATE console_component_config SET  `type` = 'SELECT', gmt_modified = now()
WHERE cluster_id != -2
  AND `type` = 'INPUT'
  AND `key`  = 'high-availability'
  and dependencyKey = 'deploymode$perjob'
  AND component_id in (
    SELECT id FROM console_component WHERE component_type_code = 0 AND version_name = '1.16'
                                       and deploy_type = 2
);

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'NONE', 'NONE',null,'deploymode$perjob$high-availability',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 AND t.version_name = '1.16'
                            and deploy_type = 2
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 0 and g.`key` = 'NONE'
                                                                   and g.value = 'NONE' and g.`dependencyKey` = 'deploymode$perjob$high-availability' and g.cluster_id !=-2
                                );

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'ZOOKEEPER', 'ZOOKEEPER',null,'deploymode$perjob$high-availability',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 AND t.version_name = '1.16'
                            and deploy_type = 2
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 0 and g.`key` = 'ZOOKEEPER'
                                                                   and g.value = 'ZOOKEEPER' and g.`dependencyKey` = 'deploymode$perjob$high-availability' and g.cluster_id !=-2
                                );


INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, '', 1, 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory', 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory',null,'deploymode$perjob$high-availability',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 AND t.version_name = '1.16'
                            and deploy_type = 2
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 0 and g.`key` = 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory'
                                                                   and g.value = 'org.apache.flink.kubernetes.highavailability.KubernetesHaServicesFactory' and g.`dependencyKey` = 'deploymode$perjob$high-availability' and g.cluster_id !=-2
                                );

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'high-availability.zookeeper.quorum', '',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 AND t.version_name = '1.16'
                            and deploy_type = 2
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 0 and g.`key` = 'high-availability.zookeeper.quorum'
                                                                   and g.value = '' and g.`dependencyKey` = 'deploymode$perjob' and g.cluster_id !=-2
                                );

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'high-availability.zookeeper.path.root', '',null,'deploymode$perjob',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2 AND t.version_name = '1.16'
                            and deploy_type = 2
                            and t.id not in
                                (select g.component_id
                                 from console_component_config g where g.component_type_code = 0 and g.`key` = 'high-availability.zookeeper.path.root'
                                                                   and g.value = '' and g.`dependencyKey` = 'deploymode$perjob' and g.cluster_id !=-2
                                );