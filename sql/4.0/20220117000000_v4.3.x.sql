ALTER  TABLE console_security_log ADD COLUMN `node_address` varchar(200) NOT NULL COMMENT '地址';
ALTER  TABLE console_security_log ADD COLUMN `operator_result` text COMMENT '操作结果';
ALTER  TABLE console_security_log ADD COLUMN `failure_reason` text COMMENT '失败原因';

UPDATE console_security_log SET `operator_result` = "成功";