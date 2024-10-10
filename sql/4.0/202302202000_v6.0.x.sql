-- ref: http://zenpms.dtstack.cn/zentao/story-view-10448.html
-- （1）去除 flink 1.8 版本；（2）去除 flink 1.10 on k8s 版本

UPDATE schedule_dict SET depend_name = '0,1' WHERE `type` = 1 AND dict_value = 110;
DELETE FROM schedule_dict WHERE `type` = 1 AND dict_value = 180;