-- star_rocks 35 & hash_data 36
DELETE FROM `task_param_template` WHERE engine_type IN (35, 36);
INSERT INTO `task_param_template` (`compute_type`, `engine_type`, `task_type`, `params`) VALUES ('1', '35', '0', '');
INSERT INTO `task_param_template` (`compute_type`, `engine_type`, `task_type`, `params`) VALUES ('1', '36', '0', '');
