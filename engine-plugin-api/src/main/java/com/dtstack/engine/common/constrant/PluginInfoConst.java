package com.dtstack.engine.common.constrant;

/**
 * plugin info const
 *
 * @author ：wangchuan
 * date：Created in 16:25 2022/10/31
 * company: www.dtstack.com
 */
public interface PluginInfoConst {

    String SELF_CONF = "selfConf";

    String SECURITY_CONF = "securityConf";

    String SSL_CONF = "sslConf";

    String KERBEROS_CONF = "kerberosConf";

    String TBDS_CONF = "tbdsConf";

    String LDAP_CONF = "ldapConf";

    String PROXY_CONF = "proxyConf";

    String STORE_KERBEROS_CONF = "storeKerberosConf";

    String COMMON_CONF = "commonConf";

    String HADOOP_USER_NAME = "hadoopUserName";

    /**
     * 优先使用 hadoop.user.name，hadoopUserName 参数是历史的产物
     */
    String HADOOP_USER_NAME_DOT = "hadoop.user.name";

    String HADOOP_PROXY_ENABLE = "hadoop.proxy.enable";

    String OTHER_CONF = "otherConf";

    String COMPONENT_TYPE = "componentType";

    String COMPONENT_VERSION = "componentVersion";

    String ENGINE_ZOOKEEPER_URL = "engine.zookeeper.url";

    String APP_TYPE = "appType";

    String PLUGIN_INFO_VERSION = "version";
}
