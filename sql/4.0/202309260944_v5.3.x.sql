-- BMR 2.x Spark2.4
-- 修改
update schedule_dict set  dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1007",
                "3.2":"-3001"
            }
        ]
    }
}'  where dict_code = 'extra_version_template' and dict_name = 'BMR 2.x';
delete from console_component_config where cluster_id = -2 and component_id = -1007 and type = 'CUSTOM_CONTROL' and required = 0 and `key` = 'spark.sql.hive.metastore.version' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id = -1007 and type = 'CUSTOM_CONTROL' and required = 0 and `key` = 'spark.sql.hive.metastore.jars' and `dependencyKey` = 'deploymode$perjob';
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
            (null,-2,-1007,1,'CUSTOM_CONTROL',0,'spark.sql.hive.metastore.version','3.0',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0),
            (null,-2,-1007,1,'CUSTOM_CONTROL',0,'spark.sql.hive.metastore.jars','./__spark_libs__/metastore/*',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

-- HDP 2.6.x Spark2.1
update schedule_dict set dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1008",
                "2.4":"-1006",
                "3.2":"-1006"
            }
        ]
    }
}' where dict_code = 'extra_version_template' and dict_name = 'HDP 2.6.x';
delete from console_component_config where cluster_id = -2 and component_id = -1008 and type = 'CUSTOM_CONTROL'  and `key` = 'spark.sql.hive.metastore.version' and dependencyKey = 'deploymode$perjob' ;
delete from console_component_config where cluster_id = -2 and component_id = -1008 and type = 'CUSTOM_CONTROL'  and `key` = 'spark.sql.hive.metastore.jars' and dependencyKey = 'deploymode$perjob' ;
delete from console_component_config where cluster_id = -2 and component_id = -1008 and type = 'CUSTOM_CONTROL'  and `key` = 'hdp.version' and dependencyKey = 'deploymode$perjob' ;
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
            (null,-2,-1008,1,'CUSTOM_CONTROL',0,'spark.sql.hive.metastore.version','3.0',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0),
            (null,-2,-1008,1,'CUSTOM_CONTROL',0,'spark.sql.hive.metastore.jars',null,null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1008,1,'CUSTOM_CONTROL',1,'hdp.version',null,null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

-- HDP 3.x
update schedule_dict set dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1008",
                "2.4":"-1006",
                "3.2":"-1009"
            }
        ]
    }
}' where dict_name = 'HDP 3.x' and dict_code  = 'extra_version_template';
delete from console_component_config where cluster_id = -2 and component_id = -1009 and type = 'CUSTOM_CONTROL'  and `key` = 'hdp.version' and dependencyKey = 'deploymode$perjob' ;
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1009,1,'CUSTOM_CONTROL',1,'hdp.version',null,null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

