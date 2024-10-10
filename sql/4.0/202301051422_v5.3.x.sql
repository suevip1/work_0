-- add data.security.control
delete from console_component_config where component_id = -143 and component_type_code = 34 and `key` = 'data.security.control';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -143, 34, 'SWITCH', 0, 'data.security.control', 'false', null, null, null, null, now(), now(), 0);

DELETE FROM schedule_dict WHERE dict_code = 'jdbc_url_tip' AND dict_name ='Common' AND depend_name = 'data.security.control';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Common', '是否对接客户自己的权限管控体系', NULL, 10, 0, 'STRING', 'data.security.control', 0, now(), now(), 0);

-- 如果有历史客户已经对接了自己的权限管控体系，且配置了 common 组件，则需要将 「data.security.control」刷成 「true」
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select t.cluster_id ,t.id, t.component_type_code, 'SWITCH', 0, 'data.security.control', 'true', null,null,null, null, now(), now(),0
from console_component t  where t.component_type_code = 34 and t.is_deleted = 0 and t.cluster_id != -2
-- 已经配了 data.security.control 的组件不再配置
    and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 34 and g.key = 'data.security.control' and g.cluster_id !=-2 )
-- 集群具备 hive 组件且具备配置参数 hive.proxy.enable = true
    and exists (select 1 from console_component_config g where g.cluster_id = t.cluster_id and g.component_type_code = 9 and g.is_deleted = 0 and g.cluster_id != -2
                                                                                   and g.`key` = 'hive.proxy.enable' and g.`value` = 'true') ;
