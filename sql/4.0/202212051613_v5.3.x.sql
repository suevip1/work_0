-- 支持项目参数
CREATE TABLE `schedule_project_param` (
     `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
     `param_name` varchar(128)  NOT NULL COMMENT '参数名称',
     `param_value` varchar(128) NOT NULL COMMENT '参数值',
     `param_desc` varchar(256) DEFAULT '' COMMENT '描述',
     `param_type` int(11) DEFAULT '9' COMMENT '参数类型: 9 常量',
     `project_id` bigint(11) unsigned NOT NULL COMMENT '项目 id',
     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
     `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     `create_user_id` bigint(11) unsigned DEFAULT NULL COMMENT '创建用户',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `schedule_project_param`
    ADD UNIQUE INDEX `uk_project_param`(`project_id`, `param_name`);

-- 项目参数与任务的关联关系
CREATE TABLE `schedule_task_project_param` (
    `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
    `task_id` bigint(11) unsigned NOT NULL COMMENT '任务id',
    `app_type` int(11) NOT NULL DEFAULT '0' COMMENT 'RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)',
    `project_param_id` bigint(11) unsigned NOT NULL COMMENT '项目参数 id',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_param` (`task_id`,`project_param_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 支持工作流级参数
-- 增加 flow_id 字段：避免无法删除主节点原先的子节点参数，场景：parent_task 下 son_task，第一次提交了带工作流参数的 son_task，
-- 第二次删掉了 son_task，此时需要根据 flow_id 删除掉 son_task 的工作流参数
ALTER TABLE `schedule_task_chain_param`
    ADD COLUMN `flow_id` bigint(11) UNSIGNED NULL COMMENT '工作流id' AFTER `param_command`;
