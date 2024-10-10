alter table schedule_job add COLUMN fill_type tinyint(2) NOT NULL DEFAULT '0' COMMENT '0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 中间实例 3 黑名单';

alter table schedule_fill_data_job add COLUMN max_parallel_num tinyint(2) NOT NULL DEFAULT '0' COMMENT '最大并行数';
alter table schedule_fill_data_job add COLUMN number_parallel_num tinyint(2) NOT NULL DEFAULT '0' COMMENT '当前运行数';
alter table schedule_fill_data_job add COLUMN fill_data_info mediumtext COMMENT '补数据信息';
alter table schedule_fill_data_job add COLUMN fill_generat_status tinyint(2) NOT NULL DEFAULT '0' COMMENT '补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败';
alter table schedule_fill_data_job add COLUMN node_address varchar(256) DEFAULT NULL COMMENT '节点地址';


ALTER TABLE `schedule_job` DROP INDEX `idx_jobKey`;
ALTER TABLE `schedule_job` ADD UNIQUE `idx_jobKey` (`job_key`(255));




