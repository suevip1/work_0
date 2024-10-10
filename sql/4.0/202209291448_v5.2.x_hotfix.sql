-- add trino param template
delete from `task_param_template` where `compute_type` = 1 and `engine_type` = 30;
INSERT INTO `task_param_template`(`compute_type`, `engine_type`, `task_type`, `params`) VALUES (1, 30, 0, '');