DROP INDEX index_batch_task_task on `schedule_task_task_shade`;
ALTER TABLE `schedule_task_task_shade` ADD UNIQUE KEY `index_batch_task_task` (`task_id`,`parent_task_id`,`app_type`,`project_id`) USING BTREE;
