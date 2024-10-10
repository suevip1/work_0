-- add by qiuyun

-- 5.0.x 分支，新增 s3 组件
INSERT INTO schedule_dict
    (id, dict_code, dict_name,
     dict_value,
     dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
    VALUES (null, 'component_model', 'S3', '{
	"owner": "STORAGE",
	"dependsOn": [],
	"versionDictionary": "S3_VERSION",
	"allowCoexistence": false,
	"nameTemplate": "s3{}",
	"supportedPlugins": []
}', null, 12, 0, 'STRING', '', 0, now(), now(), 0);