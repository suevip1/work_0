delete from schedule_dict where dict_code = 'component_model' and `type` = 12 and dict_name = 'COMMON';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('component_model', 'COMMON', '{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "common"
}', null, 12, 0, 'STRING', '', 0, now(), now(), 0);

delete from schedule_dict where dict_code = 'typename_mapping' and `type` = 6 and dict_name = 'common';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('typename_mapping', 'common', '-143', null, 6, 0, 'LONG', '', 0, now(), now(), 0);

delete from console_component_config where component_id = -143 and component_type_code = 34;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (-2, -143, 34, 'INPUT',0, 'hadoop.proxy.enable', 'false', null, null, null, null, now(), now(), 0);

delete from schedule_dict where dict_code = 'jdbc_url_tip' and dict_name ='Common' and depend_name = 'hadoop.proxy.enable';
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, `type`, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
    ('jdbc_url_tip', 'Common', 'hadoop 集群代理账号，true：开启，false：关闭', NULL, 10, 0, 'STRING', 'hadoop.proxy.enable', 0, now(), now(), 0);