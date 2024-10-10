ALTER TABLE `console_calender`
ADD COLUMN `calender_time_format` varchar(255) NOT NULL default 'yyyyMMddHHmm' COMMENT '日历时间格式';

ALTER TABLE `schedule_task_calender`
ADD COLUMN `expand_time` varchar(255) NULL COMMENT '扩展时间, eg: 0810';