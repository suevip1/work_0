INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES ('typename_mapping', 'yarn2-hdfs2-tony', '-119', NULL, 6, 0, 'LONG', '', 0, NOW(), NOW(), 0);
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`)
VALUES ('typename_mapping', 'yarn3-hdfs3-tony', '-121', NULL, 6, 0, 'LONG', '', 0, NOW(), NOW(), 0);

INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -119, 27, 'INPUT', 0, 'tony.application.single-node', 'false', NULL, NULL, NULL, "是否为单节点模式", NOW(), NOW(), 0);
INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -119, 27, 'INPUT', 0, 'python3.path', '/data/anaconda3/bin/python3', NULL, NULL, NULL, "python3路径", NOW(), NOW(), 0);

INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -121, 27, 'INPUT', 0, 'tony.application.single-node', 'false', NULL, NULL, NULL, "是否为单节点模式", NOW(), NOW(), 0);
INSERT INTO `console_component_config` ( `cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
( -2, -121, 27, 'INPUT', 0, 'python3.path', '/data/anaconda3/bin/python3', NULL, NULL, NULL, "python3路径", NOW(), NOW(), 0);

INSERT INTO `task_param_template` ( `gmt_create`, `gmt_modified`, `is_deleted`, `compute_type`, `engine_type`, `task_type`, `params`) VALUES
( NOW(), NOW(), '0', '1', '31', '0', '# 实例个数
tony.am.instances = 1

# 内存大小，字符串格式
tony.am.memory = 2g

# 核数
tony.am.vcores = 1

# gpu个数
tony.am.gpus = 0

# 实例个数
tony.ps.instances = 1

# 内存大小，字符串格式
tony.ps.memory = 2g

# 核数
tony.ps.vcores = 1

# gpu个数
tony.ps.gpus = 0

# worker实例个数
tony.worker.instances = 1

# 内存大小，字符串格式
tony.worker.memory = 2g

# 核数
tony.worker.vcores = 1

# gpu个数
tony.worker.gpus = 0');

