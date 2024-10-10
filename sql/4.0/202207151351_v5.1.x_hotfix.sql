-- 修复错误配置项：yarnSessionName --> flinkSessionName, ref：http://zenpms.dtstack.cn/zentao/bug-view-60961.html
update console_component_config set `key` = 'flinkSessionName' where `key` = 'yarnSessionName' and component_type_code = 0;

DELETE from schedule_dict WHERE `type` = 25 AND dict_name = 'yarnSessionName' and dict_code = 'tips';