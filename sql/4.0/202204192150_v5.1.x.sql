-- add sparksql parameter, add by qiuyun
delete from `console_component_config` where component_id in (-108, -130)  and `key` = 'sparkspark.sql.crossJoin.enabled';
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`) VALUES
   (-2, -108, 1, 'INPUT', 1, 'spark.sql.crossJoin.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-04-19 21:57:53', '2022-04-19 21:57:53');
INSERT INTO `console_component_config`(`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`) VALUES
    (-2, -130, 1, 'INPUT', 1, 'spark.sql.crossJoin.enabled', 'true', NULL, 'deploymode$perjob', NULL, NULL, '2022-04-19 21:57:53', '2022-04-19 21:57:53');