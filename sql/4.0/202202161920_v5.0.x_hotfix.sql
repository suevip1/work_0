-- k8s 资源组件添加异常问题修复
update schedule_dict set dict_name = '', dict_value = '{
    "KUBERNETES":"kubernetes",
    "S3":{
        "FLINK":[
            {
                "1.12":"k8s-s3-flink112"
            }
        ],
        "S3":"s3"
    },
    "NFS":{
        "FLINK":[
            {
                "1.10":"k8s-nfs-flink110"
            }
        ],
        "NFS":"nfs"
    },
    "HDFS":{
        "FLINK":[
            {
                "1.10":"k8s-hdfs2-flink110"
            },
            {
                "1.12":"k8s-hdfs2-flink112"
            }
        ],
        "SPARK":[
            {
                "2.4":"k8s-hdfs2-spark240"
            }
        ],
        "HDFS":"k8s-hdfs2-hadoop2"
    }
}'
where `type` = 14 and dict_code = 'component_model_config' and (dict_name = '' or dict_name = ' ')
  and depend_name = 'KUBERNETES';

-- S3 组件被误删问题修复
delete from schedule_dict where dict_code = 'component_model' and type = 12 and dict_name = 'S3';
INSERT INTO schedule_dict
(id, dict_code, dict_name,
 dict_value,
 dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES (null, 'component_model', 'S3', '{
	"owner": "STORAGE",
	"dependsOn": ["RESOURCE"],
	"versionDictionary": "S3_VERSION",
	"allowCoexistence": false
}', null, 12, 0, 'STRING', '', 0, now(), now(), 0);
