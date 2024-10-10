INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('hive_version', '3.x-cdp', '3.x-cdp', null, 4, 3, 'STRING', '', 1, now(), now(), 0);

UPDATE schedule_dict
SET dict_name  = '3.x-apache',
    dict_value = '3.x-apache'
WHERE dict_code = 'hive_version' and dict_name = '3.x';
