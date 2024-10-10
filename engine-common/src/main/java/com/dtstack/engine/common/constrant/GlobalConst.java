package com.dtstack.engine.common.constrant;

/**
 * @Auther: dazhi
 * @Date: 2020/12/22 9:51 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface GlobalConst {

    String PATH_CUT = "&sftp:";

    String RULE_LOG_FILED = "ruleLogList";

    String SUBMIT_QUEUE_SIZE = "engine.submit.job.queue.size";

    String SUBMIT_CONSUMER_QUEUE_MIN_NUM = "engine.submit.job.consumer.min.num";

    String SUBMIT_CONSUMER_QUEUE_MAX_NUM = "engine.submit.job.consumer.max.num";

    String PASSWORD = "PASSWORD";

    String taskParamToReplace = "taskParamsToReplace";

    String SEMICOLON = ";";

    String sqlText = "sqlText";
    String PROXY = "proxy";

    String LOG_TRACE = "logTrace";

    String KUBERNETES_CONTEXT = "kubernetes.context";

    String COMMA = ",";

    String STAR = "※";

    String NO = "1";

    String OFF = "0";

    Integer YES = 1;

    Integer NONE = 0;

    String ESTIMATED_FINISH_TIME = "历史数据不足，暂无法评估";

    String OPEN_KERBEROS = "openKerberos";

    String HADOOP_CONFIG = "hadoopConfig";

    String SSL_CONFIG = "sslConfig";

    String PATH = "path";

    String SEPARATOR = "/";

    String KERBEROS_CONFIG = "kerberosConfig";

    String CALENDER_TEMPLATE = "%s.csv";
    String CALENDER_TITLE = "调度日期" +
            "1. 按%s格式填写" +
            "2. 日期上限1000个" +
            "3. 从第二行开始解析";

    String GLOBAL_PARAM_CALENDER_TITLE = "自定义日期" +
            "1. 请按定义的日期格式填写;" +
            "2. 日期上限1000个;" +
            "3. 从第二行开始解析;";

    String GLOBAL_PARAM_CYCTIME_TITLE_FIRST_COLUMN = "计划时间(%s)";

    String GLOBAL_PARAM_CYCTIME_TITLE_SECOND_COLUMN = "参数值";

    String URL = "url";

    String SLASH = "/";

    String PARTITION = "-";

    String TASK_RULE_TIMEOUT = "taskRuleTimeout";

    String TIMEOUT = "timeout";

    String EVENT_FAIL = "状态:3 超时%s分钟未触发";

    String TOKEN = "token";

    /**
     * 行分隔符
     */
    String LINE_SEPARATOR = "\\r?\\n";

    String TASK_PARAMS = "taskParams";

    String EXE_ARGS = "exeArgs";

    String STOP = "stop-";

    String IS_FAIL_RETRY = "isFailRetry";

    String MAX_RETRY_NUM = "maxRetryNum";

    String RETRY_INTERVAL_TIME = "retryIntervalTime";

    String MAX_JOB_POOL_SIZE = "maxJobPoolSize";
    String MIN_JOB_POOL_SIZE = "minJobPoolSize";

    String MULTI_PARTITIONS = "multiPartitions";

    Long ZERO_FLOW_ID = 0L;

    String DATA_SECURITY_CONTROL = "data.security.control";

    String JDBC_URL = "jdbcUrl";

    String USERNAME = "username";

    String PASS_WORD = "password";

    String TENANT_ID = "tenantId";

    String LOCK_SUFFIX = ".lock";

    String HIVE_MONITOR_RESOURCE_ENABLE= "hive.monitor.resources.enable";

    String PROMETHEUS_HOST = "metrics.prometheus.server.host";

    String PROMETHEUS_PORT = "metrics.prometheus.server.host";

    String PUSH_GATEWAY_HOST = "metrics.prometheus.pushgateway.host";

    String PUSH_GATEWAY_PORT = "metrics.prometheus.pushgateway.port";

    String SPARK_THRIFT_SERVER_MONITOR_KEY = "sparkThriftServerUrl";

    Long DEFAULT_FETCH_CACHE_INTERVAL = 5L;

    String TASK_KEY_SEPARATOR = "-";

    String HIVE_SET_CHECK_ENABLE = "hive.set.check.enable";

    String HOT_RELOADING = "flink.job.hot.start";

    String KERBEROS_PATH = "kerberos";

    String STATUS_BLACK_LIST = "StatusBlackList_";

    Integer HALF_YEAR_IN_DAYS = 183;
    Integer RESTRICT_JOB_QUERY_NUM = 950000;
}
