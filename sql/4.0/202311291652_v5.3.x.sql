-- add starRocks 3.x
delete from `schedule_dict` where dict_code = 'starrocks_version' and dict_name in ('3.x', '2.x') and type = 37;
INSERT INTO `schedule_dict`
(id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES
    (null, 'starrocks_version', '2.x', '2.x', null, 37, 1, 'STRING', null, 1, now(), now(), 0),
    (null, 'starrocks_version', '3.x', '3.x', null, 37, 1, 'STRING', null, 1, now(), now(), 0);

-- 处理历史数据
update console_component set hadoop_version = '2.x', version_name = '2.x', gmt_modified = now()
    where component_type_code = 35 and hadoop_version = '';

-- 增加版本字典
update schedule_dict set dict_value = '{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "starrocks",
	"versionDictionary": "STARROCKS_VERSION"
}', gmt_modified = now() where `type` = 12 and dict_name = 'STARROCKS'
    and instr(dict_value, 'versionDictionary') <= 0;