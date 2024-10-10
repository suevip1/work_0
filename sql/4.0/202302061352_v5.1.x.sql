alter table alert_trigger_record
    add owner_user_id int null comment '责任人';

create index index_owner_user_id
    on alert_trigger_record (owner_user_id);

