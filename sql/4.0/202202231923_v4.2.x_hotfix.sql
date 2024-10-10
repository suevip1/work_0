-- 针对 HDP 3.1.x 删除多余的自定义参数
delete from console_component_config where component_id = -200
    and type = 'CUSTOM_CONTROL' and `key` in
    ('hive.exec.copyfile.maxsize',
     'spark.driver.extraJavaOptions',
     'spark.executor.extraJavaOptions',
     'spark.sql.catalogImplementation',
     'spark.sql.hive.convertMetastoreOrc') ;