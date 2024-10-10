-- 删除控制台远程 debug 参数, ref:http://zenpms.dtstack.cn/zentao/bug-view-61803.html
DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.taskmanager';
DELETE FROM `console_component_config` WHERE `key` = 'env.java.opts.jobmanager';

DELETE FROM schedule_dict WHERE dict_name = 'env.java.opts.taskmanager' and `type` = 25;
DELETE FROM schedule_dict WHERE dict_name = 'env.java.opts.jobmanager' and `type` = 25;