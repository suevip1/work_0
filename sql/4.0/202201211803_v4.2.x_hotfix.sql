-- add by qiuyun
-- read_id can be null
ALTER TABLE `alert_record`
    MODIFY COLUMN `read_id` int(11) NULL DEFAULT 0 COMMENT '应用记录id';