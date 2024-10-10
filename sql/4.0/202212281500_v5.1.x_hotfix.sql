-- 修复字段长度问题，ref: http://zenpms.dtstack.cn/zentao/bug-view-71838.html
ALTER TABLE  `schedule_job_resource_file`
    MODIFY COLUMN `job_resource_files` text NULL COMMENT 'job 资源文件信息' AFTER `type`;