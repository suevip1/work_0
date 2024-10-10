ALTER TABLE baseline_task ADD batch_type tinyint(1) DEFAULT '1' COMMENT '批次类型： 1 单批次 2 多批次';
ALTER TABLE baseline_job ADD batch_type tinyint(1) DEFAULT '1' COMMENT '批次类型： 1 单批次 2 多批次';
ALTER TABLE baseline_job ADD `cyc_time` varchar(63) NOT NULL COMMENT '计划时间: yyyy-MM-dd hh:mm:ss 对于多批次的实例，此计划时间就是生成的每个批次的实例计划时间；对于单批次的实例，此计划时间是生成基线实例里所有任务实例中计划时间最早的实例的计划时间';
alter table baseline_task modify column `reply_time` varchar(63) DEFAULT '' COMMENT '承诺时间: 00:00';

CREATE TABLE `baseline_task_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `baseline_task_id` varchar(256) NOT NULL COMMENT '基线id',
  `cyc_time` varchar(63) NOT NULL DEFAULT '' COMMENT '计划时间: yyyy-MM-dd hh:mm:ss',
  `reply_time` varchar(63) DEFAULT '' COMMENT '承诺时间: 00:00',
  `open_status` tinyint(1) DEFAULT '0' COMMENT '开启状态: 0 开始，1 关闭',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COMMENT='基线批次表';

delete from baseline_task_batch where baseline_task_id IN (select id from baseline_task WHERE batch_type = 1 and `reply_time` is not null and `reply_time` != '');
insert into baseline_task_batch (`baseline_task_id`,`cyc_time`,`reply_time`,`open_status`,`is_deleted`) select id AS `baseline_task_id`,'' AS `cyc_time` , reply_time AS `reply_time` , 0 AS `open_status` ,0 AS `is_deleted`  from baseline_task where batch_type = 1 and `reply_time` is not null and `reply_time` != '';
