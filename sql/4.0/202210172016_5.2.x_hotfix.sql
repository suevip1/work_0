-- flink 112 session 模式, 新增 slotmanager.number-of-slots.debug.max 默认值改为 2 ref:http://zenpms.dtstack.cn/zentao/bug-view-66779.html
DELETE from console_component_config where component_id = -115 and `key` = 'slotmanager.number-of-slots.debug.max' and `dependencyKey` = 'deploymode$session';
INSERT INTO `console_component_config` (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES
    (-2, -115, 0, 'INPUT', 1, 'slotmanager.number-of-slots.debug.max', '2', NULL, 'deploymode$session', NULL, NULL, now(), now(), 0);

