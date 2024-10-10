DELETE FROM schedule_dict WHERE dict_code = 'typename_mapping' AND dict_name IN ('mysql5','mysql8','mysql');
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
 VALUES ('typename_mapping', 'mysql', '-104', null, 6, 0, 'LONG', '', 0, now(),now(), 0);