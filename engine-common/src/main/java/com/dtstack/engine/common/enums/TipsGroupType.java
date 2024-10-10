package com.dtstack.engine.common.enums;

/**
 * @auther: shuxing
 * @date: 2022/4/12 14:20 周二
 * @email: shuxing@dtstack.com
 * @description:
 */
public enum TipsGroupType {

    // Spark
    SPARK_MAIN("主要"),
    SPARK_RESOURCE("资源"),
    SPARK_NETWORK("网络"),
    SPARK_EVENT_LOG("时间日志"),
    SPARK_JVM("JVM"),
    SPARK_ENVIRONMENT_VARIABLE("环境变量"),
    SPARK_SAFETY("安全"),
    SPARK_KUBERNETES("kubernetes"),
    // Fink
    FINK_COMMON_PARAM("公共参数"),
    FINK_HA("高可用"),
    FINK_METRIC_MONITORING("metric监控"),
    FINK_FAULT_TOLE_AND_CHECKING("容错和checkpointing"),
    FINK_ADVANCED("高级"),
    FINK_JVM_PARAM("JVM参数"),
    FINK_YARN("Yarn"),
    FINK_DTSTACK_PARAM("数栈平台参数"),
    // volcano
    VOLCANO_MIRROR("镜像参数"),
    VOLCANO_DBDRIVER("数据库驱动"),
    VOLCANO_HDFS("hdfs参数"),

    OTHER("其它"),
    CUSTOM("自定义参数");

    public String value;

    TipsGroupType(String value){
        this.value = value;
    }


}
