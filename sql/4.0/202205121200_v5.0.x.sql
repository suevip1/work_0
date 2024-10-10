update console_component set version_name =  '2.4'
where  component_type_code = 1 and version_name = '2.4(CDH 6.2)';


update console_component set hadoop_version =  '240'
where  component_type_code = 1 and hadoop_version = '240cdh620';