-- star_rocks && hash_data
DELETE FROM environment_param_template WHERE `task_type` IN (44, 45);
INSERT INTO `environment_param_template` (`task_type`, `task_name`, `task_version`, `app_type`, `params`) VALUES (44, 'STARROCKS', NULL, -1, '');
INSERT INTO `environment_param_template` (`task_type`, `task_name`, `task_version`, `app_type`, `params`) VALUES (45, 'HASHDATA', NULL, -1, '');