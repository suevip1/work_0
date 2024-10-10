UPDATE schedule_dict
 SET dict_value = '{
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
                                  "2.4":"yarn3-hdfs3-spark240cdh620"
                              }
                          ],
                          "DT_SCRIPT":"yarn3-hdfs3-dtscript",
                          "HDFS":"yarn3-hdfs3-hadoop3",
                          "TONY":"yarn3-hdfs3-tony",
                          "LEARNING":"yarn3-hdfs3-learning"
                      }
}'
WHERE dict_code = 'component_model_config' AND dict_name = 'CDP 7.x';

DELETE FROM schedule_dict WHERE dict_code = 'typename_mapping' AND dict_name = 'yarn3-hdfs3-spark240cdh620';

INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted) VALUES ('typename_mapping', 'yarn3-hdfs3-spark240cdh620', '-108', null, 6, 0, 'LONG', '', 0, now(),now(), 0);

