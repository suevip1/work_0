-- 迁移 alert_channel 至业务中心
DELETE FROM  `dt_pub_service`.`alert_channel`;
INSERT INTO `dt_pub_service`.`alert_channel` (`id`,`cluster_id`, `alert_gate_name`, `alert_gate_type`, `alert_gate_code`, `alert_gate_json`, `alert_gate_source`, `alert_template`, `file_path`, `is_default`, `is_deleted`, `gmt_created`, `gmt_modified`)
SELECT `id`,`cluster_id`, `alert_gate_name`, `alert_gate_type`, `alert_gate_code`, `alert_gate_json`, `alert_gate_source`, `alert_template`, `file_path`, `is_default`, `is_deleted`, `gmt_created`, `gmt_modified`
FROM `dagschedulex`.`alert_channel`;


-- 迁移 alert_channel 至业务中心
DELETE FROM   `dt_pub_service`.`alert_content`;
INSERT INTO `dt_pub_service`.`alert_content` (`id`,`tenant_id`, `project_id`, `app_type`, `content`, `status`, `send_info`, `alert_message_status`, `gmt_create`, `gmt_modified`, `is_deleted`)
SELECT `id`,`tenant_id`, `project_id`, `app_type`, `content`, `status`, `send_info`, `alert_message_status`, `gmt_create`, `gmt_modified`, `is_deleted`
FROM  `dagschedulex`.`alert_content`;


-- 迁移 alert_record 至业务中心
DELETE FROM `dt_pub_service`.`alert_record`;
INSERT INTO `dt_pub_service`.`alert_record` (`id`, `alert_channel_id`, `alert_gate_type`, `alert_content_id`, `tenant_id`, `app_type`, `user_id`, `read_id`, `read_status`, `title`, `status`, `context`, `job_id`, `alert_record_status`, `alert_record_send_status`, `failure_reason`, `is_deleted`, `node_address`, `send_time`, `send_end_time`, `gmt_create`, `gmt_modified`)
SELECT `id`, `alert_channel_id`, `alert_gate_type`, `alert_content_id`, `tenant_id`, `app_type`, `user_id`, `read_id`, `read_status`, `title`, `status`, `context`, `job_id`, `alert_record_status`, `alert_record_send_status`, `failure_reason`, `is_deleted`, `node_address`, `send_time`, `send_end_time`, `gmt_create`, `gmt_modified`
FROM `dagschedulex`.`alert_record`;


-- 迁移 console_security_log 至业务中心
DELETE FROM `dt_pub_service`.`pub_security_log`;
INSERT INTO `dt_pub_service`.`pub_security_log` (`id`, `tenant_id`, `operator`, `operator_id`, `app_tag`, `action`, `gmt_create`, `operation`, `operation_object`, `is_deleted`, `node_address`, `operator_result`, `failure_reason`, `gmt_modified`, `detail_info`, `project_info`)
SELECT `id`, `tenant_id`, `operator`, `operator_id`, `app_tag`, `action`, `gmt_create`, `operation`, `operation_object`, `is_deleted`, `node_address`, `operator_result`, `failure_reason`, `gmt_modified`, `detail_info`, `project_info`
FROM `dagschedulex`.`console_security_log`;

-- 迁移告警 sftp 至业务中心
insert into dt_pub_service.dsc_info (data_type,data_name,dtuic_tenant_id,data_type_code,`status`,tenant_id,data_json)
values ('FTP','default_alert_sftp',-1,7,1,-1,
REPLACE(REPLACE(TO_BASE64(JSON_OBJECT('auth',(select `value` from console_component_config
where component_id = -9999 and `key` = 'auth'),'password',(select `value` from console_component_config
where component_id = -9999 and `key` = 'password' and `type` = 'PASSWORD'),'rsaPath',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'rsaPath' and `type` = 'INPUT'),
'fileTimeout',(select `value` from console_component_config
where component_id = -9999 and `key` = 'fileTimeout'),'host',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'host' ),'isUsePool',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'isUsePool'),'maxIdle',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'maxIdle'),'maxTotal',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'maxTotal'),
'maxWaitMillis',(select `value` from console_component_config
where component_id = -9999 and `key` = 'maxWaitMillis'),'minIdle',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'minIdle'),'path',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'path'),'port',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'port'),'timeout',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'timeout'),'username',
(select `value` from console_component_config
where component_id = -9999 and `key` = 'username'))), CHAR(10), ''), CHAR(13), ''));

insert into dt_pub_service.dsc_info(is_meta,`status`,link_json,dtuic_tenant_id,data_json,tenant_id,data_type,data_name)
select 2,1,dsc.link_json,
       dt_uic_tenant_id,
       REPLACE(REPLACE(TO_BASE64(dsc.link_json), CHAR(10), ''), CHAR(13), '') as data_json,
       dt_uic_tenant_id                                                       as tenant_id,
       'FTP'                                                                  as data_type,
       concat(cc.cluster_name, '_FTP', cluster_id)                            as data_name
from (select concat('{', group_concat(sftp), '}') as link_json, dt_uic_tenant_id, cluster_id
      from (
               select replace(substring(JSON_OBJECT(`key`, value), 2), '}', '') as sftp,
                      cct.dt_uic_tenant_id,
                      cct.cluster_id
               from console_component_config ccc
                        left join
                    (select cluster_id, dt_uic_tenant_id
                     from console_cluster_tenant cct

                     where cct.dt_uic_tenant_id not in (select dtuic_tenant_id
                                                        from `dt_pub_service`.dsc_info
                                                        where is_meta = 2
                                                          and data_type = 'FTP')) as cct
                    on ccc.cluster_id = cct.cluster_id
               where ccc.component_type_code = 10
                 and ccc.dependencyValue is null
                 and cct.dt_uic_tenant_id is not null) tmp
      group by dt_uic_tenant_id) as dsc
         left join console_cluster cc
                   on cc.id = dsc.cluster_id;


-- 告警、审计迁移至业务中心，这三张表不用清理了
DELETE FROM schedule_dict WHERE dict_code = 'data_clear_name' AND dict_name in ('alert_content','alert_record','console_security_log');
