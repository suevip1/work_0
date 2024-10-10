update schedule_dict set depend_name = 'Hadoop2' where `type` = 0 and dict_name in ('CDH 5.1.x', 'CDH 5.2.x', 'CDH 5.3.x', 'CDH 5.4.x', 'CDH 5.5.x', 'CDH 5.6.x');
update schedule_dict set dict_name = 'yarnHW-hdfsHW-flinkHW' where `type` = 6 and dict_name = 'yarHW-hdfsHW-flinkHW';
delete from schedule_dict where `type` = 6 and dict_name = 'sftp';
alter table console_component add column version_name varchar(63) not null default '' comment '组件版本名';

#增加mysql版本
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES
	 ('mysql_version','5.x','.',NULL,13,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('mysql_version','8.x','8.0',NULL,13,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);

#刷组件版本
#mysql
update console_component set version_name = '8.x' where component_type_code = 22 and version_name = '';
#yarn or hadoop
delimiter //

CREATE PROCEDURE hadoop_version_move (code int, dictType int, msg text)
BEGIN
	declare error int default false;
	declare message text default '';
	declare cnt_empty int default 0;

update console_component set hadoop_version = '2.7.6' where hadoop_version is null or hadoop_version = '';
update console_component cc inner join console_component_config ccc on cc.id = ccc.component_id and ccc.is_deleted = 0 and ccc.`key` like '%hadoopVersion%' set cc.version_name = ccc.value where cc.component_type_code = code and (cc.version_name = '' or cc.version_name is null) and cc.is_deleted = 0;
end //

delimiter ;

call hadoop_version_move(4, 0, 'hadoop component');
call hadoop_version_move(5, 0, 'yarn component');

update console_component cc1 inner join (select * from console_component where component_type_code = 4 and is_deleted = 0 and version_name <> '' and version_name is not null) cc2 on cc1.cluster_id = cc2.cluster_id set cc1.version_name = cc2.version_name where cc1.component_type_code = 5 and cc1.is_deleted = 0 and (cc1.version_name = '' or cc1.version_name is null);
update console_component cc1 inner join (select * from console_component where component_type_code = 5 and is_deleted = 0 and version_name <> '' and version_name is not null) cc2 on cc1.cluster_id = cc2.cluster_id set cc1.version_name = cc2.version_name where cc1.component_type_code = 4 and cc1.is_deleted = 0 and (cc1.version_name = '' or cc1.version_name is null);

update console_component cc inner join console_component cc1 on cc.cluster_id = cc1.cluster_id and cc1.component_type_code = 15 and cc1.is_deleted = 0 set cc.version_name = 'Hadoop 2.x' where cc.component_type_code = 4 and cc.hadoop_version REGEXP '^2\.[.0-9]*$' and (cc.version_name is null or cc.version_name = '');
update console_component cc inner join console_component cc1 on cc.cluster_id = cc1.cluster_id and cc1.component_type_code = 15 and cc1.is_deleted = 0 set cc.version_name = 'Hadoop 3.x' where cc.component_type_code = 4 and cc.hadoop_version REGEXP '^3\.[.0-9]*$' and (cc.version_name is null or cc.version_name = '');

delimiter //

#flink or spark or spark_thrift or hive or inceptor sql
delimiter //

CREATE PROCEDURE version_move (code int, dictType int, msg text)
BEGIN

	declare error int default false;
	declare message text default '';
	DECLARE hasNext INT DEFAULT TRUE;
	DECLARE cId BIGINT;
	declare cnt int;
	DECLARE cid_cursor CURSOR for select cc.id from console_component cc left join schedule_dict sd on cc.hadoop_version = sd.dict_value and cc.component_type_code = code and cc.is_deleted = 0 and (cc.version_name is null or cc.version_name = '') and sd.`type` = dictType and sd.is_deleted = 0 where cc.component_type_code = code and cc.is_deleted = 0 and (cc.version_name is null or cc.version_name = '') and sd.dict_name is null;
declare cnt_cursor cursor for select cc.id, count(1) from console_component cc inner join schedule_dict sd on cc.hadoop_version = sd.dict_value and cc.component_type_code = code and cc.is_deleted = 0 and (cc.version_name is null or cc.version_name = '') and sd.`type` = dictType and sd.is_deleted = 0 group by cc.id;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET hasNext = FALSE;
OPEN cid_cursor;
FETCH cid_cursor INTO cId;
if hasNext is TRUE then
		set error = true;
end if;
CLOSE cid_cursor;

	set hasNext = true;
	set message = '';
	set error = false;
open cnt_cursor;
fetch cnt_cursor into cId, cnt;

outer_block: BEGIN
	while hasNext is true do
		if cnt > 1 then
			set error = true;
			leave outer_block;
end if;
fetch cnt_cursor into cId, cnt;
end while;
end outer_block;
close cnt_cursor;


update console_component c inner join schedule_dict d on c.hadoop_version = d.dict_value and c.component_type_code = code and c.is_deleted = 0 and d.`type` = dictType and d.is_deleted = 0 set version_name = d.dict_name where c.version_name is null or c.version_name = '';
end //

delimiter ;

call version_move (0, 1, 'flink components');
call version_move (1, 2, 'spark components');
call version_move (6, 3, 'spark thrift components');
call version_move (9, 4, 'hive server components');
call version_move (19, 7, 'inceptor sql components');

update schedule_dict set dict_value = '.' where `type` = 4 and dict_name = '1.x' and is_deleted = 0;
update schedule_dict set dict_value = '.' where `type` = 3 and dict_name = '1.x' and is_deleted = 0;
update schedule_dict set dict_value = '2.7.2:tbds' where `type` = 0 and dict_name = 'TBDS 5.1.x' and is_deleted = 0;
update schedule_dict set dict_value = '.' where `type` = 7 and dict_name = '6.2.x';
update console_component set hadoop_version = '.' where component_type_code = 19 and hadoop_version = '6.2.x' and is_deleted = 0;
update console_component set hadoop_version = '2.7.2:tbds' where component_type_code = 4 and hadoop_version = 'tbds2.7.2' and is_deleted = 0;
update console_component set hadoop_version = '2.7.2:tbds' where component_type_code = 5 and hadoop_version = 'tbds2.7.2' and is_deleted = 0;

#组件模型配置
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES
	 ('component_model','HDFS','{
	"owner": "STORAGE",
	"dependsOn": ["RESOURCE"],
	"allowCoexistence": false,
	"isTreeVersion": true,
	"versionDictionary": "HADOOP_VERSION",
	"nameTemplate": "hdfs{}",
	"computeTemplate": "hadoop{}",
	"defaultResource": "DEFAULT"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','FLINK','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "FLINK_VERSION",
	"nameTemplate": "flink{}",
	"defaultStorage": "DEFAULT",
	"supportedPlugins": [
		"flink112-standalone",
		"yarn3-hdfs3-flink110",
		"yarn2-hdfs2-flink180",
		"k8s-hdfs2-flink110",
		"yarn2-hdfs2-flink110",
		"yarnHW-hdfsHW-flinkHW",
		"yarn3-hdfs3-flink180",
		"flink110-standalone",
		"flink180-standalone",
		"k8s-s3-flink112",
		"yarn2-hdfs2-flink112",
		"yarn3-hdfs3-flink112",
		"yarn2tbds-hdfs2tbds-flink110",
		"k8s-hdfs2-flink112",
		"k8s-hdfs3-flink112"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','SPARK','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": true,
	"versionDictionary": "SPARK_VERSION",
	"nameTemplate": "spark{}",
	"defaultResource": "DEFAULT",
	"defaultStorage": "DEFAULT",
	"dependConfigTemplates": [{
		"type": "YARN",
		"versionName": "HDP 3.1.x",
		"templateId": -200
	}],
	"supportedPlugins": ["yarn3-hdfs3-spark210","yarn3-hdfs3-spark240","yarn2-hdfs2-spark210","k8s-hdfs2-spark240"]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','SPARK_THRIFT','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"versionDictionary": "SPARK_THRIFT_VERSION",
	"nameTemplate": "hive{}",
	"supportedPlugins": [
		"hive",
		"hive2"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','HIVE_SERVER','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"versionDictionary": "HIVE_VERSION",
	"nameTemplate": "hive{}",
	"versionConfigTemplates": [{
		"templateId": -212
	}],
	"supportedPlugins": [
		"hive",
		"hive2",
		"hive3"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','SFTP','{
	"owner": "COMMON",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "dummy"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','YARN','{
	"owner": "RESOURCE",
	"dependsOn": [],
	"allowCoexistence": false,
	"isTreeVersion": true,
	"versionDictionary": "HADOOP_VERSION",
	"nameTemplate": "yarn{}"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','KUBERNETES','{
	"owner": "RESOURCE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "k8s"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','NFS','{
	"owner": "STORAGE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "nfs"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
	 ('component_model','LEARNING','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"nameTemplate": "learning",
	"defaultResource": "YARN",
	"defaultStorage": "DEFAULT",
	"supportedPlugins": [
		"yarn2-hdfs2-learning",
		"yarn3-hdfs3-learning"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES
                                                                                                                                                         ('component_model','DT_SCRIPT','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE", "STORAGE"],
	"allowCoexistence": false,
	"nameTemplate": "dtscript",
	"defaultResource": "YARN",
	"defaultStorage": "DEFAULT",
	"supportedPlugins": [
		"yarn2-hdfs2-dtscript",
		"yarn3-hdfs3-dtscript"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','LIBRA_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "postgresql"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','IMPALA_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "impala"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','TIDB_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "tidb"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','ORACLE_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "oracle"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','GREENPLUM_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "greenplum"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','PRESTO_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "presto"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','DTSCRIPT_AGENT','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "dtscript-agent"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','INCEPTOR_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"versionDictionary": "INCEPTOR_SQL",
	"nameTemplate": "inceptor{}"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','ANALYTICDB_FOR_PG','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "adb-postgresql"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);
INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,`type`,sort,data_type,depend_name,is_default,gmt_create,gmt_modified,is_deleted) VALUES
                                                                                                                                                         ('component_model','MYSQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "mysql{}",
	"versionDictionary": "MYSQL_VERSION"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','SQL_SERVER','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "sqlserver"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','TRINO_SQL','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "trino"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','OCEANBASE','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "oceanbase"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','DB2','{
	"owner": "COMPUTE",
	"dependsOn": [],
	"allowCoexistence": false,
	"nameTemplate": "db2"
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0),
                                                                                                                                                         ('component_model','TONY','{
	"owner": "COMPUTE",
	"dependsOn": ["RESOURCE","STORAGE"],
	"allowCoexistence": false,
	"nameTemplate": "tony",
	"defaultResource": "DEFAULT",
	"defaultStorage": "DEFAULT",
	"supportedPlugins": [
		"yarn2-hdfs2-tony",
		"yarn3-hdfs3-tony"
	]
}',NULL,12,0,'STRING','',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0);


delete from console_component_config where component_id = -117 and `key` = 'hive.metastore.uris';
insert into console_component_config (cluster_id, component_id, component_type_code, `type`, required, `key`, value, `values`, dependencyKey, dependencyValue, `desc`, gmt_create, gmt_modified, is_deleted)
values (-2, -212, 9, 'INPUT', 0, 'hive.metastore.uris', '', null, null, null, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

drop procedure hadoop_version_move;
drop procedure version_move;
