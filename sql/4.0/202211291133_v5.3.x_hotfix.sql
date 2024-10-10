-- 针对 sql 重命名(202209061513_v5.3.x.sql --> 202210120000_v5.3.x.sql)影响的修复
-- ref: http://zenpms.dtstack.cn/zentao/bug-view-69861.html
-- 数据安全参数模板
delete from schedule_dict where dict_code = 'security_param_template' and dict_name = 'HDFS';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES
    ('security_param_template', 'HDFS', '{"hdfs2":-1005,"hdfs3":-1005}', null, 33, 0, 'STRING', null, 0, NOW(), NOW(), 0);

-- 调整参数展示顺序
delete from schedule_dict where `type` = 25 and dict_name in ('serviceName', 'description', 'username', 'password') and dict_desc = '4';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','serviceName',null,25, null,'4', 1);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','description',null,25, null,'4', 2);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','username',null,25, null,'4', 3);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','password',null,25, null,'4', 4);

-- 密码以密文形式显示
DELETE FROM schedule_dict WHERE dict_code = 'key_encrypt' and  `type` = 17 and dict_name = 'password';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('key_encrypt', 'password', '', null, 17, 1, 'STRING', '', 0, now(),now(), 0);

-- hadoop 代理参数更正为必填项(PS:需求来源于修竹)
delete from console_component_config where component_id = -143 and component_type_code = 34;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -143, 34, 'INPUT', 1, 'hadoop.proxy.enable', 'false', null, null, null, null, now(), now(), 0);