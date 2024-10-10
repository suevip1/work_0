update environment_param_template
set params = replace(params, '## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job',
                     '## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session')
where task_name = 'SYNC'
  AND task_version in ('1.8', '1.10', '1.12','1.16');