-- http://zenpms.dtstack.cn/zentao/story-view-9158.html
ALTER TABLE `console_calender_time`ADD COLUMN `extra_info` varchar(128) DEFAULT '' COMMENT '附加信息，例如基于计划时间的全局参数对应的参数值';

ALTER TABLE `schedule_task_param`ADD COLUMN `offset` varchar(32) DEFAULT '' COMMENT '偏移量，用于基于偏移量的全局参数';

ALTER TABLE `schedule_task_param`ADD COLUMN `replace_target` varchar(32) DEFAULT '' COMMENT '替换目标，用于基于偏移量的全局参数';


DROP INDEX `task_param` ON  `schedule_task_param`;

ALTER table `schedule_task_param` ADD UNIQUE KEY `task_param_replace_target` (`task_id`,`param_id`,`replace_target`);