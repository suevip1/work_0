package com.dtstack.engine.common.constrant;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * 跟业务中心交互的常量
 * @author qiuyun
 * @version 1.0
 * @date 2022-05-13 16:37
 */
public interface UicConstant {
    String USER_GROUP_OU = "userGroup_ou";
    String BASE_DN = "BaseDN";
    String USER_OU = "user_ou";
    String SERVICE_NAME = "serviceName";
    String JDBC_URL = "jdbcUrl";
    String USER_NAME = "username";
    String PASSWORD = "password";
    String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
    String JDBC_HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    String JDBC_TRINO_DRIVER = "io.trino.jdbc.TrinoDriver";

    String DESCRIPTION = "description";
    List<String> SYNC_RANGER_HDFS_KEYS = Collections.unmodifiableList(Lists.newArrayList(
            "username",
            "password",
            "description",
            "hadoop.security.authentication",
            "hadoop.security.authorization",
            "hadoop.security.auth_to_local",
            "dfs.datanode.kerberos.principal",
            "dfs.namenode.kerberos.principal",
            "hadoop.rpc.protection",
            "dfs.namenode.rpc-address.ns1.nn1",
            "dfs.namenode.rpc-address.ns1.nn2"
            ));
    String FS_DEFAULT_NAME = "fs.default.name";
    String DFS_NAMENODE_RPC_ADDRESS_NS1_NN1 = "dfs.namenode.rpc-address.ns1.nn1";
    String DFS_NAMENODE_RPC_ADDRESS_NS1_NN2 = "dfs.namenode.rpc-address.ns1.nn2";
    String HDFS_PREFIX = "hdfs://";

    String CUSTOM_CONFIG = "customConfig";
    String DT_CENTER_SOURCE_ID = "dtCenterSourceId";
    String KEY_PATH = "keyPath";
    String CLUSTER_CONFIG = "Cluster.Config";
    String CLUSTER_NUM_LABEL = "clusterNum";
}