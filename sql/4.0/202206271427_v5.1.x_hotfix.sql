-- 补充缺失参数, ref:http://zenpms.dtstack.cn/zentao/bug-view-59701.html
DELETE FROM `console_component_config` WHERE `cluster_id` = -2 AND `component_id` = -107 AND component_type_code = 1 and `key` = 'spark.executorEnv.KUBERNETES_HOST_ALIASES';
INSERT INTO `console_component_config`
  (`cluster_id`, `component_id`, `component_type_code`, `type`, `required`, `key`, `value`, `values`, `dependencyKey`, `dependencyValue`, `desc`, `gmt_create`, `gmt_modified`, `is_deleted`)
values
  (-2, -107, 1, 'INPUT', 0, 'spark.executorEnv.KUBERNETES_HOST_ALIASES', '', NULL, 'deploymode$perjob', NULL, NULL, '2022-03-02 11:37:54', '2022-03-02 11:37:54', 0);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.executorEnv.KUBERNETES_HOST_ALIASES' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.executorEnv.KUBERNETES_HOST_ALIASES','用于在spark executor pod中添加自定义域名解析，格式: ip1 hostname1;ip2 hostname2',25,'环境变量','1', 4);