delete from schedule_dict where dict_code = 'hadoop_version' and dict_name = 'HDP 3.1.x' and dict_value = '3.1.1' and depend_name = 'HDP' and type = 0;
delete from schedule_dict where dict_code = 'component_model_config' and dict_name = 'HDP 3.1.x' and depend_name = 'YARN' and type = 14;
-- 新增 HDP 3.1.x
insert into schedule_dict(id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES
            (null,'hadoop_version','HDP 3.1.x','3.1.1',null,0,2,'STRING','HDP',0,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into schedule_dict(id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) values
             (null,'component_model_config','HDP 3.1.x','{
    "YARN":"yarn3",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn3-hdfs3-flink180"
            },
            {
                "1.10":"yarn3-hdfs3-flink110"
            },
            {
                "1.12":"yarn3-hdfs3-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}',null,14,1,'STRING','YARN',0,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
delete from schedule_dict where dict_code = 'extra_version_template' and dict_name = 'HDP 3.1.x' and depend_name = 'YARN' and type =15;
insert into schedule_dict(id, dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUE
(null,'extra_version_template','HDP 3.1.x','{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1008",
                "2.4":"-1011",
                "3.2":"-1006"
            }
        ]
    }
}',null,15,1,'STRING','YARN',0,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

delete from console_component_config where cluster_id =  -2 and component_id = -1011 and type = 'CUSTOM_CONTROL' and `key` = 'hdp.version' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id =  -2 and component_id = -1011 and type = 'CUSTOM_CONTROL' and `key` = 'spark.sql.hive.metastore.jars' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id =  -2 and component_id = -1011 and type = 'CUSTOM_CONTROL' and `key` = 'spark.sql.hive.metastore.version' and dependencyKey = 'deploymode$perjob';
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1011,1,'CUSTOM_CONTROL',1,'hdp.version',null,null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1011,1,'CUSTOM_CONTROL',1,'spark.sql.hive.metastore.jars','./__spark_libs__/metastore/*',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1011,1,'CUSTOM_CONTROL',1,'spark.sql.hive.metastore.version','3.0',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

-- CDP 7.x
update schedule_dict set dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.4":"-1012"
            }
        ]
    }
}' where dict_name = 'CDP 7.x' and dict_code  = 'extra_version_template';
delete from console_component_config where cluster_id = -2 and component_id = -1012 and type = 'CUSTOM_CONTROL' and `key`='spark.sql.hive.metastore.jars' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id = -1012 and type = 'CUSTOM_CONTROL' and `key`='spark.sql.hive.metastore.version' and dependencyKey = 'deploymode$perjob';
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1012,1,'CUSTOM_CONTROL',1,'spark.sql.hive.metastore.jars','./__spark_libs__/metastore/*',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config (id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUE
    (null,-2,-1012,1,'CUSTOM_CONTROL',1,'spark.sql.hive.metastore.version','3.0',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

-- TDH 5.2.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 5.2.x' and dict_code  = 'extra_version_template';
delete from console_component_config where cluster_id = -2 and component_id = -1016 and component_type_code = 1 and type = 'CUSTOM_CONTROL' and `key` = 'dtscript.container.env.JAVA_HOME';
delete from console_component_config where cluster_id = -2 and component_id = -1013 and component_type_code = 1 and type = 'CUSTOM_CONTROL' and `key` = 'containerized.master.env.JAVA_HOME' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id = -1013 and component_type_code = 1 and type = 'CUSTOM_CONTROL' and `key` = 'containerized.taskmanager.env.JAVA_HOME' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id = -1013 and component_type_code = 1 and type = 'CUSTOM_CONTROL' and `key` = 'containerized.master.env.JAVA_HOME' and dependencyKey = 'deploymode$session';
delete from console_component_config where cluster_id = -2 and component_id = -1013 and component_type_code = 1 and type = 'CUSTOM_CONTROL' and `key` = 'containerized.taskmanager.env.JAVA_HOME' and dependencyKey = 'deploymode$session';
delete from console_component_config where cluster_id = -2 and component_id = -1014 and component_type_code = 1 and type = 'INPUT' and `key` = 'spark.executorEnv.JAVA_HOME' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id = -1014 and component_type_code = 1 and type = 'INPUT' and `key` = 'spark.yarn.appMasterEnv.JAVA_HOME' and dependencyKey = 'deploymode$perjob';

insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1016,1,'CUSTOM_CONTROL',0,'dtscript.container.env.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,null,null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1013,1,'CUSTOM_CONTROL',0,'containerized.master.env.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1013,1,'CUSTOM_CONTROL',0,'containerized.taskmanager.env.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1013,1,'CUSTOM_CONTROL',0,'containerized.master.env.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$session',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1013,1,'CUSTOM_CONTROL',0,'containerized.taskmanager.env.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$session',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1014,1,'INPUT',0,'spark.executorEnv.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1014,1,'INPUT',0,'spark.yarn.appMasterEnv.JAVA_HOME','/etc/transwarp/conf/jdk1.8.0_144',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
-- TDH 6.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 6.x' and dict_code  = 'extra_version_template';
-- TDH 7.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1013"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1014",
                "3.2":"-1014"
            }
        ],
        "DT_SCRIPT":"-1016"
    }
}' where dict_name = 'TDH 7.x' and dict_code  = 'extra_version_template';

-- HW MRS 3.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.12":"-1015"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}' where dict_name = 'HW MRS 3.x' and dict_code  = 'extra_version_template';
-- -100
delete from console_component_config where cluster_id = -2 and component_id =-1015 and type ='CUSTOM_CONTROL' and `key` = 'containerized.master.env.HADOOP_CONF_DIR' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id =-1015 and type ='CUSTOM_CONTROL' and `key` = 'containerized.taskmanager.env.HADOOP_CONF_DIR' and dependencyKey = 'deploymode$perjob';
delete from console_component_config where cluster_id = -2 and component_id =-1015 and type ='CUSTOM_CONTROL' and `key` = 'containerized.master.env.HADOOP_CONF_DIR' and dependencyKey = 'deploymode$session';
delete from console_component_config where cluster_id = -2 and component_id =-1015 and type ='CUSTOM_CONTROL' and `key` = 'containerized.taskmanager.env.HADOOP_CONF_DIR' and dependencyKey = 'deploymode$session';
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1015,0,'CUSTOM_CONTROL',0,'containerized.master.env.HADOOP_CONF_DIR','./',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1015,0,'CUSTOM_CONTROL',0,'containerized.taskmanager.env.HADOOP_CONF_DIR','./',null,'deploymode$perjob',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1015,0,'CUSTOM_CONTROL',0,'containerized.master.env.HADOOP_CONF_DIR','./',null,'deploymode$session',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);
insert into console_component_config(id, cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted) VALUES
    (null,-2,-1015,0,'CUSTOM_CONTROL',0,'containerized.taskmanager.env.HADOOP_CONF_DIR','./',null,'deploymode$session',null,null,'2023-09-26 13:44:13','2023-09-26 13:44:13',0);

-- HW HD6.x
update schedule_dict set dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.12":"-1015"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}' where dict_name = 'HW HD6.x' and dict_code  = 'extra_version_template';
