insert into alert_alarm_rule(alert_alarm_id, business_type, business_id, gmt_create, gmt_modified, is_deleted)
select id, 9, -1, now(), now(), 0
from alert_alarm
where extra_params -> '$.businessHolderFlag' = 1
  and id not in
      (
          select alert_alarm_id
          from alert_alarm_rule
          where business_type = 9);