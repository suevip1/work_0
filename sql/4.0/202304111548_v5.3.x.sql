-- StarRocks 增加 Fe Nodes
-- 1）新增模板参数
delete from console_component_config where `component_id` = -154 and `component_type_code` = 35 and `key` = 'Fe Nodes';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required,
                                      `key`, value, `values`, dependencyKey, dependencyValue,
                                      `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -154, 35, 'INPUT', 1, 'Fe Nodes', '', null, null, null, null, now(), now(), 0);

-- 2）新增注释
delete from schedule_dict where `dict_code` = 'tips' and `type` = 25 and `dict_desc` = '35' and `dict_name` = 'Fe Nodes';
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`,
                            `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`,
                            `is_deleted`)
VALUES ('tips', 'Fe Nodes', '格式<fe_host>:<fe_http_port>，指定多个URL时，必须由英文逗号（,）分开', '35', 25, 6, 'STRING', '', 0, now(), now(), 0);

-- 3）处理历史参数
--  3.1）如果是自定义参数，将其转换为必填参数
update console_component_config set `type` = 'INPUT', required = 1, gmt_modified = now() where cluster_id != -2
    and `key` = 'Fe Nodes' and `type` = 'CUSTOM_CONTROL'  and component_type_code = 35;