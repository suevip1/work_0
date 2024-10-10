-- sdk adapt
delete from environment_param_template where task_type = 5;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (5, 'DEEP_LEARNING', null, -1, '## 每个worker所占内存，比如512m
worker.memory=512m

## worker的数量
worker.num=1

## 每个worker所占的cpu核的数量
worker.cores=1

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', now(), now(), 0);

delete from environment_param_template where task_type = 9;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (9, 'HADOOP_MR', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 12;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (12, 'CARBON_SQL', null, -1, '##Driver程序使用的CPU核数,默认为1
##driver.cores=1

##Driver程序使用内存大小,默认512m
##driver.memory=512m

##对Spark每个action结果集大小的限制，最少是1M，若设为0则不限制大小。
##若Job结果超过限制则会异常退出，若结果集限制过大也可能造成OOM问题，默认1g
##driver.maxResultSize=1g

##SparkContext 启动时是否记录有效 SparkConf信息,默认false
##logConf=false


##启动的executor的数量，默认为1
executor.instances=1

#每个executor使用的CPU核数，默认为1
executor.cores=1

##每个executor内存大小,默认512m
##executor.memory=512m
isCarbondata=true

##任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10', now(), now(), 0);

delete from environment_param_template where task_type = 15;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (15, 'LIBRA_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 18;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (18, 'IMPALA_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 19;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (19, 'TIDB_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 20;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (20, 'ORACLE_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 21;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (21, 'GREENPLUM_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 28;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (28, 'INCEPTOR_SQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 30;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (30, 'ANALYTICDB_FOR_PG', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 32;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (32, 'MYSQL', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 33;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (33, 'SQL_SERVER', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 34;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (34, 'DB2', null, -1, '', now(), now(), 0);

delete from environment_param_template where task_type = 35;
INSERT INTO environment_param_template (task_type, task_name, task_version, app_type, params, gmt_create, gmt_modified, is_deleted) VALUES
    (35, 'OCEANBASE', null, -1, '', now(), now(), 0);