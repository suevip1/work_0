UPDATE schedule_dict SET is_deleted = 1 WHERE `type` = 2 AND dict_name = '2.4(CDH 6.2)';

UPDATE schedule_dict SET dict_value = '{
                                           "YARN":"yarn3",
                                           "HDFS":{
                                               "FLINK":[
                                                   {
                                                       "1.8":"yarn3-hdfs3-flink180"
                                                   },
                                                   {
                                                       "1.10":"yarn3-hdfs3-flink110"
                                                   },
                                                   {
                                                       "1.12":"yarn3-hdfs3-flink112"
                                                   }
                                               ],
                                               "SPARK":[
                                                   {
                                                       "2.1":"yarn3-hdfs3-spark210",
                                                       "2.4":"yarn3-hdfs3-spark240cdh620"
                                                   }
                                               ],
                                               "DT_SCRIPT":"yarn3-hdfs3-dtscript",
                                               "HDFS":"yarn3-hdfs3-hadoop3",
                                               "TONY":"yarn3-hdfs3-tony",
                                               "LEARNING":"yarn3-hdfs3-learning"
                                           }
                                       }'
                                       WHERE dict_name = 'CDH 6.2.x' AND type = 14;

update console_component set version_name =  '2.4'
where  component_type_code = 1 and version_name = '2.4(CDH 6.2)';