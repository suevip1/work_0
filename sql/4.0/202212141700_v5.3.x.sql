DELETE
FROM schedule_dict
WHERE type = 18;

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('exclude_xml_param', 'TDH 7.x', 'hadoop.security.group.mapping.ldap.bind.password.file', '', 18, 0, 'LONG',
        'core-site.xml', 0, now(), now(), 0);


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type,
                           depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('exclude_xml_param', 'TDH 6.x', 'hadoop.security.group.mapping.ldap.bind.password.file', '', 18, 0, 'LONG',
        'core-site.xml', 0, now(), now(), 0);