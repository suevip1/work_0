-- modify jdbcUrl tip, ref:http://zenpms.dtstack.cn/zentao/bug-view-65526.html
update schedule_dict set dict_value = 'jdbc:hive2://host:port/%s' where dict_desc = '6' and dict_name = 'jdbcUrl' and dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:hive2://host:port/%s' where dict_desc = '9'  and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:postgresql://host:port/postgres' where dict_desc = '8' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:impala://host:port/%s;AuthMech=3' where dict_desc = '11' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:mysql://host:port/%s' where dict_desc = '12' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:oracle:thin:@host:port:orcl' where dict_desc = '13' and dict_name = 'jdbcUrl' and dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:pivotal:greenplum://host:port;DatabaseName=schema' where dict_desc = '14'  and dict_name = 'jdbcUrl' and dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:presto://host:port/catalog/schema' where dict_desc = '16'  and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:hive2://host:port/schema' where dict_desc = '19'  and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:postgresql://host/schema' where dict_desc = '21' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:mysql://host:port/%s' where dict_desc = '22' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:jtds:sqlserver://host:port;DatabaseName=schema' where dict_desc = '23' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:trino://host:port/catalog/schema' where dict_desc = '26' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:oceanbase://host:port/database' where dict_desc = '25'  and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:sap://host:port/database' where dict_desc = '33' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;
update schedule_dict set dict_value = 'jdbc:db2://host:port/database' where dict_desc = '24' and dict_name = 'jdbcUrl' and  dict_code = 'tips' and `type` = 25;