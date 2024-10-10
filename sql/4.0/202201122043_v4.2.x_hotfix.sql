-- add by qiuyun
-- fix checkpoint savepath too short, problem : http://zenpms.dtstack.cn/zentao/bug-view-49761.html

ALTER TABLE `schedule_engine_job_checkpoint`
    MODIFY COLUMN `checkpoint_savepath` varchar(255) COMMENT 'checkpoint存储路径';