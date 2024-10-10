-- add new computing component: volcano, modified by qiuyun
DELETE FROM `schedule_dict` where `dict_code` = 'component_model' and `dict_name` = 'VOLCANO' and `type` = 12;
INSERT INTO `schedule_dict`
(`dict_code`, `dict_name`, `dict_value`, `dict_desc`, `type`, `sort`, `data_type`, `depend_name`, `is_default`, `gmt_create`, `gmt_modified`, `is_deleted`)
    VALUES
('component_model', 'VOLCANO', '{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false
}', NULL, 12, 0, 'STRING', '', 0, '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);

update schedule_dict set dict_name = '', dict_value = '{
  "KUBERNETES": "kubernetes",
  "S3": {
    "CSP": {
      "FLINK": [
        {
          "1.12": "k8s-s3-flink112"
        }
      ],
      "S3": "s3",
      "DT_SCRIPT": "",
      "SPARK":[]
    }
  },
  "NFS": {
    "": {
      "FLINK": [
        {
          "1.10": "k8s-nfs-flink110"
        }
      ],
      "NFS": "nfs",
      "DT_SCRIPT": "",
      "SPARK":[]
    }
  },
  "HDFS": {
    "hdfs2": {
      "FLINK": [
        {
          "1.10": "k8s-hdfs2-flink110"
        },
        {
          "1.12": "k8s-hdfs2-flink112"
        }
      ],
      "SPARK": [
        {
          "2.4": "k8s-hdfs2-spark240"
        }
      ],
      "DT_SCRIPT": "k8s-hdfs2-dtscript",
      "VOLCANO":"k8s-hdfs2-volcano",
      "HDFS": "k8s-hdfs2-hadoop2"
    },
    "hdfs3": {
      "FLINK": [],
      "DT_SCRIPT": "k8s-hdfs3-dtscript",
      "SPARK":[],
      "VOLCANO":"k8s-hdfs3-volcano",
      "HDFS": "k8s-hdfs3-hadoop3"
    }
  }
}'
where `type` = 14 and dict_code = 'component_model_config' and (dict_name = '' or dict_name = ' ')
  and depend_name = 'KUBERNETES';

delete from schedule_dict where dict_code = 'typename_mapping' and dict_name in ('k8s-hdfs2-volcano','k8s-hdfs3-volcano');
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'k8s-hdfs2-volcano', '-137', null, 6, 0, 'LONG', '', 0, '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO schedule_dict (dict_code, dict_name, dict_value, dict_desc, type, sort, data_type, depend_name, is_default, gmt_create, gmt_modified, is_deleted)
VALUES ('typename_mapping', 'k8s-hdfs3-volcano', '-137', null, 6, 0, 'LONG', '', 0, '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);

delete from console_component_config where component_id = -137;
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'image.pull.policy', 'IfNotPresent', null, null, null, '镜像拉取策略', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'image.pull.secrets', 'regsecret', null, null, null, 'k8s secrets', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'image.url', '', null, null, null, '镜像仓库地址', '2022-04-14 14:42:27', '2022-04-14 14:42:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.model.service.image', 'modelservice-cpu:1.0.0', null, null, null, '适配cpu的模型推理服务镜像', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.py4j.image', 'py4j-hadoop2:1.0.0', null, null, null, '适配hadoop的py4j镜像（默认hadoop2）', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.logtracer.image', 'logtracer-hadoop2:1.0.0', null, null, null, '适配hadoop的容器内日志搜集镜像（默认hadoop2）', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.python.image', 'python:1.0.0', null, null, null, 'python镜像', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.pytorch.image', 'pytorch-cpu:1.0.0', null, null, null, '适配cpu的pytorch镜像', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.tensorflow.image', 'tensorflow-cpu:1.0.0', null, null, null, '适配cpu的tensorflow镜像','2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'kubernetes.jupyter.image', 'jupyter-cpu:1.0.0', null, null, null, '适配cpu的jupyter镜像','2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'jdbcUrl', '', null, null, null, '访问hive库的jdbc地址','2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'hadoop.username', 'admin', null, null, null, '操作hdfs的用户名', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);
INSERT INTO console_component_config (cluster_id, component_id, component_type_code, type, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
VALUES (-2, -137, 29, 'INPUT', 1, 'logtracerDestPath', 'hdfs://ns1/dtInsight/logs', null, null, null, 'hdfs上保存日志的路径', '2022-03-31 11:39:27', '2022-03-31 11:39:27', 0);