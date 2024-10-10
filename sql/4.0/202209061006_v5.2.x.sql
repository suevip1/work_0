-- flink 112 session 模式, 新增 slotmanager.number-of-slots.debug.max, ref:http://zenpms.dtstack.cn/zentao/task-view-7677.html
delete from console_component_config where component_id = -115 and `key` = 'slotmanager.number-of-slots.debug.max' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
    (-2, -115, 0, 'INPUT', 1, 'slotmanager.number-of-slots.debug.max', '0', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);

-- 历史集群 slotmanager.number-of-slots.debug.max 默认为 0
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value,
                                      `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified,
                                      is_deleted)
select  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'slotmanager.number-of-slots.debug.max', '0',null,'deploymode$session',null, null, now(), now(),0
from console_component t  where t.component_type_code = 0 and t.is_deleted = 0 and t.cluster_id != -2
                            -- flink on yarn 组件 session 模式下 没有 'slotmanager.number-of-slots.debug.max'，才需要补充
                            and t.id not in (select g.component_id from console_component_config g where g.component_type_code = 0 and g.`key` = 'slotmanager.number-of-slots.debug.max' and g.cluster_id !=-2 and g.dependencyKey = 'deploymode$session')
                            and t.version_name = '1.12'
                            and t.deploy_type = 1;
-- 归类
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'slotmanager.number-of-slots.debug.max' AND dict_desc = '0';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','slotmanager.number-of-slots.debug.max','实时任务用于调试的最大 slot 资源', 25, '公共参数', '0', 12);

update schedule_dict set dict_value = '离线任务 flink session 允许的最大 slot 数' where `type` = 25
 and dict_name = 'slotmanager.number-of-slots.max' AND dict_desc = '0';
