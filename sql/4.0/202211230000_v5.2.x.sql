drop procedure if exists alter_schedule_engine_job_retry;
delimiter $
create procedure alter_schedule_engine_job_retry()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_engine_job_retry'  AND column_name = 'job_expand_id';
  IF indexCount<=0
  THEN
  alter table schedule_engine_job_retry ADD job_expand_id int(11) NOT NULL DEFAULT 0 COMMENT '任务id' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call alter_schedule_engine_job_retry();
drop procedure if exists alter_schedule_engine_job_retry;

UPDATE schedule_engine_job_retry jr RIGHT JOIN schedule_job_expand je ON jr.job_id = je.job_id SET jr.job_expand_id = je.id;

drop procedure if exists schedule_job_expand_run_num;
delimiter $
create procedure schedule_job_expand_run_num()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'run_num';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `run_num` tinyint(2) NOT NULL DEFAULT 1 COMMENT '运行次数' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_run_num();
drop procedure if exists schedule_job_expand_run_num;

drop procedure if exists schedule_job_expand_status;
delimiter $
create procedure schedule_job_expand_status()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'status';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_status();
drop procedure if exists schedule_job_expand_status;

drop procedure if exists schedule_job_expand_exec_start_time;
delimiter $
create procedure schedule_job_expand_exec_start_time()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'exec_start_time';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `exec_start_time` datetime DEFAULT NULL COMMENT '执行开始时间' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_exec_start_time();
drop procedure if exists schedule_job_expand_exec_start_time;

drop procedure if exists schedule_job_expand_exec_end_time;
delimiter $
create procedure schedule_job_expand_exec_end_time()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'exec_end_time';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `exec_end_time` datetime DEFAULT NULL COMMENT '执行结束时间' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_exec_end_time();
drop procedure if exists schedule_job_expand_exec_end_time;

drop procedure if exists schedule_job_expand_exec_time;
delimiter $
create procedure schedule_job_expand_exec_time()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'exec_time';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `exec_time` int(11) DEFAULT '0' COMMENT '执行时间' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_exec_time();
drop procedure if exists schedule_job_expand_exec_time;

drop procedure if exists schedule_job_expand_application_id;
delimiter $
create procedure schedule_job_expand_application_id()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'application_id';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `application_id` varchar(256) DEFAULT NULL COMMENT '独立运行的任务需要记录额外的id' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_application_id();
drop procedure if exists schedule_job_expand_application_id;

drop procedure if exists schedule_job_expand_engine_job_id;
delimiter $
create procedure schedule_job_expand_engine_job_id()
begin
  DECLARE indexCount int;
  SELECT count(*) into indexCount  FROM information_schema.columns  WHERE table_schema = database() AND table_name = 'schedule_job_expand'  AND column_name = 'engine_job_id';
  IF indexCount<=0
  THEN
  alter table schedule_job_expand ADD `engine_job_id` varchar(256) DEFAULT NULL COMMENT '离线任务计算引擎id' ,ALGORITHM=COPY;
  END IF;
end $
delimiter ;
call schedule_job_expand_engine_job_id();
drop procedure if exists schedule_job_expand_engine_job_id;

UPDATE schedule_job_expand je RIGHT JOIN schedule_job j ON j.job_id = je.job_id
SET je.status = j.status,
je.exec_start_time = j.exec_start_time,
je.exec_end_time = j.exec_end_time,
je.exec_time = j.exec_time,
je.application_id = j.application_id,
je.engine_job_id = j.engine_job_id;

DROP INDEX index_job_id ON schedule_job_expand;
ALTER TABLE schedule_job_expand ADD UNIQUE index_job_id(job_id(128),run_num);


DELETE FROM `schedule_dict` WHERE dict_code = 'formatted_version' AND `type` = 34 and `dict_name` = '180';
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('formatted_version', '180', '1.80', NULL, '34', '1', 'INTEGER', '1', '1', NOW(), NOW(), '0');

DELETE FROM `schedule_dict` WHERE dict_code = 'formatted_version' AND `type` = 34 and `dict_name` = '110';
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('formatted_version', '110', '1.10', NULL, '34', '1', 'INTEGER', '1', '1', NOW(), NOW(), '0');

DELETE FROM `schedule_dict` WHERE dict_code = 'formatted_version' AND `type` = 34 and `dict_name` = '112';
INSERT INTO `schedule_dict` (`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`) VALUES
('formatted_version', '112', '1.12', NULL, '34', '1', 'INTEGER', '1', '1', NOW(), NOW(), '0');
