-- add by qiuyun
ALTER TABLE `schedule_engine_job_cache`
    ADD COLUMN `tenant_id` int(11) NULL COMMENT 'uic租户id';

-- patch history data
update schedule_engine_job_cache e, schedule_job b
set e.tenant_id = b.dtuic_tenant_id
 where e.job_id = b.job_id
  and e.tenant_id is null;

ALTER TABLE `console_engine_tenant`
    ADD INDEX `idx_tenant_id`(`tenant_id`) USING BTREE;
