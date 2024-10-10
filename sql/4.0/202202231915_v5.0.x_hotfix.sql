-- v5.0.x，针对 HDP 3.x 增加自定义参数：spark.sql.hive.metastore.jars，spark.sql.hive.metastore.version
-- 保持幂等
delete from `schedule_dict` where dict_code = 'extra_version_template' and dict_name = 'HDP 3.x' and type = 15;
INSERT INTO `schedule_dict`(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES ( 'extra_version_template', 'HDP 3.x', '{
    "HDFS":{
        "SPARK":[
            {
                "2.1":"-1003",
                "2.4":"-1003"
            }
        ]
    }
}', NULL, 15, 1, 'STRING', 'YARN', 0, '2022-02-23 17:43:31', '2022-02-23 17:43:31', 0);