DELETE FROM schedule_dict WHERE dict_code = 'key_encrypt' and data_type = 17;
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('key_encrypt', 'javax.jdo.option.ConnectionPassword', '', null, 17, 1, 'STRING', '', 0, now(),now(), 0);