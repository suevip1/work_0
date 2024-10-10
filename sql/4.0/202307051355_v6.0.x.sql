-- 考虑到几乎没有客户使用 hdfs 采集 k8s 日志，统一变更为 nfs, ref:http://zenpms.dtstack.cn/zentao/task-view-11084.html
update console_component_config set `values` = 'NFS', `gmt_modified` = now()
where `component_id` = -1010 and `cluster_id` = -2 and `key` = 'traceType' and `values` = 'HDFS';

-- 删掉旧配置
delete from console_component_config where `component_id` = -1010 and `cluster_id` = -2 and `key` = 'logAggregationDir';

-- 增加新配置
delete from console_component_config where `component_id` = -1010 and `cluster_id` = -2 and `key` = 'path';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1010, 15, 'INPUT', 1, 'path', '', NULL, '', '', '示例：/nas/dev/flink', now(), now(), 0);

delete from console_component_config where `component_id` = -1010 and `cluster_id` = -2 and `key` = 'server';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
    ( -2, -1010, 15, 'INPUT', 1, 'server', '', NULL, '', '', '示例：localhost', now(), now(), 0);

-- 统一移除用户历史配置，以便让用户重新配置
update console_component_auxiliary_config set is_deleted = 1, gmt_modified = now() where `key` = 'traceType' and `value` = 'HDFS';
update console_component_auxiliary_config set is_deleted = 1, gmt_modified = now() where `key` = 'logAggregationDir';
update console_component_auxiliary set is_deleted = 1, gmt_modified = now()  where id in
    (select auxiliary_id from console_component_auxiliary_config where is_deleted = 1);
