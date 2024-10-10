package com.dtstack.engine.common.constrant;

import java.io.File;

/**
 * 常量
 * Date: 2018/1/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ConfigConstant {


    public static String SP = File.separator;
    public static final String SPLIT = "_";
    public static final String RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT = "default";
    public static final String COMMA = ",";

    public static final String FLINK_HIDDEN = "hidden";
    public static final String FLINK_DEFAULT = "default";
    public static final String FLINK_CHANGE_DEFAULTED = "changeDefaulted";


    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String BACKSLASH = "/";

    /**
     * first clusterName，second queueName
     */
    public static final String DEFAULT_GROUP_NAME = String.join(SPLIT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT);

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String LOCAL_KEYTAB_DIR_PARENT = USER_DIR + "/kerberos/keytab";
    public static final String LOCAL_KRB5_DIR_PARENT = USER_DIR + "/kerberos/krb5";
    public static final String LOCAL_KRB5_MERGE_DIR_PARENT = USER_DIR + "/kerberos/merge";
    public static final String MERGE_KRB5_NAME = "mergeKrb5.conf";
    public static final String MERGE_KRB5_CONTENT_KEY = "mergeKrbContent";


    public static final String VERSION = "version";
    public static final String IS_METADATA = "metadata";
    public static final String STORE_TYPE = "storeType";
    public static final String CUSTOMER_PRIORITY_VAL = "job.priority";
    public static final String VERSION_NAME = "versionName";


    public static final String AKKA_LOCALMODE = "akka.localMode";
    public static final String AKKA_ACTOR_PROVIDER = "akka.actor.provider";

    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";
    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";

    public static final String AKKA_DAGSCHEDULEX_SYSTEM = "dagschedulex";

    public static final String AKKA_MASTER_MASTERADDRESS = "akka.master.masterAddress";

    public static final String AKKA_ASK_TIMEOUT = "akka.ask.timeout";
    public static final String AKKA_ASK_RESULTTIMEOUT = "akka.ask.resultTimeout";
    public static final String AKKA_ASK_SUBMIT_TIMEOUT = "akka.ask.submitTimeout";
    public static final String AKKA_ASK_CONCURRENT = "akka.ask.concurrent";

    public static final String AKKA_WORKER_NODE_LABELS = "akka.worker.node.labels";
    public static final String AKKA_WORKER_SYSTEMRESOURCE_PROBE_INTERVAL = "akka.worker.systemresource.probe.Interval";
    public static final String AKKA_WORKER_TIMEOUT = "akka.worker.timeout";
    public static final String AKKA_WORKER_LOGSTORE_JDBCURL = "akka.worker.logstore.jdbcUrl";
    public static final String AKKA_WORKER_LOGSTORE_USERNAME = "akka.worker.logstore.username";
    public static final String AKKA_WORKER_LOGSTORE_PASSWORD = "akka.worker.logstore.password";

    public static final String DAGSCHEULEX_JDBC_URL = "jdbc.url";
    public static final String DAGSCHEULEX_JDBC_USERNAME = "jdbc.username";
    public static final String DAGSCHEULEX_JDBC_PASSWORD = "jdbc.password";

    public static final String DATASOURCE_TYPE = "dataSourceType";
    public static final String JDBCURL = "jdbcUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    /**ldap用户组名称**/
    public static final String LDAP_GROUP_NAME = "ldapGroupName";
    public static final String INITIAL_SIZE = "initialSize";
    public static final String MINIDLE = "minIdle";
    public static final String MAXACTIVE = "maxActive";
    public static final String METASTORE_URI = "hive.metastore.uris";
    public static final String RDB_TIME_OUT = "rdbTimeout";

    public static final String SQL_CHECKPOINT_TIMEOUT = "sql.checkpoint.timeout";
    public static final String FLINK_CHECKPOINT_TIMEOUT = "flink.checkpoint.timeout";
    public static final Long DEFAULT_CHECKPOINT_TIMEOUT = 600000L;

    public static final String KERBEROS = "kerberos";
    public static final String KERBEROS_PATH = "kerberos";

    public static final String REMOTE_DIR = "remoteDir";
    public static final String PRINCIPAL_FILE = "principalFile";
    public static final String PRINCIPAL = "principal";
    public static final String KRB_NAME = "krbName";
    public static final String OPEN_KERBEROS = "openKerberos";
    public static final String KRBNAME_DEFAULT = "krb5.conf";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String KRB5_CONF = "krb5.conf";
    public static final String KEYTAB_SUFFIX = ".keytab";
    public static final String KERBEROS_CONFIG = "kerberosConfig";
    public static final String SSL_CLIENT = "sslClient";
    // Kerberos 文件上传的时间戳
    public static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";
    // Kerberos .lock 文件
    public static final String LOCK_SUFFIX = ".lock";


    public static final String LDAP_USER_NAME = "dtProxyUserName";
    public static final String LDAP_PASSWORD = "dtProxyPassword";

    public static final String HADOOP_USER_NAME = "hadoopUserName";

    public static final String ZIP_SUFFIX = ".zip";
    public static final String USER_DIR_UNZIP = System.getProperty("user.dir") + File.separator + "unzip";
    public static final String USER_DIR_DOWNLOAD = System.getProperty("user.dir") + File.separator + "download";


    public static final String XML_SUFFIX = ".xml";

    public static final long DEFAULT_KUBERNETES_PARENT_NODE = -2L;
    public static final long DEFAULT_TENANT  = -1L;

    public static final String PARAMS_DELIM = "&";
    public static final String URI_PARAMS_DELIM = "?";

    public static final String PLUGIN_INFO = "pluginInfo";


    /**
     * component_config other类型 key
     */
    public static final String TYPE_NAME_KEY = "typeName";
    public static final String TYPE_NAME = "typeName";
    public static final String HADOOP_VERSION = "hadoopVersion";
    public static final String MD5_ZIP_KEY = "md5zip";
    public static final String MD5_SUM_KEY = "md5sum";
    public static final String FLINK_ON_STANDALONE_CONF = "flinkOnStandaloneConf";


    public static final Long DEFAULT_CLUSTER_ID = -1L;

    public static final String DEFAULT_CLUSTER_NAME = "default";
    public final static String CLUSTER = "cluster";
    public final static String QUEUE = "queue";
    public final static String TENANT_ID = "tenantId";
    public static final String DEPLOY_MODEL = "deployMode";
    public static final String ARCHIVE_FS_DIR = "jobmanager.archive.fs.dir";
    public static final String NAMESPACE = "namespace";
    public static final String MAILBOX_CUTTING = "@";

    /**
     * special hadoop version
     */
    public static final String TBDS = "TBDS";
    public static final String TBDS_SECURE_ID = "tbdsSecureId";
    public static final String TBDS_SECURE_KEY = "tbdsSecureKey";
    public static final String TBDS_USER_NAME = "tbdsUserName";

    /**
     * S3 version
     */
    public static final String CSP = "CSP";

    /**
     * Flink version
     */
    public static final String FLINK_VERSION_112 = "112";




    public static final String DATA_SOURCE_ID = "dtDataSourceId";


    /**
     * column
     */

    public static final String GMT_MODIFIED = "gmt_modified";

    public static final Integer NO = 0;
    public static final Integer YES = 1;

    public static final String CALENDAR_TEMPLATE_DIR_PREFIX = "calendar_template_";

    public static final String CHECK_POINTS_DIR = "state.checkpoints.dir";
    public static final String SAVE_POINTS_DIR = "state.savepoints.dir";
    public static final String COMPLETE_LOGS_DIR = "jobmanager.archive.fs.dir";
    public static final String HA_STORAGE_DIR = "high-availability.storageDir";
    public static final String JOB_URL_FORMAT = "/jobs/%s";
    public static final String JOB_CHECKPOINTS_URL_FORMAT = "/jobs/%s/checkpoints";

    public final static String CHECKPOINT_ID_KEY = "id";
    public final static String CHECKPOINT_SAVEPATH_KEY = "external_path";
    public final static String CHECKPOINT_STATE_SIZE = "state_size";
    public final static String END_TO_END_DURATION = "end_to_end_duration";
    public final static String TRIGGER_TIMESTAMP_KEY = "trigger_timestamp";
    public static final String CHECKPOINT_STATUS_KEY = "status";
    public static final String CHECKPOINT_COMPLETED_STATUS = "COMPLETED";
    public static final String WORKER_PROXY_MIN_POOL_SIZE = "worker.proxy.min.pool.size";
    public static final String WORKER_PROXY_MAX_POOL_SIZE = "worker.proxy.max.pool.size";
    public static final String WORKER_PROXY_IDLE_KEEP_ALIVE_TIME = "worker.proxy.idle.keep.alive.time";
    public static final String WORKER_PROXY_QUEUE_SIZE = "worker.proxy.queue.size";
    public static final String USER_ID = "userId";
}
