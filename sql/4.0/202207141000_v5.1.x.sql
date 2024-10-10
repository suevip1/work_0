-- console_param, ref:http://zenpms.dtstack.cn/zentao/task-view-6691.html
ALTER TABLE `console_param`
    MODIFY COLUMN `param_type` int(11) NULL DEFAULT 1 COMMENT '参数类型: 9 常量，8 时间' AFTER `param_desc`,
    ADD COLUMN `date_benchmark` tinyint(4) NULL COMMENT '日期基准: 1 自然日, 2 自定义日期' AFTER `create_user_id`,
    ADD COLUMN `date_format` varchar(32) NULL COMMENT '日期格式' AFTER `date_benchmark`,
    ADD COLUMN `calender_id` int(11) NULL COMMENT '调度日历 id' AFTER `date_format`;

-- 更改历史数据，符合时间格式，则更新参数类型为时间(8)、日期基准为自然日(1)；否则直接更新为常量(9)
update `console_param`
set `param_type`   = (case when instr(param_value, 'yyyy') > 0 then 8 else 9 end),
    date_benchmark = (case when instr(param_value, 'yyyy') > 0 then 1 end)
where `param_type` = 1;

ALTER TABLE `console_calender`
    ADD COLUMN `use_type` tinyint(4) NULL DEFAULT 2 COMMENT '自定义调度日历用处, 1 全局参数, 2 自定义调度日期' AFTER `gmt_modified`;