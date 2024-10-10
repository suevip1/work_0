ALTER TABLE `console_component_auxiliary` DROP INDEX `uk_cluster_component_type`,
ADD UNIQUE INDEX `uk_cluster_component_type`(`cluster_id`, `component_type_code`, `type`) USING BTREE;

delete from `schedule_dict` where dict_code = 'LOG_TRACE' and `type` = 31;
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`) VALUES
    ('LOG_TRACE', 'LOG_TRACE', '-1010', NULL, 31, 0, 'LONG', '', 0, now(), now());

delete from `console_component_config` where `component_id` = -1010 and `component_type_code` = 15;
INSERT INTO  `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1010, 15, 'SELECT', 1, 'traceType', '', 'HDFS', '', '', '采集类型', now(), now(), 0);
INSERT INTO  `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1010, 15, 'INPUT', 1, 'logAggregationDir', '', NULL, '', '', 'log.aggregation.dir', now(), now(), 0);