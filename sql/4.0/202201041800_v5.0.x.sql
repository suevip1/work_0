update schedule_dict set dict_value = '5.x' where dict_code = 'mysql_version' and dict_name = '5.x';

delete from schedule_dict where type in (0,12,14,15,16);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'Apache Hadoop 2.x', '2.7.6', null, 0, 1, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'Apache Hadoop 3.x', '3.0.0', null, 0, 2, 'STRING', 'Apache Hadoop', 0, '2021-12-28 10:18:58', '2021-12-28 10:18:58', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'HDP 2.6.x', '2.7.3', null, 0, 1, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'HDP 3.x', '3.1.1', null, 0, 2, 'STRING', 'HDP', 0, '2021-12-28 10:18:59', '2021-12-28 10:18:59', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 5.x', '2.3.0', null, 0, 1, 'STRING', 'CDH', 0, '2021-12-28 10:19:00', '2021-12-28 10:19:00', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.0.x', '3.0.0', null, 0, 11, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.1.x', '3.0.0', null, 0, 12, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDH 6.2.x', '3.0.0', null, 0, 13, 'STRING', 'CDH', 0, '2021-12-28 10:19:01', '2021-12-28 10:19:01', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'CDP 7.x', '3.1.1', null, 0, 15, 'STRING', 'CDP', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 5.2.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TBDS 5.1.x', '2.7.2:tbds', null, 0, 1, 'STRING', 'TBDS', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 7.x', '2.7.0', null, 0, 2, 'STRING', 'TDH', 0, '2021-12-28 10:19:02', '2021-12-28 10:19:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('hadoop_version', 'TDH 6.x', '2.7.0', null, 0, 1, 'STRING', 'TDH', 0, '2021-12-28 11:44:02', '2021-12-28 11:44:02', 0);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'HDFS', '{
	"owner": "STORAGE",
	"dependsOn": ["RESOURCE"],
	"allowCoexistence": false,
	"versionDictionary": "HADOOP_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'FLINK', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "FLINK_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SPARK', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "SPARK_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-28 16:54:54', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SPARK_THRIFT', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"versionDictionary": "SPARK_THRIFT_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'HIVE_SERVER', '{
	"owner": "COMPUTE",
    "dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"versionDictionary": "HIVE_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'SFTP', '{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "dummy"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'YARN', '{
	"owner": "RESOURCE",
	"dependsOn": [],
	"allowCoexistence": false,
	"versionDictionary": "HADOOP_VERSION"
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'KUBERNETES', '{
	"owner": "RESOURCE",
	"dependsOn": [],
	"allowCoexistence": false
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'NFS', '{
	"owner": "STORAGE",
	"dependsOn": ["RESOURCE"],
	"allowCoexistence": false
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model', 'LEARNING', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false
}', null, 12, 0, 'STRING', '', 0, '2021-12-07 11:26:57', '2021-12-07 11:26:57', 0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','DT_SCRIPT','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','LIBRA_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "postgresql"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','IMPALA_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "impala"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','TIDB_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "tidb"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','ORACLE_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "oracle"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','GREENPLUM_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "greenplum"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','PRESTO_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "presto"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','DTSCRIPT_AGENT','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "dtscript-agent"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','INCEPTOR_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "inceptor",
    "versionDictionary":"INCEPTOR_SQL"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','ANALYTICDB_FOR_PG','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "adb-postgresql",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','MYSQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "mysql",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','SQL_SERVER','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "sqlserver",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','TRINO_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "trino",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','OCEANBASE','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "oceanbase",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','DB2','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
    "nameTemplate": "db2",
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES ('component_model','TONY','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);




INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:01:55', '2021-12-28 11:01:55', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'Apache Hadoop 3.x', '{
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
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:03:45', '2021-12-28 11:03:45', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.0.x', '{
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
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:23', '2021-12-28 11:04:23', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.0.x', '{
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
            {   "2.1":"yarn3-hdfs3-spark210",
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:40', '2021-12-28 11:04:40', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.1.x', '{
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
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:04:55', '2021-12-28 11:04:55', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 6.2.x', '{
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
                "2.4(CDH 6.2)":"yarn3-hdfs3-spark240cdh620"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:05:06', '2021-12-28 11:05:06', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 2.6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:06:38', '2021-12-28 11:06:38', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDH 5.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:07:19', '2021-12-28 11:07:19', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'HDP 3.x', '{
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
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:43:05', '2021-12-28 11:43:05', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 5.2.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:33', '2021-12-28 11:44:33', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 6.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:44:43', '2021-12-28 11:44:43', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TDH 7.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.1":"yarn2-hdfs2-spark210",
                "2.4":"yarn2-hdfs2-spark240"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'CDP 7.x', '{
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
                "2.4":"yarn3-hdfs3-spark240"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-28 11:45:02', '2021-12-28 11:45:02', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', 'TBDS 5.1.x', '{
    "YARN":"yarn2",
    "HDFS":{
        "FLINK":[
            {
                "1.8":"yarn2-hdfs2-flink180"
            },
            {
                "1.10":"yarn2-hdfs2-flink110"
            },
            {
                "1.12":"yarn2-hdfs2-flink112"
            }
        ],
         "HDFS":"yarn2-hdfs2-hadoop2"
    }
}', null, 14, 1, 'STRING', 'YARN', 0, '2021-12-29 17:35:27', '2021-12-29 17:35:27', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', ' ', '{
    "k8s":"kubernetes",
    "S3":{
        "FLINK":[
            {
                "1.10":"k8s-nfs-flink110"
            }
        ],
        "S3":"S3"
    },
    "NFS":{
        "FLINK":[
            {
                "1.12":"k8s-s3-flink112"
            }
        ],
        "NFS":"NFS"
    }
}', null, 14, 1, 'STRING', 'KUBERNETES', 0, '2021-12-30 21:01:58', '2021-12-30 21:01:58', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.x', '{
    "1.x":"hive"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '2.x', '{
    "2.x":"hive2"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '3.x-apache', '{
    "3.x-apache":"hive3"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '3.x-cdp', '{
    "3.x-cdp":"hive3"
}', null, 14, 1, 'STRING', 'HIVE_SERVER', 0, '2021-12-31 14:53:44', '2021-12-31 14:53:44', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.x', '{
    "1.x":"hive"
}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '2.x', '{
    "2.x":"hive2"
}', null, 14, 1, 'STRING', 'SPARK_THRIFT', 0, '2021-12-31 15:00:16', '2021-12-31 15:00:16', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.12', '{
    "1.12":"flink112-standalone"
}', null, 14, 1, 'STRING', 'FLINK', 0, '2021-12-31 17:34:04', '2021-12-31 17:34:04', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.10', '{
    "1.10":"flink110-standalone"
}', null, 14, 1, 'STRING', 'FLINK', 0, '2021-12-31 17:34:21', '2021-12-31 17:34:21', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('component_model_config', '1.8', '{
    "1.8":"flink110-standalone"
}', null, 14, 1, 'STRING', 'FLINK', 0, '2022-01-04 17:53:05', '2022-01-04 17:53:05', 0);

update console_component set version_name = 'Apache Hadoop 2.x' where version_name = 'Hadoop 2.x' and component_type_code IN (4,5);
update console_component set version_name = 'Apache Hadoop 3.x' where version_name = 'Hadoop 3.x' and component_type_code IN (4,5);

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', '3.x-cdp', '{
    "3.x-cdp":"-212"
}', null, 15, 1, 'STRING', 'HIVE_SERVER', 0, '2022-01-10 17:36:43', '2022-01-10 17:36:43', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('extra_version_template', '3.x-apache', '{
    "3.x-apache":"-212"
}', null, 15, 1, 'STRING', 'HIVE_SERVER', 0, '2022-01-10 17:36:43', '2022-01-10 17:36:43', 0);

update schedule_dict set dict_value = '6.2.x' where dict_code = 'inceptor_sql_version';


INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default,
                           gmt_create, gmt_modified, is_deleted)
VALUES ('hdfs_type_name', 'TBDS 5.1.x', 'hdfs2tbds', null, 16, 1, 'STRING', null, 0, now(),
        now(), 0);



