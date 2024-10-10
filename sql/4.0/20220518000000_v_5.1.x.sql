CREATE TABLE IF NOT EXISTS `baseline_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '基线名称',
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `owner_user_id` int(11) NOT NULL COMMENT '负责人id',
  `reply_time` varchar(63) NOT NULL COMMENT '承诺时间: 00:00',
  `early_warn_margin` int(11) NOT NULL COMMENT '预警余量 单位分钟',
  `open_status` tinyint(1) DEFAULT '0' COMMENT '开启状态: 0 开始，1 关闭',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='基线表';

CREATE TABLE IF NOT EXISTS `baseline_task_task_shade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `baseline_task_id` varchar(256) NOT NULL COMMENT '基线id',
  `task_id` varchar(256) NOT NULL COMMENT '任务id',
  `task_app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_baseline_task_id` (`baseline_task_id`,`task_id`,`task_app_type`,`is_deleted` )
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='基线任务关联表';

CREATE TABLE IF NOT EXISTS `baseline_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '基线名称',
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `baseline_task_id` int(11) NOT NULL COMMENT '基线id',
  `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `owner_user_id` int(11) NOT NULL COMMENT '负责人id',
  `baseline_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '基线实例状态: 0 安全,1 预警,2 破线,3 定时未完成,4 其他',
  `finish_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '基线完成状态: 0 未完成 1 已完成',
  `business_date` datetime NOT NULL COMMENT '业务日期',
  `expect_finish_time` datetime COMMENT '预计完成时间',
  `finish_time` datetime COMMENT '实际完成时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_baseline_task_id` (`baseline_task_id`) USING BTREE,
  KEY `index_business_date` (`business_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='基线实例表';

CREATE TABLE IF NOT EXISTS `baseline_job_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `baseline_job_id` int(11) NOT NULL COMMENT '基线实例id',
  `task_id` int(11) NOT NULL COMMENT '任务id',
  `task_app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `job_id` varchar(256) NOT NULL COMMENT '工作任务id',
  `estimated_exec_time` int(11) DEFAULT '0' COMMENT '预计执行时间',
  `expect_start_time` datetime DEFAULT NULL COMMENT '预计开始',
  `expect_end_time` datetime DEFAULT NULL COMMENT '预计完成',
  `early_warning_start_time` datetime DEFAULT NULL COMMENT '预警开始时间',
  `early_warning_end_time` datetime DEFAULT NULL COMMENT '预警结束时间',
  `broken_line_start_time` datetime DEFAULT NULL COMMENT '破线开始时间',
  `broken_line_end_time` datetime DEFAULT NULL COMMENT '破线结束时间',
  `baseline_task_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '基线任务类型:0 基线选择任务 1 基线链路任务',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
   KEY `index_baseline_job_id` (`baseline_job_id`) USING BTREE,
   KEY `index_task_id_app_type` (`task_id`,`task_app_type`) USING BTREE,
   KEY `index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='基线任务关联表';

CREATE TABLE IF NOT EXISTS `baseline_block_job_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(256) DEFAULT '' COMMENT '任务名称',
  `app_type` int(11) DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
  `task_id` int(11) DEFAULT '0' COMMENT '任务id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `baseline_task_id` int(11) NOT NULL COMMENT '基线id',
  `baseline_job_id` int(11) NOT NULL COMMENT '基线实例id',
  `owner_user_id` int(11) NOT NULL COMMENT '负责人id',
  `job_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '实例等待状态',
  `blocking_reason` varchar(256) NOT NULL COMMENT '阻塞原因',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_baseline_task_id` (`baseline_task_id`) USING BTREE,
  KEY `index_baseline_job_id` (`baseline_job_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='基线实例阻塞原因';

CREATE TABLE IF NOT EXISTS `alert_alarm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_id` int(11) NOT NULL DEFAULT '0' COMMENT 'uic租户id',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型',
  `name` varchar(200) DEFAULT NULL COMMENT '告警名称',
  `alarm_type` tinyint(1) DEFAULT NULL COMMENT '告警类型 0：任务  1：基线',
  `create_user_id` int(11) NOT NULL COMMENT '创建的用户',
  `open_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 正常 1关闭 2删除',
  `extra_params` text NOT NULL COMMENT '额外参数:例如时间设置 格式: {"规则字段名称": values}...',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_project_tenant` (`project_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警规则配置表';

CREATE TABLE IF NOT EXISTS `alert_alarm_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alert_alarm_id` int(11) NOT NULL DEFAULT '0' COMMENT '告警规则id',
  `business_type` int(11) NOT NULL COMMENT '业务类型: 0 用户id , 1 任务id , 2 规则id , 3 基线id',
  `business_id` int(11) NOT NULL DEFAULT '0' COMMENT '业务id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_alert_alarm_id` (`alert_alarm_id`),
  KEY `index_business` (`business_id`,`business_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警关联表';

CREATE TABLE IF NOT EXISTS `alert_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_type` int(11) NOT NULL COMMENT '应用类型',
  `name` varchar(200) DEFAULT NULL COMMENT '规则显示名称',
  `key` varchar(200) DEFAULT NULL COMMENT '规则类型枚举: TASK:任务 BASELINE:基线',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `template` text NOT NULL COMMENT '模板内容',
  `params` text NOT NULL COMMENT '参数替换 json格式{user:[....]}',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='告警规则配置表';

CREATE TABLE IF NOT EXISTS `alert_trigger_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '项目id',
  `app_type` int(11) NOT NULL COMMENT '应用类型',
  `alert_alarm_name` varchar(200) DEFAULT NULL COMMENT '规则名称',
  `trigger_time` datetime NOT NULL COMMENT '触发时间',
  `rule_id` int(11) NOT NULL DEFAULT '0' COMMENT '告警规则id',
  `rule_name` varchar(200) DEFAULT NULL COMMENT '规则显示名称',
  `alarm_type` tinyint(1) DEFAULT NULL COMMENT '告警类型 0：任务  1：基线',
  `task_name` varchar(200) DEFAULT NULL COMMENT '任务名称 当alarm_type =0 的时间有值',
  `task_type` tinyint(1) DEFAULT NULL COMMENT '任务类型',
  `baseline_name` varchar(200) DEFAULT NULL COMMENT '基线名称 当alarm_type =1 的时间有值',
  `alert_alarm_id` int(11) NOT NULL DEFAULT '0' COMMENT '告警规则id',
  `alert_channel_name` varchar(512) DEFAULT NULL COMMENT '触发类型：钉钉，短信。。。',
  `owner_user_name` varchar(512) DEFAULT NULL COMMENT '责任人名称',
  `receiver_user_name` varchar(512) DEFAULT NULL COMMENT '接收人名称',
  `content` text NOT NULL COMMENT '告警内容',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `index_task_name` (`task_name`),
  KEY `index_baseline_name` (`baseline_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警触发记录表';

CREATE TABLE IF NOT EXISTS `alert_trigger_record_receive` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alert_trigger_record_id` int(11) NOT NULL COMMENT '告警触发记录id',
  `receive_user_id` int(11) NOT NULL COMMENT '接收人id',
  `receive_user_name` varchar(200) DEFAULT NULL COMMENT '接收人名称',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
   KEY `index_receive_user_id` (`receive_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警接收人记录表';

CREATE TABLE IF NOT EXISTS `alert_trigger_record_repeat_send` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(256) NOT NULL COMMENT '实例id',
  `rule_id` int(11) NOT NULL COMMENT '规则id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`),
   KEY `index_job_id` (`job_id`,`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='告警防止重复触发表';

DELETE FROM `alert_rule`;

INSERT INTO `alert_rule` (`id`, `app_type`, `name`, `key`, `title`, `template`, `params`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
(11, '1', '失败', 'error_status', '[${sign!}]离线任务：${task.name!}失败告警', '[${sign!}]离线任务：${task.name!}失败告警\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：周期调度\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n责任人：${user.userName!}\n请及时处理!', '{}', '2022-05-25 19:20:32', '2022-05-25 19:20:32', '0'),
(1, '1', '成功', 'finish_status', '[${sign!}]离线任务：${task.name!}运行成功', '[${sign!}]离线任务：${task.name!}运行成功\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：周期调度\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n责任人：${user.userName!}', '{}', '2022-05-25 18:52:44', '2022-05-25 18:52:44', '0'),
(3, '1', '停止', 'stop_status', '[${sign!}]离线任务：${task.name!}停止告警', '[${sign!}]离线任务：${task.name!}停止告警\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：周期调度\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n责任人：${user.userName!}\n请及时处理!', '{}', '2022-05-25 19:21:59', '2022-05-25 19:21:59', '0'),
(4, '1', '定时未完成', 'timing_no_finish', '[${sign!}]离线任务：${task.name!}定时未完成告警', '[${sign!}]离线任务：${task.name!}定时未完成告警\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：周期调度\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n责任人：${user.userName!}\n请及时处理!', '{}', '2022-05-25 19:23:23', '2022-05-25 19:23:23', '0'),
(5, '1', '超时未完成', 'time_out_no_finish', '[${sign!}]离线任务：${task.name!}超时未完成告警', '[${sign!}]离线任务：${task.name!}超时未完成告警\n任务：${task.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n调度类型：周期调度\n计划时间：${jobCycTime!}\n开始时间：${job.execStartTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${job.execEndTime!,dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${jobExecTime!}\n当前状态：${statusStr!}\n责任人：${user.userName!}\n请及时处理!', '{}', '2022-05-25 19:23:55', '2022-05-25 19:23:55', '0'),
(6, '1', '完成', 'baseline_finish', '[${sign!}]基线“${baselineJob.name!}”完成', '[${sign!}]基线“${baselineJob.name!}”完成\n基线：${baselineJob.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n基线任务：<%\nif(baselineTask.taskVOS!=null){\n    for(item in baselineTask.taskVOS){\n    	if (item.taskName!=null) {\n			print(item.taskName+\',\');	\n    	}\n	}\n}\n%>\n计划时间：${baselineJob.expectFinishTime! , dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n承诺时间：${baselineTask.replyTime!}\n开始时间：${execStartTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${execEndTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${execTime!\'-\'}\n当前状态：完成\n责任人：${user.userName!}', '{}', '2022-05-25 20:24:37', '2022-05-25 20:24:37', '0'),
(7, '1', '预警', 'baseline_early_warning', '[${sign!}]基线“${baselineJob.name!}”预警', '[${sign!}]基线“${baselineJob.name!}”预警\n基线：${baselineJob.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n基线任务：<%\nif(baselineTask.taskVOS!=null){\n    for(item in baselineTask.taskVOS){\n    	if (item.taskName!=null) {\n			print(item.taskName+\',\');	\n    	}\n	}\n}\n%>\n计划时间：${baselineJob.expectFinishTime! , dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n承诺时间：${baselineTask.replyTime!}\n开始时间：${execStartTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${execEndTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${execTime!\'-\'}\n当前状态：预警\n相关任务：${relatedTaskName!}余量小于${baselineTask.earlyWarnMargin!}分钟\n责任人：${user.userName!}\n请及时处理！', '{}', '2022-05-26 14:45:53', '2022-05-26 14:45:53', '0'),
(8, '1', '破线', 'baseline_broken_line', '[${sign!}]基线“${baselineJob.name!}”破线', '[${sign!}]基线“${baselineJob.name!}”破线\n基线：${baselineJob.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n基线任务：<%\nif(baselineTask.taskVOS!=null){\n    for(item in baselineTask.taskVOS){\n    	if (item.taskName!=null) {\n			print(item.taskName+\',\');	\n    	}\n	}\n}\n%>\n计划时间：${baselineJob.expectFinishTime! , dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n承诺时间：${baselineTask.replyTime!}\n开始时间：${execStartTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${execEndTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${execTime!\'-\'}\n当前状态：破线（预计无法按承诺时间完成）\n相关任务：${relatedTaskName!}\n责任人：${user.userName!}\n请及时处理！', '{}', '2022-05-26 14:49:02', '2022-05-26 14:49:02', '0'),
(9, '1', '出错', 'baseline_error', '[${sign!}]基线“${baselineJob.name!}”出错', '[${sign!}]基线“${baselineJob.name!}”出错\n基线：${baselineJob.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n基线任务：<%\nif(baselineTask.taskVOS!=null){\n    for(item in baselineTask.taskVOS){\n    	if (item.taskName!=null) {\n			print(item.taskName+\',\');	\n    	}\n	}\n}\n%>\n计划时间：${baselineJob.expectFinishTime! , dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n承诺时间：${baselineTask.replyTime!}\n开始时间：${execStartTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${execEndTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${execTime!\'-\'}\n当前状态：出错\n相关任务： ${relatedTaskName!}\n责任人：${user.userName!}\n请及时处理！', '{}', '2022-05-26 14:49:02', '2022-05-26 14:49:02', '0'),
(10, '1', '定时未完成', 'baseline_time_out_no_finish', '[${sign!}]基线“${baselineJob.name!}”出错', '[${sign!}]基线“${baselineJob.name!}”定时未完成\n基线：${baselineJob.name!}\n项目：${project.projectAlias!}\n租户：${tenant.tenantName!}\n基线任务：<%\nif(baselineTask.taskVOS!=null){\n    for(item in baselineTask.taskVOS){\n    	if (item.taskName!=null) {\n			print(item.taskName+\',\');	\n    	}\n	}\n}\n%>\n计划时间：${baselineJob.expectFinishTime! , dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n承诺时间：${baselineTask.replyTime!}\n开始时间：${execStartTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n结束时间：${execEndTime!, dateFormat=\'yyyy-MM-dd HH:mm:ss\'}\n运行时长：${execTime!\'-\'}\n当前状态：${baselineStatusStr}\n责任人：${user.userName!}\n请及时处理！', '{}', '2022-05-26 14:49:02', '2022-05-26 14:49:02', '0');

UPDATE `alert_rule` SET id = 0 WHERE id = 11;