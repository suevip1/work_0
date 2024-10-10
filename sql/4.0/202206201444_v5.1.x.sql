-- 调整 ranger 参数展示顺序, ref:http://zenpms.dtstack.cn/zentao/bug-view-57698.html
delete from schedule_dict where `type` = 25 and dict_name in ('url', 'username', 'password') and dict_desc = '31';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','url','',25,'','31', 1);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','username','',25,'','31', 2);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','password','',25,'','31', 3);

update schedule_dict set dict_value = 'jdbc 连接账号密码，若开启了 ldap+ranger 时，则填写 ldap 中的默认配置的账号密码'
  where `type` = 25 and dict_desc = 26 and dict_name = 'username';