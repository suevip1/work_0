-- add by qiuyun
ALTER TABLE `console_security_log`
    ADD INDEX `idx_gmt_create`(`gmt_create`) USING BTREE;