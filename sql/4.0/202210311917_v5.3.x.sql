-- http://zenpms.dtstack.cn/zentao/story-view-8166.html spark + iceberg
-- (1) 所有 hadoop 版本都支持 spark320
UPDATE schedule_dict
SET dict_value = '{
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
}'
WHERE dict_name = 'HDP 3.0.x'
AND dict_code = 'component_model_config'
AND type = 14;

UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn3-hdfs3-spark240",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}'
WHERE dict_name = 'CDH 6.0.x'
AND dict_code = 'component_model_config'
AND type = 14;

UPDATE schedule_dict
SET dict_value = '{
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
}'
WHERE dict_name = 'CDH 6.1.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
               "2.4":"yarn3-hdfs3-spark240cdh620",
               "3.2":"yarn3-hdfs3-spark320"
           }
        ],
       "DT_SCRIPT":"yarn3-hdfs3-dtscript",
       "HDFS":"yarn3-hdfs3-hadoop3",
       "TONY":"yarn3-hdfs3-tony",
       "LEARNING":"yarn3-hdfs3-learning"
   }
}'
WHERE dict_name = 'CDH 6.2.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'HDP 2.6.x'
AND dict_code = 'component_model_config'
AND type = 14;

UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'CDH 5.x'
  AND dict_code = 'component_model_config'
  AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
}'
WHERE dict_name = 'HDP 3.x'
AND dict_code = 'component_model_config'
AND type = 14;

UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'TDH 5.2.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'TDH 6.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn2-hdfs2-spark240",
                "3.2":"yarn2-hdfs2-spark320"
            }
        ],
        "DT_SCRIPT":"yarn2-hdfs2-dtscript",
        "HDFS":"yarn2-hdfs2-hadoop2",
        "TONY":"yarn2-hdfs2-tony",
        "LEARNING":"yarn2-hdfs2-learning"
    }
}'
WHERE dict_name = 'TDH 7.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
                                  "2.4":"yarn3-hdfs3-spark240cdh620",
                                  "3.2":"yarn3-hdfs3-spark320"
                              }
                          ],
                          "DT_SCRIPT":"yarn3-hdfs3-dtscript",
                          "HDFS":"yarn3-hdfs3-hadoop3",
                          "TONY":"yarn3-hdfs3-tony",
                          "LEARNING":"yarn3-hdfs3-learning"
                      }
}'
WHERE dict_name = 'CDP 7.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
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
                "2.4":"yarn3-hdfs3-spark240",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3-hdfs3-dtscript",
        "HDFS":"yarn3-hdfs3-hadoop3",
        "TONY":"yarn3-hdfs3-tony",
        "LEARNING":"yarn3-hdfs3-learning"
    }
}'
WHERE dict_name = 'BMR 2.x'
AND dict_code = 'component_model_config'
AND type = 14;



UPDATE schedule_dict
SET dict_value = '{
    "YARN":"yarn2tbds",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn2tbds-hdfs2tbds-flink110",
                "1.12":"yarn2tbds-hdfs2tbds-flink112"
            }
        ],
         "HDFS":"yarn2tbds-hdfs2tbds-hadoop2tbds",
         "SPARK":[
             {
                 "2.4":"yarn2tbds-hdfs2tbds-spark240",
                 "3.2":"yarn2tbds-hdfs2tbds-spark320"
             }
         ],
         "DT_SCRIPT":"yarn2tbds-hdfs2tbds-dtscript"
    }
}'
WHERE dict_name = 'TBDS 5.1.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
    "YARN":"yarn3hw",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240hw",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw"
    }
}'
WHERE dict_name = 'HW MRS 3.x'
AND dict_code = 'component_model_config'
AND type = 14;


UPDATE schedule_dict
SET dict_value = '{
    "YARN":"yarn3hw",
    "HDFS":{
        "FLINK":[
            {
                "1.10":"yarn3hw-hdfs3hw-flink110"
            }
        ],
        "SPARK":[
            {
                "2.4":"yarn3hw-hdfs3hw-spark240hw",
                "3.2":"yarn3-hdfs3-spark320"
            }
        ],
        "DT_SCRIPT":"yarn3hw-hdfs3hw-dtscript",
        "HDFS":"yarn3hw-hdfs3hw-hadoop3hw"
    }
}'
WHERE dict_name = 'HW HD6.x'
AND dict_code = 'component_model_config'
AND type = 14;

-- 修改spark3.2适配参数
UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001",
                "3.2":"-1001"
            }
        ]
    }
}'
WHERE dict_name = 'TDH 5.2.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;

UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001",
                "3.2":"-1001"
            }
        ]
    }
}'
WHERE dict_name = 'TDH 6.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;


UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
    	"FLINK":[
            {
                "1.8":"-1002"
            },
            {
                "1.10":"-1002"
            },
            {
                "1.12":"-1002"
            }
        ],
        "SPARK":[
            {
                "2.1":"-1001",
                "2.4":"-1001",
                "3.2":"-1001"
            }
        ]
    }
}'
WHERE dict_name = 'TDH 7.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;


UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1003",
                "3.2":"-1003"
            }
        ]
    }
}'
WHERE dict_name = 'BMR 2.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;


UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1003",
                "3.2":"-1003"
            }
        ]
    }
}'
WHERE dict_name = 'HDP 3.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;


UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}'
WHERE dict_name = 'HW MRS 3.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;

UPDATE schedule_dict
SET dict_value = '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1004",
                "2.4":"-1004",
                "3.2":"-1004"
            }
        ]
    }
}'
WHERE dict_name = 'HW HD6.x'
  AND dict_code = 'extra_version_template'
  AND type = 15;

-- (2) 修改 spark 的 component_config
-- <1> 去掉 spark320 和 spark240 的 spark.sql.extensions 配置项 (包括模板和历史集群)
DELETE FROM console_component_config WHERE `key` = 'spark.sql.extensions' AND component_type_code = 1;

-- <2> spark210,240,320 增加 HiveMetastore 相关参数
DELETE FROM console_component_config WHERE component_id in (-108,-130,-132) AND `key` in ('spark.hadoop.hive.exec.scratchdir','spark.hadoop.hive.metastore.uris','spark.hadoop.hive.metastore.warehouse.dir','spark.hadoop.hive.metastore.sasl.enabled','spark.hadoop.hive.metastore.kerberos.principal');
INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `desc`)
VALUES (-2, -108, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.scratchdir', 'hdfs:///dtInsight/spark/tmp', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -108, 1, 'INPUT', 1, 'spark.hadoop.hive.metastore.uris', '', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -108, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.warehouse.dir', 'hdfs:///dtInsight/hive/warehouse', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -108, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.sasl.enabled', 'false', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -108, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.kerberos.principal', '', NULL, 'deploymode$perjob', NULL, NULL);

INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `desc`)
VALUES (-2, -130, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.scratchdir', 'hdfs:///dtInsight/spark/tmp', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -130, 1, 'INPUT', 1, 'spark.hadoop.hive.metastore.uris', '', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -130, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.warehouse.dir', 'hdfs:///dtInsight/hive/warehouse', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -130, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.sasl.enabled', 'false', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -130, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.kerberos.principal', '', NULL, 'deploymode$perjob', NULL, NULL);

INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 0, 'spark.hadoop.hive.exec.scratchdir', 'hdfs:///dtInsight/spark/tmp', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 1, 'spark.hadoop.hive.metastore.uris', '', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.warehouse.dir', 'hdfs:///dtInsight/hive/warehouse', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.sasl.enabled', 'false', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.hadoop.hive.metastore.kerberos.principal', '', NULL, 'deploymode$perjob', NULL, NULL);




-- <3> spark320 增加 DataLake 相关参数
DELETE FROM console_component_config WHERE component_id = -132 AND `key` in ('spark.dataLake.type','spark.sql.catalog.spark_catalog.type','spark.sql.catalog.spark_catalog');
INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 1, 'spark.dataLake.type', 'none', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.catalog.spark_catalog.type', 'hive', NULL, 'deploymode$perjob', NULL, NULL),
       (-2, -132, 1, 'INPUT', 0, 'spark.sql.catalog.spark_catalog', 'org.apache.iceberg.spark.SparkSessionCatalog', NULL, 'deploymode$perjob', NULL, NULL);


-- <3> 新增 HiveMetastore DataLake 分组，和各新增参数的 tips
-- 修改历史分组的顺序
UPDATE schedule_dict SET sort = 10  WHERE `type` = 25 AND dict_name = '主要' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 20  WHERE `type` = 25 AND dict_name = '资源' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 30  WHERE `type` = 25 AND dict_name = '网络' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 40  WHERE `type` = 25 AND dict_name = '事件日志' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 50  WHERE `type` = 25 AND dict_name = 'JVM' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 60  WHERE `type` = 25 AND dict_name = '环境变量' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 70  WHERE `type` = 25 AND dict_name = 'kubernetes' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 80  WHERE `type` = 25 AND dict_name = '安全' AND dict_desc = '1';

UPDATE schedule_dict SET sort = 110 WHERE `type` = 25 AND dict_name = 'metric' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 120 WHERE `type` = 25 AND dict_name = 'sql' AND dict_desc = '1';
UPDATE schedule_dict SET sort = 130 WHERE `type` = 25 AND dict_name = '自定义参数' AND dict_desc = '1';

-- 增加 HiveMetastore,DataLake 分组
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'HiveMetastore' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','HiveMetastore',25,'1', 90);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'DataLake' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,`type`,dict_desc, sort) VALUES ('tips','DataLake',25,'1', 100);


-- 增加 HiveMetastore DataLake 的 tips 提示
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hadoop.hive.exec.scratchdir' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hadoop.hive.exec.scratchdir','该目录由 Hive 用来存储查询的不同 map/reduce 阶段的计划，以及存储这些阶段的中间输出',25,'HiveMetastore','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hadoop.hive.metastore.uris' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hadoop.hive.metastore.uris','Hive 元数据服务 uri',25,'HiveMetastore','1',2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hadoop.hive.metastore.warehouse.dir' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hadoop.hive.metastore.warehouse.dir','Hive 用于存放 Hive 元数据的目录位置',25,'HiveMetastore','1', 3);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hadoop.hive.metastore.sasl.enabled' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hadoop.hive.metastore.sasl.enabled','metastore thrift 接口的安全策略',25,'HiveMetastore','1', 4);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.hadoop.hive.metastore.kerberos.principal' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.hadoop.hive.metastore.kerberos.principal','链接 metastore 所需要的 kerberos 票据的 principal',25,'HiveMetastore','1', 5);


DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.dataLake.type' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.dataLake.type','spark 对接的数据湖插件类型',25,'DataLake','1', 1);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.catalog.spark_catalog.type' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.catalog.spark_catalog.type','iceberg 对接的 catalog 类型',25,'DataLake','1', 2);

DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.sql.catalog.spark_catalog' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.sql.catalog.spark_catalog','iceberg 所使用的 catalog 类',25,'DataLake','1', 3);



-- 历史集群补充参数
DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.exec.scratchdir';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.hadoop.hive.exec.scratchdir', 'hdfs:///dtInsight/spark/tmp',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                         AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version IN ('210','240','320')
        );



DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.metastore.uris';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'spark.hadoop.hive.metastore.uris', '',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                           AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version IN ('210','240','320')
        );



DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.metastore.warehouse.dir';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.hadoop.hive.metastore.warehouse.dir', 'hdfs:///dtInsight/hive/warehouse',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                           AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version IN ('210','240','320')
        );


DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.metastore.sasl.enabled';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.hadoop.hive.metastore.sasl.enabled', 'false',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                          AND t.id IN (

                SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version IN ('210','240','320')
            );


DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.hadoop.hive.metastore.kerberos.principal';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.hadoop.hive.metastore.kerberos.principal', '',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                           AND t.id IN (

                SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version IN ('210','240','320')
            );



DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.dataLake.type';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 1, 'spark.dataLake.type', 'none',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                          AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version  = '320');



DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.sql.catalog.spark_catalog.type';
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.sql.catalog.spark_catalog.type', 'hive',null,'deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                           AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version  = '320');


DELETE FROM console_component_config WHERE component_type_code = 1 AND cluster_id != -2 AND `key` = 'spark.sql.catalog.spark_catalog';

INSERT INTO console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, `value`,
                                      `values`, dependencyKey, dependencyValue, `desc`)
SELECT  t.cluster_id ,t.id, t.component_type_code, 'INPUT', 0, 'spark.sql.catalog.spark_catalog', 'org.apache.iceberg.spark.SparkSessionCatalog','null','deploymode$perjob',null, null
FROM console_component t WHERE t.component_type_code = 1 and t.is_deleted = 0 and t.cluster_id != -2
                           AND t.id IN (

            SELECT id FROM console_component WHERE component_type_code = 1 AND is_deleted = 0 AND hadoop_version  = '320');



--  spark320 增加 spark.yarn.populateHadoopClasspath 参数
DELETE FROM console_component_config WHERE component_id = -132 AND `key` = 'spark.yarn.populateHadoopClasspath';
INSERT INTO `console_component_config` (`cluster_id`,
                                        `component_id`,
                                        `component_type_code`,
                                        `type`,
                                        `required`,
                                        `key`,
                                        `value`,
                                        `values`,
                                        `dependencyKey`,
                                        `dependencyValue`,
                                        `desc`)
VALUES (-2, -132, 1, 'INPUT', 0, 'spark.yarn.populateHadoopClasspath', 'true', NULL, 'deploymode$perjob', NULL, NULL);

-- 增加 spark.yarn.populateHadoopClasspath tip
DELETE FROM schedule_dict WHERE `type` = 25 AND dict_name = 'spark.yarn.populateHadoopClasspath' AND dict_desc = '1';
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,`type`,depend_name,dict_desc, sort)
VALUES ('tips','spark.yarn.populateHadoopClasspath','spark 任务是否加载 hadoop 集群的 hadoop 相关环境变量',25,'主要','1', 10);






