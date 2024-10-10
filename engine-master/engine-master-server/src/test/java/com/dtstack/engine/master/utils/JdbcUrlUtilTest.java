package com.dtstack.engine.master.utils;

import org.junit.Test;

public class JdbcUrlUtilTest {

    @Test
    public void getUrlInfo() {
        JdbcUrlUtil.getUrlInfo("jdbc:hive2://172.16.85.248:10004/%s");
    }


    @Test
    public void testGetSchema() {
        String uri;
        uri = "jdbc:hive2://cdp7-scm-node1:10000/cdp_default?queryTimeout=700";
        System.out.println(JdbcUrlUtil.getSchema(uri));
        uri = "jdbc:hive2://cdp7-scm-node1:10000/cdp_default;principal=hive/cdp7-scm-node1@DTSTACK.COM;ssl=true;sslTrustStore=/run/cloudera-scm-agent/process/2092-hive_on_tez-HIVESERVER2/cm-auto-global_truststore.jks";
        System.out.println(JdbcUrlUtil.getSchema(uri));
        uri = "jdbc:hive2://:/cdp_default;initFile=;sess_var_list?hive_conf_list#hive_var_list";
        System.out.println(JdbcUrlUtil.getSchema(uri));
        uri = "jdbc:hive2://:/cdp_default";
        System.out.println(JdbcUrlUtil.getSchema(uri));
    }

}