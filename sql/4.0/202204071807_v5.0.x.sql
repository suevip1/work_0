-- updown chain param, add by qiuyun
CREATE TABLE `schedule_task_chain_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `app_type` int(11) NOT NULL COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `task_type` tinyint(4) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
  `param_name` varchar(50) NOT NULL COMMENT '参数名称',
  `type` tinyint(4) NOT NULL COMMENT '参数种类，0系统参数, 1自定义参数, 2组件参数, 5输入参数, 6输出参数',
  `output_param_type` tinyint(4) DEFAULT NULL COMMENT '输出参数类型,3:计算结果 2:常量 1:自定义参数',
  `father_task_id` int(11) DEFAULT NULL COMMENT '来源任务id',
  `father_app_type` int(11) DEFAULT NULL COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `father_param_name` varchar(50) DEFAULT NULL COMMENT '来源参数名称',
  `param_command` varchar(1024) DEFAULT NULL COMMENT '参数内容',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uk_task_app_param` (`task_id`,`app_type`,`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule_job_chain_output_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(32) NOT NULL COMMENT '任务实例id',
  `job_type` tinyint(4) NOT NULL COMMENT '任务运行类型, 0正常调度, 1补数据,2临时运行',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `app_type` int(11) NOT NULL COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `task_type` tinyint(4) NOT NULL COMMENT '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
  `param_name` varchar(50) NOT NULL COMMENT '参数名称',
  `output_param_type` tinyint(4) NOT NULL COMMENT '参数类型:1自定义参数,2常量,3计算结果',
  `param_command` varchar(1024) NOT NULL COMMENT '参数内容',
  `param_value` varchar(1024) DEFAULT NULL COMMENT '参数值',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_job_param` (`job_id`,`param_name`),
  KEY `idx_task_app_job` (`task_id`,`app_type`,`job_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;