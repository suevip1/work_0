update schedule_dict set dict_value = 'jdbc:hive2://host:port/%s' where `type` = 10 and dict_name = 'InceptorSql' and dict_value = 'jdbc:hive2://host:port/schema';

update schedule_dict set dict_value = 'jdbc:hive2://host:port/%s' where dict_value = 'jdbc:hive2://host:port/schema' and `type` = 25 and dict_desc = '19';