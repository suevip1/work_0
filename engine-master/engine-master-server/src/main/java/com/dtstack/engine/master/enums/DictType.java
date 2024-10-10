package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.enums.EComponentType;

/**
 * @author yuebai
 * @date 2021-03-02
 * 0～4,7,12 各个组件对于版本
 * 5 各个版本组件的额外配置
 * 6 默认模版id和typename对应关系
 * 33 ranger 参数模板
 */
public enum DictType {
    HADOOP_VERSION(0),
    FLINK_VERSION(1),
    SPARK_VERSION(2),
    SPARK_THRIFT_VERSION(3),
    HIVE_VERSION(4),
    COMPONENT_CONFIG(5),
    TYPENAME_MAPPING(6),
    INCEPTOR_SQL(7),
    DATA_CLEAR_NAME(8),
    ALERT_CONFIG(9),
    JDBC_URL_TIP(10),
    SSL_TEMPLATE(11),
    COMPONENT_MODEL(12),
    MYSQL_VERSION(13),
    RESOURCE_MODEL_CONFIG(14),
    EXTRA_VERSION_TEMPLATE(15),
    HDFS_TYPE_NAME(16),
    ENCRYPT_KEY(17),
    EXCLUDE_XML_PARAM(18),
    S3_VERSION(22),
    TIPS(25),
    TASK_CLIENT_TYPE(23),
    VERSION_DATASOURCE_X(24),
    KNOX(30),
    LOG_TRACE(31),
    CONSOLE_MODE(32),
    SECURITY_PARAM_TEMPLATE(33),
    VERSION_FORMAT(34),
    FLINK_VERSION_PARAM(35),
    EXECUTOR_CONFIG(36),
    STARROCKS_VERSION(37),
    FILL_LIMIT(99),
    ;

    public Integer type;

    DictType(Integer type) {
        this.type = type;
    }

    public static Integer getByEComponentType(EComponentType type) {
        switch (type) {
            case FLINK:
                return FLINK_VERSION.type;
            case SPARK:
                return SPARK_VERSION.type;
            case HIVE_SERVER:
                return HIVE_VERSION.type;
            case SPARK_THRIFT:
                return SPARK_THRIFT_VERSION.type;
            // case S3:
            //     return S3_VERSION.type;
            default:
                return null;
        }
    }
}
