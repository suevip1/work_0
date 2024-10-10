update console_component_config SET `value` = '/data/miniconda3/bin/python3'
where `key` = 'spark.yarn.appMasterEnv.PYSPARK_PYTHON' and component_type_code = 1 and component_id IN (-108,-130,-132);
