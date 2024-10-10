-- hana
DELETE FROM environment_param_template WHERE `task_type` IN (43);
INSERT INTO `environment_param_template` (`task_type`, `task_name`, `task_version`, `app_type`, `params`) VALUES (43, 'HANA', NULL, -1, '');