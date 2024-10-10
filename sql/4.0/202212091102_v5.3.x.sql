-- rdb 任务支持上下游参数传递, ref:http://zenpms.dtstack.cn/zentao/task-view-8652.html
ALTER TABLE `schedule_job_chain_output_param`
    ADD COLUMN `parsed_param_command` varchar(1024) NULL COMMENT '替换变量后的参数内容，用于存放 rdb 计算型的参数内容' AFTER `param_command`;
ALTER TABLE `schedule_job_chain_output_param`
    MODIFY COLUMN `param_value` mediumtext COMMENT '参数值' AFTER `parsed_param_command`;