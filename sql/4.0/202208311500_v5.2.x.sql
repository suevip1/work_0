-- 删除spark 3.1的metric信息
delete
from console_component_config
where component_id in (
    select id
    from console_component
    where component_type_code = 1
      and version_name = 3.1
    union all
    select -132)
  and `key` like 'metrics%'
  and component_type_code = 1;