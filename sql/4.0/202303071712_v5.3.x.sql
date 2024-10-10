CREATE TABLE `schedule_workflow_sub_tmp_run_info`
(
    `id`           int(11)    NOT NULL AUTO_INCREMENT,
    `task_id` int(11)    NOT NULL COMMENT '任务id',
    `flow_id` int(11) NOT NULL COMMENT '工作流任务 id',
    `flow_job_id` varchar(256) NOT NULL COMMENT '临时运行工作流 jobId',
    `app_type` int(11) NOT NULL DEFAULT '1' COMMENT '应用类型',
    `param_task_action` longtext  COMMENT '任务临时运行参数' ,
    `job_id` varchar(256) NOT NULL COMMENT '临时运行 jobId',
    `is_parent_fail` tinyint(1)   NOT NULL DEFAULT '0' COMMENT '1: 上游任务失败',
    `is_skip` tinyint(1)   NOT NULL DEFAULT '0' COMMENT '1: 条件分支跳过任务',
    `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   int(10)    NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    KEY `key_flow_job_id` (`flow_job_id`),
    KEY `key_flow_id_task_id_app_type` (`flow_id`, `task_id`, `app_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT '工作流子任务临时运行参数表';


CREATE TABLE `schedule_workflow_tmp_run_info`
(
    `id`           int(11)    NOT NULL AUTO_INCREMENT,
    `flow_id` int(11) NOT NULL COMMENT '工作流任务 id',
    `flow_job_id` varchar(256) NOT NULL COMMENT '临时运行工作流 jobId',
    `app_type` int(11) NOT NULL DEFAULT '1' COMMENT '应用类型',
    `graph` longtext  COMMENT '工作流内拓扑关系 json',
    `status` tinyint(1) NOT NULL  COMMENT '工作流临时运行状态',
    `error_msg` longtext  COMMENT '错误信息',
    `gmt_create`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`   int(10)    NOT NULL DEFAULT '0' COMMENT '0正常 1逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_flow_job_id` (`flow_job_id`),
    KEY `key_flow_id_app_type` (`flow_id`,`app_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '工作流临时运行信息';

-- schedule_workflow_sub_tmp_run_info，schedule_workflow_tmp_run_info 7天逻辑删除，30天强删
INSERT INTO `schedule_dict` (`dict_code`,
                             `dict_name`,
                             `dict_value`,
                             `dict_desc`,
                             `type`,
                             `sort`,
                             `data_type`) VALUES
    ('data_clear_name',
     'schedule_workflow_sub_tmp_run_info',
     '{\"deleteDateConfig\":7,\"clearDateConfig\":30"}',
     NULL,
     '8',
     '1',
     'STRING');

INSERT INTO `schedule_dict` (`dict_code`,
                             `dict_name`,
                             `dict_value`,
                             `dict_desc`,
                             `type`,
                             `sort`,
                             `data_type`) VALUES
    ('data_clear_name',
     'schedule_workflow_tmp_run_info',
     '{\"deleteDateConfig\":7,\"clearDateConfig\":30"}',
     NULL,
     '8',
     '1',
     'STRING');